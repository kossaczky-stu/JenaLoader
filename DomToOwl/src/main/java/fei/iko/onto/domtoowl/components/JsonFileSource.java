/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.domtoowl.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author igor
 */
public class JsonFileSource {

    private String sourceFilename;

    public JsonFileSource() {
        sourceFilename = "src/main/java/pokus.json";
    }

    public JsonFileSource(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public JsonNode get() throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(sourceFilename));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonData);
    }

}
