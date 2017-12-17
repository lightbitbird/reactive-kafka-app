package com.kafka.test

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.Control
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._

trait ConsumerTest {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  protected val config: Config

  def runCommittable() = {

    Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
      .mapAsync(1) { msg =>

        println("consumer msg => " + msg)
        msg.committableOffset.commitScaladsl()
      }.runWith(Sink.ignore)
  }

  def done() = {

    Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
      .mapAsync(1) { msg =>

        println("consumer msg => " + msg)
        msg.committableOffset.commitScaladsl()
      }.runWith(Sink.ignore)
  }

  def committableSource = {
    Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
  }

  def committableSink = {
    Flow[CommittableMessage[String, String]]
      .mapAsync(1)(msg => msg.committableOffset.commitScaladsl())

  }

  def consumerSettings = {
    val cConfig = config.getConfig("akka.kafka.consumer")
    val bootstrapServer = cConfig.getString("bootstrap-servers")
    val topics = cConfig.getString("topics")
    val groupId = cConfig.getString("groupId")
    val commit = cConfig.getBoolean("commit")
    val start = cConfig.getString("start-from")
    val fetchBytes = cConfig.getInt("fetch.bytes")

    val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServer)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, fetchBytes.toString)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, start)
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, commit.toString)
      .withPollInterval(20.millis)
      .withPollTimeout(50.millis)

    consumerSettings
  }

}

object ConsumerTest extends App {
  def apply: ConsumerTest = new ConsumerTest() {
    override implicit lazy val system = ActorSystem("kafka-producer-api")
    override implicit lazy val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher
    protected override val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")
  }
println("Hello!")
  ConsumerTest.apply.done()

}