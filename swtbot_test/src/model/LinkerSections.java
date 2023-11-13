package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LinkerSections {
	private Collection<String> section = new ArrayList<>();
	private String address = "";
	
	public LinkerSections (Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("name".equalsIgnoreCase(name)) {
					addSection(childElement.getTextContent());
				} else if ("address".equalsIgnoreCase(name)) {
					setAddress(childElement.getTextContent());
				}
			}
		}
	}

	private void setAddress(String address1) {
		address = address1;
	}

	private void addSection(String name) {
		section.add(name);
	}
	
	public Collection<String> getName() {
		return section;
	}
	
	public String getAddress() {
		return address;
	}
}
