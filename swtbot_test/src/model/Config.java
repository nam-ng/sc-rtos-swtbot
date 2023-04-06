package model;

import org.w3c.dom.Element;

public class Config {
	private String id;
	private boolean isActive = false;

	public Config(Element element) {
		parseAttribute(element);
	}

	private void parseAttribute(Element element) {
		id = element.getAttribute("id");
		isActive = Boolean.parseBoolean(element.getAttribute("active"));
	}

	public String getId() {
		return id;
	}

	public boolean isActive() {
		return isActive;
	}
}
