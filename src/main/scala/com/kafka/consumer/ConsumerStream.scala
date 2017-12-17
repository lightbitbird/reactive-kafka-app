package com.kafka.consumer

import java.io.File
import java.nio.file.StandardOpenOption._

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import com.kafka.graph.KafkaGraph
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future
import scala.concurrent.duration._

trait ConsumerStream {
  graph: KafkaGraph =>

  implicit lazy val system = ActorSystem("kafka-consumer-api")
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
  implicit lazy val ec = system.dispatcher

  lazy val logger = Logging(system, getClass)
  lazy val outputTopic: String = "topic2"

  override def consumerSource: Source[ConsumerRecord[String, String], Control] = {
    val cConfig = config.getConfig("akka.kafka.consumer")
    val bootstrapServer = cConfig.getString("bootstrap-servers")
    val topics = cConfig.getString("topics")
    val groupId = cConfig.getString("groupId")
    val commit = cConfig.getBoolean("commit")
    val start = cConfig.getString("start-from")
    val fetchBytes = cConfig.getInt("fetch.bytes")

    val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServer)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, fetchBytes.toString)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, start)
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, commit.toString)
      .withPollInterval(20.millis)
      .withPollTimeout(50.millis)

    Consumer.plainSource(consumerSettings, Subscriptions.topics(topics))
  }

  override def toFileSink: Sink[ByteString, _] = {
    if (config.hasPath("akka.output.file.name")) {
      val name = config.getString("akka.output.file.name")
      FileIO.toPath(new File(name).toPath, Set(WRITE, CREATE, TRUNCATE_EXISTING))
    } else {
      Sink.ignore
    }
  }

  override def loggerSink: Sink[String, Future[Done]] = {
    Sink.foreach[String](msg => logger.info("message = " + msg))
  }

  override def toProducerSink: Sink[ProducerRecord[String, String], _] = {
    val pConfig = config.getConfig("akka.kafka.producer")
    val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers(pConfig.getString("bootstrap-servers"))
      .withProperty(ProducerConfig.BATCH_SIZE_CONFIG, pConfig.getString("batch.size"))
      .withProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, pConfig.getString("buffer.memory"))
      .withProperty(ProducerConfig.LINGER_MS_CONFIG, pConfig.getString("linger.ms"))
      .withProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, pConfig.getString("compression.type"))
    Producer.plainSink(producerSettings)
  }
}

