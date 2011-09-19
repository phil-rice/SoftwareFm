package org.softwarefm.display;

import java.util.Map;

import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;
import org.softwarefm.display.data.IDataGetter;
import org.softwarefm.display.smallButtons.ImageButtonConfig;
import org.softwarefm.display.smallButtons.SimpleImageButton;

public class JarSimpleImageButton extends SimpleImageButton{

	public JarSimpleImageButton(IButtonParent parent, ImageButtonConfig config) {
		super(parent, config);
	}
	
	@Override
	public void data(IDataGetter dataGetter, String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		super.data(dataGetter, entity, url, context, data);
		Map<SmallIconPosition, String> map = makeMap(dataGetter);
		setSmallIconMap(map);
	}

	private Map<SmallIconPosition, String> makeMap(IDataGetter dataGetter) {
		String repositoryJavadoc = Strings.nullSafeToString(dataGetter.getDataFor("data.jar.javadoc"));
		String repositorySource = Strings.nullSafeToString(dataGetter.getDataFor("data.jar.source"));
		String eclipseJavadoc = Strings.nullSafeToString(dataGetter.getDataFor("data.raw.javadoc"));
		String eclipseSource = Strings.nullSafeToString(dataGetter.getDataFor("data.raw.source"));
		Map<SmallIconPosition, String> map = Maps.newMap();
		if (eclipseJavadoc.length()>0)
			map.put(SmallIconPosition.TopLeft, SmallIconsAnchor.javadocKey);
		if (eclipseSource.length()>0)
			map.put(SmallIconPosition.BottomLeft, SmallIconsAnchor.sourceKey);
		if (repositoryJavadoc.length()>0)
			map.put(SmallIconPosition.TopRight, SmallIconsAnchor.softwareFmKey);
		if (repositorySource.length()>0)
			map.put(SmallIconPosition.BottomRight, SmallIconsAnchor.softwareFmKey);
		return map;
	}


}
