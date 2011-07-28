package org.arc4eclipse.panel;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SelectedArtefactPanel extends Composite implements IStatusChangedListener {

	private final IArc4EclipseRepository repository;
	private final IDisplayContainer displayContainer;
	private final IDisplayManager displayManager;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SelectedArtefactPanel(Composite parent, int style, IDisplayManager displayManager, IArc4EclipseRepository repository, IFunction1<IBinding, BindingRipperResult> bindingRipper) {
		super(parent, style);
		this.displayManager = displayManager;
		this.displayContainer = IDisplayContainer.Utils.displayContainer(this);
		this.repository = repository;
		setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> data, Map<String, Object> context) throws Exception {
		BindingContext bindingContext = new BindingContext(repository, ITitleLookup.Utils.titleLookup());
		displayManager.populate(displayContainer, bindingContext, data);

	}

	public static void main(String args[]) {
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new SelectedArtefactPanel(from, SWT.NULL, IDisplayManager.Utils.displayManager(), repository, IBindingRipper.Utils.ripper());
			}
		});
	}
}
