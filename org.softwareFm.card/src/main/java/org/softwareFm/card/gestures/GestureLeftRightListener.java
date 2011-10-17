package org.softwareFm.card.gestures;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Control;

abstract public class GestureLeftRightListener {

	private final Control control;
	private final MouseListener mouseListener;
	private final MouseMoveListener mouseMoveListener;

	private int originalX = -1;
	protected int originalY;

	public GestureLeftRightListener(Control control, final int sensitivity) {
		this.control = control;
		mouseListener = new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				originalX = -1;
			}

			@Override
			public void mouseDown(MouseEvent e) {
				originalX = e.x;
				originalY = e.y;
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		};
		mouseMoveListener = new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				if (originalX == -1)
					return;
				int deltaX = originalX - e.x;
				if (Math.abs(deltaX)> sensitivity){
					if (deltaX>0){
						System.out.println("left");
						goLeft();
						originalX = -1;
					}else{
						System.out.println("right");
						goRight();
						originalX = -1;
						
					}
				}
			}
		};
		control.addMouseListener(mouseListener);
		control.addMouseMoveListener(mouseMoveListener);
	}

	public void dispose() {
		control.removeMouseListener(mouseListener);
		control.removeMouseMoveListener(mouseMoveListener);
	}

	public void goLeft() {
	}

	public void goRight() {
	}

}
