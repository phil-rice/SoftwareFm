package org.softwarefm.core.composite;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.labelAndText.IButtonConfig;
import org.softwarefm.core.labelAndText.IButtonConfigurator;
import org.softwarefm.core.labelAndText.IButtonCreator;
import org.softwarefm.core.labelAndText.IFormProblemHandler;
import org.softwarefm.core.labelAndText.IGetTextWithKey;
import org.softwarefm.core.labelAndText.KeyAndProblem;
import org.softwarefm.core.labelAndText.TextAndFormComposite;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

final class ManualImportComposite extends TextAndFormComposite {

	private FileAndDigest fileAndDigest;
	protected ArtifactData artifactData;

	ManualImportComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		addListener(new SelectedBindingAdapter() {
			@Override
			public void digestDetermined(FileAndDigest fileAndDigest, int selectionCount) {
				artifactData = null;
				ManualImportComposite.this.fileAndDigest = fileAndDigest;
			}

			@Override
			public void artifactDetermined(ArtifactData artifactData, int selectionCount) {
				ManualImportComposite.this.artifactData = artifactData;
				fileAndDigest = artifactData.fileAndDigest;
				setText(unknownDigestMsg(TextKeys.msgManualImportArtifactDetermined, artifactData.fileAndDigest));
				setText(TextKeys.keyManualImportGroupId, artifactData.groupId);
				setText(TextKeys.keyManualImportArtifactId, artifactData.artifactId);
				setText(TextKeys.keyManualImportVersion, artifactData.version);
			}

			private void clearForm() {
				artifactData = null;
				fileAndDigest = null;

				setText(TextKeys.keyManualImportGroupId, "<Not Relevant>");
				setText(TextKeys.keyManualImportArtifactId, "<Not Relevant>");
				setText(TextKeys.keyManualImportVersion, "<Not Relevant>");
			}

			@Override
			public void notJavaElement(int selectionCount) {
				setText(msg(TextKeys.msgManualImportNotJavaElement));
				clearForm();
			}

			@Override
			public void notInAJar(File file, int selectionCount) {
				setText(msg(TextKeys.msgManualImportNotAJar));
				clearForm();
			}

			@Override
			public void codeSelectionOccured(CodeData codeData, int selectionCount) {
				artifactData = null;
				setText(TextKeys.keyManualImportGroupId, "<Searching>");
				setText(TextKeys.keyManualImportArtifactId, "<Searching>");
				setText(TextKeys.keyManualImportVersion, "<Searching>");
			}

			@Override
			public void unknownDigest(FileAndDigest fileAndDigest, int selectionCount) {
				ManualImportComposite.this.fileAndDigest = fileAndDigest;
				artifactData = null;

				setText(unknownDigestMsg(TextKeys.msgManualImportUnknownDigest, fileAndDigest));
				setText(TextKeys.keyManualImportGroupId, "Enter Group Id");
				setText(TextKeys.keyManualImportArtifactId, "Enter Artifact Id");
				setText(TextKeys.keyManualImportVersion, "Enter Version Id");
			}

			public boolean invalid() {
				return getComposite().isDisposed();
			}
			
			@Override
			public String toString() {
				return ManualImportComposite.this.getClass().getSimpleName();
			}
		});
	}
	
	@Override
	protected IFormProblemHandler makeProblemHandler() {
		return IFormProblemHandler.Utils.buttonTooltipProblemHandler(TextKeys.btnSharedProblems, container.imageRegistry);
	}

	@Override
	protected String[] makeKeys() {
		return new String[] { TextKeys.keyManualImportGroupId, TextKeys.keyManualImportArtifactId, TextKeys.keyManualImportVersion };
	}

	@Override
	protected IButtonConfigurator makeButtonConfigurator() {
		return new IButtonConfigurator() {
			public void configure(final SoftwareFmContainer<?> container, IButtonCreator creator) {
				creator.createButton(new IButtonConfig() {
					public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
						List<KeyAndProblem> result = Lists.newList();
						if (fileAndDigest == null || fileAndDigest.file == null)
							result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportFileUnknown)));
						else if (fileAndDigest.digest == null)
							result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportDigestUnknown, fileAndDigest.file)));
						checkOk(result, TextKeys.keyManualImportGroupId);
						checkOk(result, TextKeys.keyManualImportArtifactId);
						checkOk(result, TextKeys.keyManualImportVersion);
						if (artifactData != null) {
							boolean groupIdEqual = artifactData.groupId.equals(getText(TextKeys.keyManualImportGroupId));
							boolean artifactIdEqual = artifactData.artifactId.equals(getText(TextKeys.keyManualImportArtifactId));
							boolean versionEqul = artifactData.version.equals(getText(TextKeys.keyManualImportVersion));
							boolean unchanged = groupIdEqual && artifactIdEqual && versionEqul;
							if (unchanged)
								result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportArtifactDataUnchanged)));
						}
						return result;
					}

					private void checkOk(List<KeyAndProblem> result, String key) {
						String text = getText(key);
						String keyAsName = IResourceGetter.Utils.getOrException(container.resourceGetter, key);
						if (text == null || text.length() == 0) {
							result.add(new KeyAndProblem(key, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorSharedNeedsValue, keyAsName)));
						} else {
							if (!Strings.isIdentifier(text))
								result.add(new KeyAndProblem(key, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportIllegalIdentifier, keyAsName)));
						}
					}

					public void execute() throws Exception {
						new Runnable() {
							public void run() {
								String groupId = getText(TextKeys.keyManualImportGroupId);
								String artifactId = getText(TextKeys.keyManualImportArtifactId);
								String version = getText(TextKeys.keyManualImportVersion);
								ArtifactData artifactData = new ArtifactData(fileAndDigest, groupId, artifactId, version);
								setEnabledForButton(TextKeys.btnSharedOk, false);
								ICallback2.Utils.call(container.importManually, artifactData, container.selectedBindingManager.currentSelectionId());
							}
						}.run();
					}

					public String key() {
						return TextKeys.btnSharedOk;
					}
				});
				creator.createButton(new IButtonConfig() {
					public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
						return Collections.emptyList();
					}
					
					public void execute() throws Exception {
					}
					
					public String key() {
						return TextKeys.btnSharedProblems;
					}
				});
			}
		};
	}
}