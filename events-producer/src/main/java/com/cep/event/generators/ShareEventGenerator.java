package com.cep.event.generators;

import com.cep.event.ShareArticleEvent;

import java.util.Random;

/**
 * Created by Olga on 17.12.2015.
 */
public class ShareEventGenerator {

    private EventPublisher eventPublisher;

    public ShareEventGenerator() {
        this.eventPublisher = new EventPublisher();
    }

    public void generateEvents(int numberEvents) {
        Random random = new Random();

        for (int i = 0; i < numberEvents; i++) {
            int userFromId = random.nextInt(EventPublisher.USERS_COUNT) + 1;
            int userToId = random.nextInt(EventPublisher.USERS_COUNT) + 1;
            int articleId = random.nextInt(EventPublisher.ARTICLES_COUNT) + 1;
            ShareArticleEvent shareArticleEvent = new ShareArticleEvent("article" + articleId, "user" + userFromId, "user" + userToId);
            eventPublisher.sendMessage(shareArticleEvent);
        }
    }

    public ShareEventGenerator withPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        return this;
    }


}
