/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import junitx.framework.FileAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author igor
 */
public class JenaLoaderTest {
    
    public JenaLoaderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
                final File f = new File("src/test/csv/result.owl");
                f.delete();                
    }
/* zatial nie je portovane na JenaLoader
//    @Test
    public void testLoadEntity() throws OWLOntologyCreationException, IOException, Exception {
        // ontology loader configuration
        LoaderConfiguration c = new LoaderConfiguration();
        // options for generation TBOX axioms
        c.setGenerateSubjectClassDeclaration(false);
        c.setGenerateObjectClassDeclaration(false);
        // options for generation ABOX class assertions for EntityClass instances
        c.setGenerateSubjectClassAssertion(true);
        c.setGenerateObjectClassAssertion(true);
        // options for generation RBOX object property axioms
        c.setGenerateFuncionalityRestriction(true);
        c.setGenerateDomainRestriction(true);
        c.setGenerateRangeRestriction(true);
        c.setGenerateInverseProperty(true);
        // options for generation RBOX data property axioms
        c.setGenerateDataProperties(true);
        c.setGenerateBoolAs(null);    // defaults to subclass
        c.setGenerateEnumAs(null);    // defaults to subclass

        JenaLoader loader = new JenaLoader(c, null, null);

        // Read description of entity attributes from csv
        List<AttributeDesc> desc = AttributeDesc.readDescriptionFromCsv("src/test/csv/csvtest_desc.csv");
        if (desc == null || desc.isEmpty()) {
            return;
        }
        if (AttributeDesc.validateWithConfig(c, desc) == false) {
            return;
        }

        DataSourceFactory df = new CsvDataSourceFactory("src/test/csv",true, ',', "\\|");        
        DataSource data = df.createDataSource("csvtest_data.csv");
        loader.loadEntity(null, "Osoba", 0, desc, data);
        loader.saveOntology("src/test/csv/result.owl");

        // check result
        final File expected = new File("src/test/csv/csvtest.owl");
        final File output = new File("src/test/csv/result.owl");        
        FileAssert.assertEquals(expected, output);
    }
*/

    @Test
    public void testLoadDataTripples() {
    }
    
}
