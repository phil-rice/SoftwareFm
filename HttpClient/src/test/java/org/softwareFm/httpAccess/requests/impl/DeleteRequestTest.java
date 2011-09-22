package org.softwareFm.httpAccess.requests.impl;

import org.junit.Test;
import org.softwareFm.httpClient.requests.impl.DeleteRequest;

public class DeleteRequestTest extends AbstractRequestTest {

	@Test
	public void testPost() {
		
		checkRequest(DeleteRequest.class, builder.delete("someUrl"), builder);
		checkRequest(DeleteRequest.class, builder.delete("/someUrl"), builder);
	}

}
