package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.labelAndText.IButtonConfig;
import org.softwarefm.labelAndText.IButtonConfigurator;
import org.softwarefm.labelAndText.IButtonCreator;
import org.softwarefm.labelAndText.IGetTextWithKey;
import org.softwarefm.labelAndText.KeyAndProblem;
import org.softwarefm.labelAndText.TextAndFormComposite;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

final class ManualImportComposite extends TextAndFormComposite {

	private FileNameAndDigest fileNameAndDigest;
	protected ProjectData projectData;

	ManualImportComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		addListener(new SelectedBindingAdapter() {
			@Override
			public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				projectData = null;
				ManualImportComposite.this.fileNameAndDigest = fileNameAndDigest;
			}

			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				ManualImportComposite.this.projectData = projectData;
				setText(unknownDigestMsg(TextKeys.msgManualImportProjectDetermined, projectData.fileNameAndDigest));
				setText(TextKeys.keyManualImportGroupId, projectData.groupId);
				setText(TextKeys.keyManualImportArtifactId, projectData.artifactId);
				setText(TextKeys.keyManualImportVersion, projectData.version);
			}

			private void clearForm() {
				projectData = null;
				fileNameAndDigest = null;

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
			public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				setText(msg(TextKeys.msgManualImportNotAJar));
				clearForm();
			}

			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				projectData = null;
				setText(TextKeys.keyManualImportGroupId, "<Searching>");
				setText(TextKeys.keyManualImportArtifactId, "<Searching>");
				setText(TextKeys.keyManualImportVersion, "<Searching>");
			}

			@Override
			public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				ManualImportComposite.this.fileNameAndDigest = fileNameAndDigest;
				projectData = null;

				setText(unknownDigestMsg(TextKeys.msgManualImportUnknownDigest, fileNameAndDigest));
				setText(TextKeys.keyManualImportGroupId, "Enter Group Id");
				setText(TextKeys.keyManualImportArtifactId, "Enter Artifact Id");
				setText(TextKeys.keyManualImportVersion, "Enter Version Id");
			}
		});
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
						if (fileNameAndDigest == null || fileNameAndDigest.file == null)
							result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportFileUnknown)));
						else if (fileNameAndDigest.digest == null)
							result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportDigestUnknown, fileNameAndDigest.file)));
						checkOk(result, TextKeys.keyManualImportGroupId);
						checkOk(result, TextKeys.keyManualImportArtifactId);
						checkOk(result, TextKeys.keyManualImportVersion);
						if (projectData != null) {
							boolean groupIdEqual = projectData.groupId.equals(getText(TextKeys.keyManualImportGroupId));
							boolean artifactIdEqual = projectData.artifactId.equals(getText(TextKeys.keyManualImportArtifactId));
							boolean versionEqul = projectData.version.equals(getText(TextKeys.keyManualImportVersion));
							boolean unchanged = groupIdEqual && artifactIdEqual && versionEqul;
							if (unchanged)
								result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorManualImportprojectDataUnchanged)));
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
								ProjectData projectData = new ProjectData(fileNameAndDigest, groupId, artifactId, version);
								setEnabledForButton(TextKeys.btnSharedOk, false);
								ICallback.Utils.call(container.importManually, projectData);
							}
						}.run();
					}

					public String key() {
						return TextKeys.btnSharedOk;
					}
				});
			}
		};
	}
}