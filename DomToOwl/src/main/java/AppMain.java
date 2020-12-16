
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fei.iko.onto.domtoowl.components.JsonFileSource;
import fei.iko.onto.domtoowl.components.JsonOWLBuilder;
import fei.iko.onto.domtoowl.components.OwlWriter;
import fei.iko.onto.domtoowl.owlnode.BuilderConfig;
import java.io.File;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import static org.apache.log4j.ConsoleAppender.SYSTEM_OUT;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author igor
 */
public class AppMain {
    public static void main(String[] args) throws Exception {
        // log4j initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);       
        // json mapper initialization
        ObjectMapper mapper = new ObjectMapper();

        // Loading Builder Configuration
        BuilderConfig bconfig = mapper.readValue(new File("src/test/json/ps_conf.json"), BuilderConfig.class);
        // processor component
        JsonOWLBuilder builder = new JsonOWLBuilder(bconfig);        
        // data producer component
        JsonFileSource source = new JsonFileSource("src/test/json/ps_data.json");
        // data consumer component
        OwlWriter writer = new OwlWriter("src/test/json/result.ttl");

        // run prcessing data
        JsonNode jnode = source.get();
        builder.addToOntology(jnode);
        writer.put(builder.getOntology());        
    }
}
