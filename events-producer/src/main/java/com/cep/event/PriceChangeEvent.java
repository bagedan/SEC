package com.cep.event;

public class PriceChangeEvent extends Event {

    private String stockId;
    private double currentPrice;
    private long timestamp;

    public PriceChangeEvent() {
        super(EventType.PRICE_CHANGE);
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
