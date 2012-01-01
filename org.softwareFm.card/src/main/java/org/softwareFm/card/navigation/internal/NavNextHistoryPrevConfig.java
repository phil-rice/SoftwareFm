/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.navigation.internal;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class NavNextHistoryPrevConfig<T> {

	public static <T> NavNextHistoryPrevConfig<T> forTests() {
		return forTests(ICallback.Utils.<T> noCallback());
	}

	public static <T> NavNextHistoryPrevConfig<T> forTests(ICallback<T> callback) {
		return new NavNextHistoryPrevConfig<T>(10, Functions.<String, Image> constant(null), Functions.<T> toStringFn(), callback);
	}

	private final int defaultMargin = 0;
	private final int defaultNavIconWidth = 10;
	public final int navIconWidth;

	public final int leftMargin;
	public final int rightMargin;
	public final int topMargin;
	public final int bottomMargin;
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
		this.leftMargin = defaultMargin;
		this.rightMargin = defaultMargin;
		this.topMargin = defaultMargin;
		this.bottomMargin = defaultMargin;
		this.navIconWidth = defaultNavIconWidth;
	}

	private NavNextHistoryPrevConfig(int navIconWidth, int height, IFunction1<String, Image> imageFn, IFunction1<T, String> stringFn, ICallback<T> gotoCallback, int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		this.navIconWidth = navIconWidth;
		this.height = height;
		this.imageFn = imageFn;
		this.stringFn = stringFn;
		this.gotoCallback = gotoCallback;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;
	}

	public NavNextHistoryPrevConfig<T> withMargins(int leftMargin, int rightMargin, int topMargin, int bottomMargin) {
		return new NavNextHistoryPrevConfig<T>(navIconWidth, height, imageFn, stringFn, gotoCallback, leftMargin, rightMargin, topMargin, bottomMargin);
	}

	public NavNextHistoryPrevConfig<T> withNavIconWidth(int navIconWidth) {
		return new NavNextHistoryPrevConfig<T>(navIconWidth, height, imageFn, stringFn, gotoCallback, leftMargin, rightMargin, topMargin, bottomMargin);
	}

}