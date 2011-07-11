package org.arc4eclipse.repositoryClient.api.impl;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.arc4eclipse.httpClient.api.impl.HttpClientMock;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.requests.MemoryResponseCallback;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;

public class RepositoryClientTest extends TestCase {

	private Class<TestClass> clazz;
	private Method method1;
	private Method method2;

	public void testAccessors() {
		checkGetDetails("/jarDetails.json", IEntityType.PROJECT, clazz);
		checkGetDetails("/jarDetails.json", IEntityType.PROJECT, method1);

		checkGetDetails("/jarDetails.json", IEntityType.RELEASE, clazz);
		checkGetDetails("/jarDetails.json", IEntityType.RELEASE, method1);

		checkGetDetails("/jarDetails/org.arc4eclipse.repositoryClient.api.impl.json", IEntityType.PACKAGE, clazz);
		checkGetDetails("/jarDetails/org.arc4eclipse.repositoryClient.api.impl.json", IEntityType.PACKAGE, method1);

		checkGetDetails("/jarDetails/org.arc4eclipse.repositoryClient.api.impl/TestClass.json", IEntityType.CLASS, clazz);
		checkGetDetails("/jarDetails/org.arc4eclipse.repositoryClient.api.impl/TestClass.json", IEntityType.CLASS, method1);

		checkGetDetails("/jarDetails/org.arc4eclipse.repositoryClient.api.impl/TestClass/methods/method1.json", IEntityType.METHOD, method1);
		checkGetDetails("/jarDetails/org.arc4eclipse.repositoryClient.api.impl/TestClass/methods/method2.json", IEntityType.METHOD, method2);

	}

	private void checkGetDetails(String expected, IEntityType entityType, Object thing) {
		HttpClientMock thinClient = new HttpClientMock(false, "/data" + expected, 200, "result");
		IJarDetails jarDetails = IJarDetails.Utils.makeJarDetails("jarDetails", "R1.0");
		IRepositoryClient<Object, IEntityType> client = IRepositoryClient.Utils.repositoryClient(IPathCalculator.Utils.classPathCalculator(jarDetails), thinClient);
		MemoryResponseCallback<Object, IEntityType> callback = IResponseCallback.Utils.memoryCallback();
		client.getDetails(thing, entityType, callback);

		assertEquals(entityType, callback.aspect);
		assertEquals(thing, callback.thing);
		assertEquals("result", callback.response.asString());

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		clazz = TestClass.class;
		method1 = clazz.getMethod("method1");
		method2 = clazz.getMethod("method2");
	}
}
