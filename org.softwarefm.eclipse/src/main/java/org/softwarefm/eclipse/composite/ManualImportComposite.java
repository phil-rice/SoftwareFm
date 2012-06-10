package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
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
import org.softwarefm.labelAndText.TextAndFormComposite;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class ManualImportComposite extends TextAndFormComposite {

	private FileNameAndDigest fileNameAndDigest;
	private ProjectData projectData;

	public ManualImportComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		addListener(new SelectedBindingAdapter() {
			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				setText(SwtConstants.groupIdKey, projectData.groupId);
				setText(SwtConstants.artifactIdKey, projectData.artifactId);
				setText(SwtConstants.versionKey, projectData.version);
			}
		});
	}

	@Override
	protected IButtonConfigurator makeButtonConfigurator() {
		return new IButtonConfigurator() {
			public void configure(final SoftwareFmContainer<?> container, IButtonCreator creator) {
				creator.createButton(new IButtonConfig() {
					public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
						List<KeyAndProblem> result = Lists.newList();
						if (fileNameAndDigest == null || fileNameAndDigest.fileName == null)
							result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.fileUnknown)));
						else if (fileNameAndDigest.digest == null)
							result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.digestUnknown, fileNameAndDigest.fileName)));
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

	@Override
	protected String[] makeKeys() {
		return new String[] { SwtConstants.groupIdKey, SwtConstants.artifactIdKey, SwtConstants.versionKey };
	}

	@Override
	public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		projectData = null;
		this.fileNameAndDigest = fileNameAndDigest;
		killLastLineAndappendText(unknownDigestMsg(fileNameAndDigest));
		setText(SwtConstants.groupIdKey, "Enter Group Id");
		setText(SwtConstants.artifactIdKey, "Enter Artifact Id");
		setText(SwtConstants.versionKey, "Enter Version Id");
	}

	@Override
	public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		projectData = null;
		this.fileNameAndDigest = fileNameAndDigest;
		killLastLineAndappendText(digestDeterminedMsg(fileNameAndDigest) + "\n" + searchingMsg());
	}

	@Override
	public void projectDetermined(ProjectData projectData, int selectionCount) {
		this.projectData = projectData;
		killLastLineAndappendText(projectDeterminedMsg(projectData) + "\nIf you think this is linked to the wrong data, follow <these instructions>");
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
	protected void notJavaElement(int selectionCount) {
		projectData = null;
		killLastLineAndappendText(notJavaElementMsg());
		clearForm();
	}

	@Override
	public void notInAJar(FileNameAndDigest fileNameAndDigest, int selectionCount) {
		projectData = null;
		killLastLineAndappendText(notInAJarMsg(fileNameAndDigest));
	}

	@Override
	public void classAndMethodSelectionOccured(ExpressionData expressionData, int selectionCount) {
		projectData = null;
		setText(SwtConstants.groupIdKey, "<Searching>");
		setText(SwtConstants.artifactIdKey, "<Searching>");
		setText(SwtConstants.versionKey, "<Searching>");
		setText(searchingMsg());
	}

	public static void main(String[] args) {
		Swts.Show.display(ManualImportComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests();
				return new ManualImportComposite(from, container).getComposite();
			}
		});
	}
}
