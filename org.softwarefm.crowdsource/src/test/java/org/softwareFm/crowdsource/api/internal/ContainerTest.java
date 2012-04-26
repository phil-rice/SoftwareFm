/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import java.util.HashSet;

import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.newGit.internal.RepoTest;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryCallback;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.ConstantFnWithKick;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class ContainerTest extends RepoTest {

	private IContainerBuilder container;

	public void testAccessingContainerAffectsJobCount() {
		container.register(Object.class, new Object());
		for (int i = 0; i < 10; i++) {
			ConstantFnWithKick<Object, String> job = new ConstantFnWithKick<Object, String>("value");
			ITransaction<String> transaction = container.access(Object.class, job);
			assertEquals("i: " + i, 1, transactionManager.activeJobs());
			assertEquals("i: " + i, 1, container.activeJobs());
			job.kick();
			transaction.get();
			assertEquals("i: " + i, 0, transactionManager.activeJobs());
			assertEquals("i: " + i, 0, container.activeJobs());
		}
	}

	public void testRegisteringFactoryMeansItemBuiltIfRequested() {
		MadeObjectForTestFactory factory = new MadeObjectForTestFactory(false);
		container.register(MadeObject.class, factory);
		MemoryCallback<MadeObject> memory = ICallback.Utils.memory();
		container.access(MadeObject.class, memory).get();
		container.access(MadeObject.class, memory).get();
		assertEquals(factory.list, memory.getResults());
		assertEquals(2, new HashSet<MadeObject>(factory.list).size());
	}

	public void testRegisteringFactoryMeansItemNotBuiltIfNotRequested() {
		container.register(Object.class, new Object());
		MadeObjectForTestFactory factory = new MadeObjectForTestFactory(false);
		container.register(MadeObject.class, factory);
		MemoryCallback<Object> memory = ICallback.Utils.memory();
		container.access(Object.class, memory).get();
		container.access(Object.class, memory).get();
		assertEquals(0, factory.list.size());
	}

	public void testRegisteringFactoryMeansItemAddedToTransactionalsIfTransactional() {
		MadeObjectForTestFactory factory = new MadeObjectForTestFactory(true);
		container.register(MadeObject.class, factory);
		MemoryCallback<MadeObject> memory = ICallback.Utils.memory();
		container.access(MadeObject.class, memory).get();
		container.access(MadeObject.class, memory).get();
		assertEquals(factory.list, memory.getResults());
		for (MadeObject madeObject: factory.list){
			MadeObjectForTestTransactional transactional = (MadeObjectForTestTransactional) madeObject;
			assertTrue(transactional.commitCalled());
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = new Container(transactionManager, null) {
		};
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(ContainerTest.class);
	}
}