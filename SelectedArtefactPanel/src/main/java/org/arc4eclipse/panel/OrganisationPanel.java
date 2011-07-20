package org.arc4eclipse.panel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class OrganisationPanel extends AbstractRepositoryDataPanel<IOrganisationData> {

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
		super(parent, style, repository);

		txtName = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Name", context, Arc4EclipseRepositoryConstants.organisationNameKey);
		FormData fd_boundLabelAndText = new FormData();
		fd_boundLabelAndText.right = new FormAttachment(100, -10);
		fd_boundLabelAndText.bottom = new FormAttachment(0, 54);
		fd_boundLabelAndText.top = new FormAttachment(0, 12);
		fd_boundLabelAndText.left = new FormAttachment(0, 12);
		txtName.setLayoutData(fd_boundLabelAndText);

		txtUrl = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Organisation Url", context, Arc4EclipseRepositoryConstants.organisationUrlKey);
		FormData fd_boundLabelAndText_1 = new FormData();
		fd_boundLabelAndText_1.right = new FormAttachment(100, -10);
		fd_boundLabelAndText_1.left = new FormAttachment(0, 12);
		fd_boundLabelAndText_1.bottom = new FormAttachment(0, 116);
		fd_boundLabelAndText_1.top = new FormAttachment(0, 66);
		txtUrl.setLayoutData(fd_boundLabelAndText_1);

		txtDescription = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Description", context, Arc4EclipseRepositoryConstants.descriptionKey);
		FormData fd_boundLabelAndText_2 = new FormData();
		fd_boundLabelAndText_2.right = new FormAttachment(100, -10);
		fd_boundLabelAndText_2.left = new FormAttachment(0, 12);
		fd_boundLabelAndText_2.bottom = new FormAttachment(0, 165);
		fd_boundLabelAndText_2.top = new FormAttachment(0, 120);
		txtDescription.setLayoutData(fd_boundLabelAndText_2);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IBound<IOrganisationData>> boundChildren() {
		return Arrays.<IBound<IOrganisationData>> asList(txtUrl, txtDescription, txtName);
	}

	@Override
	public Class<IOrganisationData> getDataClass() {
		return IOrganisationData.class;
	}

	@Override
	public IFunction1<Map<String, Object>, IOrganisationData> mapper() {
		return IArc4EclipseRepository.Utils.organisationData();
	}

}
