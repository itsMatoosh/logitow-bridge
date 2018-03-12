package com.logitow.bridge.event.structure;

import com.logitow.bridge.build.Structure;

/**
 * Called when a structure is saved to a file.
 */
public class StructureSaveEvent extends StructureEvent {
    /**
     * The path the structure has been saved in.
     */
    public String savePath;

    public StructureSaveEvent(Structure structure, String savePath) {
        super(structure);
        this.savePath = savePath;
    }

    @Override
    public void onCalled() {
        Structure.logger.info("Structure: {}, saved to: {}", structure, savePath);
    }
}
