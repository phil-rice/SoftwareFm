/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.unrecognisedJar;

import java.awt.Desktop;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardTable;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitleSpec;

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
		setColumnTitle(cardConfig, groupColumn, SoftwareFmConstants.groupId);
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

}