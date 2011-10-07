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

public class ArtifactSocialLargeButtonFactory extends LargeButtonFactory {

	@Override
	public LargeButtonDefn apply(GuiBuilder guiBuilder) throws Exception {
		return guiBuilder.largeButton("largeButton.artifact.social", //
				makeSfmSmallButton(guiBuilder, "smallButton.artifact.social.details", "smallButton.jar.identifiers.title"),//

				guiBuilder.smallButton("smallButton.artifact.rss", "smallButton.artifact.rss.title", "smallButton.data", ArtifactsAnchor.rssKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.rss").guard(dataJarArtifactId, blankKey).title(artifactRssAdd).data(dataArtifactRss).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.rssKey, dataArtifactRss)).//
								listActions(guiBuilder.action("action.rss.browse", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.rssKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.rssKey))).data(dataArtifactRss),//

				guiBuilder.smallButton("smallButton.artifact.twitter", "smallButton.artifact.twitter.title", "smallButton.data", ArtifactsAnchor.twitterKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.tweet").guard(dataJarArtifactId, blankKey).title(artifactTwitterAdd).data(dataArtifactTweets).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.twitterKey, dataArtifactTweets)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.twitterKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.twitterKey))).data(dataArtifactTweets),//

				guiBuilder.smallButton("smallButton.artifact.facebook", "smallButton.artifact.facebook.title", "smallButton.data", ArtifactsAnchor.facebookKey,//
						guiBuilder.listDisplayer("displayer.list", "listEditorId.facebook").guard(dataJarArtifactId, blankKey).title(artifactFacebookAdd).data(dataArtifactFacebook).//
								actions(makeMainListActions(guiBuilder, ArtifactsAnchor.facebookKey, dataArtifactFacebook)).//
								listActions(guiBuilder.action("action.list.viewTweets", GeneralAnchor.browseKey).tooltip("action.browse.tooltip"), //
										listEditAction(guiBuilder, ArtifactsAnchor.facebookKey),//
										listDeleteAction(guiBuilder, ArtifactsAnchor.facebookKey))).data(dataArtifactFacebook));
	}

	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");
		Swts.xUnit(ArtifactSocialLargeButtonFactory.class.getSimpleName(), root, "json", new SoftwareFmViewUnit.SoftwareFmViewUnitBuilder(new IFunction1<SoftwareFmFixture, LargeButtonDefn[]>() {
			@Override
			public LargeButtonDefn[] apply(SoftwareFmFixture from) throws Exception {
				return new LargeButtonDefn[] { from.artifactSocialButton };
			}
		}));
	}
}
