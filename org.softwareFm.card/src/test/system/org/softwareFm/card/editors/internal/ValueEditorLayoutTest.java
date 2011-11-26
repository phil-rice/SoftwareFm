/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.junit.Test;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.SwtIntegrationTest;
import org.softwareFm.utilities.booleans.Booleans;
import org.softwareFm.utilities.callbacks.ICallback;

public class ValueEditorLayoutTest extends SwtIntegrationTest {

	private final static int titleHeight = 121;
	int leftMargin = 11;
	int rightMargin = 13;
	private final static int topMargin = 17;

	@Test
	public void testComputeSizeIsTitlePlusEditorPlusOKCancel() {
		checkComputeSize(false, SWT.DEFAULT, SWT.DEFAULT, new Point(1000, 15));// Label is 15 by default
		checkComputeSize(true, SWT.DEFAULT, SWT.DEFAULT, new Point(1000, 15));
	}

	public void testLayoutSetsTitle() throws Exception {
		for (Boolean b : Booleans.falseTrue)
			checkLayout(b, new ICallback<ValueEditorComposite<Label>>() {
				@Override
				public void process(ValueEditorComposite<Label> t) throws Exception {
					Title title = t.getTitle();
					Control titleControl = title.getControl();
					assertEquals(new Rectangle(150, 250, 1000, titleHeight + topMargin), titleControl.getBounds());
				}
			});
	}

	// Hard to test without duplicating alorithm, so just locking down results.
	public void testLayoutSetsOKCancel() throws Exception {
		for (Boolean b : Booleans.falseTrue)
			checkLayout(b, new ICallback<ValueEditorComposite<Label>>() {
				@Override
				public void process(ValueEditorComposite<Label> t) throws Exception {
					OkCancel okCancel = t.getOkCancel();
					Point idealOKCancelSize = okCancel.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
					Control control = okCancel.getControl();
					assertEquals(new Rectangle(899, 314, idealOKCancelSize.x, idealOKCancelSize.y), control.getBounds());
				}
			});

	}

	public void testLayoutSetsEditorSize() throws Exception {
		checkLayout(false, new ICallback<ValueEditorComposite<Label>>() {
			@Override
			public void process(ValueEditorComposite<Label> t) throws Exception {
				Label control = t.getEditor();
				assertEquals(new Rectangle(11, 10, 956, 15), control.getBounds());
			}
		});
		checkLayout(true, new ICallback<ValueEditorComposite<Label>>() {
			@Override
			public void process(ValueEditorComposite<Label> t) throws Exception {
				Label control = t.getEditor();
				assertEquals(new Rectangle(11, 10, 956, 296), control.getBounds());
			}
		});

	}

	private void checkLayout(boolean useAllHeight, ICallback<ValueEditorComposite<Label>> callback) throws Exception {
		Composite parent = new Composite(shell, SWT.NULL) {
			@Override
			public Rectangle getClientArea() {
				return new Rectangle(150, 250, 1300, 1000);
			}
		};

		ValueEditorComposite<Label> composite = makeComposite(parent, SWT.COLOR_CYAN, useAllHeight);
		composite.setBounds(150, 250, 1000, 500);// as the client area with a border
		ValueEditorLayout valueEditorLayout = new ValueEditorLayout();
		valueEditorLayout.layout(composite, true);
		callback.process(composite);
	}

	private void checkComputeSize(boolean useAllHeight, int wHint, int hHint, Point expectedSize) {
		Composite parent = new Composite(shell, SWT.NULL) {
			@Override
			public Rectangle getClientArea() {
				return new Rectangle(10, 20, 1000, 500);
			}
		};

		ValueEditorComposite<Label> composite = makeComposite(parent, SWT.COLOR_CYAN, useAllHeight);
		ValueEditorLayout valueEditorLayout = new ValueEditorLayout();
		Point okCancelSize = composite.getOkCancel().getControl().computeSize(wHint, hHint);
		int expectedHeight = okCancelSize.y + titleHeight + expectedSize.y;
		Point actualSize = valueEditorLayout.computeSize(composite, wHint, hHint, true);
		assertEquals(new Point(expectedSize.x, expectedHeight), actualSize);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	private ValueEditorComposite<Label> makeComposite(Composite parent, int backgroundInt, final boolean useAllHeight) {
		Color background = shell.getDisplay().getSystemColor(backgroundInt);

		CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(shell.getDisplay()).withTitleHeight(121).withMargins(leftMargin, rightMargin, topMargin, 19);
		ValueEditorComposite<Label> result = new ValueEditorComposite<Label>(parent, SWT.NULL, cardConfig, "url", "cardType", "key", "initialValue", TitleSpec.noTitleSpec(background), new IDetailsFactoryCallback() {
			@Override
			public void cardSelected(String cardUrl) {
			}

			@Override
			public void gotData(Control control) {
			}

			@Override
			public void afterEdit(String url) {
			}

			@Override
			public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
			}
		}) {

			@Override
			public boolean useAllHeight() {
				return useAllHeight;
			}

			@Override
			protected Label makeEditorControl(Composite parent, String originalValue) {
				Label label = new Label(parent, SWT.NULL);
				label.setText("some value");
				return label;
			}

			@Override
			protected String getValue() {
				fail();
				return null;
			}

			@Override
			protected void updateEnabledStatusOfButtons() {
			}

		};
		return result;
	}

}