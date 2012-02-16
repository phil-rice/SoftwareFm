/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.details.IDetailAdder;
import org.softwareFm.swt.editors.internal.StyledTextEditorDetailAdder;
import org.softwareFm.swt.editors.internal.TextEditorDetailAdder;
import org.softwareFm.swt.editors.internal.UrlEditorDetailAdder;

public interface IEditorDetailAdder extends IDetailAdder {

	public static class Utils {
		public static IEditorDetailAdder text() {
			return new TextEditorDetailAdder();
		}

		public static IEditorDetailAdder styledText() {
			return new StyledTextEditorDetailAdder();
		}

		public static IEditorDetailAdder url() {
			return new UrlEditorDetailAdder();
		}

		public static IFunction1<String, IEditorDetailAdder> defaultEditorFn() {
			IFunction1<String, IEditorDetailAdder> editorFn = new IFunction1<String, IEditorDetailAdder>() {
				@Override
				public IEditorDetailAdder apply(String editorName) throws Exception {
					if (editorName.equals("text"))
						return text();
					if (editorName.equals("url"))
						return url();
					else if (editorName.equals("styledText"))
						return styledText();
					else if (editorName.equals("none"))
						return null;
					throw new IllegalStateException(editorName);
				}
			};
			return editorFn;

		}
	}
}