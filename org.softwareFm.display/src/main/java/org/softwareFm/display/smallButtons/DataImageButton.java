package org.softwareFm.display.smallButtons;

import java.util.Map;

import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.simpleButtons.SmallIconPosition;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.utilities.maps.Maps;

public class DataImageButton extends SimpleImageButton{

	private final String dataKey;

	public DataImageButton(IButtonParent parent, ImageButtonConfig config, String dataKey) {
		super(parent, config, true);
		this.dataKey = dataKey;
	}
	
	@Override
	public void data(IDataGetter dataGetter, String entity, String url) {
		super.data(dataGetter, entity, url);
		Map<SmallIconPosition, String> map = makeMap(dataGetter);
		setSmallIconMap(map);
	}

	private Map<SmallIconPosition, String> makeMap(IDataGetter dataGetter) {
		Object data = dataGetter.getDataFor(dataKey);
		Map<SmallIconPosition, String> map = Maps.newMap();
		if (data != null)
			map .put(SmallIconPosition.TopRight, SmallIconsAnchor.softwareFmKey);
		return map;
	}


}
