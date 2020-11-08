/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import static fei.iko.onto.loader.JenaLoader.LOG;

enum AttrType {
    OBJECT, // OWL ObjectProperty axioms and assertions
    DATA, // OWL DataProperty axioms and assertions
    BOOL, // SubClass or DataProperty, depending on loader configuration
    ENUM // SubClass or DataProperty or ObjectProperty, depending on loader configuration
}

enum EnumMultiplicity {
    MANY2ONE, // functional property
    ONE2MANY, // inverse functional property
    ONE2ONE;  // functional and inverse functional property
}

/**
 * AttributeDesc.
 *
 * @author igor
 */
public class AttributeDesc {

    // Identifier (String) or number (Integer) of column.
    // Obligatory
    private Object column;              // zvazit premenovanie na id prip. attrId

    // Property name - String used to create name of owl ObjectProperty, DataProperty, or SubClass. 
    // Obligatory
    private String property;

    // Attribute type determining type of generated OWL axioms:  
    // Optional. Default: DATA
    private AttrType type;

    // Multiplicity of attribute used for funtionality restrictions of generated object property . 
    // Optional. Default: null - no restriction generated
    // JenaLoader ignoruje (null)
    private EnumMultiplicity multiplicity;
    
    // Name of inverse object property. Optional.
    //   Possible values:
    //     String used as a name of inverse property for and generation of inverse property axiom
    //     "-" is reserved string constant indicating, that Property name is used as 
    //         a name of inverse property name (ei.e. only inverse property is generated). 
    // Optional.
    // JenaLoader generuje bud priamu alebo inverznu rolu (pre "-"), nie vsak obe  
    private String inverse;

    // String used as value Datatype in DataPropertyAssertions and RangeType in DataPropertyRangeAxiom. 
    // Optional. Default - no Datatype in assertions used and nor range axiom generated.
    private String datatype;          

    // Name of range concept in ObjectPropertyRangeAxiom. 
    // Optional.  Default - objectRootConcept. At least one of objectRootConcept and objectRangeConcept must be set.
    // JenaLoader ignoruje
    private String objectRangeConcept; // Name of range concept in ObjectPropertyRangeAxiom. Optional - defaults to objectRootConcept

    // Name of root concept used as concept-part of object individual name in ObjectPropertyAssertions. 
    // Optional.  Default - objectRangeConcept. At least one of objectRootConcept and objectRangeConcept must be set.
    // JenaLoader povinny
    private String objectRootConcept;  

    public static boolean validateWithConfig(LoaderConfiguration config, List<AttributeDesc> desc) {
        int index = 0;
        for (AttributeDesc ad : desc) {
            if (ad.validateWithConfig(config) == false) {
                return false;
            }
        }
        return true;
    }

    private boolean validateWithConfig(LoaderConfiguration config) {
        if (column == null) {
            LOG.log(Level.CONFIG, "Attribute description: KeyColumn not set!");
            return false;
        }

        // trim Strings
        property = ((property == null || property.trim().isEmpty())) ? null : property.trim();
        datatype = ((datatype == null || datatype.trim().isEmpty())) ? null : datatype.trim();
        inverse = ((inverse == null || inverse.trim().isEmpty())) ? null : inverse.trim();
        objectRangeConcept = ((objectRangeConcept == null || objectRangeConcept.trim().isEmpty())) ? null : objectRangeConcept.trim();
        objectRootConcept = ((objectRootConcept == null || objectRootConcept.trim().isEmpty())) ? null : objectRootConcept.trim();

        // property is obligatory
        if (property == null) {
            LOG.config("Attribute description: property not set!");
            return false;
        }

        if (type == null) {
            LOG.info("Attribute description: attribute type not set. DATA used.");
            // DATA is default type
            type = AttrType.DATA;
        }

        if (type == AttrType.OBJECT) {
            // if one of objectRootConcept or objectRangeConcept name not set, use the other as default
            if (objectRootConcept == null) {
                objectRootConcept = objectRangeConcept;
            }
            if (objectRangeConcept == null) {
                objectRangeConcept = objectRootConcept;
            }
            // objectRootConcept is obligatory for OBJECT properties
            if (objectRootConcept == null) {
                LOG.config("Attribute description: objectRootConcept not set");
                return false;
            }
        }

        // modify description for BOOL attributes according Loader configuration
//        if (type == AttrType.BOOL && !config.isGenerateBoolAsSubclass()) {
        if ((type == AttrType.BOOL) && (config.getGenerateBoolAs() != null)) {
            if (config.getGenerateBoolAs().equalsIgnoreCase("data")) {
                type = AttrType.DATA;
                datatype = "BOOLEAN";
            } else if (!config.getGenerateBoolAs().equalsIgnoreCase("subclass")) {
                LOG.info("Attribute description: Unknown generation type for BOOL. SUBCLASS used");
            }
        }

        // modify description for ENUM attributes according Loader configuration
        if (type == AttrType.ENUM && (config.getGenerateEnumAs() != null)) {
            if (config.getGenerateEnumAs().equalsIgnoreCase("object")) {
                type = AttrType.OBJECT;
                objectRootConcept = null;          // zvazit ine alternativy
                objectRangeConcept = null;         // zvazit ine alternativy
            } else if (config.getGenerateEnumAs().equalsIgnoreCase("data")) {
                type = AttrType.DATA;
            } else if (!config.getGenerateEnumAs().equalsIgnoreCase("subclass")) {
                LOG.info("Attribute description: Unknown generation type for ENUM. SUBCLASS used");
            }
        }
        return true;
    }

