package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProjectSettings {
	private Collection<String> toolchains = new ArrayList<>();
	private Collection<String> boards = new ArrayList<>();
	private Collection<String> applications = new ArrayList<>();
	
	private IncludeDirectory incdirs;
	private Collection<LinkerSections> sections = new ArrayList<>();
	private Collection<Options> options = new ArrayList<>();
	
	public ProjectSettings(Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("incdir".equalsIgnoreCase(name)) {
					setIncludeDirectory(new IncludeDirectory(childElement));
				} else if ("sections".equalsIgnoreCase(name)) {
					addLinkerSections(new LinkerSections(childElement));
				} else if ("options".equalsIgnoreCase(name)) {
					addOptions(new Options(childElement));
				} else {
					parseFilter(name, childElement);
				}
			}
		}
	}
	
	private void parseFilter(String name, Element element) {
		switch (name) {
		case "toolchain":
			addToolchains(element.getTextContent());
			break;
		case "board":
			addBoard(element.getTextContent());
			break;
		case "application":
			addApplication(element.getTextContent());
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
	
	private void addApplication(String board) {
		applications.add(board);
	}

	public Collection<String> getApplications() {
		return applications;
	}
	
	private void addOptions(Options option) {
		options.add(option);
	}

	public Collection<Options> getOptions() {
		return options;
	}
	
	private void setIncludeDirectory(IncludeDirectory incdir) {
		incdirs = incdir;
	}

	public IncludeDirectory getIncludeDirectory() {
		return incdirs;
	}
	
	private void addLinkerSections(LinkerSections linkersection) {
		sections.add(linkersection);
	}

	public Collection<LinkerSections> getLinkerSections() {
		return sections;
	}
}
