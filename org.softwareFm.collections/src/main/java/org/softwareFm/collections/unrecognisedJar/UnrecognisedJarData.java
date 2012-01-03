package org.softwareFm.collections.unrecognisedJar;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.utilities.collections.Lists;

public class UnrecognisedJarData {
	public static UnrecognisedJarData forTests(String projectName, File jarFile, GroupIdArtifactVersion...groupIdArtifactVersions) {
		return new UnrecognisedJarData(projectName, jarFile, groupIdArtifactVersions);
	}

	private final BindingRipperResult result;
	public final String projectName;
	public final File jarFile;
	public final List<GroupIdArtifactVersion> groupIdArtifactVersions;

	private UnrecognisedJarData(String projectName, File jarFile, GroupIdArtifactVersion...groupIdArtifactVersions) {
		this.result = null;
		this.projectName = projectName;
		this.jarFile = jarFile;
		this.groupIdArtifactVersions = Collections.unmodifiableList(Arrays.asList(groupIdArtifactVersions));
	}

	public UnrecognisedJarData(BindingRipperResult result,List<GroupIdArtifactVersion> groupIdArtifactVersions) {
		this.result = result;
		this.groupIdArtifactVersions = Lists.immutableCopy(groupIdArtifactVersions);
		this.projectName = result.javaProject.getElementName();
		this.jarFile = result.path.toFile();
	}

	@Override
	public String toString() {
		return "UnrecognisedJarData [projectName=" + projectName + ", jarFile=" + jarFile + "]";
	}


}
