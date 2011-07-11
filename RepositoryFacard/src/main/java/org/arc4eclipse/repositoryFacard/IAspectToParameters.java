package org.arc4eclipse.repositoryFacard;

public interface IAspectToParameters<Thing, Aspect, Data> {

	String[] makeParameters(Thing thing, Aspect aspect, Data data);

	Data makeFrom(String string);

	Data makeEmpty();
}
