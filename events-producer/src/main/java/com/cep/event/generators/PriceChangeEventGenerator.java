package com.cep.event.generators;

import com.cep.event.PriceChangeEvent;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.*;

/**
 * Created by Tkachi on 2015/12/26.
 */
public class PriceChangeEventGenerator implements Runnable{

    private String price_event_queue = "PRICE";

    private static String serverIP = "127.0.0.1";
    private static String keyspace = "cep_demo";

    private int initialPriceMin = 100;
    private int initialPriceMax = 1000;
    private EventPublisher publisher;

    private double priceChangePercentsMax = 1;

    public PriceChangeEventGenerator() {
        this.publisher = new EventPublisher(price_event_queue);
    }

    public void run() {
        List<String> stockIds = readStockIds();
        Map<String, Double> currentPriceData = generateInitialPrices(stockIds);
        //send events with original price
        sendEvents(currentPriceData);

        //change price on some random % to up or down. take care of price < 0
        while(true){
            currentPriceData = updatePrices(currentPriceData);
            sendEvents(currentPriceData);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Double> updatePrices(Map<String, Double> currentPriceData) {
        Random random = new Random();
        for(String key : currentPriceData.keySet()){
            double currentPrice = currentPriceData.get(key);
            boolean priceGoingUp = random.nextBoolean();
            double changePercents = random.nextDouble() * priceChangePercentsMax;

            if(priceGoingUp){
                currentPrice = changePercents + (changePercents/100d)*currentPrice;
            }else{
                currentPrice = changePercents - (changePercents/100d)*currentPrice;
                if(currentPrice<0){
                    currentPrice = 0;
                }
            }

            currentPriceData.put(key, currentPrice);
        }

        return currentPriceData;
    }

    private void sendEvents(Map<String, Double> currentPriceData) {
        System.out.println("Sending price events");

        for(Map.Entry<String, Double> entry: currentPriceData.entrySet()){
            PriceChangeEvent event = new PriceChangeEvent();
            event.setStockId(entry.getKey());
            event.setCurrentPrice(entry.getValue());
            event.setTimestamp(new Date().getTime());
            publisher.sendMessage(event);
        }
    }

    private Map<String, Double> generateInitialPrices(List<String> stockIds) {
        Map<String, Double> initialPrices = new TreeMap<String, Double>();
        Random random = new Random();
        for(String stockId : stockIds){
            double initPrice = random.nextInt(initialPriceMax) + initialPriceMin;
            initialPrices.put(stockId, initPrice);
        }
        return initialPrices;
    }

    private List<String> readStockIds() {
        Cluster cluster = Cluster.builder()
                .addContactPoints(serverIP)
                .build();

        Session session = cluster.connect(keyspace);
        String cqlStatement = "SELECT stock_id FROM stocks";
        List<String> stockIds = new ArrayList<String>();
        for (Row row : session.execute(cqlStatement)) {
            String tag = row.getString("stock_id");
            stockIds.add(tag);
        }
        return stockIds;
    }

}
