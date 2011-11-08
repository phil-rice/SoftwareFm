package org.softwareFm.card.navigation;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;

public class NavNextHistoryPrevConfig<T> {

	private final int margin = 0;
	public final int navIconWidth = 10;

	public final int leftMargin = margin;
	public final int rightMargin = margin;
	public final int topMargin = margin;
	public final int bottomMargin = margin;
	public final int height;
	public final IFunction1<String, Image> imageFn;
	public final IFunction1<T, String> stringFn;
	public final ICallback<T> gotoCallback;

	public NavNextHistoryPrevConfig(int height, IFunction1<String, Image> imageFn, IFunction1<T, String> stringFn, ICallback<T> gotoCallback) {
		super();
		this.height = height;
		this.imageFn = imageFn;
		this.stringFn = stringFn;
		this.gotoCallback = gotoCallback;
	}
}
