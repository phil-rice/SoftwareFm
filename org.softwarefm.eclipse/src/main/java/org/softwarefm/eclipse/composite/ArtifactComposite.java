package org.softwarefm.eclipse.composite;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.constants.UrlConstants;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;

public class ArtifactComposite extends StackedBrowserAndControl<LinkComposite> {

	public ArtifactComposite(Composite parent, final SoftwareFmContainer<?> container) {
		super(parent, container, new IFunction1<Composite, LinkComposite>() {
			public LinkComposite apply(Composite from) throws Exception {
				return new LinkComposite(from, container);
			}
		});
		setUrlAndShow(UrlConstants.aboutArtifactComposite);
		addListener(new ISelectedBindingListener() {
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
			}

			public void notJavaElement(int selectionCount) {
				setUrlAndShow(UrlConstants.notJavaElementUrl);
			}

			public void notInAJar(File file, int selectionCount) {
				setUrlAndShow(UrlConstants.notJarUrl);
			}

			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				setText(digestDeterminedMsg(TextKeys.msgArtifactFoundDigest, fileAndDigest) + "\n" + searchingMsg());
			}

			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				showSecondaryControl();
			}

			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				String url = urlStrategy.projectUrl(artifactData).getHostAndUrl();
				setUrlAndShow(url);
				container.state.setUrlSuffix(null);
			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}

			@Override
			public String toString() {
				ArtifactComposite artifactComposite = ArtifactComposite.this;
				return artifactComposite.getClass().getSimpleName() + "@" + System.identityHashCode(artifactComposite) + " Url/text: " + getTextOrUrl();
			}
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " (" + getTextOrUrl() + ") " + getBrowserForTest();
	}

	public static void main(String[] args) {
		Swts.Show.display(ArtifactComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				ArtifactComposite artifactComposite = new ArtifactComposite(from, SoftwareFmContainer.makeForTests(from.getDisplay()));
				artifactComposite.setUrlAndShow(UrlConstants.notJarUrl);
				return artifactComposite.getComposite();
			}
		});
	}

}
