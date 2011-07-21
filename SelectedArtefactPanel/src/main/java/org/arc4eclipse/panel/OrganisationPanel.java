package org.arc4eclipse.panel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.swtBinding.basic.MasterBoundLabelAndText;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class OrganisationPanel extends AbstractRepositoryDataPanel<IOrganisationData> {

	private final BoundLabelAndText<IOrganisationData> txtName;
	private final MasterBoundLabelAndText<IOrganisationData> txtUrl;
	private final BoundLabelAndText<IOrganisationData> txtDescription;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public OrganisationPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style, repository);

		txtName = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Name", context, Arc4EclipseRepositoryConstants.organisationNameKey, this);
		FormData fd_boundLabelAndText = new FormData();
		txtName.setLayoutData(fd_boundLabelAndText);

		txtUrl = new MasterBoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Organisation Url", context, Arc4EclipseRepositoryConstants.organisationUrlKey);
		fd_boundLabelAndText.right = new FormAttachment(100, -10);
		fd_boundLabelAndText.left = new FormAttachment(0, 12);
		FormData fd_boundLabelAndText_1 = new FormData();
		fd_boundLabelAndText_1.bottom = new FormAttachment(0, 60);
		fd_boundLabelAndText_1.top = new FormAttachment(0, 10);
		txtUrl.setLayoutData(fd_boundLabelAndText_1);

		txtDescription = new BoundLabelAndText<IOrganisationData>(this, SWT.NONE, "Description", context, Arc4EclipseRepositoryConstants.descriptionKey, this);
		fd_boundLabelAndText.top = new FormAttachment(0, 72);
		fd_boundLabelAndText.bottom = new FormAttachment(0, 114);
		fd_boundLabelAndText_1.right = new FormAttachment(100, -10);
		fd_boundLabelAndText_1.left = new FormAttachment(0, 12);
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

	@Override
	public void setPrimaryKey(String primaryKey) {
		txtUrl.setText(primaryKey);
	}

	@Override
	public String url() {
		return context.repository.generator().forOrganisation(txtUrl.getText());
	}

}
