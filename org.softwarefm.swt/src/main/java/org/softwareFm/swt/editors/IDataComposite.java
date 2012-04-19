/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.card.CardOutlinePaintListener;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

public interface IDataComposite<T extends Control> {
	CardConfig getCardConfig();

	TitleWithTitlePaintListener getTitle();

	/** The body is everything that isn't the title */
	Composite getBody();

	/** The inner body is needed to allow the {@link CardOutlinePaintListener} to draw around it. the editor and and any footers (e.g. okCancel) are children of it */
	Composite getInnerBody();

	/** the actual editor component */
	T getEditor();

	/** should the editor use all the available height, or it's own computed size */
	boolean useAllHeight();

	String getCardType();

	TitleSpec getTitleSpec();

}