/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.softwareFm.crowdsource.utilities.collections.Files;

public interface IProcessResult {

	void process(HttpResponse response) throws Exception;

	abstract public static class Utils {
		public static IProcessResult processString(final String string) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					response.setEntity(new StringEntity(string));
				}
			};
		}

		public static IProcessResult doNothing() {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
				}
			};
		}

		public static IProcessResult processFile(final File file) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					String contentType = Files.defaultMimeType(file.getName());
					String format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(file.lastModified());
					response.setHeader("Last-Modified", format);// e.g. Last-Modified:Wed, 14 Dec 2011 19:57:26 GMT
					response.setHeader("Content-Type", contentType);
					response.setEntity(new FileEntity(file, contentType));
				}
			};
		}

		public static IProcessResult processStream(final InputStream inputStream, final String contentType) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					response.setEntity(new InputStreamEntity(inputStream, -1));
					response.setHeader("Content-Type", contentType);
				}
			};
		}

		public static IProcessResult processError(final int statusCode, final String message) {
			return new IProcessResult() {
				@Override
				public void process(HttpResponse response) throws Exception {
					response.setStatusCode(statusCode);
					response.setReasonPhrase(message);
					response.setEntity(new StringEntity(message));
				}
			};
		}
	}
}