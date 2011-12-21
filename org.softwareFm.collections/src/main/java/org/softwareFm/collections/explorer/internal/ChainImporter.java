package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.Future;

import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.collections.explorer.internal.NewJarImporter.ImportStage;
import org.softwareFm.utilities.future.Futures;

class ChainImporter implements IChainImporter {

	private final IMutableCardDataStore cardDataStore;

	public ChainImporter(IMutableCardDataStore cardDataStore) {
		this.cardDataStore = cardDataStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.collections.explorer.internal.IChainImporter#process(java.lang.Runnable, org.softwareFm.collections.explorer.internal.NewJarImporter.ImportStage)
	 */
	@Override
	public Future<?> process(Runnable afterOk, ImportStage... stages) {
		return process(afterOk, 0, stages);
	}

	private Future<?> process(final Runnable afterOk, final int index, final ImportStage[] stages) {
		if (index >= stages.length) {
			afterOk.run();
			return Futures.doneFuture(null);
		}
		ImportStage stage = stages[index];
		switch (stage.command) {
		case MAKE_REPO:
			return cardDataStore.makeRepo(stage.url,  new IAfterEditCallback() {
				@Override
				public void afterEdit(String url) {
					process(afterOk, index + 1, stages);
				}
			});
		case POST_DATA:
			if (stage.url == null)
				return process(afterOk, index + 1, stages);
			else
				return cardDataStore.put(stage.url, stage.data, new IAfterEditCallback() {
					@Override
					public void afterEdit(String url) {
						process(afterOk, index + 1, stages);
					}
				});
		default:
			throw new IllegalStateException(stage.command.toString());
		}
	}

}