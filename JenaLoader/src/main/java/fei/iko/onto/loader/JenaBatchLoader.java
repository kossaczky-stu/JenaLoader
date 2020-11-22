/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import org.codehaus.jackson.map.ObjectMapper;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author igor
 */
public class JenaBatchLoader extends JenaLoader {
    
    private JenaBatchLoader(LoaderConfiguration config) {
        super(config);
    }
    
    private void loadTable(DataSourceFactory dsf, DataDesc dd) throws Exception {

        if (!dd.validateWithConfig(config)) {
            return;
        }

        // datasource factory creates datasource using datasource description 
        DataSource data = dsf.createDataSource(dd.dataSource);

        switch (dd.dataType) {
            case ENTITY:
                loadEntity(dd.rootConcept, dd.concept, dd.keyColumn, dd.attributes, data);
//                loader.loadEntity(td.getRootConcept(), td.getConcept(), td.getKeyColumn(), td.getAttributes(), data);
                break;
            case PAIR:
                 String rootConcept = dd.rootConcept; 
                 String objectRootConcept = dd.attributes.get(0).getObjectRootConcept();
                 String property = dd.attributes.get(0).getProperty();         // moze byt null
                 String inverseProperty = dd.attributes.get(0).getInverse();   // moze byt null
                 loadPairs(rootConcept, objectRootConcept , property, inverseProperty, data);
                break;
            case TRIPPLE:
                //loadDataTripples(td.rootConcept, data);
                break;
            default:
                // TODO report input error
                break;
        }
    }

    public static void load(String batchfile) throws IOException, OWLOntologyCreationException, Exception {

        // Read description from json
        ObjectMapper mapper = new ObjectMapper();
        BatchDesc desc = mapper.readValue(new File(batchfile), BatchDesc.class);

        Connection conn = null; 
        DataSourceFactory dsFactory = null;
        if (desc.connection != null) {
            // MYSQL datasource
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection(
                        desc.connection.dburl,
                        desc.connection.user,
                        desc.connection.pwd);
                dsFactory = new SqlDataSourceFactory(conn);
                LOG.log(Level.INFO, "Connected to {0}", desc.connection.dburl);  
            } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }
        } else if (desc.csvdir != null && !desc.csvdir.isEmpty()) {
            // CSV datasource
            // TODO zvazit pridanie withheader, delim, separator do desc pre csv
            boolean withHeader = true;
            dsFactory = new CsvDataSourceFactory(desc.csvdir, withHeader, ',', "\\|");               
        }
        
//        desc.config.setDefaults();
        JenaBatchLoader loader = new JenaBatchLoader(desc.config);
        
//      loader.createNewOntology(desc.config.uri);      TODO mozno v buducnosti 
        for (DataDesc dataDesc : desc.data) {
            loader.loadTable(dsFactory, dataDesc);
        }
        
        if (conn!=null)
            conn.close();
    }
}
