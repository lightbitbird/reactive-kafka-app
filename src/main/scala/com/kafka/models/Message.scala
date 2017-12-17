package com.kafka.models

case class Message(topic: String, messages: Seq[String])
