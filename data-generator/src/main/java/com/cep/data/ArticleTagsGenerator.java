package com.cep.data;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.*;

/**
 * Created by Tkachi on 2015/12/26.
 */
public class ArticleTagsGenerator {

    private static int numberOfRows = 5000;

    private static String serverIP = "127.0.0.1";
    private static String keyspace = "cep_demo";
    private static Session session;

    private String articleNamePrefix = "article";

    private int maxTagsPerArticle = 5;

    public static void main(String[] args) {

        if(args.length!=1){
            System.out.println("Generate default number of rows: " + numberOfRows);
        }else{
            numberOfRows = Integer.valueOf(args[0]);
        }
        Cluster cluster = Cluster.builder()
                .addContactPoints(serverIP)
                .build();

        session = cluster.connect(keyspace);
        ArticleTagsGenerator articleTagsGenerator = new ArticleTagsGenerator();
        articleTagsGenerator.generateArticles(numberOfRows);
    }

    private void generateArticles(int numberOfRows) {
        List<String> availableTags = readTags();
        PreparedStatement updateStatement = session.prepare("INSERT INTO article_tags (articleid, stockid ) VALUES ( ? , ?);");

        for(int i = 0; i< numberOfRows; i++){
            List<String> subSet = getRandomSubSet(availableTags);
            String articleId = articleNamePrefix + i;
            for(String tag:subSet){
                session.execute( updateStatement.bind(articleId, tag));
                System.out.println("For article " + articleId + " insert tag " + tag);
            }
        }
        session.close();
    }

    private List<String> getRandomSubSet(List<String> availableTags) {
        Collections.shuffle(availableTags);
        Random random = new Random();
        int numberOfTags = random.nextInt(maxTagsPerArticle) +1;
        return availableTags.subList(0, numberOfTags);
    }

    private List<String> readTags() {
        String cqlStatement = "SELECT stock_id FROM stocks";
        List<String> tags = new ArrayList<String>();
        for (Row row : session.execute(cqlStatement)) {
            String tag = row.toString();
//            System.out.println(tag);
            tags.add(tag);
        }
        return tags;
    }


}
