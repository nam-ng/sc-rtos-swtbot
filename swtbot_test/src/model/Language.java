package model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Language extends AbstractNode {
	private String id;

	public Language(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				parseFilter(name, childElement);
			}
		}
	}

	private void parseAttribute(Element element) {
		id = element.getAttribute("id");
	}

	public String getId() {
		return id;
	}
	
}
