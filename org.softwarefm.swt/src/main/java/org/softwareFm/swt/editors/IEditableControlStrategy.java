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
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.okCancel.IOkCancel;

public interface IEditableControlStrategy<T extends Control> {

	T createControl(Composite from);

	void populateInitialValue(T control, Object value);

	void whenModifed(T control, ICardData cardData, String key, Runnable whenModified);

	void addEnterEscapeListeners(IOkCancel okCancel, T control);

	public static class Utils {

		public static IEditableControlStrategy<Text> text() {
			return new TextControlStrategy(false);

		}

		public static IEditableControlStrategy<Text> password() {
			return new TextControlStrategy(true);
		}

		public static IEditableControlStrategy<StyledText> styledText() {
			return new IEditableControlStrategy<StyledText>() {
				@Override
				public StyledText createControl(Composite from) {
					return new StyledText(from, SWT.WRAP | SWT.READ_ONLY);
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
			};
		}
	}
}

class TextControlStrategy implements IEditableControlStrategy<Text> {
	private final boolean password;

	public TextControlStrategy(boolean password) {
		this.password = password;
	}

	@Override
	public Text createControl(Composite from) {
		return new Text(from, SWT.WRAP | SWT.READ_ONLY);
	}

	@Override
	public void populateInitialValue(Text control, Object value) {
		if (password)
			control.setEchoChar('#');
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
