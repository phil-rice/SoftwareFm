package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class BoundCompositeWithValue<Key, Thing, Aspect, Data> extends BoundComposite<Key, Thing, Aspect, Data> {
	private final Aspect aspect;
	protected final String[] keys;
	private Key key;
	private Thing thing;
	private Data data;

	public BoundCompositeWithValue(Composite parent, int style, Aspect aspect, String... keys) {
		super(parent, style);
		this.aspect = aspect;
		this.keys = keys;
	}

	
	@SuppressWarnings("unchecked")
	public void process(Key key, Thing thing, Aspect aspect, Data data) {
		this.key = key;
		this.thing = thing;
		this.data = data;
		for (Control control : getChildren()) {
			if (control instanceof IRepositoryFacardCallback)
				((IRepositoryFacardCallback<Key, Thing, Aspect, Data>) control).process(key, thing, aspect, data);
		}
		if (this.aspect.equals(aspect)) {
			Object value = ripper.get(data, keys);
			setFromValue(key, thing, data, value);
		}
	}

	abstract protected void setFromValue(Key key, Thing thing, Data data, Object value);

	protected void updateDataWith(Object value, IRepositoryFacardCallback<Key, Thing, Aspect, Data> callback) {
		ripper.put(data, keys, value);
		facard.setDetails(key, thing, aspect, data, callback);
	}

}
