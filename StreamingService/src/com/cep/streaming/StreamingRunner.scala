package com.cep.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Seconds, StreamingContext }
//import com.datastax.bdp.spark.DseSparkConfHelper

object StreamingRunner {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: StreamingRunner <url>  <queuename>  <mode:optional>")
      System.exit(1)
    }

    // Create the context with a 1 second batch size
    
   val sparkConf = new SparkConf().setAppName("cep-streaming")
   
   if(args.length>=3&&"local".equals(args(2))){
      sparkConf.setMaster("local[*]")
   }
   
   //val sparkConf = DseSparkConfHelper.enrichSparkConf(new SparkConf().setAppName("cep-streaming"));
    
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val lines = ssc.receiverStream(new ActiveMQReveiver(args(0), args(1)))
    //lines.filter(x => x.startsWith("Message"))
    lines.print()

    ssc.start()
    ssc.awaitTermination()

  }
}