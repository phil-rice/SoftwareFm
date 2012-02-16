/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.dependancy;

import org.softwareFm.common.collections.ISimpleList;
import org.softwareFm.common.exceptions.WrappedException;

public interface ITopologicalSortResult<T> extends ISimpleList<ISimpleList<T>> {

	public static class Utils {
		public static <T> void walk(ITopologicalSortResult<T> result, ITopologicalSortResultVisitor<T> visitor) {
			try {
				for (int i = 0; i < result.size(); i++) {
					ISimpleList<T> item = result.get(i);
					for (int j = 0; j < item.size(); j++)
						visitor.visit(i, item.get(j));
				}
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static <T> void inverseWalk(ITopologicalSortResult<T> result, ITopologicalSortResultVisitor<T> visitor) {
			try {
				for (int i = result.size() - 1; i >= 0; i--) {
					ISimpleList<T> item = result.get(i);
					for (int j = 0; j < item.size(); j++)
						visitor.visit(i, item.get(j));
				}
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}