package org.softwarefm.display.impl;

import java.util.Map;

import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwarefm.display.IAction;

public class ImageButtonDefn {
	private final String mainImage;
	private final String overlayImage;
	private final Map<SmallIconPosition, String> smallIconMap;
	private final IAction action;
	private final String dataLink;

	public ImageButtonDefn(String mainImage, String overlayImage, String dataLink, Map<SmallIconPosition, String> smallIconMap, IAction action) {
		super();
		this.mainImage = mainImage;
		this.overlayImage = overlayImage;
		this.dataLink = dataLink;
		this.smallIconMap = smallIconMap;
		this.action = action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((dataLink == null) ? 0 : dataLink.hashCode());
		result = prime * result + ((mainImage == null) ? 0 : mainImage.hashCode());
		result = prime * result + ((overlayImage == null) ? 0 : overlayImage.hashCode());
		result = prime * result + ((smallIconMap == null) ? 0 : smallIconMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageButtonDefn other = (ImageButtonDefn) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (dataLink == null) {
			if (other.dataLink != null)
				return false;
		} else if (!dataLink.equals(other.dataLink))
			return false;
		if (mainImage == null) {
			if (other.mainImage != null)
				return false;
		} else if (!mainImage.equals(other.mainImage))
			return false;
		if (overlayImage == null) {
			if (other.overlayImage != null)
				return false;
		} else if (!overlayImage.equals(other.overlayImage))
			return false;
		if (smallIconMap == null) {
			if (other.smallIconMap != null)
				return false;
		} else if (!smallIconMap.equals(other.smallIconMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImageButtonDefn [mainImage=" + mainImage + ", overlayImage=" + overlayImage + ", smallIconMap=" + smallIconMap + ", action=" + action + ", dataLink=" + dataLink + "]";
	}
}