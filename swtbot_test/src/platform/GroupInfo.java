package platform;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GroupInfo {
	private String groupId;
	private Collection<String> deviceList = new ArrayList<>();

	public GroupInfo(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("device".equalsIgnoreCase(name)) {
					deviceList.add(childElement.getTextContent());
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		groupId = element.getAttribute("id");
	}

	public String getGroupId() {
		return groupId;
	}

	public Collection<String> getDeviceInfoList() {
		return deviceList;
	}

	public String getDeviveInfoById(String id) {
		return deviceList.stream().filter(p -> p.equalsIgnoreCase(id)).findFirst().orElse("");
	}
}
