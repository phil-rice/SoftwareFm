/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.okCancel;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.composites.IHasComposite;

/** Represents the ok cancel buttons in an editor */
public interface IOkCancel extends IHasComposite {
	void setOkEnabled(boolean enabled);

	boolean isOkEnabled();

	/** as though the user has clicked ok */
	void ok();

	/** as though the user has clicked cancel */
	void cancel();

	Button addButton(String title, Runnable runnable);

	Control okButton();

	Control cancelButton();

}