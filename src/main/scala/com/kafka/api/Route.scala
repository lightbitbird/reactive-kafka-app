package com.kafka.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.kafka.models.Message
import com.kafka.producer.ProducerStream
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Failure, Success}

trait Route extends SprayJsonSupport {
  producer: ProducerStream =>

  implicit lazy val system = ActorSystem("kafka-producer-api")
  implicit lazy val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  protected override val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")
  val interface = config.getString("http.host")
  val port = config.getInt("http.port")

  private lazy val logger = Logging(system, getClass)

  def startApplication = {
    val binding = Http().bindAndHandle(route, interface, port)
    binding.onComplete {
      case Success(res) => logger.info("Success")
      case Failure(f) => logger.error(f, s"Failed to bind to $interface, $port")
    }
  }

  import JsonProtocol.messageFormat

  val route = pathPrefix("api") {

    path("producer") {
      post {
        entity(as[Message]) { msg =>
          logger.info(msg.toString)
          complete(ToResponseMarshallable(create(msg)))
        }
      }
    }
  }

}
