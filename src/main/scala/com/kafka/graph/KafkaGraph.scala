package com.kafka.graph

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.kafka.scaladsl.Consumer.Control
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, SourceShape}
import akka.util.ByteString
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext

trait KafkaGraph {
  implicit val system: ActorSystem
  protected val config: Config

  val logger: LoggingAdapter

  def consumerSource: Source[ConsumerRecord[String, String], Control]
  def toProducerSink: Sink[ProducerRecord[String, String], _]
  def toFileSink: Sink[ByteString, _]
  def loggerSink: Sink[String, _]

  val outputTopic: String

  def start(implicit ec: ExecutionContext): Unit = {

    val g = RunnableGraph.fromGraph(GraphDSL.createGraph(consumerSource) { implicit b =>
      source: SourceShape[ConsumerRecord[String, String]] =>
        import GraphDSL.Implicits._

        val broadcast = b.add(Broadcast[ConsumerRecord[String, String]](3))
        source ~> broadcast.in
        broadcast.out(0) ~> Flow[ConsumerRecord[String, String]].map(_.value()) ~> loggerSink
        broadcast.out(1) ~> Flow[ConsumerRecord[String, String]].map(m => ByteString(m.value() + "\n")) ~> toFileSink
        broadcast.out(2) ~> Flow[ConsumerRecord[String, String]].map(cr => new ProducerRecord[String, String](outputTopic, cr.value())) ~> toProducerSink

        ClosedShape
    })

    val control = g.run()
    control.isShutdown.foreach(_ => system.terminate())

    sys.addShutdownHook(system.terminate())
  }
}
