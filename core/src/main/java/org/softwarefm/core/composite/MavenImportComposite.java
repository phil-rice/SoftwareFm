package org.softwarefm.core.composite;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.labelAndText.Form;
import org.softwarefm.core.labelAndText.IButtonConfig;
import org.softwarefm.core.labelAndText.IButtonConfigurator;
import org.softwarefm.core.labelAndText.IButtonCreator;
import org.softwarefm.core.labelAndText.IFormProblemHandler;
import org.softwarefm.core.labelAndText.IGetTextWithKey;
import org.softwarefm.core.labelAndText.KeyAndProblem;
import org.softwarefm.core.labelAndText.TextAndControlComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class MavenImportComposite extends TextAndControlComposite<Form> {

	Form form;

	public MavenImportComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		setMessage(TextKeys.msgMavenImportTitle);
	}

	@Override
	protected Form makeComponent(SoftwareFmContainer<?> container, Composite parent) {
		IButtonConfigurator buttonConfigurator = new IButtonConfigurator() {
			public void configure(final SoftwareFmContainer<?> container, IButtonCreator creator) {
				creator.createButton(new IButtonConfig() {
					public String key() {
						return TextKeys.btnMavenImportUsePom;
					}

					public void execute() throws Exception {
						form.setEnabledForButton(TextKeys.btnMavenImportUsePom, false);
						ICallback2.Utils.call(container.importPom, form.getText(TextKeys.keyMavenImportPomUrl), container.selectedBindingManager.currentSelectionId());
					}

					public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
						List<KeyAndProblem> result = Lists.newList();
						String url = textWithKey.getText(TextKeys.keyMavenImportPomUrl);
						if (!Strings.isUrlFriendly(url)) {
							String description = IResourceGetter.Utils.getMessageOrException(container.resourceGetter, TextKeys.errorMavenImportIllegalUrl, url);
							result.add(new KeyAndProblem(TextKeys.keyMavenImportPomUrl, description));
						}
						return result;
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
		return form = new Form(getComposite(), SWT.NULL, container, buttonConfigurator, IFormProblemHandler.Utils.buttonTooltipProblemHandler(TextKeys.btnSharedProblems, container.imageRegistry), TextKeys.keyMavenImportPomUrl);
	}

	public static void main(String[] args) {
		Swts.Show.displayFormLayout(MavenImportComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				return new MavenImportComposite(from, SoftwareFmContainer.makeForTests(from.getDisplay())).getComposite();
			}
		});
	}

}
