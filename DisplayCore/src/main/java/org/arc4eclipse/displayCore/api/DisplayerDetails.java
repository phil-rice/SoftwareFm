package org.arc4eclipse.displayCore.api;

public class DisplayerDetails {
	public String entity;
	public NameSpaceAndName nameSpaceAndName;
	public String title;
	public String help;

	public DisplayerDetails(String entity, NameSpaceAndName nameSpaceAndName, String title, String help) {
		this.entity = entity;
		this.nameSpaceAndName = nameSpaceAndName;
		this.title = title;
		this.help = help;
	}

	@Override
	public String toString() {
		return "DisplayerDetails [entity=" + entity + ", title=" + title + ", nameSpaceAndName=" + nameSpaceAndName + ", help=" + help + "]";
	}

}
