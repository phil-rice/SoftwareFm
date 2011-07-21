package org.arc4eclipse.panel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.swtBinding.basic.MasterBoundLabelAndText;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class JarPanel extends AbstractRepositoryDataPanel<IJarData> {

	private final BoundLabelAndText<IJarData> txtOrganisation;
	private final BoundLabelAndText<IJarData> txtProject;
	private final MasterBoundLabelAndText<IJarData> txtDigest;
	private final BoundLabelAndText<IJarData> txtJavadoc;
	private final BoundLabelAndText<IJarData> txtSource;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public JarPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style, repository);

		txtOrganisation = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Organisation Url", context, Arc4EclipseRepositoryConstants.organisationUrlKey, this);
		FormData fd_boundLabelAndText = new FormData();
		txtOrganisation.setLayoutData(fd_boundLabelAndText);

		txtProject = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Project Url", context, Arc4EclipseRepositoryConstants.projectUrlKey, this);
		fd_boundLabelAndText.top = new FormAttachment(0, 68);
		fd_boundLabelAndText.bottom = new FormAttachment(0, 110);
		FormData fd_boundLabelAndText_1 = new FormData();
		fd_boundLabelAndText_1.right = new FormAttachment(100, -10);
		fd_boundLabelAndText_1.left = new FormAttachment(0, 12);
		txtProject.setLayoutData(fd_boundLabelAndText_1);

		txtDigest = new MasterBoundLabelAndText<IJarData>(this, SWT.NONE, "Digest", context, Arc4EclipseRepositoryConstants.hexDigestKey, this);
		fd_boundLabelAndText.right = new FormAttachment(100, -10);
		fd_boundLabelAndText.left = new FormAttachment(0, 12);
		FormData fd_boundLabelAndText_2 = new FormData();
		fd_boundLabelAndText_2.top = new FormAttachment(0, 18);
		fd_boundLabelAndText_2.bottom = new FormAttachment(0, 68);
		txtDigest.setLayoutData(fd_boundLabelAndText_2);

		txtJavadoc = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Javadoc", context, Arc4EclipseRepositoryConstants.javadocKey, this);
		fd_boundLabelAndText_2.right = new FormAttachment(100, -10);
		fd_boundLabelAndText_2.left = new FormAttachment(0, 12);
		fd_boundLabelAndText_1.top = new FormAttachment(0, 116);
		fd_boundLabelAndText_1.bottom = new FormAttachment(0, 155);
		FormData fd_txtJavadoc = new FormData();
		fd_txtJavadoc.right = new FormAttachment(100, -10);
		fd_txtJavadoc.top = new FormAttachment(0, 167);
		fd_txtJavadoc.left = new FormAttachment(0, 12);
		txtJavadoc.setLayoutData(fd_txtJavadoc);

		txtSource = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Source", context, Arc4EclipseRepositoryConstants.sourceKey, this);
		fd_txtJavadoc.bottom = new FormAttachment(0, 208);
		FormData fd_txtSource = new FormData();
		fd_txtSource.top = new FormAttachment(0, 214);
		fd_txtSource.right = new FormAttachment(100, -10);
		fd_txtSource.bottom = new FormAttachment(0, 255);
		fd_txtSource.left = new FormAttachment(0, 14);
		txtSource.setLayoutData(fd_txtSource);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IBound<IJarData>> boundChildren() {
		return Arrays.<IBound<IJarData>> asList(txtOrganisation, txtProject, txtDigest, txtJavadoc, txtSource);
	}

	@Override
	public Class<IJarData> getDataClass() {
		return IJarData.class;
	}

	@Override
	public IFunction1<Map<String, Object>, IJarData> mapper() {
		return IArc4EclipseRepository.Utils.jarData();
	}

	@Override
	public String url() {
		try {
			return context.repository.generator().forJar().apply(txtDigest.getText());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void setPrimaryKey(String primaryKey) {
		txtDigest.setText(primaryKey);
	}
}
