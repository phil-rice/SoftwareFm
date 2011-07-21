package org.arc4eclipse.panel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.swtBinding.basic.MasterBoundLabelAndText;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class ProjectPanel extends AbstractRepositoryDataPanel<IProjectData> {

	private final BoundLabelAndText<IProjectData> txtOrganisationUrl;
	private final MasterBoundLabelAndText<IProjectData> txtProjectUrl;
	private final BoundLabelAndText<IProjectData> txtDescription;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param repository
	 */
	public ProjectPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style, repository);
		txtOrganisationUrl = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Organisatio Url", context, Arc4EclipseRepositoryConstants.organisationUrlKey, this);
		FormData fd_txtOrganisation = new FormData();
		txtOrganisationUrl.setLayoutData(fd_txtOrganisation);
		txtProjectUrl = new MasterBoundLabelAndText<IProjectData>(this, SWT.NONE, "Name", context, Arc4EclipseRepositoryConstants.projectUrlKey);
		FormData fd_txtName = new FormData();
		fd_txtName.top = new FormAttachment(0, 15);
		txtProjectUrl.setLayoutData(fd_txtName);
		txtDescription = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Description", context, Arc4EclipseRepositoryConstants.descriptionKey, this);
		fd_txtOrganisation.bottom = new FormAttachment(0, 124);
		fd_txtOrganisation.top = new FormAttachment(0, 70);
		fd_txtOrganisation.left = new FormAttachment(0, 8);
		fd_txtOrganisation.right = new FormAttachment(100, -10);
		fd_txtName.right = new FormAttachment(100, -10);
		fd_txtName.left = new FormAttachment(0, 8);
		fd_txtName.bottom = new FormAttachment(0, 65);
		FormData fd_txtDescription = new FormData();
		fd_txtDescription.right = new FormAttachment(100, -10);
		fd_txtDescription.left = new FormAttachment(0, 8);
		fd_txtDescription.top = new FormAttachment(0, 130);
		fd_txtDescription.bottom = new FormAttachment(0, 180);
		txtDescription.setLayoutData(fd_txtDescription);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IBound<IProjectData>> boundChildren() {
		return Arrays.<IBound<IProjectData>> asList(txtOrganisationUrl, txtProjectUrl, txtDescription);
	}

	@Override
	public Class<IProjectData> getDataClass() {
		return IProjectData.class;
	}

	@Override
	public IFunction1<Map<String, Object>, IProjectData> mapper() {
		return IArc4EclipseRepository.Utils.projectData();
	}

	@Override
	public String url() {
		return context.repository.generator().forOrganisation(txtProjectUrl.getText());
	}

	@Override
	public void setPrimaryKey(String primaryKey) {
		txtProjectUrl.setText(primaryKey);
	}
}
