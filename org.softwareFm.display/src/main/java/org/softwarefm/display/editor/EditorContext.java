package org.softwareFm.display.editor;

import org.softwareFm.display.composites.CompositeConfig;

public class EditorContext {

	public CompositeConfig compositeConfig;
	public IUpdateStore updateStore;

	public EditorContext(CompositeConfig compositeConfig, IUpdateStore updateStore) {
		this.compositeConfig = compositeConfig;
		this.updateStore = updateStore;
	}

}
