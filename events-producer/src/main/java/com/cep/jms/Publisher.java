package com.cep.jms;

import com.cep.event.generators.ReadEventsGenerator;
import com.cep.event.generators.ShareEventGenerator;

/**
 * Created by Tkachi on 2015/12/9.
 */
public class Publisher {

    private ReadEventsGenerator readEventsGenerator;
    private ShareEventGenerator shareEventsGenerator;

    public static void main(String[] args) {
        int readCount = 5;
        int shareCount = 5;
        if(args.length == 2){
            System.out.println("Arguments provided: No read events [" + args[0] + "] : No shared events [" + args[1] + "]");
            readCount = Integer.valueOf(args[0]);
            shareCount = Integer.valueOf(args[1]);
        }else{
            System.out.println("Arguments are not provided. Using defaults: [" + readCount + "] : [" + shareCount + "]");
        }
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
