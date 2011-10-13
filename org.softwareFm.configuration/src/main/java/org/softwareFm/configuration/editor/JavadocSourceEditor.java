package org.softwareFm.configuration.editor;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndStyledText;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.Editors;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutator;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutatorCallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceEditor implements IEditor {
	private IEditorCompletion completion;
	private Composite content;
	private TitleAndText txtEclipse;
	private final String eclipseKey;
	private final String softwareFmKey;
	private final String mutatorKey;
	private ButtonParent buttonParent;
	private StyledText helpText;
	private ModifyListener modifyListener;
	private JavadocSourceButtons javadocSourceButtons;
	private TitleAndStyledText txtUrl;
	private final String urlTitle;
	private String originalEclipseValue;
	private String originalSoftwareFmValue;

	public JavadocSourceEditor(String urlTitle, String softwareFmKey, String eclipseKey, String mutatorKey) {
		this.urlTitle = urlTitle;
		this.eclipseKey = eclipseKey;
		this.softwareFmKey = softwareFmKey;
		this.mutatorKey = mutatorKey;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Control createControl(ActionContext actionContext) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());

		CompositeConfig config = actionContext.compositeConfig;

		txtUrl = new TitleAndStyledText(config, content, urlTitle, true, 2 * config.layout.textHeight);
		txtEclipse = new TitleAndText(config, content, "dialog.eclipseValue.title", true);
		txtEclipse.setEditable(false);

		buttonParent = new ButtonParent(content, config, SWT.NULL);
		// addCopyToEclipseButton(config, dataGetter, txtExperiment, true);
		// addCopyToSoftwareFmButton(config, actionContext, txtExperiment, true);

		Composite buttonPanel = new Composite(content, SWT.NULL);
		// new SimpleImageButton(parent, config, showBackground);
		buttonPanel.setLayout(new GridLayout(3, true));
		helpText = Swts.makeHelpDisplayer(content);
		Swts.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(content, actionContext.compositeConfig.layout.dataMargin);
		return content;
	}

	@Override
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, ActionContext actionContext, IEditorCompletion completion) {
		this.completion = completion;
		if (modifyListener != null)
			txtUrl.removeModifyListener(modifyListener);
		IDataGetter dataGetter = actionContext.dataGetter;
		originalEclipseValue = Strings.nullSafeToString(dataGetter.getDataFor(eclipseKey));
		originalSoftwareFmValue = Strings.nullSafeToString(dataGetter.getDataFor(softwareFmKey));
		final IJavadocSourceMutator mutator = (IJavadocSourceMutator) dataGetter.getDataFor(mutatorKey);
		if (mutator == null)
			throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, mutatorKey, dataGetter));
		txtUrl.setText(originalSoftwareFmValue);
		javadocSourceButtons = new JavadocSourceButtons(buttonParent, actionContext.compositeConfig, new Runnable() {
			@Override
			public void run() {
				cancel();
			}
		}, new ITester() {
			@Override
			public String test() {
				return null;
			}

			@Override
			public void processResults(String result) {
				boolean resultOk = result == null;
				javadocSourceButtons.setCopyButtonsEnabled(resultOk & true, resultOk & !originalSoftwareFmValue.equals(txtUrl.getText()));
			}

		}, new Runnable() {
			@Override
			public void run() {
				try {
					String eclipseValue = txtUrl.getText();
					mutator.setNewValue(eclipseValue, new IJavadocSourceMutatorCallback() {
						@Override
						public void process(String requested, String actual) {
							txtEclipse.setText(actual);
							originalEclipseValue = actual;
							setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
						}

					});
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		}, new Runnable() {
			@Override
			public void run() {
				setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
			}
		});

		txtEclipse.setText(originalEclipseValue);

		modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
			}
		};
		txtUrl.addModifyListener(modifyListener);
		Swts.setHelpText(helpText, actionContext.compositeConfig.resourceGetter, displayerDefn.helpKey);
		setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
	}

	private void setButtonValuesAfterMutate(final String originalEclipseValue, final String originalSoftwareFmValue) {
		boolean hasSomeValue = txtUrl.getText().length() > 0;
		boolean changedFromSfm = !originalSoftwareFmValue.equals(txtUrl.getText());
		boolean changedFromEclipse = !originalEclipseValue.equals(txtUrl.getText());
		javadocSourceButtons.setCopyButtonsEnabled(hasSomeValue & changedFromEclipse, hasSomeValue & changedFromSfm);
	}

	private void cancel() {
		completion.cancel();
	}

	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}

	public TitleAndText getTxtEclipse() {
		return txtEclipse;
	}

	public AbstractTitleAndText<?> getTxtUrl() {
		return txtUrl;
	}

	public JavadocSourceButtons getJavadocSourceButtons() {
		return javadocSourceButtons;
	}

	public static void main(String[] args) {
		Editors.display(JavadocSourceEditor.class.getSimpleName(),//
				new JavadocSourceEditor(ConfigurationConstants.urlJavadocTitle, ConfigurationConstants.dataArtifactJavadoc, ConfigurationConstants.dataRawJavadoc, ConfigurationConstants.dataRawJavadocMutator), //
				ConfigurationConstants.dataRawJavadoc, "rawJavadoc",//
				ConfigurationConstants.dataArtifactJavadoc, "SfmJavadoc");
	}
}
