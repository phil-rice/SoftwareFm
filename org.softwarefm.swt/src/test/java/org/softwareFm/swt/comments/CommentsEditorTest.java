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
import org.softwareFm.client.http.constants.CommentConstants;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.IDataCompositeWithOkCancel;
import org.softwareFm.swt.okCancel.IOkCancel;
import org.softwareFm.swt.swt.SwtTest;
import org.softwareFm.swt.swt.Swts;

public class CommentsEditorTest extends SwtTest {

	private CardConfig cardConfig;
	private final List<String> groupNames = Arrays.asList("group1", "group2", "group3");
	private final String url = "someUrl";

	public void testInitialLayoutWithGroups() {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, url, CommentConstants.editorTitle, "some initial comment", groupNames, ICommentsEditorCallback.Utils.exceptionCallback());
		@SuppressWarnings("unchecked")
		IDataCompositeWithOkCancel<StyledText> composite = (IDataCompositeWithOkCancel<StyledText>) editor.getComposite();
		assertEquals("some initial comment", composite.getEditor().getText());
		assertEquals("Edit comment", composite.getTitle().getText());//

		IOkCancel okCancel = composite.getFooter();
		Composite okCancelComposite = okCancel.getComposite();
		Control[] okCancelChildren = okCancelComposite.getChildren();
		assertEquals(6, okCancelChildren.length);
		checkRadioButton(okCancelComposite, 0, "Everyone", true);
		checkRadioButton(okCancelComposite, 1, "You", false);
		checkRadioButton(okCancelComposite, 2, "Group", false);
		checkCombo(okCancelComposite, 3, groupNames, "group1");
		assertEquals(okCancel.cancelButton(), okCancelChildren[4]);
		assertEquals(okCancel.okButton(), okCancelChildren[5]);
	}

	public void testInitialLayoutWithOutGroups() {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, url, CommentConstants.editorTitle, "some initial comment", Collections.<String> emptyList(), ICommentsEditorCallback.Utils.exceptionCallback());
		@SuppressWarnings("unchecked")
		IDataCompositeWithOkCancel<StyledText> composite = (IDataCompositeWithOkCancel<StyledText>) editor.getComposite();
		IOkCancel okCancel = composite.getFooter();
		Composite okCancelComposite = okCancel.getComposite();
		Control[] okCancelChildren = okCancelComposite.getChildren();
		assertEquals(4, okCancelChildren.length);
		checkRadioButton(okCancelComposite, 0, "Everyone", true);
		checkRadioButton(okCancelComposite, 1, "You", false);
		assertEquals(okCancel.cancelButton(), okCancelChildren[2]);
		assertEquals(okCancel.okButton(), okCancelChildren[3]);
	}

	public void testOkButtonOnlyEnabledInitiallyWhenHaveSomeComment() {
		checkOkButtonInitiallyEnabled("some initial comment", true);
		checkOkButtonInitiallyEnabled("", false);

	}

	public void testOkButtonOnlyEnabledWhenHaveSomeComment() {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, url, CommentConstants.editorTitle, "some initial comment", Collections.<String> emptyList(), ICommentsEditorCallback.Utils.exceptionCallback());
		checkOkEnabled(editor, "", false);
		checkOkEnabled(editor, "a", true);
		checkOkEnabled(editor, "", false);
		checkOkEnabled(editor, "abc", true);
	}

	static class CommentsEditorFailAdapter implements ICommentsEditorCallback {
		@Override
		public void youComment(String url, String text) {
			fail();
		}

		@Override
		public void groupComment(String url, int groupIndex, String text) {
			fail();
		}

		@Override
		public void everyoneComment(String url, String text) {
			fail();
		}

		@Override
		public void cancel() {
			fail();
		}

	}

	public void testOkButtonCallsOkCallback() {
		checkOkButton(new ICallback<IOkCancel>() {
			@Override
			public void process(IOkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 0);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void everyoneComment(String url, String text) {
				assertEquals("expected", text);
				assertEquals(CommentsEditorTest.this.url, url);
			}
		});
		checkOkButton(new ICallback<IOkCancel>() {
			@Override
			public void process(IOkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 1);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void youComment(String url, String text) {
				assertEquals(CommentsEditorTest.this.url, url);
				assertEquals("expected", text);
			}
		});
		checkOkButton(new ICallback<IOkCancel>() {
			@Override
			public void process(IOkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 2);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void groupComment(String url, int index, String text) {
				assertEquals(CommentsEditorTest.this.url, url);
				assertEquals("expected", text);
				assertEquals(0, index);
			}
		});
		checkOkButton(new ICallback<IOkCancel>() {
			@Override
			public void process(IOkCancel okCancel) throws Exception {
				Swts.Buttons.selectRadioButton(okCancel.getComposite(), 2);
				Combo combo = Swts.<Combo>getDescendant(okCancel.getComposite(), 3);
				combo.select(1);
			}
		}, new CommentsEditorFailAdapter() {
			@Override
			public void groupComment(String url, int index, String text) {
				assertEquals(CommentsEditorTest.this.url, url);
				assertEquals("expected", text);
				assertEquals(1, index);
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void checkOkButton(ICallback<IOkCancel> pressButtons, ICommentsEditorCallback callback) {
		String text = "expected";
		AtomicInteger count = new AtomicInteger();
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, url, CommentConstants.editorTitle, "some comment", //
				groupNames, ICommentsEditorCallback.Utils.withCount(callback, count));

		IDataCompositeWithOkCancel<StyledText> composite = (IDataCompositeWithOkCancel<StyledText>) editor.getComposite();
		composite.getEditor().setText(text);
		IOkCancel okCancel = composite.getFooter();
		ICallback.Utils.call(pressButtons, okCancel);
		okCancel.ok();
		dispatchUntilQueueEmpty();
		assertEquals(1, count.get());
		editor.getControl().dispose();
	}

	@SuppressWarnings("unchecked")
	public void testCancelButtonCallsCancelCallback() {
		final AtomicInteger cancelCount = new AtomicInteger();
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, url, CommentConstants.editorTitle, "some comment", Collections.<String> emptyList(), new ICommentsEditorCallback() {

			@Override
			public void cancel() {
				cancelCount.incrementAndGet();
			}

			@Override
			public void youComment(String url, String text) {
				fail();
			}

			@Override
			public void groupComment(String url, int groupIndex, String text) {
				fail();
			}

			@Override
			public void everyoneComment(String url, String text) {
				fail();
			}
		});
		IDataCompositeWithOkCancel<StyledText> composite = (IDataCompositeWithOkCancel<StyledText>) editor.getComposite();
		composite.getEditor().setText("expected");
		IOkCancel okCancel = composite.getFooter();
		okCancel.cancel();
		assertEquals(1, cancelCount.get());
	}

	@SuppressWarnings("unchecked")
	protected void checkOkButtonInitiallyEnabled(String comment, boolean expected) {
		CommentsEditor editor = new CommentsEditor(shell, cardConfig, url, CommentConstants.editorTitle, comment, Collections.<String> emptyList(), ICommentsEditorCallback.Utils.exceptionCallback());
		IDataCompositeWithOkCancel<StyledText> composite = (IDataCompositeWithOkCancel<StyledText>) editor.getComposite();
		IOkCancel okCancel = composite.getFooter();
		assertEquals(expected, okCancel.isOkEnabled());
	}

	@SuppressWarnings("unchecked")
	protected void checkOkEnabled(CommentsEditor editor, String text, boolean expected) {
		IDataCompositeWithOkCancel<StyledText> composite = (IDataCompositeWithOkCancel<StyledText>) editor.getComposite();
		StyledText styledText = composite.getEditor();
		styledText.setText(text);
		styledText.notifyListeners(SWT.Modify, new Event());
		assertEquals(expected, composite.getFooter().isOkEnabled());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(display, new CardConfig(ICardFactory.Utils.noCardFactory(), new CardDataStoreMock()));

	}

}
