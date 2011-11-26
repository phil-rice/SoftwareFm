/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.arc4eclipse.panelExerciser;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.jdtBinding.api.IJarDigester;
import org.arc4eclipse.panelExerciser.fixtures.AllTestFixtures;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;

public class PopulateWithBindingListAndPanelTestFixture {
	public static void main(String[] args) throws Exception {
		IJarDigester digester = IJarDigester.Utils.digester();
		IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		repository.addStatusListener(IStatusChangedListener.Utils.sysout());
		// Rewrite
		System.out.println("Jar Data");
		for (JarDataAndPath dataAndPath : AllTestFixtures.allJarDataConstants()) {
			Map<String, Object> jarData = dataAndPath.data;
			for (String key : jarData.keySet()) {
				String digest = digester.apply(dataAndPath.jar);
				Object value = jarData.get(key);
				repository.modifyJarData(digest, key, value, Collections.<String, Object> emptyMap()).get();
			}
		}
		putData(repository, AllTestFixtures.allOrganisationDataConstants(), Arc4EclipseRepositoryConstants.organisationUrlKey, repository.generator().forOrganisation());
		putData(repository, AllTestFixtures.allProjectDataConstants(), Arc4EclipseRepositoryConstants.projectUrlKey, repository.generator().forProject());
		repository.shutdown();
	}

	private static void putData(IArc4EclipseRepository repository, Iterable<Map<String, Object>> maps, String urlKey, IFunction1<String, String> urlMapper) throws IllegalAccessException {
		try {
			System.out.println("Data for " + urlKey);
			for (Map<String, Object> item : maps) {
				String rawUrl = (String) item.get(urlKey);
				if (rawUrl == null)
					throw new NullPointerException(MessageFormat.format(Arc4EclipseRepositoryConstants.cannotFindDataFor, urlKey, item));
				String url = urlMapper.apply(rawUrl);
				for (String key : item.keySet()) {
					Object value = item.get(key);
					repository.modifyData(url, key, value, Collections.<String, Object> emptyMap()).get();
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}