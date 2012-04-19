/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.comments;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.DataCompositeWithFooterLayout;
import org.softwareFm.swt.editors.DataWithOkCancelComposite;
import org.softwareFm.swt.swt.Swts;

public class CommentsEditor implements IHasComposite {
	static class CommentsComposite extends DataWithOkCancelComposite<Composite> {

		private final StyledText editor;
		private final Button everyoneButton;
		private final Button youButton;
		private Button groupButton;
		private Combo groupCombo;
		private final String url;
		private final ICommentsEditorCallback callback;

		public CommentsComposite(Composite parent, String titleKey, final ICardData cardData, String initialText, List<String> groups, final ICommentsEditorCallback callback) {
			super(parent, cardData.getCardConfig(), cardData.cardType(), titleKey, true);
			this.callback = callback;
			this.url = cardData.url();
			editor = new StyledText(getInnerBody(), SWT.WRAP);
			editor.setText(initialText);

			editor.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					getFooter().setOkEnabled(editor.getText().length() > 0);
				}
			});
			editor.notifyListeners(SWT.Modify, new Event());
			final Composite buttonComposite = getFooter().getComposite();
			everyoneButton = Swts.Buttons.makeRadioButton(buttonComposite, CardConfig.resourceGetter(cardData), CommentConstants.commentsButtonEveryoneText, Runnables.noRunnable);
			youButton = Swts.Buttons.makeRadioButton(buttonComposite, CardConfig.resourceGetter(cardData), CommentConstants.commentsButtonYouText, Runnables.noRunnable);
			if (groups.size() > 0) {
				groupButton = Swts.Buttons.makeRadioButton(buttonComposite, CardConfig.resourceGetter(cardData), CommentConstants.commentsButtonGroupText, Runnables.noRunnable);
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
			moveOkCancelButtonsToEnd();
			editor.forceFocus();
			Swts.setChildrenBackgroundTo(buttonComposite, getTitleSpec().background);
			everyoneButton.setSelection(true);
		}

		@Override
		protected void ok() {
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
		protected void cancel() {
			callback.cancel();
		}

		@Override
		public Composite getEditor() {
			return editor;
		}

	}

	private final CommentsComposite content;

	public CommentsEditor(Composite parent, CardConfig cardConfig, String url, String titleKey, String initialText, List<String> groupNames, ICommentsEditorCallback callback) {
		ICardData cardData = ICardData.Utils.create(cardConfig, CommentConstants.commentCardType, url, Maps.emptyStringObjectMap());// need one of these to get titlespec
		this.content = new CommentsComposite(parent, titleKey, cardData, initialText, groupNames, callback);
		content.setLayout(new DataCompositeWithFooterLayout());

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
				return new CommentsEditor(from, cardConfig, "someUrl", CommentConstants.editorTitle, "here\nis an \n initial comment", groupNames, ICommentsEditorCallback.Utils.sysout()).getComposite();
			}
		});
	}

}