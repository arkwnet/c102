use std::fs::File;
use std::io::Read;
use std::io::Write;
use chrono::DateTime;
use chrono::Local;
use actix_web::{get, post, web, App, HttpResponse, HttpServer, Responder};
use serde::{Serialize, Deserialize};
use rusqlite::{params, Connection, Result};

#[derive(Debug, Serialize, Deserialize)]
struct Display {
  upper_left: String,
  upper_right: String,
  lower_left: String,
  lower_right: String
}

#[derive(Serialize, Deserialize, Debug)]
struct Receive {
  id: String,
  items: Vec<Item>,
  total: String,
  payment: String,
  cash: String,
  change: String
}


#[derive(Serialize, Deserialize, Debug)]
struct Item {
  name: String,
  price: String,
  quantity: String
}

#[derive(Debug)]
struct Sale {
  id: String,
  timestamp: String,
  name: String,
  quantity: u16,
  subtotal: u16
}

#[derive(Debug)]
struct Payment {
  id: String,
  timestamp: String,
  method: String,
  total: u16,
  cash: u16,
  change: u16
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
  HttpServer::new(|| {
    App::new().service(get_display).service(post_display).service(post_record)
  })
  .bind(("app", 8080))?
  .run()
  .await
}

#[get("/display")]
async fn get_display() -> HttpResponse {
  let mut file = File::open("display.json").unwrap();
  let mut buf = String::new();
  let _ = file.read_to_string(&mut buf);
  HttpResponse::Ok().content_type("application/json").body(buf)
}

#[post("/display")]
async fn post_display(display: web::Json<Display>) -> impl Responder {
  let serialized = serde_json::to_string(&display).unwrap();
  let mut file = File::create("display.json").unwrap();
  let _write_all = file.write_all(&serialized.as_bytes());
  let _flush = file.flush();
  HttpResponse::Ok().body("")
}

#[post("/record")]
async fn post_record(receive: web::Json<Receive>) -> impl Responder {
  let db = open_db();
  if let Ok(connection) = db {
    let _connection = connection;
    let local_datetime: DateTime<Local> = Local::now();
    let timestamp: String = local_datetime.to_string();
    for item in &receive.items {
      let quantity: u16 = item.quantity.parse::<u16>().unwrap();
      let price: u16 = item.price.parse::<u16>().unwrap();
      let subtotal = quantity * price;
      let _sale = Sale {
        id: receive.id.clone(),
        timestamp: timestamp.clone(),
        name: item.name.clone(),
        quantity: quantity,
        subtotal: subtotal
      };
      let _insert_sale = insert_sale(&_connection, &_sale);
    }
    let _payment = Payment {
      id: receive.id.clone(),
      timestamp: timestamp.clone(),
      method: receive.payment.clone(),
      total: receive.total.parse::<u16>().unwrap(),
      cash: receive.cash.parse::<u16>().unwrap(),
      change: receive.change.parse::<u16>().unwrap()
    };
    let _insert_payment = insert_payment(&_connection, &_payment);
  }
  HttpResponse::Ok().body("")
}

fn open_db() -> Result<Connection, rusqlite::Error> {
  let path = "./record.db";
  let connection = Connection::open(&path)?;
  println!("{}", connection.is_autocommit());
  Ok(connection)
}

fn insert_sale(connection: &Connection, sale: &Sale) -> Result<usize, rusqlite::Error> {
  return Ok(connection.execute(
    "insert into sale (id, timestamp, name, quantity, subtotal) values (?1, ?2, ?3, ?4, ?5)",
    params![sale.id, sale.timestamp, sale.name, sale.quantity, sale.subtotal]
  )?);
}

fn insert_payment(connection: &Connection, payment: &Payment) -> Result<usize, rusqlite::Error> {
  return Ok(connection.execute(
    "insert into payment (id, timestamp, method, total, cash, change) values (?1, ?2, ?3, ?4, ?5, ?6)",
    params![payment.id, payment.timestamp, payment.method, payment.total, payment.cash, payment.change]
  )?);
}
