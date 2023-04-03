package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.LogUtil;

public class Application {
	private String appId;
	private int appOrder;
	private ProjectConfiguration configuration;
	private Collection<Target> targets = new ArrayList<>();

	public Application(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("projectconfiguration".equalsIgnoreCase(name)) {
					setProjectConfiguration(new ProjectConfiguration(childElement));
				} else if ("target".equalsIgnoreCase(name)) {
					addTarget(new Target(childElement));
				}
			}
		}
	}

	public String getApplicationId() {
		return appId;
	}

	public int getApplicationOrder() {
		return appOrder;
	}

	private void parseAttribute(Element element) {
		appId = element.getAttribute("id");
		try {
			appOrder = Integer.parseInt(element.getAttribute("order"));
		} catch (Exception e) {
			LogUtil.logException(e);
			appOrder = 0;
		}
	}

	public ProjectConfiguration getProjectConfiguration() {
		return configuration;
	}

	private void setProjectConfiguration(ProjectConfiguration config) {
		configuration = config;
	}

	public Collection<Target> getTargets() {
		return targets;
	}

	private void addTarget(Target target) {
		targets.add(target);
	}

}
