package com.cep.cassandra

import com.datastax.spark.connector.cql._
import scala.collection.JavaConverters._
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Host
import com.datastax.driver.core.Metadata
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session

object CassandraClient {

  val host: String = "127.0.0.1"
  val keyspace: String = "cep_demo"
  val cluster: Cluster = Cluster.builder().addContactPoint(host).build()
  val session: Session = cluster.connect(keyspace)

  def getStockIdsByArticle(articleid: String): Array[String] = {

    var cql = "SELECT stockid FROM article_tags  WHERE articleid=?"
    val results = session.execute(cql, articleid).all().asScala.toList 

    var row: Row = null
    
    var array=new Array[String](results.size)
    
    var index:Int=0;
  
    for(row <- results){
      array(0)=row.getString("stockid")
      index+=1
    }
    
    return array

  }

  def main(args: Array[String]): Unit = {
    val x = CassandraClient.cluster.getClusterName
    println(x)
  }
}