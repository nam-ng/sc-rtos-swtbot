package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IncludeDirectory {
	private Collection<String> paths = new ArrayList<>();
	
	public IncludeDirectory (Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("path".equalsIgnoreCase(name)) {
					addPath(childElement.getTextContent());
				}
			}
		}
	}
	
	private void addPath(String path) {
		paths.add(path);
	}

	public Collection<String> getPaths() {
		return paths;
	}

}
