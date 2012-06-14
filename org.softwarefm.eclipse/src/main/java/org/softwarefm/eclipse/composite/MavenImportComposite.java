package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.MessageKeys;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.Form;
import org.softwarefm.labelAndText.IButtonConfig;
import org.softwarefm.labelAndText.IButtonConfigurator;
import org.softwarefm.labelAndText.IButtonCreator;
import org.softwarefm.labelAndText.IGetTextWithKey;
import org.softwarefm.labelAndText.KeyAndProblem;
import org.softwarefm.labelAndText.TextAndControlComposite;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class MavenImportComposite extends TextAndControlComposite<Form> {

	Form form;

	public MavenImportComposite(Composite parent, SoftwareFmContainer<?> container, String key) {
		super(parent, container);
		setMessage(key);
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
						form.setEnabledForButton(SwtConstants.linkFromMavenButtonText, false);
						ICallback.Utils.call(container.importPom, form.getText(SwtConstants.pomUrlKey));
					}

					public List<KeyAndProblem> canExecute(IGetTextWithKey textWithKey) {
						List<KeyAndProblem> result = Lists.newList();
						String url = textWithKey.getText(SwtConstants.pomUrlKey);
						if (!Strings.isUrlFriendly(url)) {
							String description = IResourceGetter.Utils.getMessageOrException(container.resourceGetter, MessageKeys.illegalPomUrl, url);
							result.add(new KeyAndProblem(SwtConstants.pomUrlKey, description));
						}
						return result;
					}
				});
			}
		};
		return form = new Form(getComposite(), SWT.NULL, container, buttonConfigurator, SwtConstants.pomUrlKey);
	}

	public static void main(String[] args) {
		Swts.Show.display(MavenImportComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			public Composite apply(Composite from) throws Exception {
				return new MavenImportComposite(from, SoftwareFmContainer.makeForTests(), SwtConstants.linkFromMavenDescriptionWithNoSelectionText).getComposite();
			}
		});
	}

}
