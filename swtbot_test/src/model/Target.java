package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Target {
	private Collection<String> toolchains = new ArrayList<>();
	private Collection<Board> boards = new ArrayList<>();

	public Target(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("toolchain".equalsIgnoreCase(name)) {
					toolchains.add(childElement.getTextContent());
				} else if ("board".equalsIgnoreCase(name)) {
					boards.add(new Board(childElement));
				}
			}
		}
	}

	public Collection<String> getSupportedToolchains() {
		return toolchains;
	}

	public Collection<Board> getSupportedBoards() {
		return boards;
	}

}
