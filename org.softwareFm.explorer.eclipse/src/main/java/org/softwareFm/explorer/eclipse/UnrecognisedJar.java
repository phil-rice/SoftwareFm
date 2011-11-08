package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.editors.OkCancel;
import org.softwareFm.card.internal.Card;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class UnrecognisedJar implements IHasComposite {

	private final UnrecognisedJarComposite composite;

	static class UnrecognisedJarComposite extends Composite {

		private ICard card;
		private Title title;
		private final CardConfig cardConfig;
		private final OkCancel okCancel;
		private final Map<String, Object> rawData;
		private String url;
		private String file;

		public UnrecognisedJarComposite(Composite parent, int style, CardConfig cardConfig) {
			super(parent, style);
			this.cardConfig = cardConfig.withStyleAndSelection(SWT.FULL_SELECTION, true);
			this.file = "";
			this.url = "";
			rawData = Maps.stringObjectMap(//
					ConfigurationConstants.groupId, "Please specify the group id",//
					ConfigurationConstants.artifactId, "Please specify the artifact id",//
					ConfigurationConstants.version, "Please specify the version");
			makeCard();
			okCancel = new OkCancel(this, cardConfig, new Runnable() {
				@Override
				public void run() {
					System.out.println("Url: " + url + " card: " + card);

				}
			}, new Runnable() {
				@Override
				public void run() {
					card.getControl().dispose();
					makeCard();
					layout();
				}
			});
			okCancel.setOkEnabled(false);
		}

		public void setUrlAndFile(String url, String file) {
			this.url = url;
			this.file = file;
			makeCard();
		}

		private void makeCard() {
			if (card != null)
				card.getControl().dispose();
			card = new Card(this, this.cardConfig, url, rawData);
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, card);
			title = new Title(this, cardConfig, titleSpec, file, "");
			card.getControl().moveBelow(title.getControl());
			final Table table = (Table) card.getControl();
			final TableEditor editor = new TableEditor(table);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;
			editor.minimumWidth = 40;
			table.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Control oldEditor = editor.getEditor();
					if (oldEditor != null)
						oldEditor.dispose();
					TableItem item = (TableItem) e.item;
					if (item == null)
						return;
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
							boolean groupIdChanged = hasChanged(card, ConfigurationConstants.groupId);
							boolean artifactIdChanged = hasChanged(card, ConfigurationConstants.artifactId);
							boolean versionChanged = hasChanged(card, ConfigurationConstants.version);
							boolean allChanged = groupIdChanged && versionChanged && artifactIdChanged;
							okCancel.setOkEnabled(allChanged);
						}

						private boolean hasChanged(ICard card, String key) {
							String value = (String) card.data().get(key);
							Object original = rawData.get(key);
							return !value.equals(original);
						}

					});
				}
			});
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle ca = super.getClientArea();
			return new Rectangle(ca.x + cardConfig.leftMargin, ca.y + cardConfig.topMargin, ca.width - cardConfig.leftMargin - cardConfig.rightMargin, ca.height - cardConfig.topMargin - cardConfig.bottomMargin);
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void layout(boolean changed) {
			Rectangle ca = getClientArea();
			Control okCancelControl = okCancel.getControl();
			Point okCancelSize = okCancelControl.computeSize(ca.width, SWT.DEFAULT);

			title.getControl().setLocation(ca.x, ca.y);
			title.getControl().setSize(ca.width, cardConfig.titleHeight);

			card.getControl().setLocation(ca.x, ca.y + cardConfig.titleHeight);
			card.getControl().setSize(ca.width, ca.height - cardConfig.titleHeight - okCancelSize.y - 2);

			okCancelControl.setSize(ca.width, okCancelSize.y);
			okCancelControl.setLocation(ca.x, ca.height - okCancelSize.y);
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			Point titleSize = title.getControl().computeSize(wHint, hHint);
			Point cardSize = card.getControl().computeSize(wHint, hHint == SWT.DEFAULT ? SWT.DEFAULT : hHint - titleSize.y);
			return new Point(Math.max(titleSize.y, cardSize.y), titleSize.y + cardSize.y);
		}

	}

	public UnrecognisedJar(Composite parent, int style, CardConfig cardConfig) {
		composite = new UnrecognisedJarComposite(parent, style, cardConfig);
	}
	public void setUrlAndFile(String url, String file) {
		composite.setUrlAndFile(url, file);
	}
	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(UnrecognisedJar.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, SWT.NULL, cardConfig );
				unrecognisedJar.setUrlAndFile( "/data/01/23/012398759038247587346123","jarFile.jar");
				Composite result = unrecognisedJar.getComposite();
				Swts.resizeMeToParentsSize(result);
				return result;
			}
		});
	}
}
