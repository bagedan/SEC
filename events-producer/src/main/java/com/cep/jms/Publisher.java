package com.cep.jms;

import com.cep.event.PriceChangeEvent;
import com.cep.event.generators.PriceChangeEventGenerator;
import com.cep.event.generators.ReadEventsGenerator;
import com.cep.event.generators.ShareEventGenerator;

/**
 * Created by Tkachi on 2015/12/9.
 */
public class Publisher {

    private ReadEventsGenerator readEventsGenerator;
    private ShareEventGenerator shareEventsGenerator;



    public static void main(String[] args) {
        String eventType = args[0];
        System.out.println("Event type to generate: [" + eventType + "]");
        int batchSize = 5;
        if(args.length == 2){
            batchSize = Integer.valueOf(args[1]);
        }
        System.out.println("Arguments are not provided. Running producers as threads forever with batchSize = [" + batchSize + "]");

        if(eventType.toLowerCase().equals("price")){
            runPriceGeneratorsForever();
        }else if(eventType.toLowerCase().equals("user")){
            runUserEventsGeneratorsForever(batchSize);
        }
    }

    private static void runPriceGeneratorsForever() {
        PriceChangeEventGenerator priceChangeEventGenerator = new PriceChangeEventGenerator();
        new Thread(priceChangeEventGenerator).start();
    }

    private static void runUserEventsGeneratorsForever(Integer batchSize) {
        ReadEventsGenerator readEventsGenerator = new ReadEventsGenerator();
        ShareEventGenerator shareEventsGenerator = new ShareEventGenerator();
        readEventsGenerator.setBatchSize(batchSize);
        shareEventsGenerator.setBatchSize(batchSize);
        new Thread(readEventsGenerator).start();
        new Thread(shareEventsGenerator).start();
    }

    private static void runGeneratorsOnce(Integer readCount, Integer shareCount) {
        ReadEventsGenerator readEventsGenerator = new ReadEventsGenerator();
        ShareEventGenerator shareEventsGenerator = new ShareEventGenerator();

        Publisher publisher = new Publisher(readEventsGenerator, shareEventsGenerator);
        System.out.println("Publisher started");
        publisher.generateEvents(readCount, shareCount);
    }

    public Publisher(ReadEventsGenerator readEventsGenerator, ShareEventGenerator shareEventsGenerator) {
        this.readEventsGenerator = readEventsGenerator;
        this.shareEventsGenerator = shareEventsGenerator;
    }

    public void generateEvents(int readEventsNo, int shareEventsNo){
        readEventsGenerator.generateEvents(readEventsNo);
        shareEventsGenerator.generateEvents(shareEventsNo);
    }

}
