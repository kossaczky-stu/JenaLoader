package fei.iko.onto.domtoowl.owlnode;

import java.util.TreeMap;

/**
 *
 * @author igor
 */
public class OwlObjectNode extends OwlNode {

    private TreeMap<String, OwlNode> fields;

    public OwlObjectNode(TreeMap<String, OwlNode> fields) {
        this.fields = fields;
    }

    public TreeMap<String, OwlNode> getFields() {
        return fields;
    }

    // Pre root je subjectKey="" aj propertyname=""
    @Override
    public void generate(String subjectKey, String propertyname) {
        if (nconfig.included) {
            // generate individual for this node
            builder.individualDeclaration(key);

            // generate object property 
            if (!subjectKey.isEmpty() && !propertyname.isEmpty()) {
                builder.objectAssertion(subjectKey, propertyname, key);
            }
        }
        
        // call generate for children nodes
        for (String childName : fields.keySet()) {
            OwlNode node = fields.get(childName);
            String childproperty = node.nconfig.propertyname;
            if (childproperty == null) {
                childproperty = childName;
            }

            if (nconfig.included) {
                node.generate(key, childproperty);
            } else {
                // "." sa pouzije ako separator neprazdnych mien
                String sep = (propertyname.isEmpty() || childproperty.isEmpty()) ? "" : ".";
                node.generate(subjectKey, propertyname + sep + childproperty);
            }
        }
    }
}
