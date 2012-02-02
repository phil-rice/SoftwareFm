/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation.internal;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.constants.CardConstants;

public class NavIconFunction implements IFunction1<Map<String, Object>, Image> {
	private final IFunction1<String, Image> imageFn;
	private final IFunction1<String, IResourceGetter> resourceGetterFn;

	public NavIconFunction(IFunction1<String, IResourceGetter> resourceGetterFn, IFunction1<String, Image> imageFn) {
		this.imageFn = imageFn;
		this.resourceGetterFn = resourceGetterFn;
	}

	@Override
	public Image apply(Map<String, Object> from) throws Exception {
		String cardType = (String) from.get("sling:resourceType");
		String iconName = IResourceGetter.Utils.getOr(resourceGetterFn, cardType, CardConstants.navIcon, "title." + cardType);
		return Functions.call(imageFn, iconName);
	}
}