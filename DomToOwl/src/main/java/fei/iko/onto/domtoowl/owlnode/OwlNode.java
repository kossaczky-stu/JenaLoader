package fei.iko.onto.domtoowl.owlnode;

import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author igor
 */
public abstract class OwlNode {
    String key;
    OwlBuilder builder = null;
    public NodeConfig nconfig = null;

    public void setBuilder(OwlBuilder builder) {
        this.builder = builder;
    }
    public void generate() {
        // root nema rodica
        generate("", "");
    }
    abstract void generate(String subjectKey, String name);
    
    public void init(OwlBuilder builder, NodeConfig nconfig, String  key) {
        this.builder = builder;
        this.nconfig = nconfig;
        this.key = key;
        
    }  
}
