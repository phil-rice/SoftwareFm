package org.arc4eclipse.panel;

import java.util.Map;

import org.arc4eclipse.binding.path.BindingPathCalculator;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.impl.PathCalculatorThin;
import org.arc4eclipse.repositoryFacard.IDataRipper;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.swtBinding.basic.IBindable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class SoftwareFmPanel extends Composite implements IRepositoryFacardCallback<IPath, IBinding, IEntityType, Map<Object, Object>>, IBindable<IPath, IBinding, IEntityType, Map<Object, Object>> {

	IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> repositoryClient;
	private final ClassPanel classPanel;
	private final MethodPanel methodPanel;
	private final ReleasePanel releasePanel;
	private final ProjectPanel projectPanel;
	private final OrganisationPanel organisationPanel;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SoftwareFmPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
		tabItem.setText("My Software FM");
		tabItem.setControl(new MySoftwareFmPanel(tabFolder, SWT.BORDER));
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
		tabItem2.setText("Organisation");
		organisationPanel = new OrganisationPanel(tabFolder, SWT.BORDER);
		tabItem2.setControl(organisationPanel);
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NULL);
		tabItem3.setText("Project");
		projectPanel = new ProjectPanel(tabFolder, SWT.BORDER);
		tabItem3.setControl(projectPanel);
		TabItem tabItem4 = new TabItem(tabFolder, SWT.NULL);
		tabItem4.setText("Release");
		releasePanel = new ReleasePanel(tabFolder, SWT.BORDER);
		tabItem4.setControl(releasePanel);
		TabItem tabItem5 = new TabItem(tabFolder, SWT.NULL);
		tabItem5.setText("Class");
		classPanel = new ClassPanel(tabFolder, SWT.BORDER);
		tabItem5.setControl(classPanel);
		TabItem tabItem6 = new TabItem(tabFolder, SWT.NULL);
		tabItem6.setText("Method");
		methodPanel = new MethodPanel(tabFolder, SWT.BORDER);
		tabItem6.setControl(methodPanel);
	}

	
	public void process(IPath key, IBinding thing, IEntityType aspect, Map<Object, Object> data) {
		organisationPanel.process(key, thing, aspect, data);
		projectPanel.process(key, thing, aspect, data);
		releasePanel.process(key, thing, aspect, data);
		classPanel.process(key, thing, aspect, data);
		methodPanel.process(key, thing, aspect, data);
	}

	
	public void bind(IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> facard, IDataRipper<Map<Object, Object>> ripper) {
		organisationPanel.bind(facard, ripper);
		projectPanel.bind(facard, ripper);
		releasePanel.bind(facard, ripper);
		classPanel.bind(facard, ripper);
		methodPanel.bind(facard, ripper);

	}

	public void setRepositoryClient(IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> repositoryFacard) {
		this.repositoryClient = repositoryFacard;
	}

	
	public void setData(Object data) {
		super.setData(data);
		if (!(data instanceof IBinding))
			return;

		for (IEntityType entityType : IEntityType.values()) {
			IBinding binding = (IBinding) data;
			IPath key = binding.getJavaElement().getPath();
			repositoryClient.getDetails(key, binding, entityType, this);
		}
	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private void setException(Exception e) {
	}

	public static void main(String args[]) {
		try {
			BindingPathCalculator pathCalculator = new BindingPathCalculator(new PathCalculatorThin());
			IHttpClient client = IHttpClient.Utils.defaultClient();
			IRepositoryClient<IBinding, IEntityType> repositoryClient = IRepositoryClient.Utils.repositoryClient(pathCalculator, client);
			IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> repositoryFacard = IRepositoryFacard.Utils.facard(repositoryClient);
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			SoftwareFmPanel panel = new SoftwareFmPanel(shell, SWT.NULL);
			panel.setRepositoryClient(repositoryFacard);
			panel.setBounds(0, 0, 1200, 800);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				try {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				} catch (Exception e) {
					e.printStackTrace();
					panel.setException(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
