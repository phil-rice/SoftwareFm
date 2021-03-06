package org.softwarefm.core.labelAndText;

import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.softwarefm.core.constants.SwtConstants;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.strings.Strings;

public class LabelAndText extends HasComposite {

	public static class LabelAndTextHolderLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			return Swts.computeSizeForVerticallyStackedControlWithIndent(wHint, hHint, composite.getChildren());
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Control[] children = composite.getChildren();

			int maxWidth = 0;
			for (Control child : children)
				if (child instanceof LabelAndText.LabelAndTextComposite) {
					LabelAndTextComposite labelAndTextComposite = (LabelAndTextComposite) child;
					Point size = labelAndTextComposite.label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					if (size.x > maxWidth)
						maxWidth = size.x;
				}
			Rectangle ca = composite.getClientArea();
			if (ca.width < maxWidth * 1.1)
				maxWidth = (int) (ca.width * 0.9);
			else
				maxWidth = maxWidth + 15;
			int y = ca.y;
			for (Control child : children) {
				if (child instanceof LabelAndText.LabelAndTextComposite) {
					LabelAndTextComposite labelAndTextComposite = (LabelAndTextComposite) child;
					Point size = labelAndTextComposite.text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					labelAndTextComposite.setBounds(ca.x, y, ca.width, size.y);
					labelAndTextComposite.label.setBounds(SwtConstants.xIndent, 0, maxWidth, size.y);
					labelAndTextComposite.text.setBounds(maxWidth, 0, ca.width - maxWidth - 2 * SwtConstants.xIndent, size.y);
					y += size.y + SwtConstants.yIndent;
				} else {
					Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					child.setBounds(ca.x, y, ca.width, size.y);
					y += size.y + SwtConstants.yIndent;
				}
			}
		}
	}

	private final LabelAndTextComposite composite;

	public class LabelAndTextComposite extends Composite {
		final Label label;
		final Text text;

		public LabelAndTextComposite(Composite parent, int style, ImageRegistry imageRegistry) {
			super(parent, style);
			label = new Label(this, SWT.NULL);
			text = new Text(this, SWT.NULL);
			text.addFocusListener(new FocusListener() {

				public void focusLost(FocusEvent e) {
				}

				public void focusGained(FocusEvent e) {
					text.selectAll();
					System.out.println("Selecting all: " + text.getText());
				}
			});
			text.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					text.selectAll();
				}
			});
		}

		@Override
		public Point computeSize(int wHint, int hHint, boolean changed) {
			return Swts.computeSizeForHorizontallyStackedComposites(wHint, hHint, label, text);
		}

		public void setProblems(List<String> problems) {
			boolean ok = problems.size() == 0;
			Color color = ok ? text.getDisplay().getSystemColor(SWT.COLOR_BLACK) : text.getDisplay().getSystemColor(SWT.COLOR_RED);
			text.setForeground(color);
			label.setForeground(color);
			String tooltip = Strings.join(problems, "\n");
			text.setToolTipText(tooltip);
			label.setToolTipText(tooltip);
		}
	}

	@Override
	protected Composite makeComposite(Composite parent, ImageRegistry imageRegistry) {
		return new LabelAndTextComposite(parent, SWT.NULL, imageRegistry);
	}

	public LabelAndText(Composite parent, ImageRegistry imageRegistry, String title) {
		super(parent, imageRegistry);
		composite = (LabelAndTextComposite) getComposite();
		composite.label.setText(title);
	}

	public void addModifyListener(ModifyListener listener) {
		composite.text.addModifyListener(listener);
	}

	public String getTitle() {
		return composite.label.getText();
	}

	public String getText() {
		return composite.text.getText();
	}

	public void setText(String text) {
		composite.text.setText(text);
	}

}