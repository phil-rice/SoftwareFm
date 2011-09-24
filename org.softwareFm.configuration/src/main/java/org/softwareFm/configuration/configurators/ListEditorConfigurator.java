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
				register("listEditorId.tweet", new ValueListEditor("tweet.line.title")).//
				register("listEditorId.facebook", new ValueListEditor("facebook.line.title")).//
				register("listEditorId.rss", new ValueListEditor("rss.line.title")).//
				register("listEditorId.tutorials", new ValueListEditor("tutorials.line.title")).//
				register("listEditorId.blogs", new ValueListEditor("blogs.line.title"));
	}

}
