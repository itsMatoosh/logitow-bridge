package com.logitow.bridge.event.structure;

import com.logitow.bridge.build.Structure;

/**
 * Called when a structure has been loaded from file.
 */
public class StructureLoadEvent extends StructureEvent {
    /**
     * The path the structure was loaded from.
     */
    public String loadPath;

    public StructureLoadEvent(Structure structure, String loadPath) {
        super(structure);
        this.loadPath = loadPath;
    }

    @Override
    public void onCalled() {
        Structure.logger.info("Structure: {}, loaded from: {}", structure, loadPath);
    }
}
