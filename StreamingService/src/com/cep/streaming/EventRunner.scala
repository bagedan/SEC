package com.cep.streaming

import scala.collection.mutable.HashMap
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import com.datastax.spark.connector.streaming._
import com.cep.event._
//import com.cep.event.generators.EventPublisher

//import com.datastax.bdp.spark.DseSparkConfHelper

object EventRunner {

  val QUEUE_ID = "EVENTS";
  val BORKE_URL = "tcp://127.0.0.1:61616";

  def main(args: Array[String]): Unit = {

    // Create the context with a 1 second batch size

    val sparkConf = new SparkConf().setAppName("event-streaming")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    if (args.length >= 1 && "local".equals(args(0))) {
      sparkConf.setMaster("local[*]")
    }

    //val sparkConf = DseSparkConfHelper.enrichSparkConf(new SparkConf().setAppName("cep-streaming"));

    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val eventFunc = new EventFunc(ssc)

    val eventsStream = ssc.receiverStream(new ActiveMQReveiver(BORKE_URL, QUEUE_ID))

    eventsStream.print()

    val user2Article2Interests = eventsStream.map { eventFunc.getUser2Article2Interests }.reduceByKey(_ + _)

    val user2Stock2Interests = user2Article2Interests.flatMap(eventFunc.getUser2Stocks2Interests).reduceByKey(_ + _)

    user2Stock2Interests.print()
    ssc.start()
    ssc.awaitTermination()

  }

}