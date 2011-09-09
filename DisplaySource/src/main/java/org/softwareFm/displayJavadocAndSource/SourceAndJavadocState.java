package org.softwareFm.displayJavadocAndSource;

import org.softwareFm.core.plugin.SelectedArtifactSelectionManager;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JavaProjects;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SourceAndJavadocState {

	public final EclipseRepositoryState javadocState;
	public final EclipseRepositoryState sourceState;

	public SourceAndJavadocState(IResourceGetter resourceGetter) {
		String tooltipIfEclipseNotIn = Resources.getOrException(resourceGetter, JavadocSourceConstants.noValueInRepository);
		String tooltipIfRepositoryNotIn = Resources.getOrException(resourceGetter, JavadocSourceConstants.noValueInEclipse);
		javadocState = new EclipseRepositoryState(null, null, tooltipIfEclipseNotIn, tooltipIfRepositoryNotIn);
		sourceState = new EclipseRepositoryState(null, null, tooltipIfEclipseNotIn, tooltipIfRepositoryNotIn);
	}

	private SourceAndJavadocState(EclipseRepositoryState javadocState, EclipseRepositoryState sourceState) {
		this.javadocState = javadocState;
		this.sourceState = sourceState;
	}

	public SourceAndJavadocState withBindingContext(BindingContext bindingContext) {
		if (RepositoryDataItemStatus.Utils.isResults(bindingContext.status)) {
			String entity = (String) bindingContext.context.get(RepositoryConstants.entity);
			if (RepositoryConstants.entityJar.equals(entity)) {
				BindingRipperResult ripped = (BindingRipperResult) bindingContext.context.get(DisplayCoreConstants.ripperResult);
				BindingRipperResult updated = SelectedArtifactSelectionManager.reRip(ripped);
				String eclipseJavadoc = JavaProjects.findJavadocFor(updated.classpathEntry);
				String eclipseSource = JavaProjects.findSourceFor(updated.packageFragment);
				String repositoryJavadoc = (String) bindingContext.data.get("javadoc");
				String repositorySource = (String) bindingContext.data.get("source");
				return new SourceAndJavadocState(javadocState.withNewValues(eclipseJavadoc, repositoryJavadoc), sourceState.withNewValues(eclipseSource, repositorySource));
			}
		}
		return this;
	}

	public SourceAndJavadocState with(String eclipseJavadoc, String repositoryJavadoc, String eclipseSource, String repositorySource) {
		EclipseRepositoryState javadocNewState = new EclipseRepositoryState(eclipseJavadoc, repositoryJavadoc, javadocState.eclipseTooltip(), javadocState.repositoryTooltip());
		EclipseRepositoryState sourceNewState = new EclipseRepositoryState(eclipseSource, repositorySource, sourceState.eclipseTooltip(), sourceState.repositoryTooltip());
		return new SourceAndJavadocState(javadocNewState, sourceNewState);
	}

}
