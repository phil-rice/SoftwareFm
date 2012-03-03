/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.monitor;

import java.text.MessageFormat;

/**
 * A wrapper for IProgressMonitor. I don't want to make this project dependent on eclipse...
 * 
 * The primary use for this is with the IServiceExecutor. Note that jobs return futures, and that the future has to interact with cancel. Specifically if the future is cancelled, the progress monitor needs to be told, and if the progress is cancelled, the future needs to be told
 */
public interface IMonitor {
	/** Constant indicating an unknown amount of work. */
	public final static int UNKNOWN = -1;

	/*
	 * Notifies that the main task is beginning. This must only be called once on a given progress monitor instance.
	 * 
	 * @param name the name (or description) of the main task
	 * 
	 * @param totalWork the total number of work units into which the main task is been subdivided. If the value is <code>UNKNOWN</code> the implementation is free to indicate progress in a way which doesn't require the total number of work units in advance.
	 */
	public void beginTask(String name, int totalWork);

	/* Sets the task name to the given value. This method is used to restore the task label after a nested operation was executed. Normally there is no need for clients to call this method. */
	public void setTaskName(String name);

	/** Notifies that a given number of work unit of the main task has been completed. Note that this amount represents an installment, as opposed to a cumulative amount of work done to date. */
	public void worked(int work);

	void cancel();

	boolean isCanceled();

	void done();

	public static class Utils {
		
		
		public static IMonitor noMonitor() {
			return new IMonitor() {
				@Override
				public void worked(int work) {
				}

				@Override
				public void setTaskName(String name) {
				}

				@Override
				public boolean isCanceled() {
					return false;
				}

				@Override
				public void done() {
				}

				@Override
				public void cancel() {
				}

				@Override
				public void beginTask(String name, int totalWork) {
				}
			};
		}

		public static IMonitor sysoutMonitor(final String pattern, final int quanta) {
			return new IMonitor() {

				private int soFar;
				private String taskName;
				private int totalWork;
				private boolean cancelled;

				@Override
				public void beginTask(String name, int totalWork) {
					this.taskName = name;
					this.totalWork = totalWork;
				}

				@Override
				public void worked(int work) {
					int newSoFar = soFar + work;
					if (newSoFar / quanta > soFar / quanta)
						System.out.println(MessageFormat.format(pattern, taskName, (newSoFar / quanta) * quanta, totalWork));
					soFar = newSoFar;
				}

				@Override
				public void setTaskName(String name) {
					taskName = name;
				}

				@Override
				public boolean isCanceled() {
					return cancelled;
				}

				@Override
				public void done() {
				}

				@Override
				public void cancel() {
					cancelled = true;
				}

			};
		}

		public static IMonitor sysoutMonitor(int quanta) {
			return sysoutMonitor("{0} {1} of {2}", quanta);
		}

		public static IMonitor sysoutMonitor() {
			return sysoutMonitor("{0} {1} of {2}", 1);
		}
	}

}