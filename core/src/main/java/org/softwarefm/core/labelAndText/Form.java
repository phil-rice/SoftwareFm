package org.softwarefm.core.labelAndText;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.ImageConstants;
import org.softwarefm.core.constants.SwtErrorMessages;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.labelAndText.LabelAndText.LabelAndTextComposite;
import org.softwarefm.core.labelAndText.LabelAndText.LabelAndTextHolderLayout;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.resources.ResourceGetterMock;
import org.softwarefm.utilities.runnable.Runnables;

public class Form extends Composite implements IGetTextWithKey {
	private final List<String> keys;
	private final ButtonComposite buttonComposite;
	private final IFormProblemHandler globalProblemHandler;


	
	
	public Form(Composite parent, int style, final SoftwareFmContainer<?> container, IButtonConfigurator buttonConfigurator, IFormProblemHandler globalProblemHandler, String... keys) {
		super(parent, style);
		this.globalProblemHandler = globalProblemHandler;
		this.keys = Lists.immutableCopy(keys);
		setLayout(new LabelAndTextHolderLayout());
		IResourceGetter resourceGetter = container.resourceGetter;
		for (String key : keys)
			new LabelAndText(this, container.imageRegistry, IResourceGetter.Utils.getOrException(resourceGetter, key)).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					updateButtonStatus();
				}
			});
		buttonComposite = new ButtonComposite(this);
		buttonConfigurator.configure(container, IButtonCreator.Utils.creator(buttonComposite.getComposite(), resourceGetter));
		updateButtonStatus();
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	public void setEnabledForButton(String key, boolean enabled) {
		buttonComposite.setEnabledForButton(key, enabled);
	}

	public void setText(String key, String text) {
		LabelAndTextComposite labelAndTextComposite = getLabelAndTextFor(key);
		labelAndTextComposite.text.setText(text);
	}

	public Control getButton(String key) {
		return buttonComposite.getButton(key);
	}

	private LabelAndTextComposite getLabelAndTextFor(String key) {
		int index = keys.indexOf(key);
		if (index == -1)
			throw new IllegalArgumentException(MessageFormat.format(SwtErrorMessages.unrecognisedKey, key, keys));
		LabelAndTextComposite labelAndTextComposite = (LabelAndTextComposite) getChildren()[index];
		return labelAndTextComposite;
	}

	void updateButtonStatus() {
		List<KeyAndProblem> problems = buttonComposite.updateButtonStatus(this);
		globalProblemHandler.handleAllProblems(buttonComposite, problems);
		Map<String, List<String>> map = Maps.newMap();
		for (KeyAndProblem problem : problems)
			Maps.addToList(map, problem.key, problem.problem);

		for (String key : keys) {
			List<String> problemsForKey = map.get(key);
			LabelAndTextComposite labelAndText = getLabelAndTextFor(key);
			labelAndText.setProblems(Lists.nullSafe(problemsForKey));
		}
		List<String> globalProblems = map.get(null);
		globalProblemHandler.handleGlobalProblems(buttonComposite, Lists.nullSafe(globalProblems));

	}

	public List<String> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	public static void main(String[] args) {
		Swts.Show.display(Form.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			public Composite apply(Composite from) throws Exception {
				IResourceGetter resourceGetter = new ResourceGetterMock(//
						TextKeys.btnSharedOk, "OK", TextKeys.btnSharedCancel, "Cancel",//
						"one", "One", "two", "Two", "three", "Three", "four", "Four", "five", "Five", "six", "Six", "seven", "Seven");
				SoftwareFmContainer<?> container = SoftwareFmContainer.makeForTests(from.getDisplay(),resourceGetter);
				ImageRegistry imageRegistry = new ImageRegistry(from.getDisplay());
				ImageConstants.initializeImageRegistry(from.getDisplay(), imageRegistry);
				Form form = new Form(from, SWT.BORDER, container, IButtonConfigurator.Utils.okCancel(Runnables.sysout("ok"), Runnables.sysout("cancel")), IFormProblemHandler.Utils.sysoutHandler(), "one", "two", "three", "four", "five", "six", "seven");
				return form;
			}
		});
	}

	public String getText(String key) {
		return getLabelAndTextFor(key).text.getText();
	}

}
