package fei.iko.onto.domtoowl.components;

import fei.iko.onto.domtoowl.owlnode.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class XmlOwlBuilder extends OwlBuilder<Node> {

    public XmlOwlBuilder(BuilderConfig bconfig){ super(bconfig); }

    @Override
    protected OwlNode createOwlNode(String path, Node data) {
        NodeConfig nconfig = bconfig.getConfig(path);
        OwlNode newNode = null;

        // Zistujeme ci je dany Node Elementom
        if(data.getNodeType() == Node.ELEMENT_NODE){
            //Ak ano tak ho ulozime do premennej typu Element - pretypujeme lebo ma viac fukcii
            Element elData = (Element) data;
            // Zistime ci ma Child Elementy ak ano je to bud pole alebo objekt
            if(hasChildElements(elData)){
                // Zistime ci to nie je pole - poliam som dal atribut type = array
                if(elData.hasAttributes()){
                    if(elData.getAttribute("type").equals("array")){
                        if (nconfig == null) {
                            nconfig = new NodeConfig();
                        }
                        newNode = createArrayNode(path, elData);
                    }
                // Ak to neni pole je to objekt
                }else{
                    if (nconfig == null) {
                        nconfig = new NodeConfig();
                    }
                    newNode = createObjectNode(path, elData);
                }
            // Ak nema child elementy tak to bude bud text, int, float alebo ine
            }else{
                if (nconfig == null) {
                    return null;
                }
                // Zistujeme aky typ cisla to je neviem ci je aj lepsi sposob ale ostatne mi nefungovalo tak
                // som to nechal riesene pomocou try catch
                // V prvom try sa do premennej pokusa parsnut Integer z textu daneho Elementu
                // ak hodi error tak sa zachyti a neurobi nic cize premenna ostala prazdna
                try{
                    long premenna = Integer.parseInt(elData.getTextContent());
                    newNode = new OwlLongNode(premenna);
                }catch(NumberFormatException e){
                    //not int
                }
                // Ak premenna ostala prazdna tak to iste skusame ale na Float
                try{
                    if(newNode == null) {
                        float premenna = Float.parseFloat(elData.getTextContent());
                        newNode = new OwlFloatNode(premenna);
                    }
                }catch(NumberFormatException e){
                    //not float
                }
                // Ak je premenna stale prazdna tak sa necha ako string pretoze metoda getTextContent
                // vracia java.lang.String
                if(newNode == null) {
                    newNode = new OwlStringNode(elData.getTextContent());
                }
            }

            // po vytvoreni OwlNodu spravime nasledujuce operacie
            String key = DigestUtils.md2Hex(elData.getTextContent().toString());
            if (newNode != null) {
                if (nconfig.keyField!=null) {
                    NodeList n = elData.getElementsByTagName(nconfig.keyField);
                    String kn = n.item(0).getTextContent();
                    if (kn!=null) {
                        key = kn;
                    }
                }
                // inicializujeme OwlNode s danymi konfiguraciami a klucom
                newNode.init(this, nconfig, key);
            }
        }

        // nakoniec vratime vytvoreny OwlNode
        return newNode;
    }

    private OwlObjectNode createObjectNode(String path, Node data){
        TreeMap<String, OwlNode> children = new TreeMap<>();
        // Sem isli len Elementy ktore maju viac ako jedneho child Noda cize budeme cez ne iterovat
        // do noveho nodu si ulozime prvy child Node
        Node el = data.getFirstChild();

        // Vo while cykle zistujeme ci dany Node nie je null
        while(el != null){
            // Ak nie tak zistujeme ci dany node je typu Element a ak je tak spravime potrebne operacie
            if(el.getNodeType() == Node.ELEMENT_NODE){
                String fieldName = el.getNodeName();
                OwlNode child = createOwlNode(path + "/" + fieldName, el);
                if (child != null) {
                    children.put(fieldName, child);
                }
            }
            // na konci cyklu do Nodu ulozime  jeho nasledujuceho siblinga aby sa to nezacyklilo
            el = el.getNextSibling();
        }
        if (children.isEmpty()) {
            return null;
        }
        return new OwlObjectNode(children);
    }

    private OwlArrayNode createArrayNode(String path, Node data){
        List<OwlNode> children = new ArrayList<>();
        // Sem sme davali len Elementy ktore su typu array a vlastne prechadzame to rovnako ako v createObjectNode
        Node first = data.getFirstChild();

        while(first != null){
            if(first.getNodeType() == Node.ELEMENT_NODE){
                OwlNode child = createOwlNode(path, first);
                if (child != null) {
                    children.add(child);
                }
            }
            first = first.getNextSibling();
        }

        if (children.isEmpty()) {
            return null;
        }
        return new OwlArrayNode(children);
    }

    private boolean hasChildElements(Element el) {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }
}
