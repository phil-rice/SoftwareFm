package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IStatusChangedListener;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.binding.path.JavaElementRipper;
import org.arc4eclipse.binding.path.JavaElementRipperResult;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class SelectedArtefactPanel extends Composite {

	private final OrganisationPanel organisationPanel;
	private final ProjectPanel projectPanel;
	private final IArc4EclipseRepository repository;
	private final DebugPanel debugPanel;
	private final JarPanel jarPanel;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public SelectedArtefactPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		this.repository = repository;
		setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);

		TabItem tabJar = new TabItem(tabFolder, SWT.NULL);
		tabJar.setText("Jar");
		jarPanel = new JarPanel(tabFolder, SWT.BORDER, repository);
		tabJar.setControl(jarPanel);

		TabItem tabOrg = new TabItem(tabFolder, SWT.NULL);
		tabOrg.setText("Organisation");
		organisationPanel = new OrganisationPanel(tabFolder, SWT.BORDER, repository);
		tabOrg.setControl(organisationPanel);

		TabItem tabProject = new TabItem(tabFolder, SWT.NULL);
		tabProject.setText("Project");
		projectPanel = new ProjectPanel(tabFolder, SWT.BORDER, repository);
		tabProject.setControl(projectPanel);

		TabItem tabMy = new TabItem(tabFolder, SWT.NULL);
		tabMy.setText("My Software FM");
		tabMy.setControl(new MySoftwareFmPanel(tabFolder, SWT.BORDER));

		TabItem tabDebug = new TabItem(tabFolder, SWT.NULL);
		tabDebug.setText("Debug");
		debugPanel = new DebugPanel(tabFolder, SWT.BORDER, repository);
		tabDebug.setControl(debugPanel);
		repository.addLogger(debugPanel);
		repository.addStatusListener(IJarData.class, IStatusChangedListener.Utils.requestMoreWhenGotJarData(repository));
		repository.addStatusListener(IJarData.class, jarPanel);
		repository.addStatusListener(IOrganisationData.class, organisationPanel);
		repository.addStatusListener(IProjectData.class, projectPanel);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public static void main(String args[]) {
		final IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		Swts.display("Selected Artefact Panel", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				return new SelectedArtefactPanel(from, SWT.NULL, repository);
			}
		});
	}

	@Override
	public void setData(Object arg0) {
		super.setData(arg0);
		if (arg0 instanceof IBinding) {
			IBinding binding = (IBinding) arg0;
			JavaElementRipperResult ripperResult = JavaElementRipper.rip(binding);
			IPath path = ripperResult.path;
			if (path == null)
				repository.getJarData(null);
			else
				repository.getJarData(path.toFile());
		}

	}

}
