package org.softwareFm.crowdsource.api;


public interface IContainerBuilder extends IContainer {

	<T, X extends T> void register(Class<T> class1, X x);


}
