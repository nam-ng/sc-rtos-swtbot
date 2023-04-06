package platform;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FamilyInfo {
	private String familyName;
	private Collection<BoardInfo> boardList = new ArrayList<>();

	public FamilyInfo(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("board".equalsIgnoreCase(name)) {
					boardList.add(new BoardInfo(childElement));
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		familyName = element.getAttribute("id");
	}

	public String getFamilyName() {
		return familyName;
	}

	public boolean isTargetBoard(String id) {
		for (BoardInfo info : boardList) {
			if (info.getBoardName().equalsIgnoreCase(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCustomBoard(String id) {
		for (BoardInfo info : boardList) {
			if (info.isCustomBoard() && !info.getGroupInfoList().isEmpty() && info.getDeviceList().contains(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean isBoardExist(String boardId) {
		for (BoardInfo info : boardList) {
			if ((info.getBoardName() != null && info.getBoardName().equalsIgnoreCase(boardId))
					|| isDeviceContained(boardId)) {
				return true;
			}
		}
		return false;
	}

	private boolean isDeviceContained(String boardId) {
		for (BoardInfo info : boardList) {
			for (GroupInfo group : info.getGroupInfoList()) {
				if (group.getDeviceInfoList().contains(boardId)) {
					return true;
				}
			}
		}
		return false;
	}
}
