name := "reactive-kafka-app"

version := "0.2"

scalaVersion := "2.13.6"

val akkaVersion = "2.6.16"

val akkaStreamKafkaVersion = "2.1.1"

val log4j = "2.14.1"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  //  "com.typesafe.akka" %% "akka-http" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaVersion,
  "org.json4s" %% "json4s-native" % "4.0.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.6",

  "ch.qos.logback" % "logback-classic" % "1.2.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  //  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j,
  "org.apache.logging.log4j" % "log4j-core" % log4j,
  "org.apache.logging.log4j" % "log4j-api" % log4j,
  "org.slf4j" % "log4j-over-slf4j" % "1.7.32",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

)

fork in run := true
