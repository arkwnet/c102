use std::fs::File;
use std::io::Read;
use std::io::Write;
use actix_web::{get, post, web, App, HttpResponse, HttpServer, Responder};
use serde::{Serialize, Deserialize};

#[derive(Debug, Serialize, Deserialize)]
struct Display {
  upper_left: String,
  upper_right: String,
  lower_left: String,
  lower_right: String
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
  HttpServer::new(|| {
    App::new().service(get_display).service(post_display)
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
