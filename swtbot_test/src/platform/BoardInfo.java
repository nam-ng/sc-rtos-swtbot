package platform;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BoardInfo {
	private static final String CUSTOM_ATT = "custom";
	private boolean isCustom = false;
	private String boardName;
	private Collection<GroupInfo> groupInfo = new ArrayList<>();

	public BoardInfo(Element element) {
		parseAttribute(element);
		parseBoardName(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("group".equalsIgnoreCase(name)) {
					groupInfo.add(new GroupInfo(childElement));
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		setCustomBoard(Boolean.parseBoolean(element.getAttribute(CUSTOM_ATT)));
	}

	private void parseBoardName(Element element) {
		boardName = element.getTextContent();
	}

	public String getBoardName() {
		return boardName;
	}

	public Collection<GroupInfo> getGroupInfoList() {
		return groupInfo;
	}

	public Collection<String> getDeviceList() {
		Collection<String> result = new ArrayList<>();
		if (!groupInfo.isEmpty()) {
			for (GroupInfo info : groupInfo) {
				result.addAll(info.getDeviceInfoList());
			}
		}
		return result;
	}

	private void setCustomBoard(boolean isCustom) {
		this.isCustom = isCustom;
	}

	public boolean isCustomBoard() {
		return isCustom;
	}
}
