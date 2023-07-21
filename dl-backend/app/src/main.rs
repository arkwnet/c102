use actix_web::{get, App, HttpServer, HttpResponse};

#[get("/")]
async fn index() -> HttpResponse {
  HttpResponse::Ok()
  .content_type("application/json")
  .body(r#"{"result": true, "data": "Hello world!"}"#)
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
  HttpServer::new(|| {
    App::new().service(index)
  })
  .bind(("app", 8080))?
  .run()
  .await
}
