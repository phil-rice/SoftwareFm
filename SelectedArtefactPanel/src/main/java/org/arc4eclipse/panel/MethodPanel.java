package org.arc4eclipse.panel;

import java.util.Map;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.swtBinding.basic.BoundComposite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class MethodPanel extends BoundComposite<IPath, IBinding, IEntityType, Map<Object, Object>> {

	private final LabelAndText packageText;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MethodPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		LabelAndList commentsList = new LabelAndList(this, SWT.NONE, "Comments");
		FormData fd_labelAndList_3 = new FormData();
		fd_labelAndList_3.bottom = new FormAttachment(100);
		fd_labelAndList_3.right = new FormAttachment(100);
		fd_labelAndList_3.top = new FormAttachment(0, 64);
		fd_labelAndList_3.left = new FormAttachment(0, 697);
		commentsList.setLayoutData(fd_labelAndList_3);

		LabelAndText classText = new LabelAndText(this, SWT.NONE, "Class");
		FormData fd_labelAndTex2t = new FormData();
		fd_labelAndTex2t.top = new FormAttachment(0, 49);
		fd_labelAndTex2t.bottom = new FormAttachment(0, 70);
		classText.setLayoutData(fd_labelAndTex2t);

		packageText = new LabelAndText(this, SWT.NONE, "Package");
		fd_labelAndTex2t.left = new FormAttachment(0, 12);
		fd_labelAndTex2t.right = new FormAttachment(0, 395);
		FormData fd_labelAndText = new FormData();
		fd_labelAndText.right = new FormAttachment(0, 395);
		fd_labelAndText.bottom = new FormAttachment(0, 41);
		fd_labelAndText.top = new FormAttachment(0, 20);
		fd_labelAndText.left = new FormAttachment(0, 12);
		packageText.setLayoutData(fd_labelAndText);

		LabelAndStyledText annotationText = new LabelAndStyledText(this, SWT.NONE, "Annotation");
		FormData fd_labelAndStyledText = new FormData();
		fd_labelAndStyledText.bottom = new FormAttachment(100);
		fd_labelAndStyledText.right = new FormAttachment(0, 405);
		fd_labelAndStyledText.top = new FormAttachment(0, 120);
		fd_labelAndStyledText.left = new FormAttachment(0, 12);
		annotationText.setLayoutData(fd_labelAndStyledText);

		LabelAndList friendsList = new LabelAndList(this, SWT.NONE, "Friends");
		FormData fd_labelAndList = new FormData();
		fd_labelAndList.bottom = new FormAttachment(100);
		fd_labelAndList.right = new FormAttachment(0, 673);
		fd_labelAndList.top = new FormAttachment(0, 64);
		fd_labelAndList.left = new FormAttachment(0, 470);
		friendsList.setLayoutData(fd_labelAndList);

		LabelAndText methodText = new LabelAndText(this, SWT.NONE, "Method");
		FormData fd_MethodabelAndText = new FormData();
		fd_MethodabelAndText.right = new FormAttachment(0, 395);
		fd_MethodabelAndText.top = new FormAttachment(classText, 6);
		fd_MethodabelAndText.left = new FormAttachment(classText, 0, SWT.LEFT);
		methodText.setLayoutData(fd_MethodabelAndText);

	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
