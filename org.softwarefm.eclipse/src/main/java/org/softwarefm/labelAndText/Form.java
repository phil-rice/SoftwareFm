package org.softwarefm.labelAndText;


import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
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
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.SwtConstants;
import org.softwarefm.eclipse.constants.SwtErrorMessages;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.eclipse.swt.Swts;
import org.softwarefm.labelAndText.Form.LabelAndText.LabelAndTextComposite;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.resources.ResourceGetterMock;
import org.softwarefm.utilities.runnable.Runnables;
import org.softwarefm.utilities.strings.Strings;

public class Form extends Composite implements IGetTextWithKey {
	private final List<String> keys;
	private final ButtonComposite buttonComposite;

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

	public static class LabelAndText extends HasComposite {

		private final LabelAndTextComposite composite;

		public class LabelAndTextComposite extends Composite {
			final Label label;
			final Text text;

			public LabelAndTextComposite(Composite parent, int style) {
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
					};
				});
			}

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return Swts.computeSizeForHorizontallyStackedComposites(wHint, hHint, label, text);
			}

			public void setProblems(List<String> problems) {
				boolean ok = problems.size() == 0;
				Color color = ok ? getDisplay().getSystemColor(SWT.COLOR_BLACK) : getDisplay().getSystemColor(SWT.COLOR_RED);
				text.setForeground(color);
				label.setForeground(color);
				String tooltip = Strings.join(problems, "\n");
				text.setToolTipText(tooltip);
				label.setToolTipText(tooltip);
			}
		}

		@Override
		protected Composite makeComposite(Composite parent) {
			return new LabelAndTextComposite(parent, SWT.NULL);
		}

		public LabelAndText(Composite parent, String title) {
			super(parent);
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

	public Form(Composite parent, int style, final SoftwareFmContainer<?> container, IButtonConfigurator buttonConfigurator, String... keys) {
		super(parent, style);
		this.keys = Lists.immutableCopy(keys);
		setLayout(new LabelAndTextHolderLayout());
		IResourceGetter resourceGetter = container.resourceGetter;
		for (String key : keys)
			new LabelAndText(this, IResourceGetter.Utils.getOrException(resourceGetter, key)).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					updateButtonStatus();
				}
			});
		buttonComposite = new ButtonComposite(this);
		buttonConfigurator.configure(container, IButtonCreator.Utils.creator(buttonComposite.getComposite(), resourceGetter));
		updateButtonStatus();
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	public void setEnabledForButton(String key, boolean enabled) {
		buttonComposite.setEnabledForButton(key, enabled);
	}

	public void setText(String key, String text) {
		LabelAndTextComposite labelAndTextComposite = getLabelAndTextFor(key);
		labelAndTextComposite.text.setText(text);
	}

	public Control getButton(String key) {
		return buttonComposite.getButton(key);
	}

	private LabelAndTextComposite getLabelAndTextFor(String key) {
		int index = keys.indexOf(key);
		if (index == -1)
			throw new IllegalArgumentException(MessageFormat.format(SwtErrorMessages.unrecognisedKey, key, keys));
		LabelAndTextComposite labelAndTextComposite = (LabelAndTextComposite) getChildren()[index];
		return labelAndTextComposite;
	}

	void updateButtonStatus() {
		List<KeyAndProblem> problems = buttonComposite.updateButtonStatus(this);
		Map<String, List<String>> map = Maps.newMap();
		for (KeyAndProblem problem : problems)
			Maps.addToList(map, problem.key, problem.problem);
		for (String key : keys) {
			List<String> problemsForKey = map.get(key);
			LabelAndTextComposite labelAndText = getLabelAndTextFor(key);
			labelAndText.setProblems(Lists.nullSafe(problemsForKey));
		}

	}

	public List<String> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	public static void main(String[] args) {
		Swts.Show.display(Form.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			public Composite apply(Composite from) throws Exception {
				IResourceGetter resourceGetter = new ResourceGetterMock(//
						SwtConstants.okButton, "OK", SwtConstants.cancelButton, "Cancel",//
						"one", "One", "two", "Two", "three", "Three", "four", "Four", "five", "Five", "six", "Six", "seven", "Seven");
				SoftwareFmContainer<?> container = SoftwareFmContainer.makeForTests(resourceGetter);
				Form form = new Form(from, SWT.BORDER, container, IButtonConfigurator.Utils.okCancel(Runnables.sysout("ok"), Runnables.sysout("cancel")), "one", "two", "three", "four", "five", "six", "seven");
				return form;
			}
		});
	}

	public String getText(String key) {
		return getLabelAndTextFor(key).text.getText();
	}

}
