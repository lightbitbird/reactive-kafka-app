# Reactive Kafka App With Akka Streams Kafka
===============================


**Usage** 

 [1] Setting up Apache Kafka and Zookeeper

Download and install Apache Kafka and Zookeeper components.
https://kafka.apache.org/quickstart

    > tar -xzf kafka_2.11-1.0.0.tgz
    > cd kafka_2.11-1.0.0
    
    # Start a Zookeeper instance.
    > bin/zookeeper-server-start.sh config/zookeeper.properties
    
    # Start the Kafka server.
    > bin/kafka-server-start.sh config/server.properties
    
 [1] Create topics 

    # Craete two topics of "topic1" and "topic2".
    > bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic1 
    > bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic2 

 [2] Run the Producer Web API

    >cd reactive-kafka-app
    >sbt
    
    # Run and choose [1] com.kafka.api.Application
    sbt:reactive-kafka-app>run
    Enter number: 1
    
    # Send a post request with Json format messages as below.
    # In this case, messages are sent to the producer of topic "topic1".
    http://localhost:8888/api/producer
    {"topic": "topic1","messages":["Grape","Raspberry","Orange", "Apple"]}
    
 [3] Run the Consumer http server
    
    # Run and choose [1] com.kafka.graph.GraphMain
    sbt:reactive-kafka-app>run
    Enter number: 2
    
    # You can subscripbe messages sent at [2] to the consumers of topic "topic1".
    # The subscribed messages are sent to the topic "topic2" producer at the same time.

