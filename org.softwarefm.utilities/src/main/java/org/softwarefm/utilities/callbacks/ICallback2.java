/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.callbacks;

import junit.framework.Assert;

import org.softwarefm.utilities.exceptions.WrappedException;

public interface ICallback2<T1, T2> {
	void process(T1 first, T2 second) throws Exception;

	abstract static class Utils {

		public static final <T1, T2> void call(ICallback2<T1, T2> callback, T1 t1, T2 t2) {
			try {
				callback.process(t1, t2);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static final <T1, T2> ICallback2<T1, T2> noCallback() {
			return new NoCallback2<T1, T2>();
		}

		public static <T1, T2> ICallback2<T1, T2> ensureSameParameters() {
			return new ICallback2<T1, T2>() {
				private T1 value1;
				private T2 value2;

				@Override
				public void process(T1 t1, T2 t2) throws Exception {
					if (value1 == null)
						value1 = t1;
					else
						Assert.assertSame(t1, value1);
					if (value2 == null)
						value2 = t2;
					else
						Assert.assertSame(t2, value2);

				}
			};
		}

	}
}