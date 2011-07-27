package org.arc4eclipse.panel;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IJarData;
import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.swtBinding.basic.BoundLabelAndText;
import org.arc4eclipse.swtBinding.basic.IBound;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class JarPanel extends AbstractRepositoryDataPanel<IJarData> {

	private final BoundLabelAndText<IJarData> txtOrganisation;
	private final BoundLabelAndText<IJarData> txtProject;
	private final TitleAndTextField txtDigest;
	private final BoundLabelAndText<IJarData> txtJavadoc;
	private final BoundLabelAndText<IJarData> txtSource;
	private final TitleAndTextField txtJarPath;
	private final TitleAndTextField txtJarName;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public JarPanel(Composite parent, int style, IArc4EclipseRepository repository) {
		super(parent, style, repository);

		txtOrganisation = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Organisation Url", context, Arc4EclipseRepositoryConstants.organisationUrlKey, this);
		FormData fd_organsisation = new FormData();
		txtOrganisation.setLayoutData(fd_organsisation);

		txtProject = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Project Url", context, Arc4EclipseRepositoryConstants.projectUrlKey, this);
		fd_organsisation.top = new FormAttachment(0, 175);
		fd_organsisation.bottom = new FormAttachment(0, 217);
		FormData fd_project = new FormData();
		txtProject.setLayoutData(fd_project);

		txtDigest = new TitleAndTextField(this, SWT.NONE, "Digest");
		FormData fd_digest = new FormData();
		fd_digest.bottom = new FormAttachment(txtOrganisation, -6);
		fd_digest.top = new FormAttachment(0, 125);
		txtDigest.setLayoutData(fd_digest);

		txtJavadoc = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Javadoc", context, Arc4EclipseRepositoryConstants.javadocKey, this);
		fd_project.top = new FormAttachment(0, 225);
		fd_project.bottom = new FormAttachment(0, 264);
		FormData fd_txtJavadoc = new FormData();
		fd_txtJavadoc.top = new FormAttachment(0, 265);
		txtJavadoc.setLayoutData(fd_txtJavadoc);

		txtSource = new BoundLabelAndText<IJarData>(this, SWT.NONE, "Source", context, Arc4EclipseRepositoryConstants.sourceKey, this);
		fd_txtJavadoc.bottom = new FormAttachment(0, 306);
		FormData fd_txtSource = new FormData();
		fd_txtSource.top = new FormAttachment(0, 315);
		fd_txtSource.bottom = new FormAttachment(0, 356);
		txtSource.setLayoutData(fd_txtSource);

		txtJarPath = new TitleAndTextField(this, SWT.NONE, "Jar path");
		FormData fd_masterBoundLabelAndText = new FormData();
		fd_masterBoundLabelAndText.right = new FormAttachment(0, 470);
		fd_masterBoundLabelAndText.top = new FormAttachment(0, 12);
		fd_masterBoundLabelAndText.left = new FormAttachment(txtDigest, 0, SWT.LEFT);
		txtJarPath.setLayoutData(fd_masterBoundLabelAndText);

		txtJarName = new TitleAndTextField(this, SWT.NONE, "Jar name");
		fd_masterBoundLabelAndText.bottom = new FormAttachment(txtJarName, -6);
		fd_digest.right = new FormAttachment(txtJarName, 460);
		fd_digest.left = new FormAttachment(txtJarName, 0, SWT.LEFT);
		fd_organsisation.right = new FormAttachment(txtJarName, 460);
		fd_organsisation.left = new FormAttachment(txtJarName, 0, SWT.LEFT);
		fd_project.right = new FormAttachment(txtJarName, 460);
		fd_project.left = new FormAttachment(txtJarName, 0, SWT.LEFT);
		fd_txtJavadoc.right = new FormAttachment(txtJarName, 460);
		fd_txtJavadoc.left = new FormAttachment(txtJarName, 0, SWT.LEFT);
		fd_txtSource.right = new FormAttachment(txtJarName, 458);
		fd_txtSource.left = new FormAttachment(txtJarName, 0, SWT.LEFT);
		FormData fd_boundLabelAndText = new FormData();
		fd_boundLabelAndText.right = new FormAttachment(0, 470);
		fd_boundLabelAndText.left = new FormAttachment(0, 10);
		fd_boundLabelAndText.bottom = new FormAttachment(0, 122);
		fd_boundLabelAndText.top = new FormAttachment(0, 70);
		txtJarName.setLayoutData(fd_boundLabelAndText);
	}

	public void setRipperData(BindingRipperResult result) {
		IPath path = result.path;
		File file = path.toFile();
		txtDigest.setText(result.hexDigest);
		txtJarPath.setText(file.getPath().toString());
		txtJarName.setText(file.getName());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IBound<IJarData>> boundChildren() {
		return Arrays.<IBound<IJarData>> asList(txtOrganisation, txtProject, txtJavadoc, txtSource);
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
