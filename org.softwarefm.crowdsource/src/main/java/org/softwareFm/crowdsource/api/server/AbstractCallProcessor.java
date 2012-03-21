/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import java.util.Map;

import org.apache.http.RequestLine;
import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public abstract class AbstractCallProcessor implements ICallProcessor {
	public final static Logger logger = Logger.getLogger(AbstractCallProcessor.class);
	private final String method;
	protected final String prefix;

	public AbstractCallProcessor(String method, String prefix) {
		this.method = method;
		this.prefix = prefix;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(method)) {
			String url = requestLine.getUri(); // need to trim this a bit!
			if (url.substring(1).startsWith(prefix)) {
				String actualUrl = url.substring(prefix.length() + 1);
				return execute(actualUrl, parameters);
			}
		}
		return null;
	}

	abstract protected IProcessResult execute(String actualUrl, Map<String, Object> parameters);

	protected void checkForParameter(Map<String, Object> parameters, String... keys) {
		for (String key : keys)
			if (!parameters.containsKey(key))
				throw new IllegalArgumentException(key + ", " + Maps.sortByKey(parameters, Comparators.<String>naturalOrder()));
	}

}