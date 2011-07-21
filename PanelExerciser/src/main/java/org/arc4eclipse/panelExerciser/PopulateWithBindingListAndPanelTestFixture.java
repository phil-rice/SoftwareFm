package org.arc4eclipse.panelExerciser;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.panelExerciser.fixtures.AllTestFixtures;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;

public class PopulateWithBindingListAndPanelTestFixture {
	public static void main(String[] args) throws Exception {

		IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		repository.addStatusListener(IRepositoryDataItem.class, IStatusChangedListener.Utils.sysout());
		final IUrlGenerator generator = repository.generator();
		System.out.println("Jar Data");
		for (JarDataAndPath dataAndPath : AllTestFixtures.allConstants(JarDataAndPath.class)) {
			IJarData jarData = dataAndPath.data;
			for (String key : jarData.keys())
				repository.modifyJarData(dataAndPath.jar, key, jarData.get(key));
		}

		putData(repository, IOrganisationData.class, new IFunction1<IOrganisationData, String>() {
			@Override
			public String apply(IOrganisationData from) throws Exception {
				return generator.forOrganisation(from.getOrganisationUrl());
			}
		}, IArc4EclipseRepository.Utils.organisationData());
		putData(repository, IProjectData.class, new IFunction1<IProjectData, String>() {
			@Override
			public String apply(IProjectData from) throws Exception {
				return generator.forProject(from.getOrganisationUrl(), from.getProjectName());
			}
		}, IArc4EclipseRepository.Utils.projectData());
	}

	private static <Data extends IRepositoryDataItem> void putData(IArc4EclipseRepository repository, Class<Data> clazz, IFunction1<Data, String> urlFunction, IFunction1<Map<String, Object>, Data> mapper) throws IllegalAccessException {
		try {
			System.out.println("Data for " + clazz.getSimpleName());
			for (Data item : AllTestFixtures.allConstants(clazz)) {
				for (String key : item.keys()) {
					Object value = item.get(key);
					repository.modifyData(urlFunction.apply(item), key, value, clazz);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}