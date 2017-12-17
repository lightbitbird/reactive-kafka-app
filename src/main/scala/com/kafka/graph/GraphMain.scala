package com.kafka.graph

import com.kafka.consumer.ConsumerStream
import com.typesafe.config.{Config, ConfigFactory}

object GraphMain extends App with KafkaGraph with ConsumerStream {
  protected override val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")

  start()
}
