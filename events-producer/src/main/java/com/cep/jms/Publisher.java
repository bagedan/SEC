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

        if(args.length == 2){
            System.out.println("Arguments provided: No read events [" + args[0] + "] : No shared events [" + args[1] + "]");
            runGeneratorsOnce(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
        }else if(args.length == 1) {
            System.out.println("Arguments are not provided. Running producers as threads forever with batchSize = [" + args[0] + "]");
            runGeneratorsForever(Integer.valueOf(args[0]));
        }else{
            System.out.println("Arguments are not provided. Running producers as threads forever with batchSize = 5");
            runGeneratorsForever(5);
        }

    }

    private static void runGeneratorsForever(Integer batchSize) {
        ReadEventsGenerator readEventsGenerator = new ReadEventsGenerator();
        ShareEventGenerator shareEventsGenerator = new ShareEventGenerator();
        PriceChangeEventGenerator priceChangeEventGenerator = new PriceChangeEventGenerator();
        readEventsGenerator.setBatchSize(batchSize);
        shareEventsGenerator.setBatchSize(batchSize);
        new Thread(readEventsGenerator).start();
        new Thread(shareEventsGenerator).start();
        new Thread(priceChangeEventGenerator).start();
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
