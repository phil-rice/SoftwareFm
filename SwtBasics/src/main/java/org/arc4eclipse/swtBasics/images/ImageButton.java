package org.arc4eclipse.swtBasics.images;

import org.arc4eclipse.swtBasics.IHasControl;
import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ImageButton implements IHasControl {

	private final Label label;
	private boolean state;
	private final Image main;
	private final Image depressed;

	public ImageButton(Composite parent, Image main) {
		this(parent, main, main);
	}

	public ImageButton(Composite parent, Image main, Image depressed) {
		this.main = main;
		this.depressed = depressed;
		this.label = new Label(parent, SWT.NULL);
		updateImage();
		label.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				state = !state;
				updateImage();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
	}

	private void updateImage() {
		if (state)
			label.setImage(depressed);
		else
			label.setImage(main);
	}

	@Override
	public Control getControl() {
		return label;
	}

	public static void main(String[] args) {
		Swts.display("Label", new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Images images = new Images(from.getDisplay());
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new FormLayout());
				ImageButton btn = new ImageButton(composite, images.getEditImage(), images.getLinkImage());
				FormData fd = new FormData();
				fd.bottom = new FormAttachment(100, 0);
				fd.right = new FormAttachment(100, 0);
				fd.top = new FormAttachment(0, 0);
				fd.left = new FormAttachment(0, 0);
				btn.getControl().setLayoutData(fd);
				return composite;
			}
		});
	}
}
