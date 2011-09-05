package org.softwareFm.displayJavadocAndSource;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.swtBasics.SwtBasicConstants;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.ImageButtons;
import org.softwareFm.swtBasics.images.Images;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public abstract class JavadocOrSourcePanel extends Composite implements IButtonParent, ISendToEclipseOrRepository {

	private final ConfigForTitleAnd config;
	private final String key;

	private String eclipseValue;
	private String repositoryValue;

	private final ImageButton browseButton;
	private final ImageButton editButton;
	private final ImageButton folderButton;
	private final ImageButton attachButton;
	protected BindingContext bindingContext;
	protected final DisplayerDetails displayerDetails;
	protected final DisplayerContext displayerContext;

	abstract protected void openEclipseValue(String eclipseValue);

	abstract protected String findEclipseValue(BindingContext bindingContext) throws Exception;

	public JavadocOrSourcePanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails, String linkKey) {
		super(parent, style);
		this.displayerContext = displayerContext;
		this.config = displayerContext.configForTitleAnd;
		this.displayerDetails = displayerDetails;
		this.key = displayerDetails.key;
		final ReconciliationDialog reconciliationDialog = new ReconciliationDialog(parent.getShell(), this, config, key);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		Label label = new Label(this, SWT.NULL);
		String title = Resources.getTitle(config.resourceGetter, key);
		label.setText(title);
		label.setLayoutData(new RowData(config.titleWidth, config.titleHeight));
		browseButton = ImageButtons.addRowButton(this, SwtBasicConstants.browseKey, SwtBasicConstants.browseKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				browseOrOpenFile(repositoryValue);
			}
		});
		folderButton = ImageButtons.addRowButton(this, SwtBasicConstants.folderKey, null, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
				openEclipseValue(eclipseValue);
			}
		});
		attachButton = ImageButtons.addRowButton(this, linkKey, null, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
				reconciliationDialog.open(eclipseValue, repositoryValue);
				refresh();
			}

		});
		editButton = ImageButtons.addEditButton(this, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
			}
		});
		Swts.setRowData(config, browseButton, editButton, folderButton);
	}

	private synchronized void refresh() throws Exception {
		eclipseValue = findEclipseValue(bindingContext);
	}

	public synchronized void setValue(BindingContext bindingContext) {
		this.bindingContext = bindingContext;
		try {
			eclipseValue = findEclipseValue(bindingContext);
			Map<String, Object> data = bindingContext.data;
			repositoryValue = data == null ? null : (String) data.get(key);

			browseButton.setEnabled(repositoryValue != null);
			browseButton.setTooltipText(getTooltip(repositoryValue, "softwareFm.notPresent"));
			attachButton.setEnabled(repositoryValue != null || eclipseValue != null);

			folderButton.setEnabled(eclipseValue != null);
			folderButton.setTooltipText(getTooltip(eclipseValue, "eclipse.notPresent"));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private String getTooltip(Object value, String notPresent) {
		if (value == null)
			return Resources.getOrException(config.resourceGetter, key + "." + notPresent);
		else
			return value.toString();
	}

	@Override
	public Composite getButtonComposite() {
		return this;
	}

	@Override
	public ImageRegistry getImageRegistry() {
		return config.imageRegistry;
	}

	@Override
	public IResourceGetter getResourceGetter() {
		return config.resourceGetter;
	}

	@Override
	public void buttonAdded() {
	}

	@Override
	public void sendToRepository(String value) throws Exception {
		displayerContext.repository.modifyData(displayerDetails.entity, bindingContext.url, displayerDetails.key, findEclipseValue(bindingContext), bindingContext.context);
		System.out.println("sending to repository: " + value);
	}

	protected void browseOrOpenFile(String eclipseValue) {
		try {
			URI url = new URI(eclipseValue);
			Desktop.getDesktop().browse(url);
		} catch (Exception e) {
			File file = new File(eclipseValue);
			if (file.exists())
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					e.printStackTrace();
					throw WrappedException.wrap(e1);
				}
		}
	}

	public static void main(String[] args) {
		Swts.display("Javadoc or Source", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ImageRegistry imageRegistry = Images.withBasics(from.getDisplay());
				Images.registerImages(from.getDisplay(), imageRegistry, Images.class, SwtBasicConstants.folderKey);
				Images.registerImages(from.getDisplay(), imageRegistry, DisplayJavadocConstants.class, "Javadoc.link");
				IResourceGetter resourceGetter = Resources.resourceGetterWithBasics().with(DisplayJavadocConstants.class, "JavadocAndSource");

				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new GridLayout());
				final String key = "javadoc";
				DisplayerContext displayerContext = DisplayerContext.Utils.forTest(from.getDisplay(), resourceGetter, imageRegistry);
				DisplayerDetails displayerDetails = new DisplayerDetails("anyEntity", Maps.<String, String> makeMap(DisplayCoreConstants.key, key));
				final JavadocOrSourcePanel panel = new JavadocOrSourcePanel(composite, SWT.NULL, displayerContext, displayerDetails, "Javadoc.link") {
					@Override
					protected String findEclipseValue(BindingContext bindingContext) throws Exception {
						return (String) bindingContext.data.get("x");
					}

					@Override
					public void sendToEclipse(String value) {
						System.out.println("Going to eclipse: " + value);
					}

					@Override
					public void sendToRepository(String value) {
						System.out.println("Going to repository: " + value);
					}

					@Override
					protected void openEclipseValue(String eclipseValue) {
						try {
							Desktop.getDesktop().open(new File(eclipseValue));
						} catch (IOException e) {
							throw WrappedException.wrap(e);
						}
					}

					@Override
					public void clearEclipseValue() {
						System.out.println("Clearing eclipse value");
					}

				};
				addButton(composite, panel, "null/null", key, null, null);
				addButton(composite, panel, "SFM/null", key, "SFM", null);
				addButton(composite, panel, "null/Ecl", key, null, "C:\\");
				addButton(composite, panel, "SFM/Ecl", key, "SFM", "C:\\");
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}

			private void addButton(Composite composite, final JavadocOrSourcePanel panel, String label, final String key, final String softwareFmValue, final String eclipseValue) {
				Button state1 = new Button(composite, SWT.PUSH);
				state1.setText(label);
				SelectionAdapter listener = new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Map<String, Object> data = Maps.<String, Object> makeMap("x", eclipseValue, key, softwareFmValue);
						Map<String, Object> context = Maps.<String, Object> newMap();
						panel.setValue(new BindingContext(RepositoryDataItemStatus.FOUND, "url", data, context));
					}

				};
				state1.addSelectionListener(listener);
			}
		});
	}
}
