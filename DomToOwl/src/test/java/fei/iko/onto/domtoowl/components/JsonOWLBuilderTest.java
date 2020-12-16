/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.domtoowl.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.iko.onto.domtoowl.owlnode.BuilderConfig;
import java.io.File;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author vsa
 */
public class JsonOWLBuilderTest {
    
    public JsonOWLBuilderTest() {
        // log4j initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);       
    }
    

    @Test
    public void test1() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BuilderConfig bconfig = mapper.readValue(new File("src/test/json/ps_conf.json"), BuilderConfig.class);

        // processor component
        JsonOWLBuilder builder = new JsonOWLBuilder(bconfig);

        // data producer component
        JsonFileSource source = new JsonFileSource("src/test/json/ps_data.json");

        // data consumer component
        OwlWriter writer = new OwlWriter("src/test/json/result.ttl");

        builder.addToOntology(source.get());       
        writer.put(builder.getOntology());
    }
    
}
