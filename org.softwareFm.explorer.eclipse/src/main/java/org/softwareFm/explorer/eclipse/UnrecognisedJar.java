package org.softwareFm.explorer.eclipse;

import java.io.File;
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
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.OkCancel;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.card.internal.Card;
import org.softwareFm.card.title.Title;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class UnrecognisedJar implements IHasComposite {

	private final UnrecognisedJarComposite composite;

	public static class UnrecognisedJarLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			UnrecognisedJarComposite c = (UnrecognisedJarComposite) composite;
			Point titleSize = c.title.getControl().computeSize(wHint, c.cardConfig.titleHeight);
			Point okCancelSize = c.okCancel.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point cardSize = c.card == null ? new Point(0, 0) : c.card.getControl().computeSize(wHint, hHint == SWT.DEFAULT ? SWT.DEFAULT : hHint - titleSize.y);
			return new Point(Math.max(titleSize.x, cardSize.x), titleSize.y + cardSize.y + okCancelSize.y);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			UnrecognisedJarComposite c = (UnrecognisedJarComposite) composite;
			System.out.println(Swts.boundsUpToShell(composite));
			CardConfig cc = c.cardConfig;
			Rectangle ca = c.getClientArea();
			Control okCancelControl = c.okCancel.getControl();
			Point okCancelSize = okCancelControl.computeSize(ca.width, SWT.DEFAULT);

			c.title.getControl().setBounds(ca.x, ca.y, ca.width, cc.titleHeight+cc.topMargin);

			if (c.card != null)
				c.card.getControl().setBounds(ca.x, ca.y + cc.titleHeight+cc.topMargin, ca.width, ca.height - cc.titleHeight - okCancelSize.y - cc.topMargin);

			okCancelControl.setSize(ca.width, okCancelSize.y);
			okCancelControl.setLocation(ca.x, ca.height - okCancelSize.y);
			c.redraw();
		}

	}

	static class UnrecognisedJarComposite extends Composite {

		private ICard card;
		private final Title title;
		private final CardConfig cardConfig;
		private final OkCancel okCancel;
		private File file;
		private String digest;

		public UnrecognisedJarComposite(Composite parent, int style, final CardConfig cardConfig) {
			super(parent, style);
			this.cardConfig = cardConfig.withStyleAndSelection(SWT.FULL_SELECTION, true);
			this.file = new File(".");
			title = new Title(this, cardConfig, TitleSpec.noTitleSpec(getBackground()), "", "");

			okCancel = new OkCancel(this, cardConfig, new Runnable() {
				@Override
				public void run() {
					String groupId = (String) card.data().get(ConfigurationConstants.groupId);
					String artifactId = (String) card.data().get(ConfigurationConstants.artifactId);
					String version = (String) card.data().get(ConfigurationConstants.version);
					new NewJarImporter(cardConfig, CardConstants.manuallyAdded, digest, groupId, artifactId, version).process();
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

		public void setFileAndDigest(File file, String digest) {
			this.file = file;
			this.digest = digest;
			makeCard();
		}

		private void makeCard() {
			if (card != null)
				card.getControl().dispose();
			final Map<String, Object> rawData = Maps.stringObjectMap(//
					ConfigurationConstants.groupId, "Please specify the group id",//
					ConfigurationConstants.artifactId, "Please specify the artifact id",//
					ConfigurationConstants.version, "Please specify the version");
			final Map<String, Object> startData = Maps.stringObjectMap(//
					ConfigurationConstants.groupId, "Please specify the group id",//
					ConfigurationConstants.artifactId, Strings.withoutVersion(file, "Please specify the artifact id"),//
					ConfigurationConstants.version, Strings.versionPartOf(file, "Please specify the version"));
			card = new Card(this, this.cardConfig, "neverused", startData);
			card.getComposite().setLayout(new Card.CardLayout());
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, card);
			title.setTitleAndImage(file.getName(), file.toString(), titleSpec);
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


	}

	public UnrecognisedJar(Composite parent, int style, CardConfig cardConfig) {
		composite = new UnrecognisedJarComposite(parent, style, cardConfig);
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
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay()).withUrlGeneratorMap(BasicCardConfigurator.makeUrlGeneratorMap("/prefix/"));
				UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, SWT.NULL, cardConfig);
				unrecognisedJar.setFileAndDigest(new File("a/b/c/jarFile.jar"), "01234567Test");
				unrecognisedJar.getComposite().setLayout(new UnrecognisedJarLayout());
				Composite result = unrecognisedJar.getComposite();
				Size.resizeMeToParentsSize(result);
				return result;
			}
		});
	}
}
