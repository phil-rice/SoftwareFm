/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.ConstantFnWithKick;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public class ContainerTest extends ApiTest {

	public void testAccessingContainerAffectsJobCount() {
		ITransactionManager manager = makeTransactionManager();
		IContainer container = getLocalContainer();
		for (int i = 0; i < 10; i++) {
			ConstantFnWithKick<IGitReader, String> job = new ConstantFnWithKick<IGitReader, String>("value");
			ITransaction<String> transaction = container.access(IGitReader.class, job);
			assertEquals("i: " + i, 1, manager.activeJobs());
			assertEquals("i: " + i, 1, container.activeJobs());
			job.kick();
			transaction.get();
			assertEquals("i: " + i, 0, manager.activeJobs());
			assertEquals("i: " + i, 0, container.activeJobs());
		}
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(ContainerTest.class);
	}
}