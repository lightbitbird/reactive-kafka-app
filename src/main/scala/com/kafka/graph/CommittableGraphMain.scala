package com.kafka.graph

import com.kafka.consumer.CommittableMessageStream
import com.typesafe.config.{Config, ConfigFactory}

object CommittableGraphMain extends App with KafkaCommittableGraph with CommittableMessageStream {
  protected override val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")

  start(ec)
}
