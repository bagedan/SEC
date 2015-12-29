package com.cep.streaming

import com.cep.event._
import com.datastax.spark.connector.streaming._
import scala.collection.mutable._
import com.datastax.spark.connector._
import com.cep.cassandra._

object PriceFunc extends Serializable {

  val producer: NotificationPublisher = new NotificationPublisher()

  def sortByTimeStamp(tuple: (String, scala.collection.Iterable[(Double, Long)])): (String, LinkedList[(Double, Long)]) = {
    var it = tuple._2.iterator

    var list = new LinkedList[(Double, Long)]()

    while (it.hasNext) {
      list.+:(it.next())
    }

    list = list.sortWith((x, y) => x._2.compareTo(y._2) > 0)

    (tuple._1, list)
  }

  def getPriceChange(tuple: (String, scala.collection.Iterable[(Double, Long)])): (String, (Double, Boolean)) = {
    val it = tuple._2

    if (it == null || it.size == 0) {
      return (tuple._1, (0, true))
    }

    var latest: (Double, Long) = (0, Long.MinValue)
    var earlist: (Double, Long) = (0, Long.MaxValue)

    var index: (Double, Long) = null

    for (index <- it) {
      if (index._2 < earlist._2) {
        earlist = index
      }
      if (index._2 > latest._2) {
        latest = index
      }
    }

    var percent: Double = (latest._1 - earlist._1) / earlist._1

    var increase: Boolean = true
    if (percent < 0) {
      increase = false
      percent = 0 - percent
    }

    (tuple._1, (percent, increase))

  }

  def sendNotification(tuple: ((String, String), (Double, Boolean))) = {

    var text: String = tuple._1._1 + "," + tuple._1._2 + "," + tuple._2._1 + "," + tuple._2._2
    producer.sendMessage(text)
  }

  def getNotification(stock2Percent2Incre: (String, (Double, Boolean))): Array[((String, String), (Double, Boolean))] = {
    val stock = stock2Percent2Incre._1
    val users = getUserByStock(stock)

    var size: Int = 1

    if (users != null && users.size > 0) {
      size = users.size
    }

    var array: Array[((String, String), (Double, Boolean))] = new Array[((String, String), (Double, Boolean))](size)

    if (users == null || users.size == 0) {
      array(0) = ((stock, null), stock2Percent2Incre._2)
      return array
    }

    var index: Int = 0
    var user: String = null
    for (user <- users) {
      array(index) = ((stock, user), stock2Percent2Incre._2)
      index += 1
    }

    return array

  }

  def getStock2Price2TimeStamp(event: Event): (String, (Double, Long)) = {
    val price = event.asInstanceOf[PriceChangeEvent].getCurrentPrice
    val stock = event.asInstanceOf[PriceChangeEvent].getStockId
    val timestamp = event.asInstanceOf[PriceChangeEvent].getTimestamp

    (stock, (price, timestamp))

  }

  def getUserByStock(stock: String): Array[String] = {
    CassandraClient.getUserByStockId(stock)
  }

}