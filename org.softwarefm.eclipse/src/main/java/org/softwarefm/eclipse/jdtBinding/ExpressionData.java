/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.eclipse.jdtBinding;


public class ExpressionData {

	public final String packageName;
	public final String className;
	public final String methodName;

	public ExpressionData(String packageName, String className, String methodName) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
	}

	public ExpressionData(String packageName, String className) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.methodName = null;
	}

	@Override
	public String toString() {
		return "ExpressionData [packageName=" + packageName + ", className=" + className + ", methodName=" + methodName + "]";
	}

}