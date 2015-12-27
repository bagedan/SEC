package com.cep.cassandra;

public class CassandraClientWrapper {
	
	public String[] getStockIdsByArticle(String articleid){
		return CassandraClientJava.getInstance().getStockIdsByArticle(articleid);
	} 

}
