package org.softwarefm.eclipse.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.Form;
import org.softwarefm.labelAndText.IButtonConfig;
import org.softwarefm.labelAndText.IButtonConfigurator;
import org.softwarefm.labelAndText.IButtonCreator;
import org.softwarefm.labelAndText.IGetTextWithKey;
import org.softwarefm.labelAndText.TextAndControlComposite;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.strings.Strings;

public class ImportFromMavenComposite extends TextAndControlComposite<Form> {

	Form form;

	public ImportFromMavenComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent, container);
		setMessage(SwtConstants.linkFromMavenDescriptionWithNoSelectionText);
	}

	@Override
	protected Form makeComponent(SoftwareFmContainer<?> container, Composite parent) {
		IButtonConfigurator buttonConfigurator = new IButtonConfigurator() {
			public void configure(final SoftwareFmContainer<?> container, IButtonCreator creator) {
				creator.createButton(new IButtonConfig() {
					public String key() {
						return SwtConstants.linkFromMavenButtonText;
					}

					public void execute() throws Exception {
						ICallback.Utils.call(container.importPom, form.getText(SwtConstants.pomUrlKey));
					}

					public boolean canExecute(IGetTextWithKey textWithKey) {
						return Strings.isUrlFriendly(textWithKey.getText(SwtConstants.pomUrlKey));
					}
				});
			}
		};
		return form = new Form(getComposite(), SWT.NULL, container, buttonConfigurator, SwtConstants.pomUrlKey);
	}

	public static void main(String[] args) {
		Swts.Show.display(ImportFromMavenComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				return new ImportFromMavenComposite(from, SoftwareFmContainer.make(ISelectedBindingManager.Utils.noManager(), ICallback.Utils.<String> sysoutCallback())).getComposite();
			}
		});
	}

}
