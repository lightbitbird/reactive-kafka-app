package com.kafka.api

import com.kafka.producer.ProducerStream

object Application extends App with Route with ProducerStream {

  startApplication

}
