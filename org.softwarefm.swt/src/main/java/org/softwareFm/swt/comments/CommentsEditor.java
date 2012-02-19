package org.softwareFm.swt.comments;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.IValueComposite;
import org.softwareFm.swt.editors.internal.ValueEditorBodyComposite;
import org.softwareFm.swt.editors.internal.ValueEditorLayout;
import org.softwareFm.swt.okCancel.OkCancel;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

public class CommentsEditor implements IHasComposite {
	static class CommentsComposite extends Composite implements IValueComposite<Composite> {

		private final CardConfig cardConfig;
		private final OkCancel okCancel;
		private final TitleWithTitlePaintListener titleWithTitlePaintListener;
		private final ValueEditorBodyComposite body;
		private final StyledText editor;
		private final Button everyoneButton;
		private final Button youButton;
		private Button groupButton;
		private Combo groupCombo;
		private final String url;

		public CommentsComposite(Composite parent, String titleKey, final ICardData cardData, String initialText, List<String> groups, final ICommentsEditorCallback callback) {
			super(parent, SWT.NULL);
			this.url = cardData.url();
			this.cardConfig = cardData.getCardConfig();
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, cardData);
			IResourceGetter resourceGetter = CardConfig.resourceGetter(cardData);
			titleWithTitlePaintListener = new TitleWithTitlePaintListener(this, cardConfig, titleSpec, IResourceGetter.Utils.getOrException(resourceGetter, titleKey), "initialTooltip");
			body = new ValueEditorBodyComposite(this, cardConfig, titleSpec);
			editor = new StyledText(body.innerBody, SWT.WRAP);
			editor.setText(initialText);
			body.innerBody.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					int width = body.innerBody.getSize().x;
					int y = okCancel.getControl().getLocation().y - 1;
					e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
					e.gc.drawLine(0, y, width, y);
				}
			});

			okCancel = new OkCancel(body.innerBody, resourceGetter, cardConfig.imageFn, new Runnable() {
				@Override
				public void run() {
					ok(callback);
				}
			}, new Runnable() {
				@Override
				public void run() {
					callback.cancel();
				}
			});
			editor.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					okCancel.setOkEnabled(editor.getText().length() > 0);
				}
			});
			editor.notifyListeners(SWT.Modify, new Event());
			final Composite buttonComposite = (Composite) okCancel.getControl();
			everyoneButton = Swts.Buttons.makeRadioButton(buttonComposite, resourceGetter, CommentConstants.commentsButtonEveryoneText, Runnables.noRunnable);
			youButton = Swts.Buttons.makeRadioButton(buttonComposite, resourceGetter, CommentConstants.commentsButtonYouText, Runnables.noRunnable);
			if (groups.size() > 0) {
				groupButton = Swts.Buttons.makeRadioButton(buttonComposite, resourceGetter, CommentConstants.commentsButtonGroupText, Runnables.noRunnable);
				groupCombo = new Combo(buttonComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
				String[] groupStrings = groups.toArray(new String[0]);
				groupCombo.setItems(groupStrings);
				groupCombo.setText(groups.get(0));
				groupCombo.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						Swts.Buttons.selectRadioButton(buttonComposite, 2);
					}
				});
			}
			okCancel.okButton.moveBelow(buttonComposite.getChildren()[buttonComposite.getChildren().length - 1]);
			okCancel.cancelButton.moveAbove(okCancel.okButton);
			editor.forceFocus();
			Swts.setChildrenBackgroundTo(buttonComposite, titleSpec.background);
			everyoneButton.setSelection(true);
		}

		protected void ok(ICommentsEditorCallback callback) {
			String text = editor.getText();
			boolean everyone = everyoneButton.getSelection();
			boolean you = youButton.getSelection();
			boolean group = groupButton != null && groupButton.getSelection();
			if (everyone)
				callback.everyoneComment(url, text);
			else if (you)
				callback.youComment(url, text);
			else if (group)
				callback.groupComment(url, groupCombo.getSelectionIndex(), text);
			else
				throw new IllegalStateException();
		}

		@Override
		public CardConfig getCardConfig() {
			return cardConfig;
		}

		@Override
		public TitleWithTitlePaintListener getTitle() {
			return titleWithTitlePaintListener;
		}

		@Override
		public Composite getBody() {
			return body;
		}

		@Override
		public Composite getInnerBody() {
			return body.innerBody;
		}

		@Override
		public Composite getEditor() {
			return editor;
		}

		@Override
		public OkCancel getOkCancel() {
			return okCancel;
		}

		@Override
		public boolean useAllHeight() {
			return true;
		}
	}

	private final CommentsComposite content;

	public CommentsEditor(Composite parent, CardConfig cardConfig, String titleKey, String initialText, List<String> groupNames, ICommentsEditorCallback callback) {
		ICardData cardData = ICardData.Utils.create(cardConfig, CommentConstants.commentCardType, null, Maps.emptyStringObjectMap());// need one of these to get titlespec
		this.content = new CommentsComposite(parent, titleKey, cardData, initialText, groupNames, callback);
		content.setLayout(new ValueEditorLayout());

	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public Control getControl() {
		return content;
	}

	public static void main(String[] args) {
		Swts.Show.display(CommentsEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(from.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), new CardDataStoreMock()));
				List<String> groupNames = Arrays.asList("group1", "group2", "group3");
				return new CommentsEditor(from, cardConfig, CommentConstants.editorTitle, "here\nis an \n initial comment", groupNames, ICommentsEditorCallback.Utils.sysout()).getComposite();
			}
		});
	}

}