    public static EnumMultiplicity multiplicity(String m) {
        if (m == null) {
            return null;
        }
        if (m.trim().equalsIgnoreCase("one2one")) {
            return EnumMultiplicity.ONE2ONE;
        }
        if (m.trim().equalsIgnoreCase("one2many")) {
            return EnumMultiplicity.ONE2MANY;
        }
        if (m.trim().equalsIgnoreCase("many2one")) {
            return EnumMultiplicity.MANY2ONE;
        }
        return null;
    }

    public static AttrType type(String m) {
        if (m == null) {
            return null;
        }
        if (m.trim().equalsIgnoreCase("OBJECT")) {
            return AttrType.OBJECT;
        }
        if (m.trim().equalsIgnoreCase("DATA")) {
            return AttrType.DATA;
        }
        if (m.trim().equalsIgnoreCase("BOOL")) {
            return AttrType.BOOL;
        }
        if (m.trim().equalsIgnoreCase("ENUM")) {
            return AttrType.ENUM;
        }
        return null;
    }

    static public List<AttributeDesc> readDescriptionFromCsv(String csvfile) throws FileNotFoundException, IOException {
        Reader in = new FileReader(csvfile);

        List<AttributeDesc> desc = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines()
                .withIgnoreHeaderCase()
                .withIgnoreSurroundingSpaces();

//        for (CSVRecord r : CSVFormat.RFC4180.parse(in)) {
        int line = 0;
        boolean intcolumn = true;
        for (CSVRecord r : format.parse(in)) {
            line++;

            Object column = null;
            AttrType type;
            EnumMultiplicity multiplicity;
            String property = null;
            String inverse = null;
            String datatype = null;
            String objectRangeConcept = null;
            String objectRootConcept = null;

            String strColumn = null;
            String strType = null;
            String strMultiplicity = null;
            try {
                strColumn = r.get("COLUMN");
                strType = r.get("TYPE");
                strMultiplicity = r.get("MULT");
                property = r.get("NAME");
                inverse = r.get("INVERSE");
                objectRootConcept = r.get("CONCEPT");       // TODO zmen na CONCEPT
                objectRangeConcept = r.get("RANGE");        // TODO zmen na RANGE
                datatype = r.get("DATATYPE");               // TODO pridat
            } catch (IllegalArgumentException e) {

            }

            // type of column object is determned by the first attribute description
            if (intcolumn == true) {
                try {
                    column = Integer.parseUnsignedInt(strColumn);
                } catch (NumberFormatException e2) {

                    if (line == 1) {
                        intcolumn = false;
                        column = strColumn;
                    } else {
                        // report error: COLUMN is mandatory
                        LOG.severe("COLUMN must contain nonnegative integer");
                        return null;
                    }
                }
            } else {
                column = strColumn;
            }

            // if property not set attribute is ignored
            if (property == null || property.trim().isEmpty()) {
                LOG.log(Level.INFO, "Line {0}: NAME empty. Attribute ignored.", line);
                continue;
            }

            type = AttributeDesc.type(strType);
            multiplicity = AttributeDesc.multiplicity(strMultiplicity);

            AttributeDesc ad = new AttributeDesc();
            ad.column = column;
            ad.property = property;
            ad.type = type;
            ad.inverse = inverse;
            ad.multiplicity = multiplicity;
            ad.objectRangeConcept = objectRangeConcept;
            ad.objectRootConcept = objectRootConcept;
            ad.datatype = datatype;

            desc.add(ad);
        }
        return desc;
    }

    public Object getColumn() {
        return column;
    }

    public void setColumn(Object column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public AttrType getType() {
        return type;
    }

    public void setType(AttrType type) {
        this.type = type;
    }

    public EnumMultiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(EnumMultiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    public String getInverse() {
        return inverse;
    }

    public void setInverse(String inverse) {
        this.inverse = inverse;
    }

    public String getObjectRangeConcept() {
        return objectRangeConcept;
    }

    public void setObjectRangeConcept(String objectRangeConcept) {
        this.objectRangeConcept = objectRangeConcept;
    }

    public String getObjectRootConcept() {
        return objectRootConcept;
    }

    public void setObjectRootConcept(String objectRootConcept) {
        this.objectRootConcept = objectRootConcept;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    @Override
    public String toString() {
        return "AttributeDesc{" + "attributeName=" + property + ", attributeType=" + type + ", multiplicity=" + multiplicity + ", inverseAttrName=" + inverse + ", dataTypeName=" + objectRangeConcept + ", rangeEntityRootName=" + objectRootConcept + '}';
    }

}
