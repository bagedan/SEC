package com.cep.event;

import java.io.Serializable;

/**
 * Created by Olga on 17.12.2015.
 */
public abstract class Event implements Serializable{

    private EventType eventType;

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

}
