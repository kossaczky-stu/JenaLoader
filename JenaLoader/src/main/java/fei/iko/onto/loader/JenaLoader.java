/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author igor
 */
public class JenaLoader {

    static final Logger LOG = Logger.getLogger(JenaLoader.class.getPackage().getName());

    // paramente pre zapis do suborov
    // adresar a mena lokalnych ttl suborov do ktorych sa ulozi TBOX resp ABOX, ak je dsUrl null;
    static final String workdir = null;//"/home/igor/RD/2019/MALW/TMP/";             // directory to save ontology files 
    static final String TBOX = "TBox";
    static final String ABOX = "ABox";

    static final int ABOXsize = 10000;      // max pocet entitnych objektov v jednom ABOX modeli (50 trippes je unosna hranica pre priamy upload).  
    static int loadcnt = 0;                 // pocitadlo modelov - pre logovanie a mena ttl suborov

    //* Momentalne nevyuzivame, treba doriesit efektivitu/konfiguraciu 
    // Udaje a parametre pre priamy zapis modelu do semantickej db
//    static private final String dsUrl = "http://147.175.121.215:3030/mem-test/";   // secured endpoint
    static private final String dsUrl = "http://localhost:3030/ds/";  // local in-memory ds
    static private final String gsp = "data";                // graph store protocol endpoint
    static private final String user = "iko";                // "admin";
    static private final String pwd = "iko";                 // "orbis2018";
    // parametre pre opakovane pokusy pre priamy zapis
    static final int SLEEP = 10;         // time in milis to sleep before next connection (treba min 5 - 10)
    static final int REPCNT = 5;
     //*/
    protected final LoaderConfiguration config;

    private String subjectEntityName;
    private String subjectRootEntityName;

    private OntClass entityClass;
    private OntModel tbox;
    private OntModel abox;

    private long tripples;
    private long loadedTripples;

////////////////////////////////////////////////////////////////////////////////
// CTORs
////////////////////////////////////////////////////////////////////////////////
    public JenaLoader(LoaderConfiguration config) {
        this.config = config;
    }
//                 loadPairs(rootConcept, objectRootConcept , property, inverseProperty, data); 


////////////////////////////////////////////////////////////////////////////////
// Ontology loader interface
////////////////////////////////////////////////////////////////////////////////

    public void loadPairs(String rootConcept, String rootObjectConcept, String property, String inverseProperty,  DataSource data) {
        // BP: TODO
        int counter = 0;

        tbox = null;
        tbox = newOnto();

        abox = null;
        abox = newOnto();

        // Pretoze do funkcii setDomain a setRange isli dat len Resource
        // Preto som si vytvoril Resourcy s menami danych tried - v podstate to su dane triedy lenze v resourcoch
        // Do prveho Resoucu som vytvoril string pomocou funkcie na vytvaranie tried do ktorej som dal nazov prvej triedy
        // Do druheho Resourcu som spravil to iste len s nazvom druhej triedy
        Resource r1 = abox.createResource(config.name4entityclass(rootConcept));
        Resource r2 = abox.createResource(config.name4entityclass(rootObjectConcept));

        // Vytvoril som Object Property s nazvom danej property ktory som dostal ako argument
        ObjectProperty op = tbox.createObjectProperty(config.name4property(property));
        // Priradil som danej Property Domain - to som nastavil prvu triedu
        op.setDomain(r1);
        // Ako Range som nastavil druhu triedu
        op.setRange(r2);

        // Kedze som to vytvaral do tbox a abox tak som to uploadol a zavrel
        // Najprv abox potom tbox - tak to bolo aj vo funkcii loadEntity
        // Cize po tychto ifoch by tam uz mala byt dana property a uz si len budem nacitavat individualov a tuto property
        if (abox != null) {
            uploadOnto(abox, ABOX);
            abox.close();
            abox = null;
        }

        if (tbox != null) {
            uploadOnto(tbox, TBOX);
            tbox.close();
            tbox=null;
        }

        // Pripojil som sa do tej fuseki databazy aby som odtial mohol tahat informacie
        try (RDFConnection conn = RDFConnectionFactory.connectPW(dsUrl + gsp, user, pwd)) {
            System.out.println("SUCCESS CONNECT");
            // do premennej m som si ulozil cely Model
            Model m = conn.fetch();
            // Tento while cyklus pojde pokial budu data pretoze som vyuzil funkciu getNext ktora vrati true ak nejake data su
            while(data.getNext()){
                // Do nasledujucich resourcou som si nacital individualov z idckami ktore beriem z premennej data
                // Do prveho resourcu davam data z 0-teho stlpca
                // DO druheho resourcu davam data z 1-veho stlpca
                Resource re1 = m.getResource(config.name4individual(rootConcept, data.asString(0)));
                Resource re2 = m.getResource(config.name4individual(rootObjectConcept, data.asString(1)));
                // Prvemu resourcu priradujem danu property - prvy argument, na druhy resource - druhy argument
                re1.addProperty(m.getProperty(config.name4property(property)), re2);
                // Nakoniec to naloadujem do databazy
                conn.load(m);
                counter++;
            }
            System.out.println(counter + " added properities");
        } catch (Exception e) {
            System.out.println("Exeption in uploadOnto " + loadcnt + ": " + e.getMessage());
        }
    }


