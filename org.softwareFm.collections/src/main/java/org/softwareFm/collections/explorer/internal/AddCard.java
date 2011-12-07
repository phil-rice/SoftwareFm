package org.softwareFm.collections.explorer.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardValueChangedListener;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.OKCancelWithBorder;
import org.softwareFm.card.modifiers.ICardDataModifier;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.collections.explorer.IAddCardCallback;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class AddCard implements IHasComposite, IHasCardConfig, ICardData {

	private final CardConfig cardConfig;
	final AddCardComposite content;
	private final Map<String, Object> data;
	private final String url;
	private final String cardType;

	static class AddCardComposite extends Composite implements IHasCardConfig {

		final Title title;
		final ICard card;
		final OKCancelWithBorder okCancel;
		final TableEditor editor;
		final Table table;

		public AddCardComposite(Composite parent, String titleString, final ICardData cardData, final IAddCardCallback callback) {
			super(parent, SWT.NULL);
			final CardConfig cardConfig = cardData.getCardConfig();
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, cardData);
			title = new Title(this, cardConfig, titleSpec, titleString, cardData.url());
			card = ICardFactory.Utils.createCardWithLayout(this, cardConfig, "neverused", cardData.data());
			okCancel = new OKCancelWithBorder(this, cardConfig, new Runnable() {
				@Override
				public void run() {
					callback.ok(cardData);
				}
			}, new Runnable() {
				@Override
				public void run() {
					callback.cancel(cardData);
				}
			});
			okCancel.setLayout(new OKCancelWithBorder.OKCancelWithBorderLayout());
			okCancel.setOkEnabled(false);
			table = card.getTable();
			editor = new TableEditor(table);
			addEditorToCard(callback);
			card.addValueChangedListener(new ICardValueChangedListener() {
				@Override
				public void valueChanged(ICard card, String key, Object newValue) {
					cardData.valueChanged(key, newValue);
				}
			});
		}

		private void addEditorToCard(final IAddCardCallback callback) {
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;
			editor.minimumWidth = 40;
			table.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Control oldEditor = editor.getEditor();
					if (oldEditor != null)
						oldEditor.dispose();
					int index = table.getSelectionIndex();
					if (index == -1)
						return;
					TableItem item = table.getItem(index);
					Text newEditor = new Text(table, SWT.NONE);
					newEditor.setText(Strings.nullSafeToString(card.data().get(item.getData())));
					newEditor.selectAll();
					newEditor.setFocus();
					editor.setEditor(newEditor, item, 1);
					newEditor.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent e) {
							Text text = (Text) editor.getEditor();
							TableItem item = editor.getItem();
							card.valueChanged((String) item.getData(), text.getText());
							okCancel.setOkEnabled(callback.canOk(card.data()));
						}
					});
				}
			});
		}

		@Override
		public CardConfig getCardConfig() {
			return card.getCardConfig();
		}
	}

	public AddCard(Composite parent, CardConfig cardConfig, String url, String title, String cardType, Map<String, Object> existingData, IAddCardCallback callback) {
		this.cardConfig = cardConfig.withCardDataModifiers(Lists.addAtStart(cardConfig.cardDataModifiers,  ICardDataModifier.Utils.hideCollections()));
		this.url = url;
		this.cardType = cardType;
		this.data = Maps.with(existingData, CardConstants.slingResourceType, cardType);
		this.content = new AddCardComposite(parent, title, this, callback);
		content.setLayout(new AddCardLayout());
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public String cardType() {
		return cardType;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public Map<String, Object> data() {
		return data;
	}

	@Override
	public void valueChanged(String key, Object newValue) {
		data.put(key, newValue);
	}

	public static void main(String[] args) {
		Swts.Show.display(AddCard.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				AddCard addCard = new AddCard(from, cardConfig, "url", "title", "card", CardDataStoreFixture.data1aWithP1Q2, new IAddCardCallback() {

					@Override
					public void ok(ICardData data) {
						System.out.println("ok: " + data.data());
					}

					@Override
					public void cancel(ICardData data) {
						System.out.println("cancel: " + data.data());
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return true;
					}
				});
				Composite composite = addCard.getComposite();
				composite.setLayout(new AddCardLayout());
				return composite;
			}
		});
	}

}
