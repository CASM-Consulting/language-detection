package com.cybozu.labs.langdetect;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class ProfileWeightingTest {


    static final String TEST_RESOURCES_ROOT = "src/test/resources/profileweighting/";




    /**
     * Helper to load test resources as Strings
     */
    private String loadFileContents(String filename) throws IOException {
        File f = new File(TEST_RESOURCES_ROOT + filename);
        return Files.readAllLines(f.toPath(), StandardCharsets.UTF_8).stream().collect(Collectors.joining());
    }
}
