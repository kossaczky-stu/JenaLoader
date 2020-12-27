package fei.iko.onto.domtoowl.owlnode;

import static fei.iko.onto.domtoowl.owlnode.NodeConfig.AssertionType.DATA;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author igor
 */
public class NodeConfig {

    public enum AssertionType {
        DATA, OBJECT, CLASS
    }

    public boolean included;
    public String keyField;
    public String propertyname;
    public AssertionType assertionType;
    public TreeMap<Double, String> partitions;
    public Map<String, List<String>> stringpartitions;
    public Map<String, PartInterval> numberpartitions;
    public String defaultpartition;
    public boolean indexed;

    public NodeConfig() {
        included = true;
        propertyname = null;            // json fieldname will be used
        assertionType = DATA;
        partitions = null;
        indexed = false;
    }

    public String getPartition(double value) {
        if (partitions == null || partitions.isEmpty()) {
            return null;
        }
        Map.Entry<Double, String> entry = partitions.higherEntry(value);
        if (entry != null) {
            return entry.getValue();
        } else if (defaultpartition != null && !defaultpartition.isEmpty()) {
            return defaultpartition;
        }
        return null;
    }

    public String getStringPartition(String value) {
        if (stringpartitions == null || stringpartitions.isEmpty()) {
            return null;
        }
        String v = value.trim().toLowerCase();
        for (String key : stringpartitions.keySet()) {
            for (String s : stringpartitions.get(key)) {
//                if (v.equals(s)) {
                if (v.contains(s)) {
                    return key;
                }
            }
        }
        if (defaultpartition != null && !defaultpartition.isEmpty()) {
            return defaultpartition;
        }
        return null;
    }

    public String getNumberPartition(double value) {
        if (numberpartitions == null || numberpartitions.isEmpty()) {
            return null;
        }
        for (String key : numberpartitions.keySet()) {
            PartInterval pi = numberpartitions.get(key);
            if ((pi.from == null || pi.from <= value) && (pi.to == null || pi.to >= value)) {
                return key;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "NodeConfig{" + "included=" + included + ", propertyname=" + propertyname + ", assertionType=" + assertionType + '}';
    }

}
