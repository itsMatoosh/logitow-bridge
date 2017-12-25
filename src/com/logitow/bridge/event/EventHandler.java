package bridge.event;

/**
 * Represents an event handler.
 */

public abstract class EventHandler {
    /**
     * Called when the handled event is called.
     * @param e
     */
    public abstract void onEventCalled(Event e);
}
