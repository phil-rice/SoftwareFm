package org.softwareFm.explorer.eclipse;

import java.util.List;

import org.softwareFm.collections.constants.CollectionConstants;

public class AdminView extends ExplorerView {

	@Override
	protected List<String> getRootUrls() {
		return CollectionConstants.rootUrlList;

	}
}
