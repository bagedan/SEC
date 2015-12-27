package com.cep.streaming

import com.cep.event._
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import com.datastax.spark.connector.streaming._
import scala.collection.mutable._
import com.datastax.spark.connector._
import com.cep.cassandra._

object EventFunc extends Serializable {

  def getUser2Article2Interests(event: Event): ((String, String), Int) = {

    var articleId: String = null

    var userId: String = null

    if (event.isInstanceOf[ReadArticleEvent]) {
      articleId = event.asInstanceOf[ReadArticleEvent].getArticleId
      userId = event.asInstanceOf[ReadArticleEvent].getUserId
    } else if (event.isInstanceOf[ShareArticleEvent]) {
      articleId = event.asInstanceOf[ShareArticleEvent].getArticleId
      userId = event.asInstanceOf[ShareArticleEvent].getSharedToUserId
    }

    return ((userId, articleId), 1)

  }
    def getUser2Stocks2Interests(user2Article2Interests: ((String, String), Int)): Array[((String, String), Int)] = {

    var array: Array[((String, String), Int)] = null

    val user = user2Article2Interests._1._1
    val article = user2Article2Interests._1._2
    val interests = user2Article2Interests._2

    val stocks = getStocksByArticle(article)

    stocks.foreach { x => { println(article + " ----------- " + x) } }

    array = new Array[((String, String), Int)](stocks.length)

    var stock: String = null

    var index: Int = 0
    for (stock <- stocks) {
      array(index) = ((user, stock), interests)
      index += 1
    }

    return array

  }

  def getStocksByArticle(articleId: String): Array[String] = {
    CassandraClient.getStockIdsByArticle(articleId)

  }

  def saveInterests(user2Stock2Interests: ((String, String), Int)) = {
    val user = user2Stock2Interests._1._1
    val stock = user2Stock2Interests._1._2
    val interests = user2Stock2Interests._2

    if (user != null && stock != null && interests > 0) {
      CassandraClient.saveInterests(user, stock, interests)
    }

  }

//  def getArticle2Stocks(): HashMap[String, HashSet[String]] = {
//    val map = new HashMap[String, HashSet[String]]
//    var i = 0
//    while (i < 10000) {
//      val ar = "article" + i
//      val set = new HashSet[String]()
//
//      set.add("stock1___" + ar)
//      set.add("stock2___" + ar)
//      set.add("stock3___" + ar)
//
//      map.put(ar, set)
//      i = i + 1
//    }
//
//    return map
//  }

}