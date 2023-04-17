package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TC {
	private String id;
	private Collection<Project> projects = new ArrayList<>();
	private Collection<Action> actions = new ArrayList<>();

	public TC(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("project".equalsIgnoreCase(name)) {
					addProject(new Project(childElement));
				} else if ("action".equalsIgnoreCase(name)) {
					addAction(new Action(childElement));
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		id = element.getAttribute("id");
	}

	public String getTCId() {
		return id;
	}

	private void addProject(Project project) {
		projects.add(project);
	}

	public Collection<Project> getProjects() {
		return projects;
	}

	private void addAction(Action action) {
		actions.add(action);
	}

	public Collection<Action> getActions() {
		return actions;
	}
}
