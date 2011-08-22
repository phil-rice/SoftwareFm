package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.eclipse.swt.graphics.Image;

public class DisplayerDetails {
	public String entity;
	public String key;
	public String title;
	public String help;
	public Image optionalImage;

	public DisplayerDetails(String entity, Image optionalImage, Map<String, String> map) {
		this.entity = entity;
		this.optionalImage = optionalImage;
		this.key = map.get(DisplayCoreConstants.key);
		this.title = map.get(DisplayCoreConstants.title);
		this.help = map.get(DisplayCoreConstants.help);

	}

	@Override
	public String toString() {
		return "DisplayerDetails [entity=" + entity + ", title=" + title + ", key=" + key + ", help=" + help + "]";
	}

}
