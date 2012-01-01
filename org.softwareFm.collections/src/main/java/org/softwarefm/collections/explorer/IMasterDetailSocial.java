/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.collections.explorer.internal.MasterDetailSocial;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.IFunction1;

public interface IMasterDetailSocial extends IHasComposite {
	<T extends IHasControl> T createMaster(IFunction1<Composite, T> builder, boolean preserve);

	<T extends IHasControl> T createDetail(IFunction1<Composite, T> builder, boolean preserve);

	<T extends IHasControl> T createSocial(IFunction1<Composite, T> builder, boolean preserve);

	<T extends IHasControl> T createAndShowMaster(IFunction1<Composite, T> builder);

	<T extends IHasControl> T createAndShowDetail(IFunction1<Composite, T> builder);

	<T extends IHasControl> T createAndShowSocial(IFunction1<Composite, T> builder);

	void setMaster(Control master);

	void setDetail(Control detail);

	void setSocial(Control social);

	void showSocial();

	void hideSocial();

	void hideMaster();

	void showMaster();

	void putDetailOverSocial();

	void putSocialOverDetail();

	public static class Utils {
		public static IMasterDetailSocial masterDetailSocial(Composite parent) {
			return new MasterDetailSocial(parent, SWT.NULL);
		}
	}

}