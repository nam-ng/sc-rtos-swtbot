package model;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GroupSetting {
	private String id;
	private Language language;
	private Collection<Toolchain> toolchains = new ArrayList<>();
	private String rtosType;
	private String rtosVersion;
	private Collection<Board> boards = new ArrayList<>();
	private Collection<Config> configs = new ArrayList<>();

	public GroupSetting(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				switch (name) {
				case "language":
					setLanguage(new Language(childElement));
					break;
				case "toolchain":
					addToolchain(new Toolchain(childElement));
					break;
				case "RTOSType":
					setRTOSType(childElement.getTextContent());
					break;
				case "RTOSVersion":
					setRTOSVersion(childElement.getTextContent());
					break;
				case "board":
					addBoard(new Board(childElement));
					break;
				case "config":
					addConfig(new Config(childElement));
					break;
				default:
					break;
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		id = element.getAttribute("id");
	}

	public String getSettingId() {
		return id;
	}

	private void setLanguage(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	private void addToolchain(Toolchain toolchain) {
		toolchains.add(toolchain);
	}

	public Collection<Toolchain> getToolchains() {
		return toolchains;
	}

	private void setRTOSType(String rtosType) {
		this.rtosType = rtosType;
	}

	public String getRTOSType() {
		return rtosType;
	}

	private void setRTOSVersion(String version) {
		rtosVersion = version;
	}

	public String getRTOSVersion() {
		return rtosVersion;
	}

	private void addBoard(Board board) {
		boards.add(board);
	}

	public Collection<Board> getBoards() {
		return boards;
	}

	private void addConfig(Config config) {
		configs.add(config);
	}

	public Collection<Config> getConfigs() {
		return configs;
	}
}
