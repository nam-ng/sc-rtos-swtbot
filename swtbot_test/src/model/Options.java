package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Options {
	private String toolid = "";
	private String groupid = "";
	private String optionid= "";
	private String value = "";
	
	public Options(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("toolid".equalsIgnoreCase(name)) {
					setToolid(childElement.getTextContent());
				} else if ("groupid".equalsIgnoreCase(name)) {
					setGroupid(childElement.getTextContent());
				} else if ("optionid".equalsIgnoreCase(name)) {
					setOptionid(childElement.getTextContent());
				} else if ("value".equalsIgnoreCase(name)){
					setValue(childElement.getTextContent());
				}
			}
		}
	}

	public String getToolid() {
		return toolid;
	}

	public void setToolid(String toolid) {
		this.toolid = toolid;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getOptionid() {
		return optionid;
	}

	public void setOptionid(String optionid) {
		this.optionid = optionid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
