package org.softwareFm.configuration.configurators;

import org.softwareFm.display.editor.IListEditorStoreConfigurator;
import org.softwareFm.display.lists.ListEditorStore;
import org.softwareFm.display.lists.NameAndUrlListEditor;
import org.softwareFm.display.lists.NameAndValueListEditor;
import org.softwareFm.display.lists.ValueListEditor;

public class ListEditorConfigurator implements IListEditorStoreConfigurator {

	@Override
	public void process(ListEditorStore listEtditorStore) throws Exception {
		listEtditorStore.//
				register("listEditor.nameAndValue", new NameAndValueListEditor("nameAndValue.line.title")).//
				register("listEditor.nameAndUrl", new NameAndUrlListEditor("nameAndUrl.line.title")).//
				register("listEditor.nameAndEmail", new NameAndValueListEditor("nameAndEmail.line.title")).//
				register("listEditor.tweet", new ValueListEditor("tweet.line.title"));
	}

}
