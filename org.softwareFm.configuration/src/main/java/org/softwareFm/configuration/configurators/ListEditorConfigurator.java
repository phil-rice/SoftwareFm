package org.softwareFm.configuration.configurators;

import org.softwareFm.display.editor.IListEditorStoreConfigurator;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.lists.NameAndUrlListEditor;
import org.softwareFm.display.lists.NameAndValueListEditor;
import org.softwareFm.display.lists.ValueListEditor;

public class ListEditorConfigurator implements IListEditorStoreConfigurator {

	@Override
	public void process(ListEditorStore listEditorStore) throws Exception {
		listEditorStore.//
				register("listEditorId.nameAndValue", new NameAndValueListEditor("nameAndValue.line.title")).//
				register("listEditorId.nameAndUrl", new NameAndUrlListEditor("nameAndUrl.line.title")).//
				register("listEditorId.nameAndEmail", new NameAndValueListEditor("nameAndEmail.line.title")).//
				register("listEditorId.tweet", new ValueListEditor("tweet.line.title"));
	}

}
