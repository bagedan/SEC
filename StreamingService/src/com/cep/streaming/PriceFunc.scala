package com.cep.streaming

import com.cep.event._
import com.datastax.spark.connector.streaming._
import scala.collection.mutable._
import com.datastax.spark.connector._
import com.cep.cassandra._

object PriceFunc  extends Serializable{
  
  
  def sortByTimeStamp(tuple:(String, scala.collection.Iterable[(Double, Long)]))
  : (String, LinkedList[(Double, Long)])={
    var it=tuple._2.iterator
    
    var list=new LinkedList[(Double, Long)]()
    
    while(it.hasNext){
      list.+:(it.next())
    }
    
    list=list.sortWith((x,y)=>x._2.compareTo(y._2)>0)
    
    (tuple._1,list)
  }
  
  def getPriceChange(tuple:(String, LinkedList[(Double, Long)])):(String,(Double,Boolean))={
   val list= tuple._2
   var head=list.take(1).head
   var tail =list.takeRight(1).head
   
   var percent:Double=(head._1-tail._1)/head._1
   
   var increase:Boolean=true
   if(percent<0){
     increase=false
     percent=0-percent
   }
   
   (tuple._1,(percent,increase))
   
  }
  
    def getNotification(tuple:(String,(Double,Boolean))):Array[((String,String),(Double,Boolean))]={
      val stock=tuple._1
     val users= getUserByStock(stock)
     
     if(users==null){
       return null
     }
     
     var array:Array[((String,String),(Double,Boolean))]
      =new Array[((String,String),(Double,Boolean))](users.size)
     
     var index:Int=0
     var user:String=null
     for(user <- users){
       array(index)=((stock,user),tuple._2)
       index+=1
     }
      
     return array
     
     
  }

  
  def getStock2Price2TimeStamp(event:Event):(String,(Double,Long))={
    val price=event.asInstanceOf[PriceChangeEvent].getCurrentPrice
   val stock= event.asInstanceOf[PriceChangeEvent].getStockId
   val timestamp= event.asInstanceOf[PriceChangeEvent].getTimestamp
    
    (stock,(price,timestamp))
    
  }

  def getUserByStock(stock:String):Array[String]={
    CassandraClient.getUserByStockId(stock)
  }

}