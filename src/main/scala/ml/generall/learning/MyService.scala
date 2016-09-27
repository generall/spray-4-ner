package ml.generall.learning

import akka.actor.Actor
import ml.generall.elastic.ConceptVariant
import ml.generall.resolver.TrainObject
import spray.routing._
import spray.http._
import spray.json._
import spray.httpx.SprayJsonSupport._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

case class TestJson(n: Int, s: List[String]){}

object JsonProtocol extends DefaultJsonProtocol{
  implicit val fooFormat = jsonFormat2(TestJson)
}

object ResultJsonProtocol extends DefaultJsonProtocol{
  implicit val conceptFormat = jsonFormat7(ConceptVariant)
  implicit val trainFormat = jsonFormat3(TrainObject)
  implicit val resFormat = jsonFormat2(ParseResult)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  import JsonProtocol._
  import ResultJsonProtocol._

  val myRoute =
    path("1") {
      get {
        complete {
          TestJson(1, List("test")).toJson.prettyPrint
        }
      }
    } ~ path("2") {
      get {
        complete {
          TestJson(2, List("test"))
        }
      }
    } ~ path("3") {
      post{
        entity(as[TestJson]) {
          body => {
            println(body)
            complete("ok")
          }
        }
      }
    } ~ path("parse") {
      post{
        entity(as[String]) {
          body => {
            println(x = body)
            complete{
              SentenceConverter.convert(body)
            }
          }
        }
      }
    } ~ pathPrefix("public") {
      getFromResourceDirectory("public")
    }
}