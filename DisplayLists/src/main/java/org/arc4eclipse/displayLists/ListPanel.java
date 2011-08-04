package org.arc4eclipse.displayLists;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.DisplayerContext;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Images;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ListPanel extends Composite {

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
			NameAndUrlDialog dialog = new NameAndUrlDialog(getShell(), imageFactory);
			NameAndUrl result = dialog.open(listModel.get(index));
			if (result != null) {
				listModel.set(index, result.name, result.url);
				sendDataToServer();
			}
		}
	}

	private final Composite compForums;
	private final IImageFactory imageFactory;
	private final Images images;
	private final String title;
	private final ListModel listModel;
	private final IArc4EclipseRepository repository;
	private String url;
	private final String key;
	private final IEncodeDecodeNameAndUrl encoder;

	public ListPanel(Composite parent, int style, DisplayerContext context, NameSpaceAndName nameSpaceAndName, String title) {
		super(parent, style);
		this.title = title;
		this.imageFactory = context.imageFactory;
		this.repository = context.repository;
		this.key = nameSpaceAndName.key;
		this.encoder = IEncodeDecodeNameAndUrl.Utils.defaultEncoder();
		this.listModel = new ListModel(encoder);
		images = imageFactory.makeImages(getDisplay());
		Composite compTitle = new Composite(this, SWT.NULL);
		compTitle.setLayout(new RowLayout(SWT.HORIZONTAL));
		new Label(compTitle, SWT.NULL).setText(title);
		compForums = new Composite(this, SWT.BORDER);
		ImageButton addButton = new ImageButton(compTitle, images.getAddImage());
		addButton.setTooltipText(MessageFormat.format(DisplayListsConstants.add, title));
		addButton.addListener(new IImageButtonListener() {

			@Override
			public void buttonPressed(ImageButton button) {
				NameAndUrlDialog dialog = new NameAndUrlDialog(getShell(), imageFactory);
				NameAndUrl newDialog = dialog.open(new NameAndUrl("<EnterName>", "<Enter Url>"));
				if (newDialog != null)
					listModel.add(newDialog.name, newDialog.url);
				sendDataToServer();
			}
		});
		new ImageButton(compTitle, images.getHelpImage()).setTooltipText("Help");

		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
		setSize(getSize().x, 1);
		getParent().layout();
		getParent().redraw();
	}

	public void setValue(String url, Object value) {
		this.url = url;
		List<String> values = getValues(value);
		listModel.setData(values);
		Swts.removeAllChildren(compForums);
		int index = 0;
		for (NameAndUrl nameAndUrl : listModel) {
			TitleAndTextField text = new TitleAndTextField(compForums, SWT.NULL, imageFactory, nameAndUrl.name, false);
			text.setText(nameAndUrl.url);
			text.addButton(images.getEditImage(), "Edit", new EditButtonListener(index));
			text.addButton(images.getClearImage(), "Delete", new DeleteButtonListener(index));
			text.addHelpButton("Edits");
			index += 1;
		}
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(compForums);
		if (index == 0)
			compForums.setSize(compForums.getSize().x, 22);
		getParent().layout();
		getParent().redraw();
	}

	private List<String> getValues(Object value) {
		if (value instanceof String)
			if ("".equals(((String) value).trim()))
				return new ArrayList<String>();
		if (value != null && !(value instanceof List))
			throw new IllegalArgumentException(MessageFormat.format(DisplayListsConstants.mustBeStringList, title, value.getClass(), value));
		return (List<String>) value;
	}

	public void sendDataToServer() {
		repository.modifyData(url, key, listModel.asDataForRepostory(), Collections.<String, Object> emptyMap());
	}

}
