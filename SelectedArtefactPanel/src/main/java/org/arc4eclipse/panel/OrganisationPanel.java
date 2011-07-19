package org.arc4eclipse.panel;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseCallback;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.swtBinding.basic.BindingContext;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class OrganisationPanel extends Composite implements IArc4EclipseCallback<IOrganisationData> {

	private final BoundLabelAndText<IOrganisationData> txtName;
	private final BoundLabelAndText<IOrganisationData> txtUrl;
	private final BoundLabelAndText<IOrganisationData> txtDescription;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public OrganisationPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());
		BindingContext<IOrganisationData> context = new BindingContext<IOrganisationData>(IOrganisationData.class, repository, IArc4EclipseRepository.Utils.organisationData());

		txtName = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Name", context, Arc4EclipseRepositoryConstants.organisationNameKey);
		FormData fd_boundLabelAndText = new FormData();
		fd_boundLabelAndText.right = new FormAttachment(0, 305);
		fd_boundLabelAndText.bottom = new FormAttachment(0, 33);
		fd_boundLabelAndText.top = new FormAttachment(0, 12);
		fd_boundLabelAndText.left = new FormAttachment(0, 12);
		txtName.setLayoutData(fd_boundLabelAndText);

		txtUrl = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Url", context, Arc4EclipseRepositoryConstants.organisationUrlKey);
		FormData fd_boundLabelAndText_1 = new FormData();
		fd_boundLabelAndText_1.right = new FormAttachment(0, 305);
		fd_boundLabelAndText_1.bottom = new FormAttachment(0, 66);
		fd_boundLabelAndText_1.top = new FormAttachment(0, 45);
		fd_boundLabelAndText_1.left = new FormAttachment(0, 12);
		txtUrl.setLayoutData(fd_boundLabelAndText_1);

		txtDescription = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Desc", context, Arc4EclipseRepositoryConstants.descriptionKey);
		FormData fd_boundLabelAndText_2 = new FormData();
		fd_boundLabelAndText_2.right = new FormAttachment(0, 305);
		fd_boundLabelAndText_2.bottom = new FormAttachment(0, 96);
		fd_boundLabelAndText_2.top = new FormAttachment(0, 75);
		fd_boundLabelAndText_2.left = new FormAttachment(0, 12);
		txtDescription.setLayoutData(fd_boundLabelAndText_2);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void process(IResponse response, IOrganisationData data) {
		txtName.processResponse(response, data);
		txtUrl.processResponse(response, data);
		txtDescription.processResponse(response, data);
	}
}
