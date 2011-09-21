package org.softwareFm.display.displayer;


import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.swtBasics.IHasControl;

public interface ISmallDisplayer extends IHasControl {

	boolean value();

	void setValue(boolean value);

	void data(IDataGetter dataGetter, String entity, String url);

}
