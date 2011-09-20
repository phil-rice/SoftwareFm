package org.softwareFm.display.displayer;

import java.util.Map;

import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.swtBasics.IHasControl;

public interface ISmallDisplayer extends IHasControl {

	boolean value();

	void setValue(boolean value);

	void data(IDataGetter dataGetter, String entity, String url, Map<String, Object> context, Map<String, Object> data);

}
