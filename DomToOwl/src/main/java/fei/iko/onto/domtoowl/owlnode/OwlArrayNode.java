package fei.iko.onto.domtoowl.owlnode;

import java.util.List;

/**
 *
 * @author igor
 */
public class OwlArrayNode extends OwlNode {

    private final List<OwlNode> fields;

    public OwlArrayNode(List<OwlNode> fields) {
        this.fields = fields;
    }

    public List<OwlNode> getFields() {
        return fields;
    }

    @Override
    public void generate(String subjectKey, String name) {
        int i = 0;
        for (OwlNode node : fields) {
            i++;
            // Pozn. uzly bez mena neindexujeme
            if (nconfig.indexed && !name.isEmpty()) {
                node.generate(subjectKey, name + i);
            } else {
                node.generate(subjectKey, name);
            }
        }
    }
}
