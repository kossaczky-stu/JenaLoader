import com.fasterxml.jackson.databind.ObjectMapper;
import fei.iko.onto.domtoowl.components.*;
import fei.iko.onto.domtoowl.owlnode.BuilderConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XmlAppMain {

    public static void main(String[] args) throws Exception {
        // log4j initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        // json mapper initialization
        ObjectMapper mapper = new ObjectMapper();

        // Loading Builder Configuration
        BuilderConfig bconfig = mapper.readValue(new File("src/test/json/simple2_conf.json"), BuilderConfig.class);
        // processor component
        XmlOwlBuilder builder = new XmlOwlBuilder(bconfig);
        // data producer component
        XmlFileSource source = new XmlFileSource("src/test/json/simple_data.xml");
        // data consumer component
        OwlWriter writer = new OwlWriter("src/test/json/result4.ttl");

        // run prcessing data
        Element node = source.get();
        builder.addToOntology(node);
        writer.put(builder.getOntology());
    }
}
