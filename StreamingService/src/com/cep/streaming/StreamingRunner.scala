package com.cep.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Seconds, StreamingContext }
//import com.cep.event.generators.EventPublisher

//import com.datastax.bdp.spark.DseSparkConfHelper

object StreamingRunner {
  def main(args: Array[String]): Unit = {

    // Create the context with a 1 second batch size

    val QUEUE_ID = "EVENTS";
    val BORKE_URL = "tcp://localhost:61616";
    val sparkConf = new SparkConf().setAppName("cep-streaming")

    if (args.length >= 1 && "local".equals(args(0))) {
      sparkConf.setMaster("local[*]")
    }

    //val sparkConf = DseSparkConfHelper.enrichSparkConf(new SparkConf().setAppName("cep-streaming"));

    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val lines = ssc.receiverStream(new ActiveMQReveiver(BORKE_URL, QUEUE_ID))
    //lines.filter(x => x.startsWith("Message"))
    lines.print()

    ssc.start()
    ssc.awaitTermination()

  }
}