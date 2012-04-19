/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.monitor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IMonitorFactory;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeLayout;
import org.softwareFm.swt.swt.Swts;

public class SwtThreadMonitor implements IHasComposite, IMonitorFactory {

	private final IMonitorFactory realMonitorFactory;
	private final SwtThreadMonitorComposite content;

	@SuppressWarnings("unused")
	public static class SwtThreadMonitorComposite extends DataComposite<Table> {
		private final Table table;
		private TableColumn taskName;
		private TableColumn worked;
		private TableColumn target;
		private TableColumn done;
		private TableColumn cancelled;
		private TableColumn stop;
		private final List<TableEditor> editors = Lists.newList();

		public SwtThreadMonitorComposite(Composite parent, CardConfig cardConfig, String cardType, String title) {
			super(parent, cardConfig, cardType, title);
			table = new Table(getInnerBody(), SWT.NULL);
			(taskName = new TableColumn(table, SWT.NULL)).setText("Task Name");
			(worked = new TableColumn(table, SWT.NULL)).setText("Worked");
			(target = new TableColumn(table, SWT.NULL)).setText("Target");
			(cancelled = new TableColumn(table, SWT.NULL)).setText("Cancelled");
			(done = new TableColumn(table, SWT.NULL)).setText("Done");
			(stop = new TableColumn(table, SWT.NULL)).setText("Stop");
			table.setHeaderVisible(true);

			Swts.packTables(table);
		}

		@Override
		public Table getEditor() {
			return table;
		}

		public IMonitor addAndWrapMonitor(final IMonitor monitor) {
			final IMonitor result = new IMonitor() {
				private int soFar;
				private boolean isDone;
				private TableItem tableItem;

				@Override
				public void beginTask(final String name, final int totalWork) {
					Swts.syncExec(table, new Runnable() {
						@Override
						public void run() {
							tableItem = new TableItem(table, SWT.NULL, 0);
							TableEditor tableEditor = new TableEditor(table);
							editors.add(tableEditor);
							tableEditor.minimumWidth = 50;
							tableItem.setText(new String[] {});
							String message = "Stop " + (table.getItemCount() - 1);
							Button stop = Swts.Buttons.makePushButton(table, message, new Runnable() {
								@Override
								public void run() {
									cancel();
								}
							});
							tableEditor.grabHorizontal = true;
							tableEditor.setEditor(stop, tableItem, 5);
							tableItem.setText(0, name);
							tableItem.setText(2, Integer.toString(totalWork));
							monitor.beginTask(name, totalWork);
							for (TableEditor editor : editors)
								editor.layout();
						}
					});
					updateItem();
				}

				private void updateItem() {
					Swts.syncExec(table, new Runnable() {
						@Override
						public void run() {
							tableItem.setText(1, Integer.toString(soFar));
							tableItem.setText(3, Boolean.toString(isCanceled()));
							tableItem.setText(4, Boolean.toString(isDone));
						}
					});
				}

				@Override
				public void setTaskName(String name) {
					monitor.setTaskName(name);
				}

				@Override
				public void worked(int work) {
					soFar += work;
					monitor.worked(work);
					updateItem();
				}

				@Override
				public void cancel() {
					monitor.cancel();
					updateItem();
				}

				@Override
				public boolean isCanceled() {
					return monitor.isCanceled();
				}

				@Override
				public void done() {
					isDone = true;
					monitor.done();
					updateItem();
				}
			};

			return result;
		}
	}

	public SwtThreadMonitor(Composite parent, CardConfig cardConfig, String title, IMonitorFactory realMonitorFactory) {
		this.realMonitorFactory = realMonitorFactory;
		this.content = new SwtThreadMonitorComposite(parent, cardConfig, CardConstants.threadMonitorCardType, title);
		content.setLayout(new DataCompositeLayout());
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public IMonitor createMonitor() {
		IMonitor monitor = realMonitorFactory.createMonitor();
		IMonitor result = content.addAndWrapMonitor(monitor);
		return result;
	}

	public static void main(String[] args) {
		final int stages = 5;
		final int tasks = 30;
		final IServiceExecutor helper = IServiceExecutor.Utils.defaultExecutor(SwtThreadMonitor.class.getSimpleName() + "-helper-{0}", CommonConstants.threadPoolSizeForTests);
		final AtomicReference<IServiceExecutor> ref = new AtomicReference<IServiceExecutor>();
		try {
			Swts.Show.display(SwtThreadMonitor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					CardConfig cardConfig = ICardConfigurator.Utils.cardConfigForTests(from.getDisplay());
					SwtThreadMonitor swtThreadMonitor = new SwtThreadMonitor(from, cardConfig, "Threads", IMonitorFactory.Utils.sysoutMonitors);
					final IServiceExecutor executor = IServiceExecutor.Utils.executor(SwtThreadMonitor.class.getSimpleName() + "-main-{0}", 5, swtThreadMonitor);
					ref.set(executor);
					helper.submit(new IFunction1<IMonitor, Void>() {
						@Override
						public Void apply(IMonitor from) throws Exception {
							Thread.sleep(1000);
							from.beginTask("Helping", stages);
							try {
								for (int task = 0; task < tasks; task++) {
									final int taskNo = task;
									executor.submit(new IFunction1<IMonitor, Void>() {
										@Override
										public Void apply(IMonitor from) throws Exception {
											from.beginTask("Task: " + taskNo, stages);
											try {
												for (int stage = 0; stage < stages; stage++)
													if (from.isCanceled())
														return null;
													else {
														from.worked(1);
														Thread.sleep(500);
													}
												return null;
											} finally {
												from.done();
											}
										}
									});
								}
								return null;
							} finally {
								from.done();
							}
						}
					});
					return swtThreadMonitor.getComposite();
				}
			});

		} finally {
			try {
				helper.shutdownAndAwaitTermination(CommonConstants.clientTimeOut, TimeUnit.SECONDS);
				IServiceExecutor main = ref.get();
				if (main != null)
					main.shutdownAndAwaitTermination(CommonConstants.clientTimeOut, TimeUnit.SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
				throw WrappedException.wrap(e);
			}
		}
	}
}