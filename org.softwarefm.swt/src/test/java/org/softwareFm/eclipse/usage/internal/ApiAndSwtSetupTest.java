/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public class ApiAndSwtSetupTest extends ApiAndSwtTest {

	public void testTransactionManagerIsShared() {
		getLocalContainer().access(IGitReader.class, new ICallback<IGitReader>() {
			@Override
			public void process(final IGitReader localReader) throws Exception {
				final Thread localThread = Thread.currentThread();
				getServerContainer().access(IGitReader.class, new ICallback<IGitReader>() {
					@Override
					public void process(IGitReader serverReader) throws Exception {
						Thread serverThread = Thread.currentThread();
						assertSame(localThread, serverThread);
						assertNotSame(localReader, serverReader);
					}
				});

			}
		});
	}

}