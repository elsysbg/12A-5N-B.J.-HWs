import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MainClass {

	public static void main(String[] args) throws TransformerException,
			ParserConfigurationException, SAXException, IOException {
		final Document document = openDocument("src/sample.xml");
		process(document);
		saveDocument(document, "src/output.xml");
		process2(document);
		saveDocument(document, "src/output2.xml");
	}

	private static void saveDocument(Document document, String filename)
			throws FileNotFoundException, TransformerException {
		final TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute("indent-number", 2);
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		final StreamResult out = new StreamResult(new FileOutputStream(
				new File(filename)));
		transformer.transform(new DOMSource(document), out);
	}
	
	private static void process(Document document) {
		Element rootOld = document.getDocumentElement();
		Element rootNew = document.createElement("ranking");
		
		NodeList childrenOld = rootOld.getChildNodes();

		while (true) {
			Element smallest = findSmallest(childrenOld);
			
			if(smallest == null)
			{
				break;
			}
			
			rootNew.appendChild(smallest);
		}

		document.replaceChild(rootNew, rootOld);
	}
	
	private static void process2(Document document) {
		Element root = document.getDocumentElement();
	
		NodeList children = root.getChildNodes();
		Element elementLast = null;
		
		for(int i = 0; i < children.getLength(); i++)
		{
			Node node = children.item(i);
			
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element)node;
				
				if(element.getAttribute("name").equals("A"))
				{
					if(i > 0)
					{
						String temp;
						
						temp = elementLast.getAttribute("name");
						elementLast.setAttribute("name", element.getAttribute("name"));
						element.setAttribute("name", temp);
						
						temp = elementLast.getAttribute("position");
						elementLast.setAttribute("position", element.getAttribute("position"));
						element.setAttribute("position", temp);
					}
				}
				
				elementLast = element;
			}
		}
	}
	
	private static Element findSmallest(NodeList nodes) {
		
		int min = Integer.MAX_VALUE;
		Element smallest = null;
		
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element)node;
				int position = Integer.parseInt(element.getAttribute("position"));
				
				if(position < min)
				{
					min = position; 
					smallest = element;
				}
			}
		}
		
		return smallest;
	}

	private static Document openDocument(String filename)
			throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new File(filename));
	}
}
