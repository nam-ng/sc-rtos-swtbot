package model;

import org.w3c.dom.Element;

import common.LogUtil;

public class Action {
	private String id;
	private int order;

	public Action(Element element) {
		setAttributes(element);
	}

	private void setAttributes(Element element) {
		id = element.getAttribute("id");
		try {
			order = Integer.parseInt(element.getAttribute("order"));
		} catch (Exception e) {
			LogUtil.logException(e);
			order = 0;
		}
	}

	public String getActionId() {
		return id;
	}

	public int getActionOrder() {
		return order;
	}
}
