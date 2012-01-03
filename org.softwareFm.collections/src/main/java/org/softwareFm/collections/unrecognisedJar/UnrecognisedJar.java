package org.softwareFm.collections.unrecognisedJar;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardTable;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.card.composites.CardShapedHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.internal.SoftwareFmCardConfigurator;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class UnrecognisedJar implements IHasControl {

	private final Composite content;

	public UnrecognisedJar(Composite parent, CardConfig cardConfig, TitleSpec titleSpec, UnrecognisedJarData jarData, ICallback<GroupIdArtifactVersion> callback) {
		this.content = new Composite(parent, SWT.NULL);
		new StyledText(content, SWT.NULL | SWT.READ_ONLY | SWT.WRAP).setText("SoftwareFM has not seen the jar that this item was defined it. ");
		ICardTable.Utils.cardTable(content, cardConfig, titleSpec, CollectionConstants.jarNotRecognisedCardType, Maps.stringObjectLinkedMap(//
				"project", jarData.projectName, "jarName", jarData.jarFile.getName()));
		new StyledText(content, SWT.NULL | SWT.READ_ONLY | SWT.WRAP).setText("SoftwareFM has seen some jars that look a bit like this. Select from the table below to populate.");
		makeTable(cardConfig, jarData, callback);
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(content);
	}

	private void makeTable(CardConfig cardConfig, final UnrecognisedJarData jarData, final ICallback<GroupIdArtifactVersion> callback) {
		final Table table = new Table(content, SWT.FULL_SELECTION);
		TableColumn groupColumn = new TableColumn(table, SWT.NULL, 0);
		TableColumn artifactColumn = new TableColumn(table, SWT.NULL, 1);
		TableColumn versionColumn = new TableColumn(table, SWT.NULL, 2);
		setColumnTitle(cardConfig, groupColumn, CollectionConstants.groupId);
		setColumnTitle(cardConfig, artifactColumn, CollectionConstants.artifactId);
		setColumnTitle(cardConfig, versionColumn, CollectionConstants.version);
		for (GroupIdArtifactVersion gav : jarData.groupIdArtifactVersions)
			new TableItem(table, SWT.NULL).setText(new String[] { gav.groupId, gav.artifactId, gav.version });
		table.setHeaderVisible(true);
		groupColumn.pack();
		artifactColumn.pack();
		versionColumn.pack();
		table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int index = table.getSelectionIndex();
				if (index != -1)
					ICallback.Utils.call(callback, jarData.groupIdArtifactVersions.get(index));
			}
		});
	}

	private void setColumnTitle(CardConfig cardConfig, TableColumn column, String key) {
		String title = cardConfig.nameFn.apply(cardConfig, new LineItem(null, key, null));
		column.setText(title);
		
	}

	@Override
	public Control getControl() {
		return content;
	}

	public static void main(String[] args) {

		Swts.Show.display(UnrecognisedJar.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = new SoftwareFmCardConfigurator().configure(from.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), CardDataStoreFixture.rawCardStore()));
				final TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, ICardData.Utils.create(cardConfig, CollectionConstants.jarNotRecognisedCardType, "", Maps.emptyStringObjectMap()));
				CardShapedHolder<UnrecognisedJar> holder = new CardShapedHolder<UnrecognisedJar>(from, cardConfig, titleSpec, Swts.labelFn("Title"), new IFunction1<Composite, UnrecognisedJar>() {
					@Override
					public UnrecognisedJar apply(Composite from) throws Exception {
						GroupIdArtifactVersion gav1 = new GroupIdArtifactVersion("groupid1", "artifactId1", "version1");
						GroupIdArtifactVersion gav2 = new GroupIdArtifactVersion("groupid2", "artifactId2", "version2");
						return new UnrecognisedJar(from, cardConfig, titleSpec, UnrecognisedJarData.forTests("projectName", new File("alskjd"), gav1, gav2), ICallback.Utils.<GroupIdArtifactVersion> sysoutCallback());
					}
				});
				return holder.getComposite();
			}

		});
	}
}
