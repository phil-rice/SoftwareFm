package org.arc4eclipse.panel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class ProjectPanel extends AbstractRepositoryDataPanel<IProjectData> {

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
		super(parent, style, repository);
		txtOrganisation = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Organisation", context, Arc4EclipseRepositoryConstants.organisationUrlKey);
		FormData fd_txtOrganisation = new FormData();
		fd_txtOrganisation.top = new FormAttachment(0, 14);
		fd_txtOrganisation.left = new FormAttachment(0, 8);
		txtOrganisation.setLayoutData(fd_txtOrganisation);
		txtName = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Name", context, Arc4EclipseRepositoryConstants.projectUrlKey);
		fd_txtOrganisation.bottom = new FormAttachment(0, 54);
		fd_txtOrganisation.right = new FormAttachment(100, -10);
		FormData fd_txtName = new FormData();
		fd_txtName.right = new FormAttachment(100, -10);
		fd_txtName.left = new FormAttachment(0, 8);
		fd_txtName.top = new FormAttachment(0, 60);
		txtName.setLayoutData(fd_txtName);
		txtDescription = new BoundLabelAndText<IProjectData>(this, SWT.NONE, "Desc", context, Arc4EclipseRepositoryConstants.descriptionKey);
		fd_txtName.bottom = new FormAttachment(0, 110);
		FormData fd_txtDescription = new FormData();
		fd_txtDescription.right = new FormAttachment(100, -10);
		fd_txtDescription.left = new FormAttachment(0, 8);
		fd_txtDescription.top = new FormAttachment(0, 110);
		fd_txtDescription.bottom = new FormAttachment(0, 160);
		txtDescription.setLayoutData(fd_txtDescription);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IBound<IProjectData>> boundChildren() {
		return Arrays.<IBound<IProjectData>> asList(txtOrganisation, txtName, txtDescription);
	}

	@Override
	public Class<IProjectData> getDataClass() {
		return IProjectData.class;
	}

	@Override
	public IFunction1<Map<String, Object>, IProjectData> mapper() {
		return IArc4EclipseRepository.Utils.projectData();
	}

}
