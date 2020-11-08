
import fei.iko.onto.loader.JenaBatchLoader;
import java.io.IOException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author igor
 */
public class LoaderTests {

    /**
     * 
     * Vygenerovanu ontologiu mozno pouzit ako vstup do dllernera:
     * > /home/igor/RD/DL-Learner-WORK/dllearner-1.3.1-SNAPSHOT/bin/cli  vlak2-dllearner.conf   
     * 
     */
    public static void main(String[] args) throws IOException, OWLOntologyCreationException, Exception {
//        String batch = "/home/igor/RD/2019/nbprojects/JenaLoader/src/test/csv/vlak2_desc.json";
//        String batch = "/home/igor/RD/2019/nbprojects/JenaLoader/src/main/java/sqltest.json";
        String batch = "src/test/csv/bpskola_desc.json";

        if (args.length>0) {
            batch = args[0];
        }
        
        JenaBatchLoader.load(batch);
        
    }

    // TODO ak treba sem sa mozu pridavat dalsie experimentalne loadovacie funkcie vyuzivajuce BaseLoader alebo BatchLoader API
    //      napriklad citajuce descriotion z csv alebo inych rozhrani

    /* len na testovanie - obsolete
    private static void jenaJsonBatchLoad(String batchfile, String url, String service) throws IOException, Exception  {

        // Read description from json
        ObjectMapper mapper = new ObjectMapper();
        BatchDesc desc = mapper.readValue(new File(batchfile), BatchDesc.class);

        Connection conn = null;
        DataSourceFactory df = null;
        //zatial testujem len csv-datasource (mysql mozno pridam neskor
        if (desc.csvdir != null && !desc.csvdir.isEmpty()) {
            // TODO zvazit pridanie withheader, delim, separator do desc pre csv
            boolean withHeader = true;
            df = new CsvDataSourceFactory(desc.csvdir, withHeader, ',', "\\|");
        }

//        desc.config.setDefaults();

        JenaLoader loader = new JenaLoader(desc.config, url, service);

        for (DataDesc td : desc.data) {
            if (!td.validateWithConfig(desc.config)) {
                return;
            }

            DataSource data = df.createDataSource(td.getDataSource());

//            switch (td.getDataType()) {
//                case ENTITY:
                    loader.loadEntity(td.getRootConcept(), td.getConcept(), td.getKeyColumn(), td.getAttributes(), data);
//                    break;
//                case PAIR:
//                    break;
//                case TRIPPLE:
//                    break;
//                default:
//                    // TODO report input error
//                    break;
//            }
        }
    }
   
    // len na testovanie - obsolete
    public static void jenaLoaderTest() throws IOException, Exception {
        // ontology loader configuration
        LoaderConfiguration c = new LoaderConfiguration();
        // options for generation TBOX axioms
//        c.setGenerateSubjectClassDeclaration(false);              // JenaLoader vzdy true
//        c.setGenerateObjectClassDeclaration(false);               // JenaLoader vzdy false

        // options for generation ABOX class assertions for EntityClass instances
//        c.setGenerateSubjectClassAssertion(true);                 // JenaLoader vzdy false
//        c.setGenerateObjectClassAssertion(true);                  // JenaLoader vzdy false
        // options for generation RBOX object property axioms
//        c.setGenerateFuncionalityRestriction(true);               // JenaLoader vzdy false
//        c.setGenerateDomainRestriction(true);                     // JenaLoader vzdy false
//        c.setGenerateRangeRestriction(true);                      // JenaLoader vzdy false
//        c.setGenerateInverseProperty(true);                       // JenaLoader vzdy false
        // options for generation RBOX data property axioms
        c.setGenerateDataProperties(true);                          //JenaLoader
        c.setGenerateBoolAs(null);    // defaults to subclass           //AttributeDesc "data"
        c.setGenerateEnumAs(null);    // defaults to subclass           //AttributeDesc "data" or "object"

        JenaLoader loader = new JenaLoader(c, "http://localhost:3030/ds/", "data");

        // Read description of entity attributes from csv
        List<AttributeDesc> desc = AttributeDesc.readDescriptionFromCsv("src/test/csv/csvtest_desc.csv");
        if (desc == null || desc.isEmpty()) {
            return;
        }
        if (AttributeDesc.validateWithConfig(c, desc) == false) {
            return;
        }

        DataSourceFactory df = new CsvDataSourceFactory("src/test/csv", true, ',', "\\|");
        DataSource data = df.createDataSource("csvtest_data.csv");
        loader.loadEntity(null, "Osoba", 0, desc, data);

    }
    */
}
