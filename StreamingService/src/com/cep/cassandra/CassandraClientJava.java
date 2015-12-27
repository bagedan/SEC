package com.cep.cassandra;


import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


public class CassandraClientJava {
	
	private static final CassandraClientJava instance=new CassandraClientJava();
	
	
	private static final String host="127.0.0.1";
	
    private Cluster cluster;
    private Session session;
    private String keyspace = "cep_demo";
    
    private CassandraClientJava(){
    	this.connect(host);
    }

    
    public static CassandraClientJava getInstance(){
    
    	return instance;
    }

    private  void connect(String node) {
        cluster = Cluster.builder().addContactPoint(node).build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect(keyspace);
    }


    public void close() {
        cluster.close();
    }

    public String[] getStockIdsByArticle(String articleid) {

    	List<String> list=new ArrayList<String>();
        String cql = "SELECT stockid FROM article_tags  WHERE articleid=?";
        ResultSet results=  session.execute(cql, articleid);
        
        if(results!=null)
        for (Row row : results) {
           list.add(row.getString("stockid"));
        }
        
        return (String[])list.toArray();

    }



    public static void main(String[] args) {
    	CassandraClientJava client = CassandraClientJava.getInstance();
        
    }


}