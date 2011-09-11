package org.softwareFm.displayJavadocAndSource;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.SummaryIcon;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.images.SmallIconPosition;

public class JarSummaryImageButton extends SummaryIcon {
	private Set<SmallIconPosition> filter;
	private SourceAndJavadocState state;

	public JarSummaryImageButton(Composite parent, DisplayerContext displayerContext, DisplayerDetails displayerDetails, boolean toggle, Set<SmallIconPosition> filter) {
		super(parent, displayerContext.imageRegistry, displayerDetails, toggle);
		this.filter = filter;
		this.state = new SourceAndJavadocState(displayerContext.resourceGetter);
	}

	public void setSourceAndJavadocState(SourceAndJavadocState state) {
		this.state = state;
		ImageButtons.clearSmallIcons(this);
		setSmallIconsFromState(state.sourceState, SmallIconPosition.BottomLeft, SmallIconsAnchor.sourceKey, SmallIconPosition.BottomRight, SmallIconsAnchor.softwareFmKey);
		setSmallIconsFromState(state.javadocState, SmallIconPosition.TopLeft, SmallIconsAnchor.javadocKey, SmallIconPosition.TopRight, SmallIconsAnchor.softwareFmKey);
	}

	private void setSmallIconsFromState(EclipseRepositoryState state, SmallIconPosition eclipsePosition, String eclipseIcon, SmallIconPosition repositoryPosition, String softwareFmIcon) {
		if (filter.contains(eclipsePosition))
			if (state.eclipsePresent)
				setSmallIcon(eclipsePosition, eclipseIcon);
		if (filter.contains(repositoryPosition))
			if (state.repositoryPresent)
				setSmallIcon(repositoryPosition, softwareFmIcon);
	}

	public SourceAndJavadocState getSourceAndJavadocState() {
		return state;
	}

	public void setFilter(Set<SmallIconPosition> newfilter) {
		this.filter = newfilter;
		setSourceAndJavadocState(state);
	}
}
