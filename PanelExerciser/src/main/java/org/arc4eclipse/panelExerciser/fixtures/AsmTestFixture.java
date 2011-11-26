/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.arc4eclipse.panelExerciser.fixtures;

import org.arc4eclipse.jdtBinding.mocks.IBindingBuilder;
import org.eclipse.jdt.core.dom.IBinding;

//This is a jar that doesn't have any data about it in the repository
public class AsmTestFixture {

	public static final String asmJar = "../PanelExerciser/src/main/resources/asm-all-3.1.jar";

	public final static IBinding Method_AppenderSkeleton_doAppend = IBindingBuilder.Utils.//
			parent(asmJar, AsmTestFixture.class).withPackage("org.objectweb.asm").withClass("ClassReader").//
			child().withMethod("getAccess");

}