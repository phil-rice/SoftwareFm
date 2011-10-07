package org.softwareFm.configuration.largebuttons;

import java.io.File;

import org.softwareFm.configuration.configurators.LargeButtonFactory;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.GuiBuilder;
import org.softwareFm.display.largeButton.LargeButtonDefn;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.eclipse.sample.SoftwareFmViewUnit;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.utilities.functions.IFunction1;

public class GroupLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.group", //
	
				makeSfmSmallButton(guiBuilder,"smallButton.data", GeneralAnchor.sfmKey, "smallButton.group.details", "smallButton.group.details.title", false),//
				
				guiBuilder.smallButton("smallButton.group.rss", "smallButton.project.rss.title", "smallButton.data", ArtifactsAnchor.rssKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.rss").guard(dataJarGroupId, blankKey).title(groupRssAdd).data(dataGroupRss).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.rssKey, dataGroupRss)).//
								listActions(guiBuilder.action("action.rss.browse", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.rssKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.rssKey))).data(dataGroupRss), //
										
				guiBuilder.smallButton("smallButton.group.twitter", "smallButton.group.twitter.title", "smallButton.data", ArtifactsAnchor.twitterKey, //
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataGroupTweets, blankKey).title(groupTwitterAdd).data(dataGroupTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataGroupTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).params(dataGroupTweets).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))).data(dataGroupTweets));
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit(GroupLargeButtonFactory.class.getSimpleName(), root, "json", new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.groupLargeButtonFactory };
			}
		}));
	}
}
