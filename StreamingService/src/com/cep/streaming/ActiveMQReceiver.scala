package com.cep.streaming

import java.io.{ InputStreamReader, BufferedReader, InputStream }
import java.net.Socket

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms._;

import org.apache.spark.{ SparkConf, Logging }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.streaming.receiver.Receiver

class ActiveMQReveiver(url: String, queue: String)
    extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2)  {
 
  def onStart() { 
    // Start the thread that receives data over a connection
    new Thread("activeMQ Receiver") {
      override def run() { receive() } 
    }.start()
  } 

  def onStop() {
  }

  private def receive() {
    println("start receive")
    var consumer: MessageConsumer = null

    var message: Message = null
    try {
      // Create a ConnectionFactory
      val connectionFactory = new ActiveMQConnectionFactory(url)

      // Create a Connection
      val connection = connectionFactory.createConnection()
      connection.start()

      // Create a Session
      val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

      // Create the destination (Topic or Queue)
      val destination = session.createQueue(queue)

      // Create a MessageConsumer from the Session to the Topic or Queue
      consumer = session.createConsumer(destination)

      while (!isStopped) {
        message = consumer.receive(1000 * 5)
        if (message!=null&&message.isInstanceOf[TextMessage]) {
          val text = message.asInstanceOf[TextMessage]
          store(text.getText)
        } else {
          println("received message is not text : " + message)
        }
      }
      
      consumer.close()
      session.close()
      connection.close()
      
      println("Stopped receiving")
      restart("Trying to connect again")
    } catch {
      case e: java.net.ConnectException =>
        restart("Error connecting to " + url , e)
      case t: Throwable =>
        restart("Error receiving data", t)
    }
  }
}