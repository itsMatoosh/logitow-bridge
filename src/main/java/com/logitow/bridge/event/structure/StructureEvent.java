package com.logitow.bridge.event.structure;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.event.Event;

/**
 * Represents an event concerning a structure.
 */
public abstract class StructureEvent extends Event {
    public Structure structure;

    public StructureEvent(Structure structure) {
        this.structure = structure;
    }
}
