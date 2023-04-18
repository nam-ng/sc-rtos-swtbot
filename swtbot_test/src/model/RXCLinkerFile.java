package model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RXCLinkerFile extends AbstractNode {
	private String path;

	public RXCLinkerFile(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("path".equalsIgnoreCase(name)) {
					setLinkerPath(childElement.getTextContent());
				} else {
					parseFilter(name, childElement);
				}
			}
		}
	}

	private void setLinkerPath(String path) {
		this.path = path;
	}

	public String getLinkerPath() {
		return path;
	}
}
