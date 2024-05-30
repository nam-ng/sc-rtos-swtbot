package model;

import org.w3c.dom.Element;

public class Board {
	private boolean isCustom = false;
	private boolean isDual = false;
	private String board;

	public Board(Element element) {
		parseAttribute(element);
		board = element.getTextContent();
	}

	private void parseAttribute(Element element) {
		isCustom = Boolean.parseBoolean(element.getAttribute("custom"));
		isDual = Boolean.parseBoolean(element.getAttribute("isDual"));
	}

	public boolean isCustomBoard() {
		return isCustom;
	}

	public boolean isDualMode() {
		return isDual;
	}

	public String getBoard() {
		return board;
	}
}
