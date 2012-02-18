package org.softwareFm.swt.comments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.IValueComposite;
import org.softwareFm.swt.okCancel.OkCancel;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.swt.Swts;

public class CommentsEditorTest extends SwtTest {

	private CardConfig cardConfig;
	private final List<String> groupNames = Arrays.asList("group1", "group2", "group3");

	public void testInitialLayoutWithGroups() {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, CommentConstants.editorTitle, "some initial comment", groupNames, ICommentsEditorCallback.Utils.exceptionCallback());
		@SuppressWarnings("unchecked")
		IValueComposite<StyledText> composite = (IValueComposite<StyledText>) editor.getComposite();
		assertEquals("some initial comment", composite.getEditor().getText());
		assertEquals("Edit comment", composite.getTitle().getText());//

		OkCancel okCancel = composite.getOkCancel();
		Composite okCancelComposite = okCancel.getComposite();
		Control[] okCancelChildren = okCancelComposite.getChildren();
		assertEquals(6, okCancelChildren.length);
		checkRadioButton(okCancelComposite, 0, "Everyone", true);
		checkRadioButton(okCancelComposite, 1, "You", false);
		checkRadioButton(okCancelComposite, 2, "Group", false);
		checkCombo(okCancelComposite, 3, groupNames, "group1");
		assertEquals(okCancel.cancelButton, okCancelChildren[4]);
		assertEquals(okCancel.okButton, okCancelChildren[5]);
	}

	public void testInitialLayoutWithOutGroups() {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, CommentConstants.editorTitle, "some initial comment", Collections.<String> emptyList(), ICommentsEditorCallback.Utils.exceptionCallback());
		@SuppressWarnings("unchecked")
		IValueComposite<StyledText> composite = (IValueComposite<StyledText>) editor.getComposite();
		OkCancel okCancel = composite.getOkCancel();
		Composite okCancelComposite = okCancel.getComposite();
		Control[] okCancelChildren = okCancelComposite.getChildren();
		assertEquals(4, okCancelChildren.length);
		checkRadioButton(okCancelComposite, 0, "Everyone", true);
		checkRadioButton(okCancelComposite, 1, "You", false);
		assertEquals(okCancel.cancelButton, okCancelChildren[2]);
		assertEquals(okCancel.okButton, okCancelChildren[3]);
	}

	public void testOkButtonOnlyEnabledInitiallyWhenHaveSomeComment() {
		checkOkButtonInitiallyEnabled("some initial comment", true);
		checkOkButtonInitiallyEnabled("", false);

	}

	public void testOkButtonOnlyEnabledWhenHaveSomeComment() {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, CommentConstants.editorTitle, "some initial comment", Collections.<String> emptyList(), ICommentsEditorCallback.Utils.exceptionCallback());
		checkOkEnabled(editor, "", false);
		checkOkEnabled(editor, "a", true);
		checkOkEnabled(editor, "", false);
		checkOkEnabled(editor, "abc", true);
	}

	static class CommentsEditorFailAdapter implements ICommentsEditorCallback {
		@Override
		public void youComment(String text) {
			fail();
		}

		@Override
		public void groupComment(int groupIndex, String text) {
			fail();
		}

		@Override
		public void everyoneComment(String text) {
			fail();
		}

		@Override
		public void cancel() {
			fail();
		}

	}

	public void testOkButtonCallsOkCallback() {
		checkOkButton(new ICallback<OkCancel>() {
			@Override
			public void process(OkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 0);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void everyoneComment(String text) {
				assertEquals("expected", text);
			}
		});
		checkOkButton(new ICallback<OkCancel>() {
			@Override
			public void process(OkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 1);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void youComment(String text) {
				assertEquals("expected", text);
			}
		});
		checkOkButton(new ICallback<OkCancel>() {
			@Override
			public void process(OkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 2);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void groupComment(int index, String text) {
				assertEquals("expected", text);
				assertEquals(0, index);
			}
		});
		checkOkButton(new ICallback<OkCancel>() {
			@Override
			public void process(OkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 2);
				Combo combo = (Combo) Swts.getDescendant(okCancel.getComposite(), 3);
				combo.select(1);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void groupComment(int index, String text) {
				assertEquals("expected", text);
				assertEquals(1, index);
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void checkOkButton(ICallback<OkCancel> pressButtons, ICommentsEditorCallback callback) {
		String text = "expected";
		AtomicInteger count = new AtomicInteger();
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, CommentConstants.editorTitle, "some comment", //
				groupNames, ICommentsEditorCallback.Utils.withCount(callback, count));

		IValueComposite<StyledText> composite = (IValueComposite<StyledText>) editor.getComposite();
		composite.getEditor().setText(text);
		OkCancel okCancel = composite.getOkCancel();
		ICallback.Utils.call(pressButtons, okCancel);
		okCancel.ok();
		dispatchUntilQueueEmpty();
		assertEquals(1, count.get());
		editor.getControl().dispose();
	}

	@SuppressWarnings("unchecked")
	public void testCancelButtonCallsCancelCallback() {
		final AtomicInteger cancelCount = new AtomicInteger();
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, CommentConstants.editorTitle, "some comment", Collections.<String> emptyList(), new ICommentsEditorCallback() {

			@Override
			public void cancel() {
				cancelCount.incrementAndGet();
			}

			@Override
			public void youComment(String text) {
				fail();
			}

			@Override
			public void groupComment(int groupIndex, String text) {
				fail();
			}

			@Override
			public void everyoneComment(String text) {
				fail();
			}
		});
		IValueComposite<StyledText> composite = (IValueComposite<StyledText>) editor.getComposite();
		composite.getEditor().setText("expected");
		OkCancel okCancel = composite.getOkCancel();
		okCancel.cancel();
		assertEquals(1, cancelCount.get());
	}

	@SuppressWarnings("unchecked")
	protected void checkOkButtonInitiallyEnabled(String comment, boolean expected) {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, CommentConstants.editorTitle, comment, Collections.<String> emptyList(), ICommentsEditorCallback.Utils.exceptionCallback());
		IValueComposite<StyledText> composite = (IValueComposite<StyledText>) editor.getComposite();
		OkCancel okCancel = composite.getOkCancel();
		assertEquals(expected, okCancel.isOkEnabled());
	}

	@SuppressWarnings("unchecked")
	protected void checkOkEnabled(CommentsEditor editor, String text, boolean expected) {
		IValueComposite<StyledText> composite = (IValueComposite<StyledText>) editor.getComposite();
		StyledText styledText = composite.getEditor();
		styledText.setText(text);
		styledText.notifyListeners(SWT.Modify, new Event());
		assertEquals(expected, composite.getOkCancel().isOkEnabled());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(display, new CardConfig(ICardFactory.Utils.noCardFactory(), new CardDataStoreMock()));

	}

}
