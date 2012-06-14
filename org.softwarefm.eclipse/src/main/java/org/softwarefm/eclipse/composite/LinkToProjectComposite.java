package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.MessageKeys;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.IButtonConfig;
import org.softwarefm.labelAndText.IButtonConfigurator;
import org.softwarefm.labelAndText.IButtonCreator;
import org.softwarefm.labelAndText.IGetTextWithKey;
import org.softwarefm.labelAndText.KeyAndProblem;
import org.softwarefm.labelAndText.TextAndControlComposite;
import org.softwarefm.labelAndText.TextAndFormComposite;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class LinkToProjectComposite extends TextAndControlComposite<TabFolder> {

	private FileNameAndDigest fileNameAndDigest;
	private ProjectData projectData;
	private TextAndFormComposite manualImport;
	private MavenImportComposite mavenImport;

	@Override
	protected TabFolder makeComponent(SoftwareFmContainer<?> container, Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NULL);
		mavenImport = new MavenImportComposite(tabFolder, container, MessageKeys.unrecognisedDigestForMavenImport);
		manualImport = new TextAndFormComposite(tabFolder, container) {

			@Override
			protected String[] makeKeys() {
				return new String[] { SwtConstants.groupIdKey, SwtConstants.artifactIdKey, SwtConstants.versionKey };
			}

			@Override
			protected IButtonConfigurator makeButtonConfigurator() {
				return new IButtonConfigurator() {
					public void configure(final SoftwareFmContainer<?> container, IButtonCreator creator) {
						creator.createButton(new IButtonConfig() {
							public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
								List<KeyAndProblem> result = Lists.newList();
								if (fileNameAndDigest == null || fileNameAndDigest.file == null)
									result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.fileUnknown)));
								else if (fileNameAndDigest.digest == null)
									result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.digestUnknown, fileNameAndDigest.file)));
								checkOk(result, SwtConstants.groupIdKey);
								checkOk(result, SwtConstants.artifactIdKey);
								checkOk(result, SwtConstants.versionKey);
								if (projectData != null) {
									boolean groupIdEqual = projectData.groupId.equals(getText(SwtConstants.groupIdKey));
									boolean artifactIdEqual = projectData.artifactId.equals(getText(SwtConstants.artifactIdKey));
									boolean versionEqul = projectData.version.equals(getText(SwtConstants.versionKey));
									boolean unchanged = groupIdEqual && artifactIdEqual && versionEqul;
									if (unchanged)
										result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.notChangedProjectData)));
								}
								return result;
							}

							private void checkOk(List<KeyAndProblem> result, String key) {
								String text = getText(key);
								String keyAsName = IResourceGetter.Utils.getOrException(container.resourceGetter, key);
								if (text == null || text.length() == 0) {
									result.add(new KeyAndProblem(key, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.needsValue, keyAsName)));
								} else {
									if (!Strings.isIdentifier(text))
										result.add(new KeyAndProblem(key, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.illegalIdentifier, keyAsName)));
								}
							}

							public void execute() throws Exception {
								new Runnable() {
									public void run() {
										String groupId = getText(SwtConstants.groupIdKey);
										String artifactId = getText(SwtConstants.artifactIdKey);
										String version = getText(SwtConstants.versionKey);
										ProjectData projectData = new ProjectData(fileNameAndDigest, groupId, artifactId, version);
										setEnabledForButton(SwtConstants.okButton, false);
										ICallback.Utils.call(container.importManually, projectData);
									}
								}.run();
							}

							public String key() {
								return SwtConstants.okButton;
							}
						});
					}
				};
			}
		};

		TabItem mavenImportTabItem = new TabItem(tabFolder, SWT.NULL);
		mavenImportTabItem.setText("Maven Import");
		mavenImportTabItem.setControl(mavenImport.getControl());

		TabItem manualImportTabItem = new TabItem(tabFolder, SWT.NULL);
		manualImportTabItem.setText("Manual Import");
		manualImportTabItem.setControl(manualImport.getControl());
		return tabFolder;
	}

	private void setText(String key, String text) {
		manualImport.setText(key, text);
	}

	public LinkToProjectComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		addListener(new SelectedBindingAdapter() {
			@Override
			public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				projectData = null;
				LinkToProjectComposite.this.fileNameAndDigest = fileNameAndDigest;
			}

			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				LinkToProjectComposite.this.projectData = projectData;
				setText(SwtConstants.groupIdKey, projectData.groupId);
				setText(SwtConstants.artifactIdKey, projectData.artifactId);
				setText(SwtConstants.versionKey, projectData.version);
			}

			private void clearForm() {
				projectData = null;
				setText(SwtConstants.groupIdKey, "<Not Relevant>");
				setText(SwtConstants.artifactIdKey, "<Not Relevant>");
				setText(SwtConstants.versionKey, "<Not Relevant>");
			}

			@Override
			public void notJavaElement(int selectionCount) {
				clearForm();
			}

			@Override
			public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				clearForm();
			}

			@Override
			public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
				projectData = null;
				setText(SwtConstants.groupIdKey, "<Searching>");
				setText(SwtConstants.artifactIdKey, "<Searching>");
				setText(SwtConstants.versionKey, "<Searching>");
			}

			@Override
			public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				projectData = null;
				setText(unknownDigestMsg(fileNameAndDigest));
				mavenImport.setText(unknownDigestMsg(MessageKeys.unrecognisedDigestForMavenImport, fileNameAndDigest));
				manualImport.setText(unknownDigestMsg(MessageKeys.unrecognisedDigestForManualImport, fileNameAndDigest));
				LinkToProjectComposite.this.fileNameAndDigest = fileNameAndDigest;
				setText(SwtConstants.groupIdKey, "Enter Group Id");
				setText(SwtConstants.artifactIdKey, "Enter Artifact Id");
				setText(SwtConstants.versionKey, "Enter Version Id");
			}
		});
	}

	public static void main(String[] args) {
		Swts.Show.display(LinkToProjectComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests();
				return new LinkToProjectComposite(from, container).getComposite();
			}
		});
	}

}
