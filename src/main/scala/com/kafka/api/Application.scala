package com.kafka.api

import com.kafka.producer.ProducerStream

object Application extends App with ProducerStream with Route {

  startApplication

}
