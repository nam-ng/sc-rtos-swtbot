package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RTOSVersion {
	private String versionId;
	private boolean skipApp;
	private Collection<Application> apps = new ArrayList<>();

	public RTOSVersion(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("application".equalsIgnoreCase(name)) {
					apps.add(new Application(childElement));
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		versionId = element.getAttribute("id");
		skipApp = Boolean.parseBoolean(element.getAttribute("skipapp"));
	}

	public String getVersionId() {
		return versionId;
	}

	public boolean isSkipAppSelection() {
		return skipApp;
	}

	public Collection<Application> getApplications() {
		return apps;
	}

	public Application getApplicationById(String id) {
		for (Application app : apps) {
			if (app.getApplicationId().equalsIgnoreCase(id)) {
				return app;
			}
		}
		return null;
	}
}
