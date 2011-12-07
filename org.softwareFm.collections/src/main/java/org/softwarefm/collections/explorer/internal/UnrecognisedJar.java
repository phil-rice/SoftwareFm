/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;


import java.io.File;
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
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.OKCancelWithBorder;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class UnrecognisedJar implements IHasComposite {

	private final UnrecognisedJarComposite composite;

	static class UnrecognisedJarComposite extends Composite {

		ICard card;
		final Title title;
		final CardConfig cardConfig;
		final OKCancelWithBorder okCancel;
		private File file;
		private String digest;

		public UnrecognisedJarComposite(Composite parent, int style, final CardConfig cardConfig, final ICallback<String> afterOk) {
			super(parent, style);
			this.cardConfig = cardConfig.withStyleAndSelection(SWT.FULL_SELECTION, true);
			this.file = new File(".");
			title = new Title(this, cardConfig, TitleSpec.noTitleSpec(getBackground()), "", "");

			okCancel = new OKCancelWithBorder(this, cardConfig, new Runnable() {
				@Override
				public void run() {
					String groupId = (String) card.data().get(CollectionConstants.groupId);
					String artifactId = (String) card.data().get(CollectionConstants.artifactId);
					String version = (String) card.data().get(CollectionConstants.version);
					new NewJarImporter(cardConfig, CardConstants.manuallyAdded, digest, groupId, artifactId, version).process(afterOk);
				}
			}, new Runnable() {
				@Override
				public void run() {
					card.getControl().dispose();
					makeCard();
					layout();
				}
			});
			okCancel.setLayout(new OKCancelWithBorder.OKCancelWithBorderLayout());
			okCancel.setOkEnabled(false);
		}

		public void setFileAndDigest(File file, String digest) {
			this.file = file;
			this.digest = digest;
			makeCard();
		}

		private void makeCard() {
			if (card != null)
				card.getControl().dispose();
			final Map<String, Object> rawData = Maps.stringObjectMap(//
					CollectionConstants.groupId, "Please specify the group id",//
					CollectionConstants.artifactId, "Please specify the artifact id",//
					CollectionConstants.version, "Please specify the version");
			final Map<String, Object> startData = Maps.stringObjectMap(//
					CollectionConstants.groupId, "Please specify the group id",//
					CollectionConstants.artifactId, Strings.withoutVersion(file, "Please specify the artifact id"),//
					CollectionConstants.version, Strings.versionPartOf(file, "Please specify the version"));
			card = ICardFactory.Utils.createCardWithLayout(this, this.cardConfig, "neverused", startData);
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, card);
			String name = file == null ? "" : file.getName();
			String tooltip = file == null ? "" : file.toString();
			title.setTitleAndImage(name, tooltip, titleSpec);
			card.getControl().moveBelow(title.getControl());
			final Table table = card.getTable();
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
							boolean groupIdChanged = hasChanged(card, CollectionConstants.groupId);
							boolean artifactIdChanged = hasChanged(card, CollectionConstants.artifactId);
							boolean versionChanged = hasChanged(card, CollectionConstants.version);
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

	}

	public UnrecognisedJar(Composite parent, int style, CardConfig cardConfig, ICallback<String> afterOk) {
		composite = new UnrecognisedJarComposite(parent, style, cardConfig, afterOk);
	}

	public void setFileAndDigest(File file, String digest) {
		composite.setFileAndDigest(file, digest);
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
		Show.displayNoLayout(UnrecognisedJar.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay()).withUrlGeneratorMap(ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap("/prefix/"));
				UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, SWT.NULL, cardConfig, ICallback.Utils.<String>sysoutCallback());
				unrecognisedJar.setFileAndDigest(new File("a/b/c/jarFile.jar"), "01234567Test");
				unrecognisedJar.getComposite().setLayout(new AddCardLayout());
				Composite result = unrecognisedJar.getComposite();
				Size.resizeMeToParentsSize(result);
				return result;
			}
		});
	}
}