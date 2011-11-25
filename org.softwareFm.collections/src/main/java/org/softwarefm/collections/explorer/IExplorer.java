package org.softwarefm.collections.explorer;

import java.io.File;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.display.browser.IBrowserCompositeBuilder;
import org.softwareFm.display.timeline.ITimeLine;

public interface IExplorer extends IBrowserCompositeBuilder, ITimeLine {

	void displayCard(String url, ICardAndCollectionDataStoreVisitor visitor);

	void displayUnrecognisedJar(File file, String digest);

	void displayComments(String url);

	void addCardListener(ICardChangedListener listener);

	void showContents();

	void showAddCollectionItemEditor(final ICard card, final RightClickCategoryResult result);
	
	void showAddNewArtifactEditor();

}
