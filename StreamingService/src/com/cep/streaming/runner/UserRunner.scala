package com.cep.streaming.runner

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import com.datastax.spark.connector.streaming._
import com.cep.event._
import com.cep.streaming.ActiveMQReveiver
import com.cep.streaming.EventFunc
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions
//import com.cep.event.generators.EventPublisher

//import com.datastax.bdp.spark.DseSparkConfHelper

object UserRunner {

  val QUEUE_ID = "EVENTS";
  val BORKE_URL = "tcp://127.0.0.1:61616";

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("user-event-streaming")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    if (args.length >= 1 && "local".equals(args(0))) {
      sparkConf.setMaster("local[*]")
    }

    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val eventsStream = ssc.receiverStream(new ActiveMQReveiver(BORKE_URL, QUEUE_ID))

    eventsStream.persist()
    eventsStream.print()

    val user2Article2Interests = eventsStream
      .map { EventFunc.getUser2Article2Interests }.reduceByKey(_ + _)

    val user2Stock2Interests = user2Article2Interests
      .flatMap(EventFunc.getUser2Stocks2Interests)
      .filter(x => x._1._2 != null)
      .reduceByKey(_ + _)
    user2Stock2Interests.persist()
    user2Stock2Interests.print()

    user2Stock2Interests.foreachRDD(rdd => rdd.foreach(t => EventFunc.saveInterests(t)))

    ssc.start()
    ssc.awaitTermination()

  }

}