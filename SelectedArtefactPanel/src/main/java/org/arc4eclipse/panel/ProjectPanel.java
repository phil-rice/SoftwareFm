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

public class ProjectPanel extends BoundComposite<IPath, IBinding, IEntityType, Map<Object, Object>> {

	private final LabelAndText projectText;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProjectPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		LabelAndList releasesList = new LabelAndList(this, SWT.NONE, "Releases");
		FormData fd_labelAndList = new FormData();
		fd_labelAndList.bottom = new FormAttachment(100);
		fd_labelAndList.right = new FormAttachment(0, 215);
		fd_labelAndList.top = new FormAttachment(0, 64);
		fd_labelAndList.left = new FormAttachment(0, 10);
		releasesList.setLayoutData(fd_labelAndList);

		LabelAndList tweetsList = new LabelAndList(this, SWT.NONE, "Tweets");
		FormData fd_labelAndList_1 = new FormData();
		fd_labelAndList_1.bottom = new FormAttachment(100, -3);
		fd_labelAndList_1.right = new FormAttachment(0, 675);
		fd_labelAndList_1.top = new FormAttachment(0, 66);
		fd_labelAndList_1.left = new FormAttachment(0, 470);
		tweetsList.setLayoutData(fd_labelAndList_1);

		LabelAndText tweetText = new LabelAndText(this, SWT.NONE, "Tweet");
		FormData fd_labelAndText = new FormData();
		tweetText.setLayoutData(fd_labelAndText);

		LabelAndText websiteText = new LabelAndText(this, SWT.NONE, "Website");
		fd_labelAndText.top = new FormAttachment(0, 12);
		fd_labelAndText.bottom = new FormAttachment(0, 33);
		fd_labelAndText.left = new FormAttachment(0, 454);
		FormData fd_labelAndText_1 = new FormData();
		fd_labelAndText_1.top = new FormAttachment(tweetsList, -27, SWT.TOP);
		fd_labelAndText_1.bottom = new FormAttachment(tweetsList, -6);
		websiteText.setLayoutData(fd_labelAndText_1);

		LabelAndList friendsList = new LabelAndList(this, SWT.NONE, "Friends");
		fd_labelAndText_1.right = new FormAttachment(0, 445);
		fd_labelAndText_1.left = new FormAttachment(0, 10);
		FormData fd_labelAndList_2 = new FormData();
		fd_labelAndList_2.right = new FormAttachment(0, 445);
		fd_labelAndList_2.top = new FormAttachment(0, 69);
		fd_labelAndList_2.bottom = new FormAttachment(100);
		fd_labelAndList_2.left = new FormAttachment(0, 230);
		friendsList.setLayoutData(fd_labelAndList_2);

		LabelAndList commentsList = new LabelAndList(this, SWT.NONE, "Comments");
		fd_labelAndText.right = new FormAttachment(commentsList, 0, SWT.RIGHT);
		FormData fd_labelAndList_3 = new FormData();
		fd_labelAndList_3.bottom = new FormAttachment(100);
		fd_labelAndList_3.right = new FormAttachment(100);
		fd_labelAndList_3.top = new FormAttachment(0, 64);
		fd_labelAndList_3.left = new FormAttachment(0, 697);
		commentsList.setLayoutData(fd_labelAndList_3);

		projectText = new LabelAndText(this, SWT.NONE, "Project");
		FormData fd_labelAndTex2t = new FormData();
		fd_labelAndTex2t.right = new FormAttachment(0, 444);
		fd_labelAndTex2t.left = new FormAttachment(0, 10);
		fd_labelAndTex2t.top = new FormAttachment(0, 12);
		fd_labelAndTex2t.bottom = new FormAttachment(0, 33);
		projectText.setLayoutData(fd_labelAndTex2t);

		LabelAndText labelAndText = new LabelAndText(this, SWT.NONE, "Issues");
		FormData fd_labelAndText3 = new FormData();
		fd_labelAndText3.top = new FormAttachment(tweetText, 6);
		fd_labelAndText3.right = new FormAttachment(tweetText, 446);
		fd_labelAndText3.left = new FormAttachment(tweetText, 0, SWT.LEFT);
		labelAndText.setLayoutData(fd_labelAndText3);

	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
