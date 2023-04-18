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

public class TCManager {
	private static Collection<TC> tces = new ArrayList<>();

	private TCManager() {
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
		if (!"testdata".equals(rootElement.getTagName())) {
			return;
		}
		tces.clear();
		NodeList children = rootElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("TC".equalsIgnoreCase(name)) {
					tces.add(new TC(childElement));
				}
			}
		}
	}

	public static Collection<TC> getAllTCes() {
		return tces;
	}

	public static TC getTCById(String id) {
		for (TC tc : tces) {
			if (id.equalsIgnoreCase(tc.getTCId())) {
				return tc;
			}
		}
		return null;
	}
}
