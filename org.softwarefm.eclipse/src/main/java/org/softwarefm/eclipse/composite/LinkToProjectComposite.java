package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.MessageKeys;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.SelectedBindingAdapter;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.Form;
import org.softwarefm.labelAndText.IButtonConfig;
import org.softwarefm.labelAndText.IButtonConfigurator;
import org.softwarefm.labelAndText.IButtonCreator;
import org.softwarefm.labelAndText.IGetTextWithKey;
import org.softwarefm.labelAndText.KeyAndProblem;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class LinkToProjectComposite extends SoftwareFmComposite {

	private FileNameAndDigest fileNameAndDigest;
	private ProjectData projectData;
	private final Form form;

	public LinkToProjectComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		form = new Form(getComposite(), SWT.NULL, container, makeButtonConfigurator(), makeKeys());
		setLayout(new FillLayout());
		addListener(new SelectedBindingAdapter() {
			@Override
			public void digestDetermined(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				projectData = null;
				LinkToProjectComposite.this.fileNameAndDigest = fileNameAndDigest;
			}

			@Override
			public void projectDetermined(ProjectData projectData, int selectionCount) {
				LinkToProjectComposite.this.projectData = projectData;
				form.setText(SwtConstants.groupIdKey, projectData.groupId);
				form.setText(SwtConstants.artifactIdKey, projectData.artifactId);
				form.setText(SwtConstants.versionKey, projectData.version);
			}

			private void clearForm() {
				projectData = null;
				form.setText(SwtConstants.groupIdKey, "<Not Relevant>");
				form.setText(SwtConstants.artifactIdKey, "<Not Relevant>");
				form.setText(SwtConstants.versionKey, "<Not Relevant>");
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
				form.setText(SwtConstants.groupIdKey, "<Searching>");
				form.setText(SwtConstants.artifactIdKey, "<Searching>");
				form.setText(SwtConstants.versionKey, "<Searching>");
			}

			@Override
			public void unknownDigest(FileNameAndDigest fileNameAndDigest, int selectionCount) {
				projectData = null;
				LinkToProjectComposite.this.fileNameAndDigest = fileNameAndDigest;
				form.setText(SwtConstants.groupIdKey, "Enter Group Id");
				form.setText(SwtConstants.artifactIdKey, "Enter Artifact Id");
				form.setText(SwtConstants.versionKey, "Enter Version Id");
			}
		});
	}

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
							boolean groupIdEqual = projectData.groupId.equals(form.getText(SwtConstants.groupIdKey));
							boolean artifactIdEqual = projectData.artifactId.equals(form.getText(SwtConstants.artifactIdKey));
							boolean versionEqul = projectData.version.equals(form.getText(SwtConstants.versionKey));
							boolean unchanged = groupIdEqual && artifactIdEqual && versionEqul;
							if (unchanged)
								result.add(new KeyAndProblem(null, IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.notChangedProjectData)));
						}
						return result;
					}

					private void checkOk(List<KeyAndProblem> result, String key) {
						if (form == null)
							return;
						String text = form.getText(key);
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
								String groupId = form.getText(SwtConstants.groupIdKey);
								String artifactId = form.getText(SwtConstants.artifactIdKey);
								String version = form.getText(SwtConstants.versionKey);
								ProjectData projectData = new ProjectData(fileNameAndDigest, groupId, artifactId, version);
								form.setEnabledForButton(SwtConstants.okButton, false);
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

	protected String[] makeKeys() {
		return new String[] { SwtConstants.groupIdKey, SwtConstants.artifactIdKey, SwtConstants.versionKey };
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
