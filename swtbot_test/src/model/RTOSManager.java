package model;

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

public class RTOSManager {
	private static Collection<RTOS> rtoses = new ArrayList<>();

	private RTOSManager() {
		// do nothing
	}

	public static void loadRTOSModel(File xmlFile) {
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
		if (!"rtospg".equals(rootElement.getTagName())) {
			return;
		}
		rtoses.clear();
		NodeList children = rootElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("RTOS".equalsIgnoreCase(name)) {
					rtoses.add(new RTOS(childElement));
				}
			}
		}
	}

	public static Collection<RTOSVersion> getVersionsSupportedByRTOSId(String rtosType) {
		for (RTOS rtos : rtoses) {
			if (rtos.getRTOSType().equalsIgnoreCase(rtosType)) {
				return rtos.getVersions();
			}
		}
		return new ArrayList<>();
	}

	public static RTOSVersion getVersionById(String rtosType, String versionId) {
		for (RTOS rtos : rtoses) {
			if (rtos.getRTOSType().equalsIgnoreCase(rtosType)) {
				return rtos.getVersionById(versionId);
			}
		}
		return null;
	}

	public static Application getApplication(String rtosType, String versionId, String appId) {
		RTOSVersion version = getVersionById(rtosType, versionId);
		if (version == null) {
			return null;
		}
		if (version.getApplicationById(appId) == null) {
			return null;
		}
		return version.getApplicationById(appId);
	}

	public static Application getApplication(String rtosType, String versionId, String appId, String toolchain, String board) {
		RTOSVersion version = getVersionById(rtosType, versionId);
		if (version == null) {
			return null;
		}
		Application app = version.getApplicationById(appId);
		if (app == null) {
			return null;
		}
		for (Target target : app.getTargets()) {
			if (target.getSupportedToolchainNames().contains(toolchain) && target.getSupportedBoardNames().contains(board)) {
				return app;
			}
		}
		return null;
	}
}
