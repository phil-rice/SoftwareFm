package org.arc4eclipse.swtBinding.basic;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.arc4eclipse.repositoryFacard.IDataRipper;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.swtBinding.constants.SwtBindingMessages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BoundComposite<Key, Thing, Aspect, Data> extends Composite implements IRepositoryFacardCallback<Key, Thing, Aspect, Data>, IBindable<Key, Thing, Aspect, Data> {

	protected final Logger logger = Logger.getLogger(getClass());

	protected IRepositoryFacard<Key, Thing, Aspect, Data> facard;
	protected IDataRipper<Data> ripper;

	public BoundComposite(Composite parent, int style) {
		super(parent, style);
	}

	@SuppressWarnings("unchecked")
	public void process(Key key, Thing thing, Aspect aspect, Data data) {
		logger.debug(MessageFormat.format(SwtBindingMessages.process, getClass().getSimpleName(), aspect, data));
		for (Control control : getChildren()) {
			if (control instanceof IRepositoryFacardCallback)
				((IRepositoryFacardCallback<Key, Thing, Aspect, Data>) control).process(key, thing, aspect, data);
		}
	}

	@SuppressWarnings("unchecked")
	public void bind(IRepositoryFacard<Key, Thing, Aspect, Data> facard, IDataRipper<Data> ripper) {
		logger.debug(MessageFormat.format(SwtBindingMessages.bind, getClass().getSimpleName(), facard, ripper));
		this.facard = facard;
		this.ripper = ripper;
		for (Control control : getChildren()) {
			if (control instanceof IBindable)
				((IBindable<Key, Thing, Aspect, Data>) control).bind(facard, ripper);
		}

	}

}
