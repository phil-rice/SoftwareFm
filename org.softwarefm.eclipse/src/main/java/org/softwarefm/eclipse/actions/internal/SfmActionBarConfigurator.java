package org.softwarefm.eclipse.actions.internal;

import org.softwarefm.eclipse.actions.IActionBar;
import org.softwarefm.eclipse.actions.IActionBarConfigurator;
import org.softwarefm.eclipse.actions.ISfmAction;
import org.softwarefm.eclipse.actions.SfmActionState;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.image.ArtifactsAnchor;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.utilities.resources.IResourceGetter;

public class SfmActionBarConfigurator implements IActionBarConfigurator {

	private final IResourceGetter resourceGetter;
	private final SfmActionState actionState;
	private final ISelectedBindingManager<Object> selectedBindingManager;

	@SuppressWarnings("unchecked")
	public SfmActionBarConfigurator(IResourceGetter resourceGetter, SfmActionState actionState, ISelectedBindingManager<?>selectedBindingManager) {
		super();
		this.resourceGetter = resourceGetter;
		this.actionState = actionState;
		this.selectedBindingManager = (ISelectedBindingManager<Object>) selectedBindingManager;
	}

	public void configure(IActionBar actionBar) {
		actionBar.add(action("stackoverflow", TextKeys.actionStackoverflowTooltip, TextKeys.actionStackoverflowSuffix));
		actionBar.add(action("forum", TextKeys.actionForumflowTooltip, TextKeys.actionForumflowSuffix));
		actionBar.add(action("twitter", TextKeys.actionTwitterTooltip, TextKeys.actionTwitterSuffix));
		actionBar.add(action("facebook", TextKeys.actionFacebookTooltip, TextKeys.actionFacebookSuffix));
		actionBar.add(action("tutorial", TextKeys.actionTutorialTooltip, TextKeys.actionTutorialSuffix));
		actionBar.add(action("article", TextKeys.actionArticleTooltip, TextKeys.actionArticleSuffix));
		actionBar.add(action("blog", TextKeys.actionBlogTooltip, TextKeys.actionBlogSuffix));
		actionBar.add(action("rss", TextKeys.actionRssTooltip, TextKeys.actionRssSuffix));
	}

	ISfmAction action(String item, String tooltipKey, String suffixKey) {
		final String suffix = IResourceGetter.Utils.getOrException(resourceGetter, "action.actionBar." + item + ".suffix");
		return ISfmAction.Utils.action(resourceGetter, ArtifactsAnchor.class, "sfm.icons.project."+item + ".16.png", "action.actionBar." + item + ".tooltip", new Runnable() {
			public void run() {
				actionState.setUrlSuffix(suffix);
				selectedBindingManager.reselect(selectedBindingManager.currentSelectionId());
			}
		});
	}

}
