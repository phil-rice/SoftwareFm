package org.arc4eclipse.panelExerciser;

import java.net.URL;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.arc4eclipse.binding.path.BindingPathCalculator;
import org.arc4eclipse.binding.path.JavaElementRipper;
import org.arc4eclipse.binding.path.JavaElementRipperResult;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.panel.SoftwareFmPanel;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.impl.PathCalculatorThin;
import org.arc4eclipse.repositoryFacard.IDataRipper;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class BindingListAndPanel extends org.eclipse.swt.widgets.Composite {
	private SashForm sashForm1;
	private BindingList fileList1;
	private SashForm sashForm2;
	private Text text1;
	private SoftwareFmPanel softwareFmPanel1;

	/**
	 * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void main(String[] args) {
		URL resource = ClassLoader.getSystemClassLoader().getResource("log4j.xml");
		DOMConfigurator.configure(resource);
		showGUI();
	}

	/**
	 * Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	 */
	
	protected void checkSubclass() {
	}

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		BindingPathCalculator pathCalculator = new BindingPathCalculator(new PathCalculatorThin());
		IHttpClient client = IHttpClient.Utils.builder().withCredentials("admin", "admin");
		IRepositoryClient<IBinding, IEntityType> repositoryClient = IRepositoryClient.Utils.repositoryClient(pathCalculator, client);
		IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> facard = IRepositoryFacard.Utils.facard(repositoryClient);
		BindingListAndPanel inst = new BindingListAndPanel(shell, SWT.NULL);
		inst.setRepositoryFacard(facard);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if (size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		inst.fileList1.setData(PanelExerciserTestFixture.bindings);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void setRepositoryFacard(IRepositoryFacard<IPath, IBinding, IEntityType, Map<Object, Object>> repositoryFacard) {
		softwareFmPanel1.setRepositoryClient(repositoryFacard);
		softwareFmPanel1.bind(repositoryFacard, IDataRipper.Utils.mapRipper());
	}

	public BindingListAndPanel(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setSize(648, 168);
			{
				FormData sashForm1LData = new FormData();
				sashForm1LData.left = new FormAttachment(0, 1000, 6);
				sashForm1LData.top = new FormAttachment(0, 1000, 6);
				sashForm1LData.width = 642;
				sashForm1LData.height = 162;
				sashForm1LData.bottom = new FormAttachment(1000, 1000, 0);
				sashForm1LData.right = new FormAttachment(1000, 1000, 0);
				sashForm1 = new SashForm(this, SWT.NONE);
				sashForm1.setLayoutData(sashForm1LData);
				{
					fileList1 = new BindingList(sashForm1, SWT.NONE);
					fileList1.setBounds(0, 0, 222, 162);
					fileList1.addFireSelectionListener(new IBindingSelectedListener() {
						
						public void bindingSelected(IBinding binding) {
							String text = binding == null ? "" : binding.toString();
							JavaElementRipperResult ripperResult = JavaElementRipper.rip(binding);
							text1.setText(text + "\n" + ripperResult);
							softwareFmPanel1.setData(binding);
						}
					});
				}
				{
					sashForm2 = new SashForm(sashForm1, SWT.NONE);
					sashForm2.setOrientation(SWT.VERTICAL);
					{
						text1 = new Text(sashForm2, SWT.MULTI | SWT.WRAP);
						text1.setText("text1");
					}
					{
						softwareFmPanel1 = new SoftwareFmPanel(sashForm2, SWT.NONE);
					}
				}
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
