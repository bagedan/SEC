package com.cep.event;


public class ShareArticleEvent extends Event {

    private String articleId;
    private String sharedFromUserId;
    private String sharedToUserId;

    public ShareArticleEvent(String articleId, String sharedFromUserId, String sharedToUserId) {
        super(EventType.SHARE_ARTICLE);
        this.articleId = articleId;
        this.sharedFromUserId = sharedFromUserId;
        this.sharedToUserId = sharedToUserId;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getSharedFromUserId() {
        return sharedFromUserId;
    }

    public String getSharedToUserId() {
        return sharedToUserId;
    }

    @Override
    public String toString() {
        return "ShareArticleEvent{" +
                "articleId='" + articleId + '\'' +
                ", sharedFromUserId='" + sharedFromUserId + '\'' +
                ", sharedToUserId='" + sharedToUserId + '\'' +
                '}';
    }
}
