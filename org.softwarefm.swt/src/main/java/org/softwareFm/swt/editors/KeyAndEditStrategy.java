/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;


public class KeyAndEditStrategy {
	public final boolean displayLabel;
	public final String key;
	public final IEditableControlStrategy<?> editableControlStrategy;

	public KeyAndEditStrategy(String key, IEditableControlStrategy<?> editableControlStrategy) {
		this(true, key, editableControlStrategy);
	}
	public KeyAndEditStrategy(boolean displayLabel, String key, IEditableControlStrategy<?> editableControlStrategy) {
		this.displayLabel = displayLabel;
		this.key = key;
		this.editableControlStrategy = editableControlStrategy;
	}

}