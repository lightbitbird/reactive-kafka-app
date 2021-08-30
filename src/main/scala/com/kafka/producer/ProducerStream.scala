package com.kafka.producer

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import com.kafka.api.Route
import com.kafka.models.Message
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

trait ProducerStream {
  implicit val system: ActorSystem
  protected val config: Config

  def makeSource(messages: Seq[String]): Source[String, NotUsed] = {
    Source(messages.toList)
  }

  def makeSink = {
    val pConfig = config.getConfig("akka.kafka.producer")
    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(pConfig.getString("bootstrap-servers"))
      .withProperty(ProducerConfig.BATCH_SIZE_CONFIG, pConfig.getString("batch.size"))
      .withProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, pConfig.getString("buffer.memory"))
      .withProperty(ProducerConfig.LINGER_MS_CONFIG, pConfig.getString("linger.ms"))
      .withProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, pConfig.getString("compression.type"))
    Producer.plainSink(producerSettings)
  }

  def create(json: Message) = {
    makeSource(json.messages)
      .map(_.toString())
      .map(new ProducerRecord[Array[Byte], String](json.topic, _))
      .runWith(makeSink)
  }
}
