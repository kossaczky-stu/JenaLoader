package fei.iko.onto.domtoowl.owlnode;

/**
 *
 * @author igor
 */
public class OwlStringNode extends OwlNode {

    String value;

    public OwlStringNode(String data) {
        this.value = data;
    }

    @Override
    public void generate(String subjectKey, String name) {
        // pre neinkludovane uzly nic negenerujem
        if (!nconfig.included) {
            return;
        }

        String partition = null;
        if (nconfig.stringpartitions != null) {
            partition = nconfig.getStringPartition(value);
            if (partition == null) {
                return;
            }
        }

        switch (nconfig.assertionType) {
            case DATA:
                if (!name.isEmpty()) {
                    if (partition != null) {
                        builder.dataAssertion(subjectKey, name, partition);
                    } else {
                        builder.dataAssertion(subjectKey, name, value);
                    }
                }
                break;
            case OBJECT:
                // POZN. Predpoklada sa ze tieto individualy budu v ontologii 
                // pripravenej domenovymi expertami uz zaradene do existujucich tried
                if (!name.isEmpty()) {
                    if (partition != null) {
//                        builder.dataObjectAssertion(subjectKey, name, partition, partition);    //v1
                        builder.dataObjectAssertion(subjectKey, name, value, partition);      //v2
                    } else {
                        builder.dataObjectAssertion(subjectKey, name, value);
                    }
                }
//                if (!name.isEmpty()) {
//                    builder.dataObjectAssertion(subjectKey, name, data);
//                }
                break;

            case CLASS:
                // pre CLASS name moze byt aj prazdne
                if (partition != null) {
                    builder.dataClassAssertion(subjectKey, name, partition);
                } else {
                    builder.dataClassAssertion(subjectKey, name, value);
                }
                break;
        }
    }
}
