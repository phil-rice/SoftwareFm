package org.softwareFm.explorer.eclipse;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.IFunction1;

public interface IMasterDetailSocial extends IHasComposite {
	<T extends IHasControl> T createMaster(IFunction1<Composite, T> builder, boolean preserve);

	<T extends IHasControl> T createDetail(IFunction1<Composite, T> builder, boolean preserve);

	<T extends IHasControl> T createSocial(IFunction1<Composite, T> builder, boolean preserve);

	<T extends IHasControl> T createAndShowDetail(IFunction1<Composite, T> builder);

	<T extends IHasControl> T createAndShowSocial(IFunction1<Composite, T> builder);

	void setMaster(Control master);

	void setDetail(Control detail);

	void setSocial(Control social);

	void showSocial();

	void hideSocial();
	

}
