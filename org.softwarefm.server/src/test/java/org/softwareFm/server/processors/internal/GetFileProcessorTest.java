/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.HttpResponseMock;
import org.softwareFm.server.processors.IProcessResult;

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