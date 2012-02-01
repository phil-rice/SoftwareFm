package org.softwareFm.collections.unrecognisedJar;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;

public class UnrecognisedJar implements IHasControl {
	public static TitleSpec makeTitleSpec(CardConfig cardConfig) {
		return Functions.call(cardConfig.titleSpecFn, ICardData.Utils.create(cardConfig, CollectionConstants.jarNotRecognisedCardType, "", Maps.emptyStringObjectMap()));
	}

	private final Composite content;
	private final CardConfig cardConfig;
	private final UnrecognisedJarData jarData;
	private final ICallback<GroupidArtifactVersion> callback;
	private List<GroupidArtifactVersion> gavs;
	private final StyledText status;
	private Table table;
	private final Button firstSearchButton;
	private final IResourceGetter resourceGetter;

	public UnrecognisedJar(Composite parent, CardConfig cardConfig, UnrecognisedJarData jarData, ICallback<GroupidArtifactVersion> callback) {
		this.cardConfig = cardConfig;
		this.jarData = jarData;
		this.callback = callback;
		this.resourceGetter = Functions.call(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType);
		this.content = new Composite(parent, SWT.NULL) {
			@Override
			public void setBackground(Color color) {
				super.setBackground(color);
				status.setBackground(color);
			}
		};
		final TitleSpec titleSpec = makeTitleSpec(cardConfig);

		// new StyledText(content, SWT.NULL | SWT.READ_ONLY | SWT.WRAP).setText("The SoftwareFM does not include the jar that this item was defined in. ");
		ICardTable.Utils.cardTable(content, cardConfig, titleSpec, CollectionConstants.jarNotRecognisedCardType, Maps.stringObjectLinkedMap(//
				IResourceGetter.Utils.getOrException(resourceGetter, CollectionConstants.eclipseProject), jarData.projectName,//
				IResourceGetter.Utils.getOrException(resourceGetter, CollectionConstants.unrecognisedJarName), jarData.jarFile.getName(),//
				IResourceGetter.Utils.getOrException(resourceGetter, CollectionConstants.unreocgnisedJarPath), jarData.jarFile.getAbsoluteFile().toString()));
		status = new StyledText(content, SWT.NULL | SWT.READ_ONLY | SWT.WRAP);
		status.setText(IResourceGetter.Utils.getOrException(resourceGetter, CollectionConstants.jarNotRecognisedSearchingText));
		firstSearchButton = addSearchButton(CollectionConstants.jarSearchButtonTitle, "http://www.google.co.uk/?hl=en&q={0}");
		addSearchButton(CollectionConstants.jarSearchWithMavenButtonTitle, "http://www.google.co.uk/?hl=en&q={0}+Maven");
		Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(content);

	}

	private Button addSearchButton(String key, final String pattern) {
		String titlePattern = IResourceGetter.Utils.getOrException(resourceGetter, key);
		String title = MessageFormat.format(titlePattern, jarData.jarFile.getName());
		return Swts.Buttons.makePushButton(content, resourceGetter, title, false, new Runnable() {
			@Override
			public void run() {
				try {
					Desktop.getDesktop().browse(new URI(MessageFormat.format(pattern, jarData.jarFile.getName())));
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	public void populate(List<GroupidArtifactVersion> gavs) {
		if (content.isDisposed())
			return;
		if (table != null) {
			table.dispose();
			table = null;
		}
		this.gavs = gavs;

		if (gavs.size() > 0)
			table = makeTable();
		List<GroupidArtifactVersion> cleanGavs = Iterables.list(Iterables.remove(gavs, new IFunction1<GroupidArtifactVersion, Boolean>() {
			private final String guessedArtifactIdFromFile = new GuessArtifactAndVersionDetails().guessArtifactName(jarData.jarFile);

			@Override
			public Boolean apply(GroupidArtifactVersion from) throws Exception {
				return !new GuessArtifactAndVersionDetails().matches(guessedArtifactIdFromFile, from.artifactId);
			}
		}));
		setStatusFrom(cleanGavs);
		for (GroupidArtifactVersion gav : cleanGavs) {
			new TableItem(table, SWT.NULL).setText(gav.groupId);
		}
		packColumns();
		if (table != null)
			table.moveAbove(firstSearchButton);
		content.layout();
	}

	private void setStatusFrom(List<GroupidArtifactVersion> gavs) {
		String key = findStatusTextKey(gavs);
		String statusText = IResourceGetter.Utils.getOrException(resourceGetter, key);
		status.setText(statusText);
	}

	private String findStatusTextKey(List<GroupidArtifactVersion> gavs) {

		switch (gavs.size()) {
		case 0:
			return CollectionConstants.jarNotRecognisedFound0Text;
		case 1:
			return CollectionConstants.jarNotRecognisedFound1Text;
		default:
			return CollectionConstants.jarNotRecognisedFoundManyText;
		}
	}

	private Table makeTable() {
		final Table table = new Table(content, SWT.FULL_SELECTION | SWT.V_SCROLL);
		TableColumn groupColumn = new TableColumn(table, SWT.NULL, 0);
		setColumnTitle(cardConfig, groupColumn, CollectionConstants.groupId);
		table.setHeaderVisible(true);
		table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int index = table.getSelectionIndex();
				if (index != -1)
					ICallback.Utils.call(callback, gavs.get(index));
			}
		});
		table.setLayoutData(Swts.Grid.makeGrabHorizonalAndFillGridData());
		return table;
	}

	private void packColumns() {
		if (table != null)
			for (TableColumn column : table.getColumns())
				column.pack();
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
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(Swts.titleAndContentLayout(30));
				Composite titleBar = new Composite(composite, SWT.NULL);
				titleBar.setLayout(new RowLayout());
				CardShapedHolder<UnrecognisedJar> holder = new CardShapedHolder<UnrecognisedJar>(composite, cardConfig, UnrecognisedJar.makeTitleSpec(cardConfig), Swts.labelFn("Title"), new IFunction1<Composite, UnrecognisedJar>() {
					@Override
					public UnrecognisedJar apply(Composite from) throws Exception {
						UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, cardConfig, UnrecognisedJarData.forTests("projectName", new File("artifactId-1.0.0.jar")), ICallback.Utils.<GroupidArtifactVersion> sysoutCallback());
						return unrecognisedJar;
					}
				});
				for (int i = 0; i < 3; i++)
					makeButton(titleBar, holder, i);
				Swts.Row.setRowDataFor(30, SWT.DEFAULT, titleBar.getChildren());
				return composite;
			}

			private Button makeButton(Composite titleBar, final CardShapedHolder<UnrecognisedJar> holder, final int count) {
				return Swts.Buttons.makePushButton(titleBar, null, Integer.toString(count), false, new Runnable() {
					@Override
					public void run() {
						List<GroupidArtifactVersion> gavs = Lists.newList();
						for (int i = 0; i < count; i++)
							gavs.add(new GroupidArtifactVersion("groupid" + i, "artifactId", "version" + i));
						holder.getBody().populate(gavs);
					}
				});
			}

		});
	}
}
