package org.softwareFm.swt.debug;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutorLifeCycleListener;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.editors.DataComposite;
import org.softwareFm.swt.editors.DataCompositeLayout;
import org.softwareFm.swt.swt.Swts;

public class SwtServiceLifeCycleListener implements IHasComposite, IServiceExecutorLifeCycleListener {
	public static class SwtServiceLifeCycleListenerComposite extends DataComposite<StyledText> {

		private final StyledText text;

		public SwtServiceLifeCycleListenerComposite(Composite parent, CardConfig cardConfig, String cardType, String title) {
			super(parent, cardConfig, cardType, title);
			text = new StyledText(getInnerBody(), SWT.H_SCROLL | SWT.V_SCROLL);
			text.setEditable(false);
		}

		@Override
		public StyledText getEditor() {
			return text;
		}
	}

	private final SwtServiceLifeCycleListenerComposite content;

	public SwtServiceLifeCycleListener(Composite parent, CardConfig cardConfig, String title) {
		content = new SwtServiceLifeCycleListenerComposite(parent, cardConfig, CommonConstants.debugCardType, title);
		content.setLayout(new DataCompositeLayout());
	}

	@Override
	public void starting(final IFunction1<IMonitor, ?> task) {
		final String name = Thread.currentThread().getName();
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				content.getEditor().append(name + ", Starting: " + task + "\n");
			}
		});
	}

	@Override
	public <T> void finished(final org.softwareFm.common.functions.IFunction1<IMonitor, T> task, final T value) {
		final String name = Thread.currentThread().getName();
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				content.getEditor().append(name + ", Finished: " + task + ", " + value + "\n");
			}
		});
	}

	@Override
	public void exception(final IFunction1<IMonitor, ?> task, final Throwable throwable) {
		final String name = Thread.currentThread().getName();
		Swts.asyncExec(this, new Runnable() {
			@Override
			public void run() {
				content.getEditor().append(name + ", Exception: " + task + ", " + throwable + "\n");
			}

		});
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		final ScheduledThreadPoolExecutor helperExecutor = new ScheduledThreadPoolExecutor(5);
		final IServiceExecutor executor = IServiceExecutor.Utils.defaultExecutor();
		try {
			Swts.Show.display(SwtServiceLifeCycleListener.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(from.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), new CardDataStoreMock()));
					SwtServiceLifeCycleListener swtServiceLifeCycleListener = new SwtServiceLifeCycleListener(from, cardConfig, "Test");
					executor.addLifeCycleListener(swtServiceLifeCycleListener);
					helperExecutor.schedule(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							for (int i = 0; i < 20; i++) {
								final int count = i;
								executor.submit(new IFunction1<IMonitor, Integer>() {
									private final int myNumber = count;

									@Override
									public Integer apply(IMonitor monitor) throws Exception {
										monitor.beginTask("Some Job", 1);
										try {
											if (myNumber % 4 == 0)
												throw new RuntimeException("Exception in " + myNumber);
											return myNumber;
										} finally {
											monitor.done();
										}
									}

									@Override
									public String toString() {
										return "callable(" + myNumber + ")";
									}
								});
							}
							return null;
						}
					}, 1, TimeUnit.SECONDS);
					return swtServiceLifeCycleListener.getComposite();
				}
			});
		} finally {
			helperExecutor.shutdown();
			executor.shutdownAndAwaitTermination(CommonConstants.clientTimeOut, TimeUnit.MILLISECONDS);

		}
	}
}
