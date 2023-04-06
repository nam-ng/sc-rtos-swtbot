package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProjectConfiguration extends AbstractNode {
	private Collection<Config> configs = new ArrayList<>();

	public ProjectConfiguration(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("config".equalsIgnoreCase(name)) {
					configs.add(new Config(childElement));
				} else {
					parseFilter(name, childElement);
				}
			}
		}
	}

	public Collection<Config> getConfigs() {
		return configs;
	}

	public boolean isConfigDefined(String id) {
		for (Config config: configs) {
			if (config.getId().equalsIgnoreCase(id)) {
				return true;
			}
		}
		return false;
	}

	public String getActiveConfig() {
		for (Config config : configs) {
			if (config.isActive()) {
				return config.getId();
			}
		}
		return "";
	}
}
