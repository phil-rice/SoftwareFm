package org.arc4eclipse.panel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class SelectedArtefactPanel extends Composite implements IStatusChangedListener, ISelectedBindingListener {

	private final IArc4EclipseRepository repository;
	private final IDisplayContainer displayContainer;
	private final IDisplayManager displayManager;
	private final ISelectedBindingManager selectedBindingManager;
	private final TitleAndTextField txtJarPath;
	private final TitleAndTextField txtJarName;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SelectedArtefactPanel(Composite parent, int style, IDisplayManager displayManager, IArc4EclipseRepository repository, ISelectedBindingManager selectedBindingManager) {
		super(parent, style);
		this.displayManager = displayManager;
		this.selectedBindingManager = selectedBindingManager;
		txtJarPath = new TitleAndTextField(this, SWT.NULL, DisplayCoreConstants.jarPathTitle, false);
		txtJarName = new TitleAndTextField(this, SWT.NULL, DisplayCoreConstants.jarNameTitle, false);
		this.displayContainer = IDisplayContainer.Utils.displayContainer(this);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
		this.repository = repository;
		Layout layout = new GridLayout();
		// layout.numColumns = 1;
		setLayout(layout);
		repository.addStatusListener(this);
		selectedBindingManager.addSelectedArtifactSelectionListener(this);
	}

	@Override
	public void dispose() {
		repository.removeStatusListener(this);
		selectedBindingManager.removeSelectedArtifactSelectionListener(this);
		displayContainer.dispose();
		super.dispose();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void selectionOccured(BindingRipperResult ripperResult) {
		try {
			File file = ripperResult.path.toFile();
			String path = file.getCanonicalPath();
			String name = file.getName();
			txtJarName.setText(name);
			txtJarPath.setText(path);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> data, Map<String, Object> context) throws Exception {
		BindingContext bindingContext = new BindingContext(repository, ITitleLookup.Utils.titleLookup());
		displayManager.populate(displayContainer, bindingContext, url, data, context);
	}

	public static void main(String args[]) {
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				SelectedArtefactPanel selectedArtefactPanel = new SelectedArtefactPanel(from, SWT.NULL, IDisplayManager.Utils.displayManager(), repository, ISelectedBindingManager.Utils.noSelectedBindingManager());
				return selectedArtefactPanel;
			}
		});
	}

}
