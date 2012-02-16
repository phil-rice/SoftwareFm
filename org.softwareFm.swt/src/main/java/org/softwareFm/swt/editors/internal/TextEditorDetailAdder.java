/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.IEditorDetailAdder;
import org.softwareFm.swt.title.TitleSpec;

public class TextEditorDetailAdder implements IEditorDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof String) {
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, parentCard);
			TextEditor result = new TextEditor(parentComposite, cardConfig, parentCard.url(), parentCard.cardType(), key, value, callback, titleSpec);
			result.getComposite().setLayout(new ValueEditorLayout());
			callback.gotData(result.getControl());
			return result;
		} else
			return null;
	}

}