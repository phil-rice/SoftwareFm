package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.swtBinding.basic.BindingContext;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ProjectPanel extends Composite implements IArc4EclipseCallback<IProjectData> {

	private final BoundLabelAndText<IProjectData> txtOrganisation;
	private final BoundLabelAndText<IProjectData> txtName;
	private final BoundLabelAndText<IProjectData> txtDescription;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public ProjectPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());
		BindingContext<IProjectData> context = new BindingContext<IProjectData>(IProjectData.class, repository, IArc4EclipseRepository.Utils.projectData());

		txtOrganisation = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Organisation", context, Arc4EclipseRepositoryConstants.organisationUrlKey);
		FormData fd_txtOrganisation = new FormData();
		fd_txtOrganisation.top = new FormAttachment(0, 14);
		fd_txtOrganisation.left = new FormAttachment(0, 8);
		fd_txtOrganisation.bottom = new FormAttachment(0, 35);
		txtOrganisation.setLayoutData(fd_txtOrganisation);
		txtName = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Name", context, Arc4EclipseRepositoryConstants.projectNameKey);
		fd_txtOrganisation.right = new FormAttachment(0, 315);
		FormData fd_txtName = new FormData();
		fd_txtName.right = new FormAttachment(txtOrganisation, 0, SWT.RIGHT);
		fd_txtName.top = new FormAttachment(0, 47);
		fd_txtName.left = new FormAttachment(0, 8);
		txtName.setLayoutData(fd_txtName);
		txtDescription = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Desc", context, Arc4EclipseRepositoryConstants.descriptionKey);
		fd_txtName.bottom = new FormAttachment(0, 68);
		FormData fd_txtDescription = new FormData();
		fd_txtDescription.right = new FormAttachment(txtName, 0, SWT.RIGHT);
		fd_txtDescription.top = new FormAttachment(0, 74);
		fd_txtDescription.left = new FormAttachment(0, 8);
		fd_txtDescription.bottom = new FormAttachment(0, 95);
		txtDescription.setLayoutData(fd_txtDescription);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void process(IResponse response, IProjectData data) {
		txtOrganisation.processResponse(response, data);
		txtName.processResponse(response, data);
		txtDescription.processResponse(response, data);
	}

}
