/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fei.iko.onto.loader;

import java.util.List;
import static fei.iko.onto.loader.JenaLoader.LOG;

/**
 *
 * @author igor
 */
public class DataDesc {

    enum DataType {
        TRIPPLE,
        PAIR,
        ENTITY
    }

    DataType dataType;              // Type od dataset
    String dataSource;              // String identifying data source: csv-filename or sql select command
    String concept;                 // Name of concept generated from dataset.  
    String rootConcept;             // Name of root concept. Used as concept part of name for individual generated from data set record
    Object keyColumn;               // Identifier (column number or name) of the key column. 
                                    // Values of keyColunm are used as individual part of name for individual generated from data set record.
    List<AttributeDesc> attributes;

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getRootConcept() {
        return rootConcept;
    }

    public void setRootConcept(String rootConcept) {
        this.rootConcept = rootConcept;
    }

    public Object getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(Object keyColumn) {
        this.keyColumn = keyColumn;
    }

    public List<AttributeDesc> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeDesc> attributes) {
        this.attributes = attributes;
    }

    public boolean validateWithConfig(LoaderConfiguration config) {
        // dataSource is obligatory
        if (dataSource == null || dataSource.trim().isEmpty()) {
            LOG.config("Data description: DataSource not set.");
            return false;
        }

        // ENTITY is default dataType
        if (dataType == null) {
            dataType = DataType.ENTITY;
            LOG.info("Data description: DataType not set. ENTITY used!");
        }

        // 0 is default for keyColumn, relevant only for ENTITY dataType
        if (keyColumn == null && dataType == DataType.ENTITY) {
            keyColumn = 0;
            LOG.info("Data description: KeyColumn for Entity not set . Column 0 used!");
        }

        // trim concept an rootConcept names
        concept = ((concept == null || concept.trim().isEmpty())) ? null : concept.trim();
        rootConcept = ((rootConcept == null || rootConcept.trim().isEmpty())) ? null : rootConcept.trim();
        // if one of names not set use the other as default
        if (rootConcept == null) {
            rootConcept = concept;
        }
        if (concept == null) {
            concept = rootConcept;
        }
        // concept name is obligatory
        if (concept == null) {
            LOG.config("Data description: rootConcept not set.");
            return false;
        }

        // check and update attribute descriptions
        if (getAttributes() != null) {
            return AttributeDesc.validateWithConfig(config, getAttributes());
        }
        return true;
    }
}
