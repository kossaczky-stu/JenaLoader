/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author igor
 */
public class LoaderConfiguration {
    public String uri="http://iko.edu/test";
    private String conceptPrefix = "C_";
    private String subconceptPrefix = "S_";
    private String defaultConceptPrefix = "E_";
    private String individualPrefix = "I_";

//    private boolean generateSubjectClassAssertion;          // generate concept assertions for individuals of the loaded entity (Default: false)
//    private boolean generateObjectClassAssertion;           // generate concept assertions for object individuals of ObjectProperty assertions (Default: false).
//    private boolean generateSubjectClassDeclaration;        // generate OWLClass declaration for the loaded entity and its subclasses (Default: false) PROBABLY OBSOLETE - generated automatically by protege  
//    private boolean generateObjectClassDeclaration;         // generate OWLClass declaration for root concept of ObjectProperty object individuals (Default: false)         
    private boolean generateDataProperties;                 // generate DataProperty declarations and DataProperty assertions(Default: false)
//    private boolean generateInverseProperty;                // generate InverseProperty axioms (Default: false) 
//    private boolean generateFuncionalityRestriction;        // generate Funtionality axioms (Default: false)
//    private boolean generateDomainRestriction;              // generate Domain axioms (Default: false)
//    private boolean generateRangeRestriction;               // generate Range axioms (Default: false)
    
    private String generateBoolAs;                          // type of generated OWL axiom for BOOL attributes. Possible values: "data", "subclass". Default: "subclass"
    private String generateEnumAs;                          // type of generated OWL axiom for ENUM attributes. Possible values: "object", "data", "subclass". Default: "subclass"

    
    public LoaderConfiguration() {
    }
    
    public String getPrefix() {return uri+"#";}

    public IRI ontologyIri() {
        return IRI.create(uri);
    }
    
    public String name4entityclass(String entityname) {
        return getPrefix() + conceptPrefix + entityname;
    }

    public String name4property(String attrname) {
        return getPrefix() + attrname;
    }

    public String name4subclass(String entityname, String attrname, String val) {
        return getPrefix() + subconceptPrefix + entityname + "_" + attrname + "_" + val;
    }

    public String name4subclass(String entityname, String attrname, boolean val) {
        if (val) {
            return getPrefix() + subconceptPrefix + entityname + "_" + attrname;
        } else {
            return getPrefix() + subconceptPrefix + entityname + "_NOT_" + attrname;
        }
    }

    public String name4individual(String entityname, String key) {
        if (entityname==null)
            return getPrefix() + defaultConceptPrefix + key;
        return getPrefix() + individualPrefix + entityname + "_" + key;
    }

//    public boolean isGenerateSubjectClassDeclaration() {
//        return generateSubjectClassDeclaration;
//    }
//
//    public void setGenerateSubjectClassDeclaration(boolean generateSubjectClassDeclaration) {
//        this.generateSubjectClassDeclaration = generateSubjectClassDeclaration;
//    }

//    public boolean isGenerateSubjectClassAssertion() {
//        return generateSubjectClassAssertion;
//    }
//
//    public void setGenerateSubjectClassAssertion(boolean generateSubjectClassAssertion) {
//        this.generateSubjectClassAssertion = generateSubjectClassAssertion;
//    }

    public boolean isGenerateDataProperties() {
        return generateDataProperties;
    }

    public void setGenerateDataProperties(boolean generateDataProperties) {
        this.generateDataProperties = generateDataProperties;
    }

//    public boolean isGenerateInverseProperty() {
//        return generateInverseProperty;
//    }
//
//    public void setGenerateInverseProperty(boolean generateInverseProperty) {
//        this.generateInverseProperty = generateInverseProperty;
//    }

//    public boolean isGenerateFuncionalityRestriction() {
//        return generateFuncionalityRestriction;
//    }
//
//    public void setGenerateFuncionalityRestriction(boolean generateFuncionalityRestriction) {
//        this.generateFuncionalityRestriction = generateFuncionalityRestriction;
//    }

//    public boolean isGenerateDomainRestriction() {
//        return generateDomainRestriction;
//    }
//
//    public void setGenerateDomainRestriction(boolean generateDomainRestriction) {
//        this.generateDomainRestriction = generateDomainRestriction;
//    }
//
//    public boolean isGenerateRangeRestriction() {
//        return generateRangeRestriction;
//    }
//
//    public void setGenerateRangeRestriction(boolean generateRangeRestriction) {
//        this.generateRangeRestriction = generateRangeRestriction;
//    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getConceptPrefix() {
        return conceptPrefix;
    }

    public void setConceptPrefix(String conceptPrefix) {
        this.conceptPrefix = conceptPrefix;
    }

    public String getSubconceptPrefix() {
        return subconceptPrefix;
    }

    public void setSubconceptPrefix(String subconceptPrefix) {
        this.subconceptPrefix = subconceptPrefix;
    }

    public String getDefaultConceptPrefix() {
        return defaultConceptPrefix;
    }

    public void setDefaultConceptPrefix(String defaultConceptPrefix) {
        this.defaultConceptPrefix = defaultConceptPrefix;
    }

    public String getIndividualPrefix() {
        return individualPrefix;
    }

    public void setIndividualPrefix(String individualPrefix) {
        this.individualPrefix = individualPrefix;
    }

//    public boolean isGenerateObjectClassAssertion() {
//        return generateObjectClassAssertion;
//    }
//
//    public void setGenerateObjectClassAssertion(boolean generateObjectClassAssertion) {
//        this.generateObjectClassAssertion = generateObjectClassAssertion;
//    }

//    public boolean isGenerateObjectClassDeclaration() {
//        return generateObjectClassDeclaration;
//    }
//
//    public void setGenerateObjectClassDeclaration(boolean generateObjectClassDeclaration) {
//        this.generateObjectClassDeclaration = generateObjectClassDeclaration;
//    }

    public String getGenerateBoolAs() {
        return generateBoolAs;
    }

    public void setGenerateBoolAs(String generateBoolAs) {
        this.generateBoolAs = generateBoolAs;
    }

    public String getGenerateEnumAs() {
        return generateEnumAs;
    }

    public void setGenerateEnumAs(String generateEnumAs) {
        this.generateEnumAs = generateEnumAs;
    }
    
    

    @Override
    public String toString() {
        return "LoaderConfiguration {" 
                + "\n  iri=" + ontologyIri()
//                + "\n  generateSubjectClassDeclaration=" + generateSubjectClassDeclaration 
//                + "\n  generateSubjectEntityClassAssertion=" + generateSubjectClassAssertion
//                + "\n  generateObjectClassDeclaration=" + generateObjectClassDeclaration 
//                + "\n  generateObjectEntityClassAssertion=" + generateObjectClassAssertion
//                + "\n  generateDataProperties=" + generateDataProperties
//                + "\n  generateInverseProperty=" + generateInverseProperty
//                + "\n  generateFuncionalityRestriction=" + generateFuncionalityRestriction
//                + "\n  generateDomainRestriction=" + generateDomainRestriction
//                + "\n  generateRangeRestriction=" + generateRangeRestriction
//                + "\n  generateBoolAs=" + generateBoolAs
//                + "\n  generateEnumAs=" + generateEnumAs
                + '}';
    }

 }
