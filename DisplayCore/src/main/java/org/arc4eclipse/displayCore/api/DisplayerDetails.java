package org.arc4eclipse.displayCore.api;

import org.eclipse.swt.graphics.Image;

public class DisplayerDetails {
	public String entity;
	public NameSpaceAndName nameSpaceAndName;
	public String title;
	public String help;
	public Image optionalImage;

	public DisplayerDetails(String entity, NameSpaceAndName nameSpaceAndName, String title, String help) {
		this(help, nameSpaceAndName, help, help, null);
	}

	public DisplayerDetails(String entity, NameSpaceAndName nameSpaceAndName, String title, String help, Image image) {
		this.entity = entity;
		this.nameSpaceAndName = nameSpaceAndName;
		this.title = title;
		this.help = help;
		optionalImage = image;
	}

	@Override
	public String toString() {
		return "DisplayerDetails [entity=" + entity + ", title=" + title + ", nameSpaceAndName=" + nameSpaceAndName + ", help=" + help + "]";
	}

}
