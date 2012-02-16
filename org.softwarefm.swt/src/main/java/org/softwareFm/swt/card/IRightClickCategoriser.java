/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import java.util.Map;

import org.softwareFm.swt.card.RightClickCategoryResult.Type;

/** Determines information about the item that has just been right clicked on in the card */
public interface IRightClickCategoriser {

	RightClickCategoryResult categorise(String url, Map<String, Object> map, String key);

	public static class Utils {

		public static IRightClickCategoriser noRightClickCategoriser() {
			return new IRightClickCategoriser() {

				@Override
				public RightClickCategoryResult categorise(String url, Map<String, Object> map, String key) {
					return new RightClickCategoryResult(Type.NOT_COLLECTION, null, key, url);
				}
			};
		}

	}

}