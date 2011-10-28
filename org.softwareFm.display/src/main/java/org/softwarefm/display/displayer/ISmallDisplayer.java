package org.softwareFm.display.displayer;


import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.data.IDataGetter;

public interface ISmallDisplayer extends IHasControl {

	boolean value();

	void setValue(boolean value);

	void data(IDataGetter dataGetter, String entity, String url);

}
