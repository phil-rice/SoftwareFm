package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IExtraCallProcessorFactory;
import org.softwareFm.crowdsource.api.IExtraReaderWriterConfigurator;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.jarAndClassPath.api.IGroupArtifactVersionCallback;
import org.softwareFm.jarAndClassPath.api.IJarToGroupArtifactAndVersion;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory.Utils;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.jarAndClassPath.internal.AbstractJarToGroupArtifactVersion;

public abstract class AbstractUsageProcessorIntegrationTests extends AbstractProcessorDatabaseIntegrationTests {
	private IJarToGroupArtifactAndVersion getJarToGroupArtifactVersion(IUrlGenerator jarUrlGenerator) {
		return new AbstractJarToGroupArtifactVersion(jarUrlGenerator) {
			private final Map<String, Map<String, Object>> jarMap = Maps.makeMap(//
					"digest11", make("someGroupId1", "someArtifactId1", "someVersion1"),//
					"digest12", make("someGroupId1", "someArtifactId2", "someVersion1"),//
					"digest21", make("someGroupId2", "someArtifactId1", "someVersion1"),//
					"digest22", make("someGroupId2", "someArtifactId2", "someVersion1"),//
					"digest23", make("someGroupId2", "someArtifactId3", "someVersion1"));

			@Override
			protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
				String url = fileDescription.url();
				String digest = Strings.lastSegment(url, "/");
				replyTo(callback, jarMap.get(digest));
				return Futures.doneFuture(null);
			}

			private Map<String, Object> make(String groupId, String artifactId, String version) {
				return Maps.makeMap(JarAndPathConstants.groupIdKey, groupId, JarAndPathConstants.artifactIdKey, artifactId, JarAndPathConstants.version, version);
			}
		};
	}

	@Override
	protected IExtraCallProcessorFactory getExtraProcessCalls() {
		return Utils.getExtraProcessCalls();
	}

	@Override
	protected IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator() {
		return new IExtraReaderWriterConfigurator<ServerConfig>() {
			@Override
			public void builder(IContainerBuilder builder, ServerConfig serverConfig) {
				ISoftwareFmApiFactory.Utils.getServerExtraReaderWriterConfigurator(getUrlPrefix()).builder(builder, serverConfig);
	
				IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion = getJarToGroupArtifactVersion(JarAndPathConstants.jarUrlGenerator(serverConfig.prefix));
				builder.register(IJarToGroupArtifactAndVersion.class, jarToGroupArtifactAndVersion);
	
				IProjectTimeGetter projectTimeGetter = getProjectTimeGetter(); 
				builder.register(IProjectTimeGetter.class, projectTimeGetter);
			}
		};
	}

	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Utils.getDefaultUserValues();
	}

	abstract protected IProjectTimeGetter getProjectTimeGetter();

}
