package org.softwareFm.displayLists;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.softwareFm.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.ILineEditable;
import org.softwareFm.displayCore.api.ILineEditor;
import org.softwareFm.displayCore.api.IRegisteredItems;
import org.softwareFm.displayCore.api.RegisteredItemsAdapter;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.panel.ISelectedBindingManager;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.collections.ICrud;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class ListPanel<T> extends Composite implements IButtonParent, ILineEditable<T> {

	private final DisplayerContext context;
	private final Composite compForList;
	private final String title;
	private final ListModel<T> listModel;
	private final IArc4EclipseRepository repository;
	private String url;
	private final String entity;
	private final String key;
	private final Composite compTitle;
	private final IRegisteredItems registeredItems;
	private final ILineEditor<T> lineEditor;
	private final DisplayerDetails displayerDetails;

	public ListPanel(Composite parent, int style, final DisplayerContext context, DisplayerDetails displayerDetails, IRegisteredItems registeredItems) {
		super(parent, style);
		this.displayerDetails = displayerDetails;
		this.registeredItems = registeredItems;
		this.key = displayerDetails.key;
		this.context = context;
		this.title = Resources.getTitle(context.resourceGetter, displayerDetails.key);
		this.entity = displayerDetails.entity;
		this.repository = context.repository;
		String lineEditorName = displayerDetails.map.get(DisplayCoreConstants.lineEditorKey);
		if (lineEditorName == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.missingValueInMap, DisplayCoreConstants.lineEditorKey, displayerDetails.map));
		lineEditor = registeredItems.<T> getLineEditor(lineEditorName);
		this.listModel = new ListModel<T>(lineEditor.getCodec());
		this.compTitle = new Composite(this, SWT.NULL);
		compTitle.setLayout(new RowLayout(SWT.HORIZONTAL));
		new Label(compTitle, SWT.NULL).setText(Strings.nullSafeToString(title));
		compForList = new Composite(this, SWT.BORDER);

		ImageButtons.addRowButton(this, SwtBasicConstants.addKey, SwtBasicConstants.addKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				lineEditor.add(ListPanel.this);
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
		for (T t : listModel)
			lineEditor.makeLineControl(this, compForList, index++, t);
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

	@Override
	public void sendDataToServer() {
		repository.modifyData(entity, url, key, listModel.asDataForRepostory(), Collections.<String, Object> emptyMap());
	}

	@Override
	public Composite getButtonComposite() {
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

	@Override
	public DisplayerContext getDisplayerContext() {
		return context;
	}

	@Override
	public DisplayerDetails getDisplayerDetails() {
		return displayerDetails;
	}

	@Override
	public IRegisteredItems getRegisteredItems() {
		return registeredItems;
	}

	@Override
	public ICrud<T> getModel() {
		return listModel;
	}

	@Override
	public ConfigForTitleAnd getDialogConfig() {
		return ConfigForTitleAnd.createForDialogs(getDisplay(), getResourceGetter(), getImageRegistry());
	}

	public static void main(String[] args) {
		show(new NameAndValueLineEditor(), Arrays.asList("name1$value1", "name2$value2"));
	}

	public static <T> void show(final ILineEditor<T> lineEditor, final List<String> value) {
		Swts.display(ListPanel.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				IUrlGeneratorMap urlGeneratorMap = IUrlGeneratorMap.Utils.urlGeneratorMap();
				DisplayerContext context = new DisplayerContext(//
						ISelectedBindingManager.Utils.noSelectedBindingManager(), //
						IArc4EclipseRepository.Utils.repository(), //
						urlGeneratorMap,//
						ConfigForTitleAnd.create(//
								from.getDisplay(), //
								Resources.resourceGetterWithBasics(//
										getClass().getPackage().getName() + ".ListAndPanelTest",//
										getClass().getPackage().getName() + ".DisplayLists"),//
								Images.withBasics(from.getDisplay())));
				DisplayerDetails displayerDetails = new DisplayerDetails("entity", Maps.<String, String> makeMap(//
						DisplayCoreConstants.key, "ListKey",//
						DisplayCoreConstants.lineEditorKey, "ignored"));
				ListPanel<NameAndValue> result = new ListPanel<NameAndValue>(from, SWT.NULL, context, displayerDetails, new RegisteredItemsAdapter() {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					public ILineEditor getLineEditor(String lineEditorName) {
						return lineEditor;
					}
				});

				result.setValue("someurl", value);
				return result;
			}
		});
	}
}
