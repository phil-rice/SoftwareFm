/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.details.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.details.IDetailAdder;
import org.softwareFm.swt.details.IDetailsFactoryCallback;

public class EditorDetailAdder implements IDetailAdder {

	@Override
	public IHasControl add(Composite parentComposite, IContainer container, ICard parentCard, CardConfig cardConfig, String key, Object value, IDetailsFactoryCallback callback) {
		if (value instanceof String) {
			String editorName = IResourceGetter.Utils.getOr(cardConfig.resourceGetterFn, parentCard.cardType(), "editor." + key, "text");
			IDetailAdder editorAdder = Functions.call(cardConfig.editorFn, editorName);
			if (editorAdder != null)
				return editorAdder.add(parentComposite, container, parentCard, cardConfig, key, value, callback);
		}
		return null;
	}

}