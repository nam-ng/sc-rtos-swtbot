package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Project {
	private String id;
	private String projectName;
	private Collection<GroupSetting> groupSettings = new ArrayList<>();
	private Collection<Application> apps = new ArrayList<>();

	public Project(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("projectname".equalsIgnoreCase(name)) {
					setProjectName(childElement.getTextContent());
				} else if ("group".equalsIgnoreCase(name)) {
					addGroupSetting(new GroupSetting(childElement));
				} else if ("application".equalsIgnoreCase(name)) {
					addApplication(new Application(childElement));
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		id = element.getAttribute("id");
	}

	public String getProjectId() {
		return id;
	}

	private void setProjectName(String name) {
		projectName = name;
	}

	public String getProjectName() {
		return projectName;
	}

	private void addGroupSetting(GroupSetting setting) {
		groupSettings.add(setting);
	}

	public GroupSetting getGroupSettingById(String id) {
		for (GroupSetting setting : groupSettings) {
			if (setting.getSettingId().equalsIgnoreCase(id)) {
				return setting;
			}
		}
		return null;
	}

	private void addApplication(Application app) {
		apps.add(app);
	}

	public Collection<Application> getApps() {
		return apps;
	}
}
