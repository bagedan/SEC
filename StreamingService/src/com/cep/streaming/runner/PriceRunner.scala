package com.cep.streaming.runner

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Minutes, Seconds, StreamingContext }
import com.datastax.spark.connector.streaming._
import com.cep.event._
import com.cep.streaming.ActiveMQReveiver
import org.apache.hadoop.mapreduce.task.reduce.EventFetcher
import com.cep.streaming._

object PriceRunner {

  val QUEUE_ID = "PRICE"
  val BORKE_URL = "tcp://127.0.0.1:61616"

  val price_threhold: Double = 0.05

  def main(args: Array[String]): Unit = {

    // Create the context with a 1 second batch size

    val sparkConf = new SparkConf().setAppName("stock-price-streaming")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    if (args.length >= 1 && "local".equals(args(0))) {
      sparkConf.setMaster("local[*]")
    }

    //val sparkConf = DseSparkConfHelper.enrichSparkConf(new SparkConf().setAppName("cep-streaming"));

    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val eventsStream = ssc.receiverStream(new ActiveMQReveiver(BORKE_URL, QUEUE_ID))

    eventsStream.persist()
    eventsStream.print()

    val windowStream = eventsStream.window(Minutes(2), Seconds(5))
    windowStream.persist()
    val stock2Price2TimeStamp = windowStream.map { PriceFunc.getStock2Price2TimeStamp }
      .groupByKey(1)

    val stock2change = stock2Price2TimeStamp.map(PriceFunc.getPriceChange)

    stock2change.persist()
    stock2change.print()
    
    val bigChange = stock2change.filter(x => x._2._1 > price_threhold)
    bigChange.persist()
  
    val notification = bigChange.flatMap(PriceFunc.getNotification).filter(x=>x._1._2!=null)
    
    notification.print()

    ssc.start()
    ssc.awaitTermination()

  }

}