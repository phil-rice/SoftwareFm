package org.arc4eclipse.panelExerciser.fixtures;

import static org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants.*;

import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IReleaseData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.OrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ReleaseData;
import org.arc4eclipse.binding.mocks.IBindingBuilder;
import org.arc4eclipse.panelExerciser.JarDataAndPath;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.jdt.core.dom.IBinding;

public class ApacheTestFixture {

	private final static String orgUrlLog4j = "http://logging.apache.org/";
	private final static String projNameLog4j = "Logging Services (TM)";
	public static final String log4jJar = "../PanelExerciser/src/main/resources/log4j-1.2.16.jar";
	public final static IOrganisationData orgLog4J = new OrganisationData(Maps.<String, Object> makeMap(//
			organisationNameKey, "Logging Services (TM)",//
			organisationUrlKey, orgUrlLog4j,//
			descriptionKey, "Provides the industry standard Logging Framework"));
	public final static IProjectData projLog4J = new ProjectData(Maps.<String, Object> makeMap(//
			organisationUrlKey, orgUrlLog4j,//
			projectNameKey, projNameLog4j,//
			descriptionKey, ""));
	public final static IReleaseData releaseLog4J1_2_16 = new ReleaseData(Maps.<String, Object> makeMap(//
			organisationUrlKey, orgUrlLog4j,//
			projectNameKey, projNameLog4j,//
			releaseIdentifierKey, "1.2.16",//
			descriptionKey, ""));

	public final static JarDataAndPath jarLog4J1_2_16 = new JarDataAndPath(releaseLog4J1_2_16, log4jJar);
	public final static IBinding Method_AppenderSkeleton_doAppend = IBindingBuilder.Utils.//
			parent(log4jJar).withPackage("org.apache.log4j ").withClass("AppenderSkeleton").//
			child().withMethod("doAppend");
	public final static IBinding Method_AppenderSkeleton_Append = IBindingBuilder.Utils.//
			parent(log4jJar).withPackage("org.apache.log4j ").withClass("AppenderSkeleton").//
			child().withMethod("append");
	public final static IBinding LocalVariable_AppenderSkeleton_headFilter = IBindingBuilder.Utils.//
			parent(log4jJar).withPackage("org.apache.log4j ").withClass("AppenderSkeleton").//
			child().withLocalVariable("headFilter");

}
