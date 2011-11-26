/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repository.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.jdtBinding.api.IJarDigester;
import org.softwareFm.repository.api.impl.SoftwareFmRepository;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

public interface ISoftwareFmRepository {

	Future<?> getData(String entity, String url, Map<String, Object> context);

	Future<?> modifyData(String entity, String url, String name, Object value, Map<String, Object> context);

	void notifyListenersThereIsNoData(String entity, Map<String, Object> context);

	void addStatusListener(IRepositoryStatusListener listener);

	void removeStatusListener(IRepositoryStatusListener listener);

	void addLogger(ISoftwareFmLogger logger);

	void shutdown();

	static class Utils {

		public static ISoftwareFmRepository repository() {
			return repository(IRepositoryFacard.Utils.defaultFacard(), IJarDigester.Utils.digester());
		}

		public static ISoftwareFmRepository repository(IRepositoryFacard facard, IJarDigester jarDigester) {
			return new SoftwareFmRepository(facard, jarDigester);
		}

		public static Map<String, Object> makeSecondaryContext(String entity, String key, String rawUrl) {
			return Maps.<String, Object> makeMap(//
					RepositoryConstants.entity, entity, //
					RepositoryConstants.urlKey, key,//
					RepositoryConstants.rawUrl, rawUrl);
		}

		public static Map<String, Object> makePrimaryContext(String entity) {
			return Maps.<String, Object> makeMap(//
					RepositoryConstants.entity, entity); //
		}

		public static Map<String, Object> makeSecondaryNotFoundContext(String entity) {
			return Maps.<String, Object> makeMap(//
					RepositoryConstants.entity, entity); //
		}
	}

}