/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.arc4eclipse.panelExerciser;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.xml.DOMConfigurator;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.panel.SelectedArtefactPanel;
import org.arc4eclipse.panelExerciser.fixtures.AllTestFixtures;
import org.arc4eclipse.utilities.exceptions.WrappedException;
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
import org.springframework.core.io.ClassPathResource;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class BindingListAndPanel extends org.eclipse.swt.widgets.Composite {
	private SashForm sashForm1;
	private BindingList fileList1;
	private SashForm sashForm2;
	private Text text1;
	private SelectedArtefactPanel selectedArtefactPanel;
	private final IArc4EclipseRepository repository;

	/**
	 * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		URL resource = new ClassPathResource("log4j.xml", BindingListAndPanel.class).getURL();
		DOMConfigurator.configure(resource);
		IArc4EclipseRepository repository = IArc4EclipseRepository.Utils.repository();
		try {
			showGUI(repository);
		} finally {
			repository.shutdown();
		}
	}

	/**
	 * Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	 */

	@Override
	protected void checkSubclass() {
	}

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI(IArc4EclipseRepository repository) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		BindingListAndPanel inst = new BindingListAndPanel(shell, SWT.NULL, repository);
		inst.setRepository(repository);
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
		Iterable<IBinding> bindings = AllTestFixtures.allBindings();
		inst.fileList1.setData(bindings);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void setRepository(IArc4EclipseRepository repository) {
		// TODO Auto-generated method stub

	}

	public BindingListAndPanel(org.eclipse.swt.widgets.Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		this.repository = repository;
		initGUI();
	}

	private void initGUI() {
		try {
			final IBindingRipper bindingRipper = IBindingRipper.Utils.ripper();
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
						@Override
						public void bindingSelected(IBinding binding) {
							try {
								selectedArtefactPanel.setData(binding);
								String text = binding == null ? "" : binding.toString();
								BindingRipperResult ripperResult = bindingRipper.apply(binding);
								text1.setText(text + "\n" + ripperResult);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
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
						selectedArtefactPanel = new SelectedArtefactPanel(sashForm2, SWT.NONE, IDisplayManager.Utils.displayManager(), repository, bindingRipper);
					}
				}
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}