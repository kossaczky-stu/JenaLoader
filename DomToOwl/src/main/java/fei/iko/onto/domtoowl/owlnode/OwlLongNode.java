package fei.iko.onto.domtoowl.owlnode;

/**
 *
 * @author igor
 */
public class OwlLongNode extends OwlNode {

    long value;

    public OwlLongNode(long value) {
        this.value = value;
    }

    @Override
    public void generate(String subjectKey, String name) {
        // pre neinkludovane uzly nic negenerujem
        if (!nconfig.included) {
            return;
        }

        if (nconfig.partitions == null && nconfig.numberpartitions == null && !name.isEmpty()) {
            builder.dataAssertion(subjectKey, name, value);
            return;
        }

        String partition = null;
        if (nconfig.partitions != null) {
            partition = nconfig.getPartition(value);
        } else if (nconfig.numberpartitions != null) {
            partition = nconfig.getNumberPartition(value);
        } 
        
        if (partition != null && !partition.isEmpty()) {
            switch (nconfig.assertionType) {
                case DATA:
                    if (!name.isEmpty()) {
                        builder.dataAssertion(subjectKey, name, partition);
                    }
                    break;
                case OBJECT:
                    if (!name.isEmpty()) {
                        builder.dataObjectAssertion(subjectKey, name, partition + value, partition);
                    }
                    break;
                case CLASS:
                    builder.dataClassAssertion(subjectKey, name, partition);
                    break;
            }
        } 
    }
}
