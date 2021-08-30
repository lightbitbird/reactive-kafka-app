package com.kafka.graph

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.scaladsl.Consumer.Control
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ClosedShape, SourceShape}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext

trait KafkaCommittableGraph {
  implicit val system: ActorSystem
  protected val config: Config

  val logger: LoggingAdapter

  def committableMessageSource: Source[CommittableMessage[String, String], Control]
  def toProducerSink: Sink[ProducerRecord[String, String], _]
  def flow: Flow[CommittableMessage[String, _], CommittableMessage[String, _], NotUsed]

  val outputTopic: String

  def start(implicit ec: ExecutionContext): Unit = {

    val g = RunnableGraph.fromGraph(GraphDSL.createGraph(committableMessageSource) { implicit b =>
      source: SourceShape[CommittableMessage[String, String]] =>
        import GraphDSL.Implicits._

        val broadcast = b.add(Broadcast[CommittableMessage[String, String]](2))
        source ~> broadcast.in
        broadcast.out(0) ~> flow ~> Sink.ignore
        broadcast.out(1) ~> Flow[CommittableMessage[String, String]]
          .map(cr => new ProducerRecord[String, String](outputTopic, cr.record.value())) ~> toProducerSink

        ClosedShape
    })

    val control = g.run()
    control.isShutdown.foreach(_ => system.terminate())

    sys.addShutdownHook(system.terminate())
  }

}
