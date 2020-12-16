package fei.iko.onto.domtoowl.owlnode;

import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author igor
 */
public class BuilderConfig {

    public String prefix;
    public Map<String, NodeConfig> pathConfig;

    public NodeConfig getConfig(String path) {
        String apath[] = path.split("/");
        for (Entry<String, NodeConfig> entry : pathConfig.entrySet()) {
            String apattern[] = entry.getKey().split("/");
            if (apattern.length != apath.length) {
                continue;
            }
            if (match(apattern, apath)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean match(String apattern[], String apath[]) {
        for (int i = 0; i < apath.length; i++) {
            if (apattern.length <= i) {
                return false;
            }
            if ("*".equals(apattern[i])) {
                continue;
            }
            if (!apath[i].equals(apattern[i])) {
                return false;
            }
        }
        return true;
    }

}