    public void loadEntity(String rootEntityName, String entityName, Object keyAttrId, List<AttributeDesc> desc, DataSource data) {
        System.out.println("Loading entity: " + entityName);
        tripples = 0;
        loadedTripples = 0;

        this.subjectEntityName = entityName;
        this.subjectRootEntityName = (rootEntityName == null) ? entityName : rootEntityName;

        tbox = null;
        loadSchemaDeclarations(desc);

        abox = null;
        int counter = 0;
        while (data.getNext()) {
            if (abox == null) {
                abox = newOnto();
            }

            String keyval = null;
            // ak keyatribut nie je zadany vygenerujeme kluc z dat.
            if (keyAttrId == null) {
                keyval = generateId(desc, data);
            } else {
                // get individual key
                keyval = data.asString(keyAttrId);
            }

            if (keyval == null || keyval.isEmpty()) {
                //TODO report data warning
                continue;
            }

            // create OWL individual
            // The name of subject individual is derived from the name of ROOT entity of loaded entity hierarchy
            Individual iSubject = abox.createIndividual(config.name4individual(subjectRootEntityName, keyval), entityClass);

            loadSubjectAttributes(iSubject, desc, data);

            counter++;
            // ak je ABOX plny, zapiseme/uploadujeme ho a zacneme s novym modelom
            if (counter >= ABOXsize) {
                System.out.println("ABOX part:" + abox.size() + " triples");
                uploadOnto(abox, ABOX);
                abox.close();
                abox = null;
                counter = 0;
            }
        }

        // zapiseme/uploadujeme posledny abox 
        if (abox != null) {
            System.out.println("ABOX: " + abox.size() + " triples");
            uploadOnto(abox, ABOX);
            abox.close();
            abox = null;
        }

        // upload tbox
        if (tbox != null) {
            System.out.println("TBOX: " + tbox.size() + " triples");
            uploadOnto(tbox, TBOX);
            // Pozn. tbox nemozeme zatvorit skor nez nevygenerujeme vsetky aboxy
            tbox.close();
            tbox=null;
        }

        System.out.println("Loaded triples: " + loadedTripples + "/" + tripples);
    }

////////////////////////////////////////////////////////////////////////////////
// initialization of OntModel and uploading to graph store
////////////////////////////////////////////////////////////////////////////////
    private OntModel newOnto() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        return model;
    }

    private boolean uploadOnto(OntModel model, int rep) {
        if (rep > REPCNT) {
            return false;
        }

        int s = (rep != 0) ? 2500 : SLEEP;
        if (s > 0) {
            try {
                Thread.sleep(s);
            } catch (InterruptedException ex) {
                Logger.getLogger(JenaLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
//                .destination(dsUrl).gspEndpoint(gsp);
//        try (RDFConnection conn = builder.build()) {
        boolean loaded = false;
        try (RDFConnection conn = RDFConnectionFactory.connectPW(dsUrl + gsp, user, pwd)) {
            conn.load(model);
            loaded = true;
        } catch (Exception e) {
            System.out.println("Exeption in uploadOnto " + loadcnt + ": " + e.getMessage());
//            System.out.println("");
//            e.printStackTrace();
//            System.out.println("URL:" + dsUrl);
//            System.out.println("GSP:" + gsp);
        }
        if (loaded == false) {
            return uploadOnto(model, ++rep);
        }
        return true;
    }


    private void uploadOnto(OntModel model, String box) {
        loadcnt++;
        tripples = tripples + model.size();
        // writing to ttl file
        if (workdir != null && box != null) {
            String filename = workdir + box + loadcnt + ".ttl";
//            System.out.println("Writing filename");
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                model.write(fos, "TTL");
                loadedTripples = loadedTripples + model.size();
            } catch (IOException ex) {
                System.out.println("" + ex.getMessage());
            }
        }
        
        // uploading to endpoint directly
        else if (dsUrl != null) {
            if (uploadOnto(model, 0) == true) {
                loadedTripples = loadedTripples + model.size();
            }
        } 
    }

////////////////////////////////////////////////////////////////////////////////
// Declarations TBOX and RBOX axions generated from AttributeDescription
////////////////////////////////////////////////////////////////////////////////
// Poznamka: pre loadovanie subClass axiom hierarchie entitnych tried treba vytvorit samostatnu metodu (urcenu len na to)
// Metody loadEntitySchema a loadEntityData negeneruju subclass axiomu pre subjectEntity pretoze nemusi byt v strome bezprostednou podtriedou rootu.
// Tu sa vytvaraju subclass axiomy len ne-entitne podtriedy (generovane z pre BOOL a ENUM atributov v asSubclass mode). 
    private void loadSchemaDeclarations(Collection<AttributeDesc> desc) {
        tbox = newOnto();
        // OWLClass declaration for subject entity
        entityClass = tbox.createClass(config.name4entityclass(subjectEntityName));

        // OWLProperty declaration and OWLClass declarations for subject entity
        for (AttributeDesc ad : desc) {
            declareAttribute(ad);
        }
    }

    private void declareAttribute(AttributeDesc desc) {
        String name = desc.getProperty();
        switch (desc.getType()) {
            case OBJECT:
                tbox.createObjectProperty(config.name4property(name));
                break;
            case DATA:
                if (config.isGenerateDataProperties()) {
                    tbox.createDatatypeProperty(config.name4property(name));
                }
                break;
        }
    }

////////////////////////////////////////////////////////////////////////////////
// Helper type-conversion methods    
////////////////////////////////////////////////////////////////////////////////
    static private Boolean stringValueAsBool(String val) {
        if (val.equals("1") || val.toLowerCase().startsWith("t") || val.toLowerCase().startsWith("y") || val.toLowerCase().startsWith("a")) {
            return true;
        }
        if (val.equals("0") || val.toLowerCase().startsWith("f") || val.toLowerCase().startsWith("n")) {
            return false;
        }
        return null;
    }

    static private Float stringValueAsFloat(String val) {
        Float fv;
        try {
            fv = Float.valueOf(val);
        } catch (NumberFormatException e) {
            // TODO report data failure warning
            return null;
        }
        return fv;
    }

    static private Double stringValueAsDouble(String val) {
        Double dv;
        try {
            dv = Double.valueOf(val);
        } catch (NumberFormatException e) {
            // TODO report data failure warning
            return null;
        }
        return dv;
    }

    static private Integer stringValueAsInteger(String val) {
        Integer iv;
        try {
            iv = Integer.valueOf(val);
        } catch (NumberFormatException e) {
            // TODO report data failure warning
            return null;
        }
        return iv;
    }

////////////////////////////////////////////////////////////////////////////////
// ABOX
////////////////////////////////////////////////////////////////////////////////
    private String generateId(List<AttributeDesc> desc, DataSource data) {
        String buf = "";
        for (AttributeDesc ad : desc) {
            if (ad == null || ad.getProperty() == null || ad.getProperty().trim().isEmpty()) {
                // TODO report data error
                continue;
            }
            Object id = ad.getColumn();
            if (id == null) {
                // TODO report data error
                continue;
            }

            // get attribute values
            Collection<String> vals = data.asStrings(id);
            if (vals != null && !vals.isEmpty()) {
                // for each value

                for (String v : vals) {
                    // remove leading and trailing spaces and skip empty values
                    if (v == null || v.isEmpty()) {
                        buf = buf + ".";
                    } else {
                        buf = buf + v;
                    }
                    // len prva hodnota z viacnasobneho atributu 
                    break;
                }
            }
        }
        return DigestUtils.md5Hex(buf);
    }

    private void loadSubjectAttributes(Individual iSubject, List<AttributeDesc> desc, DataSource data) {
        for (AttributeDesc ad : desc) {
            if (ad == null || ad.getProperty() == null || ad.getProperty().trim().isEmpty()) {
                // TODO report data error
                continue;
            }
            Object id = ad.getColumn();
            if (id == null) {
                // TODO report data error
                continue;
            }

            // get attribute values
            Collection<String> vals = data.asStrings(id);
            if (vals != null && !vals.isEmpty()) {
                // for each value

                for (String v : vals) {
                    // remove leading and trailing spaces and skip empty values
                    if (v == null || v.isEmpty()) {
                        continue;
                    }
                    // generate owl axiom for attribute value according to 
                    switch (ad.getType()) {
                        case OBJECT:
                            loadObjectValue(iSubject, v, ad);
                            break;
                        case DATA:
                            if (config.isGenerateDataProperties()) {
                                loadDataValue(iSubject, v, ad);
                            }
                            break;
                        case BOOL:
                            loadBoolValue(iSubject, v, ad);
                            break;
                        case ENUM:
                            loadEnumValue(iSubject, v, ad);
                            break;
                    }
                }
            }
        }
    }

    private void loadObjectValue(Individual iSubject, String val, AttributeDesc desc) {
        String attrName = desc.getProperty();

        String objectRootEntityName = desc.getObjectRootConcept();
        if (objectRootEntityName == null || objectRootEntityName.isEmpty()) {
            objectRootEntityName = null;
        }

        boolean inverse = false;
        // inverse property
        String invName = desc.getInverse();
        if (invName != null && !invName.isEmpty() && invName.equals("-")) {
            inverse = true;
        }

        Resource iObject = abox.createResource(config.name4individual(objectRootEntityName, val));
        ObjectProperty op = tbox.getObjectProperty(config.name4property(attrName));
        if (inverse == false) {
            abox.add(iSubject, op, iObject);
        } else {
            abox.add(iObject, op, iSubject);
        }
    }

    private void loadDataValue(Individual iSubject, String val, AttributeDesc desc) {
        String attrName = desc.getProperty();
        // OWL data property
        DatatypeProperty op = tbox.getDatatypeProperty(config.name4property(attrName));

        String datatypeName = desc.getDatatype();
        if (datatypeName == null || datatypeName.isEmpty()) {
            abox.add(iSubject, op, val, XSDDatatype.XSDstring);
        } else if (datatypeName.equals("BOOLEAN")) {
            Boolean v = stringValueAsBool(val);
            if (v != null) {
                abox.addLiteral(iSubject, op, v.booleanValue());
            }
        } else if (datatypeName.equals("FLOAT")) {
            Float v = stringValueAsFloat(val);
            if (v != null) {
                abox.addLiteral(iSubject, op, v.floatValue());
            }
        } else if (datatypeName.equals("DOUBLE")) {
            Double v = stringValueAsDouble(val);
            if (v != null) {
                abox.addLiteral(iSubject, op, v.doubleValue());
            }
        } else if (datatypeName.equals("INTEGER")) {
            Integer v = stringValueAsInteger(val);
            if (v != null) {
                abox.addLiteral(iSubject, op, v.intValue());
            }
        }
        // TODO pripadne pridat dalsie moznosti
    }

    private void loadBoolValue(Individual iSubject, String val, AttributeDesc desc) {
        String attrName = desc.getProperty();

        Boolean v = stringValueAsBool(val);
        if (v == null) {
            // TODO report data conversion error
            return;
        }

        OntClass subClass = tbox.createClass(config.name4subclass(subjectEntityName, attrName, v));
        tbox.add(subClass, RDFS.subClassOf, entityClass);
        abox.add(iSubject, RDF.type, subClass);

    }

    private void loadEnumValue(Individual iSubject, String val, AttributeDesc desc) {
        String attrName = desc.getProperty();

        String subclassName = config.name4subclass(subjectEntityName, attrName, val); // TODO zvazit vat.toupper()
        OntClass subClass = tbox.createClass(subclassName);
        tbox.add(subClass, RDFS.subClassOf, entityClass);
        abox.add(iSubject, RDF.type, subClass);
    }

}
