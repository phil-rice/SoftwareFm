package org.softwareFm.explorer.eclipse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;

public class Detail implements IHasComposite {

	static class DetailComposite extends ScrolledComposite{

		public DetailComposite(Composite parent, int style) {
			super(parent, style);
		}
		
	}

	private final DetailComposite content;
	private Listener detailResizeListener;
	
	public Detail(Composite parent) {
		this.content = new DetailComposite(parent, SWT.H_SCROLL|SWT.NO_REDRAW_RESIZE|SWT.NO_BACKGROUND);
		ScrollBar hbar = content.getHorizontalBar();
		hbar.addListener(SWT.Selection, new Listener() {//needed because content has paint listeners
			@Override
			public void handleEvent(Event event) {
				Swts.redrawAllChildren(content.getContent());
			}
		});
	}
	
	public void setDetail(final Control control){
		content.setContent(null);
		Swts.removeAllChildren(content);
		Swts.removeOldResizeListener(content, detailResizeListener);
		
		detailResizeListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				// System.out.println("Resizing: " + detail.getClientArea());
				Swts.setSizeToComputedSize(control, SWT.DEFAULT, content.getClientArea().height);
				content.layout();
			}
		};
		content.setContent(null);
		Swts.setSizeToComputedSize(control, SWT.DEFAULT, content.getClientArea().height); // needed for scroll bar calculations
		System.out.println("detail: " + content.isDisposed() + ", control: " + control.isDisposed());
		content.setContent(control);
		Swts.setSizeToComputedSize(control, SWT.DEFAULT, content.getClientArea().height); // needed again if the scroll bar popped in
		if (control instanceof Composite)
			if (((Composite) control).getLayout() == null)
				((Composite) control).layout();
		content.layout();
		content.addListener(SWT.Resize, detailResizeListener);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}
	
}
