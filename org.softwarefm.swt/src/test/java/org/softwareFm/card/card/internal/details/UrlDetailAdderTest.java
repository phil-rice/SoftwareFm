/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal.details;

import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.editors.internal.UrlEditor;
import org.softwareFm.card.editors.internal.UrlEditorDetailAdder;

public class UrlDetailAdderTest extends AbstractValueEditorDetailAdderTest<UrlEditorDetailAdder, UrlEditor> {

	@Override
	protected UrlEditorDetailAdder makeDetailsAdder() {
		return (UrlEditorDetailAdder) IEditorDetailAdder.Utils.url();
	}

}