package com.cep.event.generators;


import com.cep.event.ReadArticleEvent;

import java.util.Random;

public class ReadEventsGenerator{

    private EventPublisher publisher;

    public ReadEventsGenerator() {
        this.publisher = new EventPublisher();
    }

    public void generateEvents(int numberEvents) {
        Random random = new Random();

        for (int i = 0; i < numberEvents; i++) {
            int userId = random.nextInt(EventPublisher.USERS_COUNT) + 1;
            int articleId = random.nextInt(EventPublisher.ARTICLES_COUNT) + 1;
            ReadArticleEvent event = new ReadArticleEvent("article" + articleId, "user" + userId);
            publisher.sendMessage(event);
        }
    }

    public ReadEventsGenerator withPublisher(EventPublisher publisher) {
        this.publisher = publisher;
        return this;
    }
}
