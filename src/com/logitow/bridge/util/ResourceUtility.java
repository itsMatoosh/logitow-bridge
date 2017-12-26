package com.logitow.bridge.util;

import java.io.File;

/**
 * Manages the resource files.
 */
public class ResourceUtility {
    /**
     * Gets a resource file with given relative path.
     * @param fileName relative path to the file in the resource folder.
     * @return
     */
    private File getFile(String fileName) {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

}
