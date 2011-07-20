package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IReleaseData;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.swtBinding.basic.BindingContext;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ReleasePanel extends Composite implements IArc4EclipseCallback<IReleaseData> {

	private final BoundLabelAndText<IReleaseData> txtOrganisation;
	private final BoundLabelAndText<IReleaseData> txtProject;
	private final BoundLabelAndText<IReleaseData> txtRelease;
	private final BoundLabelAndText<IReleaseData> txtDescription;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public ReleasePanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());
		BindingContext<IReleaseData> context = new BindingContext<IReleaseData>(IReleaseData.class, repository, IArc4EclipseRepository.Utils.releaseData());

		txtOrganisation = new BoundLabelAndText<IReleaseData>(this, SWT.NONE, "Organisation", context, Arc4EclipseRepositoryConstants.organisationUrlKey);
		FormData fd_txtOrganisation = new FormData();
		fd_txtOrganisation.left = new FormAttachment(0, 8);
		fd_txtOrganisation.top = new FormAttachment(0, 12);
		fd_txtOrganisation.bottom = new FormAttachment(0, 33);
		fd_txtOrganisation.right = new FormAttachment(0, 295);
		txtOrganisation.setLayoutData(fd_txtOrganisation);
		txtProject = new BoundLabelAndText<IReleaseData>(this, SWT.NONE, "Project", context, Arc4EclipseRepositoryConstants.projectNameKey);
		FormData fd_txtProject = new FormData();
		fd_txtProject.right = new FormAttachment(txtOrganisation, 0, SWT.RIGHT);
		fd_txtProject.top = new FormAttachment(0, 42);
		fd_txtProject.left = new FormAttachment(0, 8);
		txtProject.setLayoutData(fd_txtProject);
		txtRelease = new BoundLabelAndText<IReleaseData>(this, SWT.NONE, "Release", context, Arc4EclipseRepositoryConstants.releaseIdentifierKey);
		fd_txtProject.bottom = new FormAttachment(0, 63);
		FormData fd_txtRelease = new FormData();
		fd_txtRelease.right = new FormAttachment(txtProject, 0, SWT.RIGHT);
		fd_txtRelease.top = new FormAttachment(0, 74);
		txtRelease.setLayoutData(fd_txtRelease);
		txtDescription = new BoundLabelAndText<IReleaseData>(this, SWT.NONE, "Description", context, Arc4EclipseRepositoryConstants.descriptionKey);
		fd_txtRelease.left = new FormAttachment(0, 8);
		fd_txtRelease.bottom = new FormAttachment(0, 95);
		FormData fd_txtDescription = new FormData();
		fd_txtDescription.right = new FormAttachment(txtRelease, 0, SWT.RIGHT);
		fd_txtDescription.left = new FormAttachment(0, 8);
		fd_txtDescription.bottom = new FormAttachment(0, 125);
		fd_txtDescription.top = new FormAttachment(0, 104);
		txtDescription.setLayoutData(fd_txtDescription);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void process(IResponse response, IReleaseData data) {
		txtOrganisation.processResponse(response, data);
		txtProject.processResponse(response, data);
		txtRelease.processResponse(response, data);
		txtDescription.processResponse(response, data);
	}

}
