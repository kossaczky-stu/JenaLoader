package fei.iko.onto.domtoowl.owlnode;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Bazova trieda OwlBuilder poskytuje zakladnu infrastrukruru pre generovanie
 * ontologie zo stromu OWL uzlov. Strom OWL uzlov vytvara metoda createRootNode,
 * ktoru musia implementovat konkretne podtriedy Ontlogiu generuje zo stromu OWL
 * uzlov metoda buildOntology
 *
 * @author igor
 */
public class OwlBuilder<T> {

    private final OntModel abox;

    protected BuilderConfig bconfig;
    private String ns = "fei#";

    // TODO prevziat z bconfigu
    private String prefixClass = ""; // "C";
    private String prefixIndiv = ""; // I
    private String prefixEnum = ""; // "E";
    private String prefixBooleanClass = ""; // "C";
    private String prefixPartitionClass = ""; // "C";

    public OwlBuilder(BuilderConfig config) {
        this.bconfig = config;
        abox = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        if (bconfig.prefix != null) {
            ns = bconfig.prefix;
        }
        abox.setNsPrefix("", ns);
    }

    /**
     * Metoda vytvori owl strom a vrati jeho korenovy uzol Musi byt
     * implementovana v specifickych podtriedach, podla zdroja dat, z ktoreho
     * OWL strom generuju
     */
    protected OwlNode createOwlNode(String path, T data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    public OntModel buildOntology(T data) throws Exception {
//        addToOntology(data);
//        return abox;
//    }
    
    public void addToOntology(T data) throws Exception {
        OwlNode owlRoot = createOwlNode("", data);
        owlRoot.generate();
    }

    public OntModel getOntology() {
        return abox;
    }

////////////////////////////////////////////////////////////////////////////////
// privatne helper metody 
////////////////////////////////////////////////////////////////////////////////

    private String individualUri(String key) {
        return ns + prefixIndiv + key;
    }

    private String propertyUri(String name) {
        return ns + name;
    }

    private String classUri(String name, boolean b) {
        if (b == true) {
            return ns + prefixBooleanClass + name + "TRUE";
        } else {
            return ns + prefixBooleanClass + name + "FALSE";
        }
    }

    private String dataClassUri(String name, String data) {
        return ns + prefixClass + name + prefixEnum + data;
    }

    // DATAOBJECT pre STRING a NUMERIC
    // z datovej hodnoty sa vygeneruje individual - bez hashovania
    // Individual sa neda ziadnej triede. 
    // Jeho priradenie do triedy moze byt importovane s ontologiou pripravenou domenovym expertom
    private String enumUri(String data) {
        // bude konfigurovatelne v DomConfigu
        return ns + prefixEnum + data;
    }

    // DATAOBJECT pre NUMERIC
    // Z mena partition sa generuje meno konceptu pre individual datovej hodnoty
    private String partitionUri(String partition) {
        // bude konfigurovatelne v DomConfigu
        return ns + prefixPartitionClass + partition;
    }

////////////////////////////////////////////////////////////////////////////////
// package private API pre generovanie owl assertions  
////////////////////////////////////////////////////////////////////////////////

    void individualDeclaration(String key) {
        abox.createIndividual(individualUri(key), null);
    }

    void objectAssertion(String subjectKey, String name, String key) {
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        Individual iObject = abox.createIndividual(individualUri(key), null);
        ObjectProperty prop = abox.createObjectProperty(propertyUri(name));
        abox.add(iSubject, prop, iObject);
    }

    void dataAssertion(String subjectKey, String name, String data) {
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        DatatypeProperty prop = abox.createDatatypeProperty(propertyUri(name));
        abox.add(iSubject, prop, data, XSDDatatype.XSDstring);
    }

    void dataAssertion(String subjectKey, String name, long data) {
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        DatatypeProperty prop = abox.createDatatypeProperty(propertyUri(name));
        abox.addLiteral(iSubject, prop, data);
    }

    void dataAssertion(String subjectKey, String name, double data) {
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        DatatypeProperty prop = abox.createDatatypeProperty(propertyUri(name));
        abox.addLiteral(iSubject, prop, data);
    }

    void dataObjectAssertion(String subjectKey, String name, String data) {
        Individual iObject = abox.createIndividual(enumUri(data), null); // eunmUri - nehashovane
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        ObjectProperty prop = abox.createObjectProperty(propertyUri(name));
        abox.add(iSubject, prop, iObject);

    }

    // DATA INDIVIDUAL and DATAOBJECT ASSERTION pre NUMERIC
    void dataObjectAssertion(String subjectKey, String name, String value, String partition) {
        Resource cls = abox.createResource(partitionUri(partition));
        Individual iObject = abox.createIndividual(enumUri(value), cls);
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        ObjectProperty prop = abox.createObjectProperty(propertyUri(name));
        abox.add(iSubject, prop, iObject);
    }

    void boolClassAssertion(String subjectKey, String name, boolean data) {
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        Resource subClass = abox.createResource(classUri(name, data));
        abox.add(iSubject, RDF.type, subClass);
    }

    void dataClassAssertion(String subjectKey, String name, String data) {
        Individual iSubject = abox.createIndividual(individualUri(subjectKey), null);
        Resource subClass = abox.createResource(dataClassUri(name, data));
        abox.add(iSubject, RDF.type, subClass);
    }
}
