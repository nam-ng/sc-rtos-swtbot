package platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import common.LogUtil;

public class PlatformModel {
	private static Collection<FamilyInfo> familyInfo = new ArrayList<>();

	private PlatformModel() {
		// do nothing
	}

	public static void loadPlatformModel(File xmlFile) {
		if (xmlFile == null || !xmlFile.exists()) {
			return;
		}
		try (Reader reader = new InputStreamReader(new FileInputStream(xmlFile), StandardCharsets.UTF_8)) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(reader);
			Document doc = builder.parse(inputSource);
			Element rootElement = doc.getDocumentElement();
			parseXML(rootElement);
		} catch (Exception e) {
			LogUtil.logException(e);
		}
	}

	private static void parseXML(Element rootElement) {
		if (!"platform".equals(rootElement.getTagName())) {
			return;
		}
		familyInfo.clear();
		NodeList children = rootElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("family".equalsIgnoreCase(name)) {
					familyInfo.add(new FamilyInfo(childElement));
				}
			}
		}
	}

	public static String getFamilyName(String boardId) {
		for (FamilyInfo info : familyInfo) {
			if (info.isBoardExist(boardId)) {
				return info.getFamilyName();
			}
		}
		return "";
	}

	public static boolean isTargetBoard(String id) {
		for (FamilyInfo info : familyInfo) {
			if (info.isTargetBoard(id)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCustomBoard(String id) {
		for (FamilyInfo info : familyInfo) {
			if (info.isCustomBoard(id)) {
				return true;
			}
		}
		return false;
	}
}
