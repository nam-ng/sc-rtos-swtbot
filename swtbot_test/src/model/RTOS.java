package model;

import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RTOS {
	private String rtosType;
	private Collection<RTOSVersion> versions = new ArrayList<>();

	public RTOS(Element element) {
		parseAttribute(element);
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String name = childElement.getTagName();
				if ("version".equalsIgnoreCase(name)) {
					versions.add(new RTOSVersion(childElement));
				}
			}
		}
	}

	private void parseAttribute(Element element) {
		rtosType = element.getAttribute("id");
	}

	public String getRTOSType() {
		return rtosType;
	}

	public Collection<RTOSVersion> getVersions() {
		return versions;
	}

	public RTOSVersion getVersionById(String id) {
		for (RTOSVersion version : versions) {
			if (version.getVersionId().equalsIgnoreCase(id)) {
				return version;
			}
		}
		return null;
	}

	public Collection<Application> getAppListByVersionId(String versionId) {
		for (RTOSVersion version : versions) {
			if (version.getVersionId().equalsIgnoreCase(versionId)) {
				return version.getApplications();
			}
		}
		return new ArrayList<>();
	}

	/**
	 * return Application with version and application id, otherwise, return null
	 * 
	 * @param versionId
	 * @param appId
	 * @return
	 */
	public Application getApplicationById(String versionId, String appId) {
		for (RTOSVersion version : versions) {
			if (version.getVersionId().equalsIgnoreCase(versionId)) {
				return version.getApplicationById(appId);
			}
		}
		return null;
	}

}
