package org.softwareFm.swt.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.okCancel.IOkCancel;

public interface IEditableControlStrategy<T extends Control> {

	T createControl(Composite from);

	void populateInitialValue(T control, Object value);

	void whenModifed(T control, ICardData cardData, String key, Runnable whenModified);

	void addEnterEscapeListeners(IOkCancel okCancel, T control);

	boolean forceFocus(T control);

	public static class Utils {

		public static IEditableControlStrategy<Text> text(int style) {
			return new TextControlStrategy(false, style);

		}

		public static IEditableControlStrategy<Text> password(int style) {
			return new TextControlStrategy(true, style);
		}

		public static IEditableControlStrategy<StyledText> message() {
			return new IEditableControlStrategy<StyledText>() {
				@Override
				public StyledText createControl(Composite from) {
					StyledText control = new StyledText(from, SWT.WRAP | SWT.READ_ONLY);
					control.setBackground(from.getBackground());
					return control;
				}

				@Override
				public void populateInitialValue(StyledText control, Object value) {
					control.setText(Strings.nullSafeToString(value));
				}

				@Override
				public void whenModifed(StyledText control, ICardData cardData, String key, Runnable whenModified) {
				}

				@Override
				public void addEnterEscapeListeners(IOkCancel okCancel, StyledText control) {
				}

				@Override
				public boolean forceFocus(StyledText control) {
					return false;
				}
			};

		}

		public static IEditableControlStrategy<StyledText> styledText(final int style) {
			return new IEditableControlStrategy<StyledText>() {
				@Override
				public StyledText createControl(Composite from) {
					return new StyledText(from, style);
				}

				@Override
				public void populateInitialValue(StyledText control, Object value) {
					control.setText(Strings.nullSafeToString(value));
				}

				@Override
				public void whenModifed(final StyledText control, final ICardData cardData, final String key, final Runnable whenModified) {
					control.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent e) {
							cardData.valueChanged(key, control.getText());
							whenModified.run();
						}
					});
				}

				@Override
				public void addEnterEscapeListeners(final IOkCancel okCancel, StyledText control) {
					control.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (okCancel.isOkEnabled())
								okCancel.ok();
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							if (okCancel.isOkEnabled())
								okCancel.ok();
						}
					});
					control.addKeyListener(new KeyListener() {
						@Override
						public void keyReleased(KeyEvent e) {
							if (e.keyCode == SWT.ESC)
								okCancel.cancel();
						}

						@Override
						public void keyPressed(KeyEvent e) {
						}
					});
				}

				@Override
				public boolean forceFocus(StyledText control) {
					if (control.getEditable()) {
						control.selectAll();
						control.setSelection(0, control.getText().length() + 1);
						return control.forceFocus();
					}
					return false;

				}

			};
		}

	}

}

class TextControlStrategy implements IEditableControlStrategy<Text> {
	private final boolean password;
	private final int style;

	public TextControlStrategy(boolean password, int style) {
		this.password = password;
		this.style = style;
	}

	@Override
	public Text createControl(Composite from) {
		Text text = new Text(from, style);
		if (password) {
			text.setEchoChar('#');
		}
		return text;
	}

	@Override
	public boolean forceFocus(Text control) {
		if (control.getEditable()) {
			control.selectAll();
			control.setSelection(0, control.getText().length() + 1);
			return control.forceFocus();
		}
		return false;
	}

	@Override
	public void populateInitialValue(Text control, Object value) {
		control.setText(Strings.nullSafeToString(value));
	}

	@Override
	public void whenModifed(final Text control, final ICardData cardData, final String key, final Runnable whenModified) {
		control.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				cardData.valueChanged(key, control.getText());
				whenModified.run();
			}
		});
	}

	@Override
	public void addEnterEscapeListeners(final IOkCancel okCancel, Text control) {
		control.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (okCancel.isOkEnabled())
					okCancel.ok();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (okCancel.isOkEnabled())
					okCancel.ok();
			}
		});
		control.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ESC)
					okCancel.cancel();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
}
