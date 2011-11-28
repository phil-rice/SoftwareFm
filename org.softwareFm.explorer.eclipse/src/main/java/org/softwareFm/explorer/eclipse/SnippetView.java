/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.ExpressionData;
import org.softwareFm.jdtBinding.api.IExpressionCategoriser;
import org.softwareFm.utilities.strings.Strings;

public class SnippetView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		final Activator activator = Activator.getDefault();
		final Text text = new Text(parent, SWT.MULTI | SWT.H_SCROLL);
		Swts.Size.resizeMeToParentsSize(text);

		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();// creates it
		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(final BindingRipperResult ripperResult) {
				if (ripperResult == null) {
					text.setText("Ripper result null");
					return;
				}
				Expression expression = ripperResult.expression;
				ExpressionData data = IExpressionCategoriser.Utils.categoriser().categorise(expression);
				String content = "Expression: " + Strings.nullSafeToString(expression) + //
						"\n\nExpressionData: " + data;
				text.setText(content);
			}
		});
	}

	@Override
	public void setFocus() {
	}

}