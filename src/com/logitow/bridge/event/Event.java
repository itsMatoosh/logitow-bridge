package bridge.event;

/**
 * Represents an Event.
 */

public abstract class Event {
    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    public abstract void onCalled();
}
