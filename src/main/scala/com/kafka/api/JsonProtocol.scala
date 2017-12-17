package com.kafka.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kafka.models.Message
import spray.json.{DefaultJsonProtocol, PrettyPrinter, RootJsonFormat}

object JsonProtocol extends DefaultJsonProtocol {
  implicit val messageFormat = jsonFormat2(Message)
//  implicit val printer = PrettyPrinter
  implicit val itemFormat: RootJsonFormat[Message] = jsonFormat(Message.apply, "topic", "messages")
}
