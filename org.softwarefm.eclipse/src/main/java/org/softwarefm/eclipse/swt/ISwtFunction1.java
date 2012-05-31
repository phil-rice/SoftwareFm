/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.eclipse.swt;

import org.eclipse.swt.custom.StyledText;
import org.softwarefm.utilities.functions.IFunction1;

public interface ISwtFunction1<From, To> extends IFunction1<From, To> {

	public static class Utils {
		public static ISwtFunction1<String,String> putInText(final StyledText text){
			return new ISwtFunction1<String, String>() {
				@Override
				public String apply(String from) throws Exception {
					text.setText(from);
					return from;
				}
			};
			
		}
	}
	
}