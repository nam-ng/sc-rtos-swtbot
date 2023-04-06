package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;

public class AbstractNode {
	private Collection<String> toolchains = new ArrayList<>();
	private Collection<String> boards = new ArrayList<>();

	protected void parseFilter(String name, Element element) {
		switch (name) {
		case "toolchain":
			addToolchains(element.getTextContent());
			break;
		case "board":
			addBoard(element.getTextContent());
			break;
		default:
			return;
		}
	}

	private void addToolchains(String toolchain) {
		toolchains.add(toolchain);
	}

	public Collection<String> getToolchains() {
		return toolchains;
	}

	private void addBoard(String board) {
		boards.add(board);
	}

	public Collection<String> getBoards() {
		return boards;
	}
}
