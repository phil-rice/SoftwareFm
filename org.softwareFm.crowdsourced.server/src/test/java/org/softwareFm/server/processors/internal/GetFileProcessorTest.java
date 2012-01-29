package org.softwareFm.server.processors.internal;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.HttpResponseMock;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;

public class GetFileProcessorTest extends AbstractProcessCallTest<GetFileProcessor> {

	public void testIgnoresNoneGet() {
		checkIgnoresNoneGet();
	}

	public void testIgnoresGetWithoutExtension() {
		IProcessResult result = processor.process(makeRequestLine(CommonConstants.GET, "someUriWithoutExtension"), Maps.stringObjectMap("a", 1));
		assertNull(result);
	}

	public void testIgnoresIfNotNamedExtension() {
		final String someData = "someData";
		remoteRoot.mkdirs();
		Files.setText(new File(remoteRoot, "url.htm"), someData);

		IProcessResult result = processor.process(makeRequestLine(CommonConstants.GET, "url.htm"), Maps.stringObjectMap("a", 1));
		assertNull(result);
	}

	public void testGetsFile() throws Exception {
		final String someData = "someData";
		remoteRoot.mkdirs();
		Files.setText(new File(remoteRoot, "url.html"), someData);
		IProcessResult result = processor.process(makeRequestLine(CommonConstants.GET, "url.html"), emptyMap);
		result.process(new HttpResponseMock() {
			@Override
			public void setEntity(HttpEntity entity) {
				try {
					assertTrue(entity instanceof FileEntity);
					FileEntity fileEntity = (FileEntity) entity;
					String actual = Files.getText(fileEntity.getContent());
					assertEquals(someData, actual);
					assertEquals("text/html", fileEntity.getContentType().getValue());
				} catch (IOException e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	@Override
	protected GetFileProcessor makeProcessor() {
		return new GetFileProcessor(remoteRoot, "html");
	}

}
