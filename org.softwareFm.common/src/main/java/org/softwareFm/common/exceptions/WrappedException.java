/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.exceptions;

public class WrappedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WrappedException(Throwable e) {
		super(e);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Throwable> T unwrap(){
		return (T) unwrap(this);
	}

	public static RuntimeException wrap(Throwable e) {
		if (e instanceof ThreadDeath)
			throw (ThreadDeath) e;// dont mess with threaddeath!
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		return new WrappedException(e);
	}

	public static Throwable unwrap(Throwable e) {
		if (e instanceof WrappedException)
			return unwrap(((WrappedException) e).getCause());
		else
			return e;
	}

}