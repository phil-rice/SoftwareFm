package org.arc4eclipse.displayLists;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.DisplayerDetails;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.panel.ISelectedBindingManager;
import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.swtBasics.text.IButtonParent;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.maps.Maps;
import org.arc4eclipse.utilities.resources.IResourceGetter;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ListPanel extends Composite implements IButtonParent {

	private final class DeleteButtonListener implements IImageButtonListener {
		private final int index;

		public DeleteButtonListener(int index) {
			this.index = index;
		}

		@Override
		public void buttonPressed(ImageButton button) {
			listModel.delete(index);
			sendDataToServer();
		}
	}

	private final class EditButtonListener implements IImageButtonListener {
		private final int index;

		public EditButtonListener(int index) {
			this.index = index;

		}

		@Override
		public void buttonPressed(ImageButton button) {
			ConfigForTitleAnd forDialogs = ConfigForTitleAnd.createForDialogs(getDisplay(), context.resourceGetter, context.imageRegistry);
			NameAndValueDialog dialog = new NameAndValueDialog(getShell(), SWT.NULL, forDialogs, nameTitle, valueTitle);
			NameAndValue result = dialog.open(listModel.get(index));
			if (result != null) {
				listModel.set(index, result.name, result.url);
				sendDataToServer();
			}
		}
	}

	private final DisplayerContext context;
	private final Composite compForList;
	private final String title;
	private final ListModel listModel;
	private final IArc4EclipseRepository repository;
	private String url;
	private final IEncodeDecodeNameAndUrl encoder;
	private final String entity;
	private final String nameTitle;
	private final String valueTitle;
	private final String key;
	private final Composite compTitle;

	public ListPanel(Composite parent, int style, final DisplayerContext context, DisplayerDetails displayerDetails) {
		super(parent, style);
		this.key = displayerDetails.key;
		this.context = context;
		this.title = Resources.getTitle(context.resourceGetter, displayerDetails.key);
		this.nameTitle = Resources.getNameAndValue_Name(context.resourceGetter, key);
		this.valueTitle = Resources.getNameAndValue_Value(context.resourceGetter, key);
		this.entity = displayerDetails.entity;
		this.repository = context.repository;
		this.encoder = IEncodeDecodeNameAndUrl.Utils.defaultEncoder();
		this.listModel = new ListModel(encoder);
		this.compTitle = new Composite(this, SWT.NULL);
		compTitle.setLayout(new RowLayout(SWT.HORIZONTAL));
		new Label(compTitle, SWT.NULL).setText(title);
		compForList = new Composite(this, SWT.BORDER);

		ImageButtons.addRowButton(this, SwtBasicConstants.addKey, SwtBasicConstants.addKey, new IImageButtonListener() {

			@Override
			public void buttonPressed(ImageButton button) {
				NameAndValueDialog dialog = new NameAndValueDialog(getShell(), SWT.NULL, ConfigForTitleAnd.createForDialogs(getDisplay(), context.resourceGetter, context.imageRegistry), nameTitle, valueTitle);
				NameAndValue newDialog = dialog.open(new NameAndValue("", ""));
				if (newDialog != null)
					listModel.add(newDialog.name, newDialog.url);
				sendDataToServer();
			}
		});
		ImageButtons.addHelpButton(this, displayerDetails.key);

		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
		setSize(getSize().x, 1);
		getParent().layout();
		getParent().redraw();
	}

	public void setValue(String url, Object value) {
		this.url = url;
		List<String> values = getValues(value);
		listModel.setData(values);
		Swts.removeAllChildren(compForList);
		int index = 0;
		for (NameAndValue nameAndValue : listModel) {
			TitleAndTextField text = new TitleAndTextField(context.configForTitleAnd, compForList, nameAndValue.name, false);
			text.setText(nameAndValue.url);
			ImageButtons.addEditButton(text, new EditButtonListener(index));
			ImageButtons.addRowButton(text, SwtBasicConstants.deleteKey, SwtBasicConstants.deleteKey, new DeleteButtonListener(index));
			ImageButtons.addHelpButton(text, Resources.getRowKey(key));
			index += 1;
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(compForList);
		if (index == 0)
			compForList.setSize(compForList.getSize().x, 22);
		getParent().layout();
		getParent().redraw();
	}

	@SuppressWarnings("unchecked")
	private List<String> getValues(Object value) {
		if (value instanceof String)
			if ("".equals(((String) value).trim()))
				return new ArrayList<String>();
		if (value != null && !(value instanceof List))
			throw new IllegalArgumentException(MessageFormat.format(DisplayListsConstants.mustBeStringList, title, value.getClass(), value));
		return (List<String>) value;
	}

	public void sendDataToServer() {
		repository.modifyData(entity, url, key, listModel.asDataForRepostory(), Collections.<String, Object> emptyMap());
	}

	public static void main(String[] args) {
		Swts.display(ListPanel.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				DisplayerContext context = new DisplayerContext(//
						ISelectedBindingManager.Utils.noSelectedBindingManager(), //
						IArc4EclipseRepository.Utils.repository(IUrlGeneratorMap.Utils.urlGeneratorMap()), //
						ConfigForTitleAnd.create(//
								from.getDisplay(), //
								Resources.resourceGetterWithBasics(getClass().getPackage().getName() + ".ListAndPanelTest"),//
								Images.withBasics(from.getDisplay())));
				List<String> value = Arrays.asList("name1$value1", "name2$value2");
				DisplayerDetails displayerDetails = new DisplayerDetails("entity", Maps.<String, String> makeMap(DisplayCoreConstants.key, "ListKey"));
				ListPanel result = new ListPanel(from, SWT.NULL, context, displayerDetails);
				result.setValue("someurl", value);
				return result;
			}
		});
	}

	@Override
	public Composite getButtonComposte() {
		return compTitle;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return context.imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return context.resourceGetter;
	}

	@Override
	public void buttonAdded() {

	}

}
