package org.arc4eclipse.panel;

import java.util.Map;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.swtBinding.basic.BoundComposite;
import org.arc4eclipse.swtBinding.basic.EntityTypeLabelAndText;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ClassPanel extends BoundComposite<IPath, IBinding, IEntityType, Map<Object, Object>> {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ClassPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		IEntityType aspect = IEntityType.CLASS;
		EntityTypeLabelAndText<IPath> packageText = new EntityTypeLabelAndText<IPath>(this, SWT.NONE, "Package", aspect, "package");
		EntityTypeLabelAndText<IPath> classText = new EntityTypeLabelAndText<IPath>(this, SWT.NONE, "Class", aspect, "class");
		EntityTypeLabelAndText<IPath> commentText = new EntityTypeLabelAndText<IPath>(this, SWT.NONE, "Comment", aspect, "comment");

		FormData fd_packageText = new FormData();
		fd_packageText.left = new FormAttachment(0, 19);
		packageText.setLayoutData(fd_packageText);
		fd_packageText.top = new FormAttachment(0);
		fd_packageText.bottom = new FormAttachment(classText, -6);
		FormData fd_classText = new FormData();
		fd_classText.left = new FormAttachment(0, 19);
		fd_classText.right = new FormAttachment(100, -23);
		fd_classText.bottom = new FormAttachment(packageText, 31, SWT.BOTTOM);
		fd_classText.top = new FormAttachment(0, 31);
		classText.setLayoutData(fd_classText);
		fd_packageText.right = new FormAttachment(100, -23);
		FormData fd_entityTypeLabelAndText = new FormData();
		fd_entityTypeLabelAndText.left = new FormAttachment(0, 19);
		fd_entityTypeLabelAndText.bottom = new FormAttachment(0, 90);
		fd_entityTypeLabelAndText.right = new FormAttachment(100, -23);
		fd_entityTypeLabelAndText.top = new FormAttachment(0, 65);
		commentText.setLayoutData(fd_entityTypeLabelAndText);
	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
