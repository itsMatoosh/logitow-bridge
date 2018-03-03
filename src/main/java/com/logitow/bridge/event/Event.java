package com.logitow.bridge.event;

import java.io.Serializable;

/**
 * Represents an event.
 */
public abstract class Event implements Serializable{
    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    public abstract void onCalled();
}
