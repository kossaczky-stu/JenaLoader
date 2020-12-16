package fei.iko.onto.domtoowl.components;

import fei.iko.onto.domtoowl.owlnode.OwlBuilder;
import fei.iko.onto.domtoowl.owlnode.OwlStringNode;
import fei.iko.onto.domtoowl.owlnode.OwlArrayNode;
import fei.iko.onto.domtoowl.owlnode.OwlLongNode;
import fei.iko.onto.domtoowl.owlnode.OwlObjectNode;
import fei.iko.onto.domtoowl.owlnode.OwlNode;
import fei.iko.onto.domtoowl.owlnode.OwlFloatNode;
import fei.iko.onto.domtoowl.owlnode.OwlBooleanNode;
import com.fasterxml.jackson.databind.JsonNode;
import fei.iko.onto.domtoowl.owlnode.BuilderConfig;
import fei.iko.onto.domtoowl.owlnode.NodeConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Json builder sluzi na vygenerovanie z stromu Owl uzlov zo stromu Jsom uzlov
 * datasource je komponenta ktora poskytuje vstupny korenovy uzol json stromu
 * metoda createRootNode vytvori owl strom a vtati jeho korenovy uzol
 *
 * @author igor
 */
public class JsonOWLBuilder extends OwlBuilder<JsonNode> {

    public JsonOWLBuilder(BuilderConfig bconfig) {
        super(bconfig);
    }

    /**
     * Metoda vytvori owl strom z json stromu, ktory poskytne jsonsource
     */
    @Override
    protected OwlNode createOwlNode(String path, JsonNode jn) {
        NodeConfig nconfig = bconfig.getConfig(path);

        OwlNode node = null;
        switch (jn.getNodeType()) {
            case OBJECT:
                if (nconfig == null) {
                    nconfig = new NodeConfig();
                }
                node = createObjectNode(path, jn);
                break;
            case BOOLEAN:
                if (nconfig == null) {
                    return null;
                }
                node = new OwlBooleanNode(jn.asBoolean());
                break;
            case STRING:
                if (nconfig == null) {
                    return null;
                }
                node = new OwlStringNode(jn.asText());
                break;
            case NUMBER:
                if (nconfig == null) {
                    return null;
                }
                if (jn.isLong() || jn.isInt() || jn.isShort()) {
                    node = new OwlLongNode(jn.asLong());
                } else if (jn.isFloatingPointNumber()) {
                    node = new OwlFloatNode(jn.floatValue());
                }
                break;
            case ARRAY:
                if (nconfig == null) {
                    nconfig = new NodeConfig();
                }
                node = createArrayNode(path, jn);
                break;
        }
        
        String key = DigestUtils.md2Hex(jn.toString());
        // use value of  the keyField instead if set in nodeconfig
        if (node != null) {
            if (nconfig.keyField!=null) {
                JsonNode kn = jn.get(nconfig.keyField);
                if (kn!=null && kn.isTextual()) {
                    key = kn.asText();                    
                }
            }
            node.init(this, nconfig, key);
        }
        return node;
    }

    private OwlObjectNode createObjectNode(String path, JsonNode jNode) {
        TreeMap<String, OwlNode> children = new TreeMap<>();

        Iterator<Entry<String, JsonNode>> iter = jNode.fields();
        while (iter.hasNext()) {
            Entry<String, JsonNode> entry = iter.next();
            String fieldName = entry.getKey();
            JsonNode fieldNode = entry.getValue();
            OwlNode child = createOwlNode(path + "/" + fieldName, fieldNode);
            if (child != null) {
                children.put(fieldName, child);
            }
        }
        if (children.isEmpty()) {
            return null;
        }
        return new OwlObjectNode(children);
    }

    private OwlArrayNode createArrayNode(String path, JsonNode jNode) {
        List<OwlNode> children = new ArrayList<>();
        Iterator<JsonNode> elements = jNode.elements();
        while (elements.hasNext()) {
            JsonNode fieldNode = elements.next();
            OwlNode child = createOwlNode(path, fieldNode);
            if (child != null) {
                children.add(child);
            }
        }
        if (children.isEmpty()) {
            return null;
        }
        return new OwlArrayNode(children);

    }

}
