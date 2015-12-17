package com.cep.event;


public class ReadArticleEvent extends Event {

    private String articleId;
    private String userId;

    public ReadArticleEvent(String articleId, String userId) {
        super(EventType.READ_ARTICLE);
        this.articleId = articleId;
        this.userId = userId;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "ReadArticleEvent{" +
                "articleId='" + articleId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
