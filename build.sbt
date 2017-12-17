name := "reactive-kafka-app"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.7"

val log4j = "2.9.1"
//val log4j = "1.7.25"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
//  "com.typesafe.akka" %% "akka-http" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.18",
  "org.json4s" %% "json4s-native" % "3.5.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11",

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
//  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j,
  "org.apache.logging.log4j" % "log4j-core" % log4j,
  "org.apache.logging.log4j" % "log4j-api" % log4j,
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

)

fork in run := true
