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

public class ReleasePanel extends BoundComposite<IPath, IBinding, IEntityType, Map<Object, Object>> {

	private final LabelAndText releaseText;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ReleasePanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		LabelAndText downloadSite = new LabelAndText(this, SWT.NONE, "Download");
		FormData fd_labelAndText_1 = new FormData();
		downloadSite.setLayoutData(fd_labelAndText_1);

		LabelAndList friendsList = new LabelAndList(this, SWT.NONE, "Friends");
		fd_labelAndText_1.right = new FormAttachment(0, 447);
		fd_labelAndText_1.left = new FormAttachment(0, 12);
		FormData fd_labelAndList_2 = new FormData();
		fd_labelAndList_2.right = new FormAttachment(0, 680);
		fd_labelAndList_2.top = new FormAttachment(0, 70);
		fd_labelAndList_2.bottom = new FormAttachment(100, 1);
		fd_labelAndList_2.left = new FormAttachment(0, 465);
		friendsList.setLayoutData(fd_labelAndList_2);

		LabelAndList commentsList = new LabelAndList(this, SWT.NONE, "Comments");
		FormData fd_labelAndList_3 = new FormData();
		fd_labelAndList_3.bottom = new FormAttachment(100);
		fd_labelAndList_3.right = new FormAttachment(100);
		fd_labelAndList_3.top = new FormAttachment(0, 64);
		fd_labelAndList_3.left = new FormAttachment(0, 697);
		commentsList.setLayoutData(fd_labelAndList_3);

		releaseText = new LabelAndText(this, SWT.NONE, "Release");
		fd_labelAndText_1.bottom = new FormAttachment(releaseText, 27, SWT.BOTTOM);
		fd_labelAndText_1.top = new FormAttachment(releaseText, 6);
		FormData fd_labelAndTex2t = new FormData();
		fd_labelAndTex2t.left = new FormAttachment(downloadSite, -434);
		fd_labelAndTex2t.right = new FormAttachment(downloadSite, 0, SWT.RIGHT);
		releaseText.setLayoutData(fd_labelAndTex2t);

		LabelAndText javadocSite = new LabelAndText(this, SWT.NONE, "Javadoc");
		FormData fd_javadocSite = new FormData();
		fd_javadocSite.right = new FormAttachment(downloadSite, 0, SWT.RIGHT);
		fd_javadocSite.bottom = new FormAttachment(0, 91);
		fd_javadocSite.top = new FormAttachment(0, 70);
		fd_javadocSite.left = new FormAttachment(0, 15);
		javadocSite.setLayoutData(fd_javadocSite);
		fd_labelAndTex2t.top = new FormAttachment(0, 14);
		fd_labelAndTex2t.bottom = new FormAttachment(0, 35);

		LabelAndText labelAndText = new LabelAndText(this, SWT.NONE, "Repository");
		FormData fd_labelAndText = new FormData();
		fd_labelAndText.right = new FormAttachment(0, 447);
		fd_labelAndText.top = new FormAttachment(javadocSite, 6);
		fd_labelAndText.left = new FormAttachment(javadocSite, 0, SWT.LEFT);
		labelAndText.setLayoutData(fd_labelAndText);

		LabelAndText sourceText = new LabelAndText(this, SWT.NONE, "Source");
		FormData fd_labelAndText_13 = new FormData();
		fd_labelAndText_13.right = new FormAttachment(0, 447);
		fd_labelAndText_13.top = new FormAttachment(labelAndText, 6);
		fd_labelAndText_13.left = new FormAttachment(labelAndText, 0, SWT.LEFT);
		sourceText.setLayoutData(fd_labelAndText_13);

	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
