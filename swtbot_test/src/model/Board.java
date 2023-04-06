package model;

import org.w3c.dom.Element;

public class Board {
	private boolean isCustom = false;
	private String board;

	public Board(Element element) {
		parseAttribute(element);
		board = element.getTextContent();
	}

	private void parseAttribute(Element element) {
		isCustom = Boolean.parseBoolean(element.getAttribute("custom"));
	}

	public boolean isCustomBoard() {
		return isCustom;
	}

	public String getBoard() {
		return board;
	}
}
