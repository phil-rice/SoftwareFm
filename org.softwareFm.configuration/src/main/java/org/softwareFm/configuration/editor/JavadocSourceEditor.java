package org.softwareFm.configuration.editor;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.AbstractTitleAndText;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndStyledText;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutator;
import org.softwareFm.jdtBinding.api.IJavadocSourceMutatorCallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceEditor implements IEditor {
	private IEditorCompletion completion;
	private Composite content;
	private TitleAndStyledText txtEclipse;
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
	private Text txtText;
	private final JavadocSourceUrlTester urlTester = new JavadocSourceUrlTester();
	private final String keyWhenSaving;

	public JavadocSourceEditor(String urlTitle, String softwareFmKey, String eclipseKey, String mutatorKey, String keyWhenSaving) {
		this.urlTitle = urlTitle;
		this.eclipseKey = eclipseKey;
		this.softwareFmKey = softwareFmKey;
		this.mutatorKey = mutatorKey;
		this.keyWhenSaving = keyWhenSaving;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Control createControl(ActionContext actionContext) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());

		CompositeConfig config = actionContext.compositeConfig;

		int height = 2 * config.layout.textHeight;
		txtUrl = new TitleAndStyledText(config, content, urlTitle, true, height);
		txtEclipse = new TitleAndStyledText(config, content, "dialog.eclipseValue.title", true, height);
		txtEclipse.setEditable(false);
		txtEclipse.setBackground(content.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		// addCopyToEclipseButton(config, dataGetter, txtExperiment, true);
		// addCopyToSoftwareFmButton(config, actionContext, txtExperiment, true);

		txtText = new Text(content, SWT.NULL);
		txtText.setForeground(content.getDisplay().getSystemColor(SWT.COLOR_RED));
		txtText.setEditable(false);

		buttonParent = new ButtonParent(content, config, SWT.NULL);
		Composite buttonPanel = new Composite(content, SWT.NULL);
		// new SimpleImageButton(parent, config, showBackground);
		buttonPanel.setLayout(new GridLayout(3, true));
		helpText = Swts.makeHelpDisplayer(content);
		Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(content, actionContext.compositeConfig.layout.dataMargin);
		return content;
	}

	@Override
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, final ActionContext actionContext, IEditorCompletion completion) {
		try {
			this.completion = completion;
			txtText.setText("");
			if (modifyListener != null)
				txtUrl.removeModifyListener(modifyListener);
			IDataGetter dataGetter = actionContext.dataGetter;
			setOriginalEclipseValue(Strings.nullSafeToString(dataGetter.getDataFor(eclipseKey)));
			setOriginalSoftwareFmValue(Strings.nullSafeToString(dataGetter.getDataFor(softwareFmKey)));
			final IJavadocSourceMutator mutator = (IJavadocSourceMutator) dataGetter.getDataFor(mutatorKey);
			if (mutator == null)
				throw new IllegalStateException(MessageFormat.format(DisplayConstants.mustHaveA, mutatorKey, dataGetter));
			txtUrl.setText(originalSoftwareFmValue);
			final String entity = ConfigurationConstants.entityForJavadocSource;
			final String url = actionContext.entityToUrlGetter.apply(entity);
			javadocSourceButtons = new JavadocSourceButtons(buttonParent, actionContext.compositeConfig, new Runnable() {
				@Override
				public void run() {
					cancel();
				}
			}, new Runnable() {
				@Override
				public void run() {
					try {
						txtText.setText(IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, ConfigurationConstants.settingEclipseValue));
						String eclipseValue = txtUrl.getText();
						mutator.setNewValue(eclipseValue, new IJavadocSourceMutatorCallback() {
							@Override
							public void process(String requested, final String actual) {
								Swts.asyncExec(txtEclipse, new Runnable() {
									@Override
									public void run() {
										txtEclipse.setText(actual);
										setOriginalEclipseValue(actual);
										setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
										txtText.setText(IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, ConfigurationConstants.setEclipseValue));
									}
								});
							}

						});
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}
			}, new Runnable() {
				@Override
				public void run() {
					try {
						actionContext.updateStore.update(entity, url, Maps.<String, Object> makeMap(keyWhenSaving, txtUrl.getText()));
						setOriginalSoftwareFmValue(txtUrl.getText());
						setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}
			}, new Runnable() {
				@Override
				public void run() {
					setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
					setOriginalSoftwareFmValue(originalEclipseValue);
					txtUrl.setText(originalSoftwareFmValue);
					setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);

				}
			}, new Runnable() {
				@Override
				public void run() {
					try {
						mutator.setNewValue("", IJavadocSourceMutatorCallback.Utils.blank());
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
					cancel();
				}
			});

			txtEclipse.setText(originalEclipseValue);

			modifyListener = new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					textModified();
				}

			};
			txtUrl.addModifyListener(modifyListener);
			Swts.setHelpText(helpText, actionContext.compositeConfig.resourceGetter, displayerDefn.helpKey);
			textModified();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void setButtonValuesAfterMutate(final String originalEclipseValue, final String originalSoftwareFmValue) {
		boolean hasSomeValue = txtUrl.getText().length() > 0;
		boolean changedFromSfm = !originalSoftwareFmValue.equals(txtUrl.getText());
		boolean changedFromEclipse = !originalEclipseValue.equals(txtUrl.getText());
		boolean noError = txtText.getText().length() == 0;
		boolean copyToEclipse = noError & hasSomeValue & changedFromEclipse;
		boolean copyToSoftwareFm = noError & hasSomeValue & changedFromSfm;// | originalEclipseValue.endsWith(".html");
		boolean copyEclipseToSoftwareFm = originalEclipseValue.endsWith(".html") && originalSoftwareFmValue.length() == 0;
		javadocSourceButtons.setCopyButtonsEnabled(copyToEclipse, copyToSoftwareFm, copyEclipseToSoftwareFm);
	}

	private void textModified() {
		txtText.setText(Strings.nullSafeToString(urlTester.apply(txtUrl.getText())));
		setButtonValuesAfterMutate(originalEclipseValue, originalSoftwareFmValue);
	}

	private void cancel() {
		completion.cancel();
	}

	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}

	public TitleAndStyledText getTxtEclipse() {
		return txtEclipse;
	}

	public AbstractTitleAndText<?> getTxtUrl() {
		return txtUrl;
	}

	public JavadocSourceButtons getJavadocSourceButtons() {
		return javadocSourceButtons;
	}

	private void setOriginalEclipseValue(String originalEclipseValue) {
		this.originalEclipseValue = originalEclipseValue;
		txtEclipse.setText(originalEclipseValue);
	}

	private void setOriginalSoftwareFmValue(String originalSoftwareFmValue) {
		this.originalSoftwareFmValue = originalSoftwareFmValue;
	}
}
