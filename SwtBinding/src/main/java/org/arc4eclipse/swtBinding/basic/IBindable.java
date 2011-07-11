package org.arc4eclipse.swtBinding.basic;

import org.arc4eclipse.repositoryFacard.IDataRipper;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;

public interface IBindable<Key, Thing, Aspect, Data> {
	void bind(IRepositoryFacard<Key, Thing, Aspect, Data> facard, IDataRipper<Data> ripper);
}
