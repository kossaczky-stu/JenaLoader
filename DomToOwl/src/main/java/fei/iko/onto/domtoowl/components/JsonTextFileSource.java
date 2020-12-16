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
import java.util.Iterator;
import java.util.stream.Stream;

/**
 *
 * @author igor
 */
public class JsonTextFileSource {

    private String sourceFilename;
    private Stream<String> lines;
    private Iterator<String> itLines;
    private ObjectMapper objectMapper;

    public JsonTextFileSource() {
        sourceFilename = "src/main/java/pokus.json";
    }

    public JsonTextFileSource(String sourceFilename) throws IOException {
        this.sourceFilename = sourceFilename;
        lines = Files.newBufferedReader(Paths.get(sourceFilename)).lines();
        itLines = lines.iterator();
        objectMapper = new ObjectMapper();
    }

    public JsonNode get() throws IOException {
        if (itLines.hasNext()) {
            return objectMapper.readTree(itLines.next());
        } else {
            return null;
        }
    }

}
