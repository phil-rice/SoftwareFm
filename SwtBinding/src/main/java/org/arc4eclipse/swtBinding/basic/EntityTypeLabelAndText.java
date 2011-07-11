package org.arc4eclipse.swtBinding.basic;

import java.util.Map;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.swt.widgets.Composite;

public class EntityTypeLabelAndText<Key> extends BoundLabelAndText<Key, IBinding, IEntityType, Map<Object, Object>> {

	public EntityTypeLabelAndText(Composite parent, int style, String title, IEntityType aspect, String... keys) {
		super(parent, style, title, aspect, keys);
	}

}
