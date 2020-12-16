package fei.iko.onto.domtoowl.components;

import fei.iko.onto.domtoowl.owlnode.OwlBuilder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author igor
 */
public class OwlWriter {
    String outFile;

    public OwlWriter(String outFile) {
        this.outFile = outFile;
    }

    public void put(OntModel model) throws Exception {
//        model.write(System.out, "TTL");
        try {
            System.out.println("Writing ontology to " + outFile);
            model.write(new FileOutputStream(outFile), "TTL");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OwlBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
