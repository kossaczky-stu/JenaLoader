package fei.iko.onto.domtoowl.components;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlFileSource {

    private String sourceFilename;

    public XmlFileSource(){ sourceFilename = "src/main/java/pokus.xml"; }

    public XmlFileSource(String sourceFilename){ this.sourceFilename = sourceFilename; }

    public Element get() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new File(sourceFilename));

        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        return root;
    }
}
