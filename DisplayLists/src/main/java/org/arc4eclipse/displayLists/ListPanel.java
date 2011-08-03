package org.arc4eclipse.displayLists;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ListPanel extends Composite {

	private final Composite compForums;
	private final IImageFactory imageFactory;
	private final Images images;
	private final String title;

	public ListPanel(Composite parent, int style, DisplayerContext context, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		this.title = title;
		imageFactory = context.imageFactory;
		images = imageFactory.makeImages(getDisplay());
		Composite compTitle = new Composite(this, SWT.NULL);
		compTitle.setLayout(new RowLayout(SWT.HORIZONTAL));
		new Label(compTitle, SWT.NULL).setText(title);
		compForums = new Composite(this, SWT.BORDER);
		new ImageButton(compTitle, images.getAddImage()).setTooltipText(MessageFormat.format(DisplayListsConstants.add, title));
		new ImageButton(compTitle, images.getHelpImage()).setTooltipText("Help");
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
		setSize(getSize().x, 1);
	}

	public void setValue(String url, Object value) {
		if (value != null && !(value instanceof List))
			throw new IllegalArgumentException(MessageFormat.format(DisplayListsConstants.mustBeList, title, value.getClass(), value));
		@SuppressWarnings("unchecked")
		List<Object> list = value == null ? Collections.emptyList() : (List<Object>) value;

		Swts.removeAllChildren(compForums);
		for (Object item : list) {
			if (!(item instanceof Map))
				throw new IllegalArgumentException(MessageFormat.format(DisplayListsConstants.itemInListMustBeMap, title, item.getClass(), item));
			Map<String, Object> map = (Map<String, Object>) item;
			Object title = map.get(DisplayListsConstants.titleKey);
			Object itemUrl = map.get(DisplayListsConstants.urlKey);
			TitleAndTextField text = new TitleAndTextField(compForums, SWT.NULL, imageFactory, Strings.nullSafeToString(title), false);
			text.setText(Strings.nullSafeToString(itemUrl));
			text.addButton(images.getEditImage(), "Edit", new IImageButtonListener() {
				@Override
				public void buttonPressed(ImageButton button) {
				}
			});
			text.addButton(images.getClearImage(), "Delete", new IImageButtonListener() {
				@Override
				public void buttonPressed(ImageButton button) {
				}
			});
			text.addHelpButton("Edits");
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(compForums);
		if (list.size() == 0)
			compForums.setSize(compForums.getSize().x, 22);
		getParent().layout();
		getParent().redraw();
	}
}
