package model;

import org.w3c.dom.Element;

public class Toolchain {
	private String version = "";
	private String name;

	public Toolchain(Element element) {
		parseAttribute(element);
		name = element.getTextContent();
	}

	private void parseAttribute(Element element) {
		version = element.getAttribute("version");
	}

	public String getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}
}
