package org.arc4eclipse.utilities.profiling;

public abstract class VoidProfilable<Situation> implements IProfilable<Situation, Void> {

	public Void start(Situation situation) throws Exception {
		return null;
	}

	public void finish(Situation situation, Void context) throws Exception {
	}

}
