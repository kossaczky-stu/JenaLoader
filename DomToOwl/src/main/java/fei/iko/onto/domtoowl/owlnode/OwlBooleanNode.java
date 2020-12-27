package fei.iko.onto.domtoowl.owlnode;

/**
 *
 * @author igor
 */
public class OwlBooleanNode extends OwlNode{
    boolean data;

    public OwlBooleanNode(boolean data) {
        this.data =  data;
    }

    @Override
    public void generate(String subjectKey, String name){
        // pre ignorovane uzly nic negenerujeme
        if (name.isEmpty()) {
            return;
        }
        // pre neinkludovane uzly nic negenerujem
        if (!nconfig.included) {
            return;
        }
        builder.boolClassAssertion(subjectKey, name, data);
    }
}
