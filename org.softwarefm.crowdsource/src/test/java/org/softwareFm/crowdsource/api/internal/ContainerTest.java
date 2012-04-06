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
		ITransactionManager manager = makeLocalTransactionManager();
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
