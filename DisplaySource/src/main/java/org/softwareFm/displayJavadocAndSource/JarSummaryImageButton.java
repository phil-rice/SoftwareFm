package org.softwareFm.displayJavadocAndSource;

import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.utilities.resources.IResourceGetter;

public class JarSummaryImageButton extends ImageButton {
	private Set<SmallIconPosition> filter;
	private SourceAndJavadocState state;

	public JarSummaryImageButton(Composite parent, ImageRegistry imageRegistry, IResourceGetter resourceGetter, String key, Set<SmallIconPosition> filter, final boolean toggle) {
		super(parent, imageRegistry, key, toggle);
		this.filter = filter;
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
