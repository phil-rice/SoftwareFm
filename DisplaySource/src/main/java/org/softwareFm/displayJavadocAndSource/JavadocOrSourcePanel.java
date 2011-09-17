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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.displayCore.api.BindingContext;
import org.softwareFm.displayCore.api.DisplayerContext;
import org.softwareFm.displayCore.api.DisplayerDetails;
import org.softwareFm.displayCore.api.IDisplayer;
import org.softwareFm.displayCore.constants.DisplayCoreConstants;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.softwareFmImages.IImageRegisterConfigurator;
import org.softwareFm.softwareFmImages.ImageButtons;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.softwareFmImages.general.GeneralAnchor;
import org.softwareFm.softwareFmImages.overlays.OverlaysAnchor;
import org.softwareFm.softwareFmImages.smallIcons.SmallIconsAnchor;
import org.softwareFm.swtBasics.IHasComposite;
import org.softwareFm.swtBasics.IHasControl;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.IImageButtonListener;
import org.softwareFm.swtBasics.images.ImageButton;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.images.SmallIconPosition;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.IButtonParent;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public abstract class JavadocOrSourcePanel implements IHasComposite, IButtonParent, ISendToEclipseOrRepository {

	private final ConfigForTitleAnd config;
	private final String key;

	protected final ImageButton repositoryButton;
	protected final ImageButton editButton;
	protected final ImageButton eclipseButton;
	protected final ImageButton attachButton;
	protected BindingContext bindingContext;
	protected final DisplayerDetails displayerDetails;
	protected final DisplayerContext displayerContext;
	protected EclipseRepositoryState state;
	private final String tooltipIfEclipseNotIn;
	private final String tooltipIfRepositoryNotIn;
	private final Composite content;

	abstract protected void openEclipseValue(String eclipseValue);

	abstract protected String findEclipseValue(BindingContext bindingContext) throws Exception;

	public JavadocOrSourcePanel(Composite parent, int style, DisplayerContext displayerContext, DisplayerDetails displayerDetails, String linkKey) {
		this.content = new Composite(parent, style);
		this.displayerContext = displayerContext;
		this.config = displayerContext.configForTitleAnd;
		this.displayerDetails = displayerDetails;
		this.key = displayerDetails.key;
		final ReconciliationDialog reconciliationDialog = new ReconciliationDialog(parent.getShell(), this, config, key);
		content.setLayout(Swts.getHorizonalNoMarginRowLayout());
		Swts.makeTitleLabel(this.getComposite(), config, key);
		repositoryButton = ImageButtons.addRowButton(this, ArtifactsAnchor.jarKey, GeneralAnchor.browseKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				browseOrOpenFile(state.repositoryValue);
			}
		});
		eclipseButton = ImageButtons.addRowButton(this, ArtifactsAnchor.jarKey, null, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
				openEclipseValue(state.eclipseValue);
			}
		});
		attachButton = ImageButtons.addRowButton(this, ArtifactsAnchor.jarKey, null, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
				reconciliationDialog.open(state.eclipseValue, state.repositoryValue);
				refresh();
			}
		});
		editButton = ImageButtons.addEditButton(this, ArtifactsAnchor.jarKey, OverlaysAnchor.editKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) throws Exception {
			}
		});
		tooltipIfEclipseNotIn = Resources.getOrException(config.resourceGetter, JavadocSourceConstants.noValueInRepository);
		tooltipIfRepositoryNotIn = Resources.getOrException(config.resourceGetter, JavadocSourceConstants.noValueInEclipse);
		state = new EclipseRepositoryState(null, null, tooltipIfEclipseNotIn, tooltipIfRepositoryNotIn);
		updateFromState();
		Swts.setRowData(config, repositoryButton, editButton, eclipseButton);
	}

	protected void updateFromState() {
		repositoryButton.setEnabled(state.repositoryPresent);
		repositoryButton.setTooltipText(state.repositoryTooltip());

		attachButton.setEnabled(state.eclipsePresent || state.repositoryPresent);

		eclipseButton.setEnabled(state.eclipsePresent);
		eclipseButton.setTooltipText(state.eclipseTooltip());
		updateSmallIconPositions();
	}

	private synchronized void refresh() throws Exception {
		state = state.withEclipseValue(findEclipseValue(bindingContext));
		updateSmallIconPositions();
	}

	abstract protected String getEclipseSmallIconKey();

	abstract protected SmallIconPosition getLeftPosition();

	abstract protected SmallIconPosition getRightPosition();

	protected void updateSmallIconPositions() {
		ImageButtons.clearSmallIcons(repositoryButton, eclipseButton, attachButton);
		eclipseButton.clearSmallIcons();
		if (state.eclipsePresent)
			ImageButtons.setSmallIcon(getLeftPosition(), getEclipseSmallIconKey(), eclipseButton, attachButton);
		if (state.repositoryPresent)
			ImageButtons.setSmallIcon(getRightPosition(), SmallIconsAnchor.softwareFmKey, repositoryButton, attachButton);
		if (state.eclipsePresent && !state.repositoryPresent)
			attachButton.setImage(ArtifactsAnchor.jarCopyToSoftwareFmKey);
		else if (!state.eclipsePresent && state.repositoryPresent)
			attachButton.setImage(ArtifactsAnchor.jarCopyFromSoftwareFmKey);
		else
			attachButton.setImage(ArtifactsAnchor.jarKey);
	}

	public synchronized void setValue(BindingContext bindingContext) {
		if (IDisplayer.Utils.entitiesMatch(bindingContext, displayerDetails.entity)) {
			this.bindingContext = bindingContext;
			try {

				String eclipseValue = findEclipseValue(bindingContext);
				Map<String, Object> data = bindingContext.data;
				String repositoryValue = data == null ? null : (String) data.get(key);
				state = new EclipseRepositoryState(eclipseValue, repositoryValue, tooltipIfEclipseNotIn, tooltipIfRepositoryNotIn);
				updateFromState();

			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

	@Override
	public Composite getButtonComposite() {
		return content;
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
	public void buttonAdded(IHasControl button) {
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

	interface PanelMakerForTest {
		JavadocOrSourcePanel make(Composite parent, DisplayerContext displayerContext, DisplayerDetails displayerDetails);
	}

	protected static void show(final PanelMakerForTest panelMaker) {
		Swts.display("Javadoc or Source", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {

				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new GridLayout());
				final String key = "javadoc";
				IResourceGetter resourceGetter = Resources.resourceGetterWithBasics().with(JavadocSourceConstants.class, "JavadocAndSource");
				ImageRegistry imageRegistry = IImageRegisterConfigurator.Utils.withBasics(from.getDisplay());
				DisplayerContext displayerContext = DisplayerContext.Utils.forTest(from, resourceGetter, imageRegistry);
				DisplayerDetails displayerDetails = new DisplayerDetails("anyEntity", Maps.<String, String> makeMap(DisplayCoreConstants.key, key));
				JavadocOrSourcePanel panel = panelMaker.make(composite, displayerContext, displayerDetails);
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

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}
}
