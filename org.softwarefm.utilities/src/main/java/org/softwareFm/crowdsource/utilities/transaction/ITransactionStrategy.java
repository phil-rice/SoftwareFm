package org.softwareFm.crowdsource.utilities.transaction;

import java.util.Collections;
import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

/** This executes the job. If the job throws RedoTransactionException this takes appropriate action (i.e. redoing a number of times / redoing it for ever / backing off a while and trying again etc...) */
public interface ITransactionStrategy {

	<To> To execute(IFunction1<IMonitor, To> job, IMonitor monitor) throws Exception;

	public static class Utils {
		public static ITransactionStrategy oneTryOnly() {
			return new ITransactionStrategy() {
				@Override
				public <To> To execute(IFunction1<IMonitor, To> job, IMonitor monitor) throws Exception {
					try {
						return job.apply(monitor);
					} catch (RedoTransactionException e) {
						throw new PoisonedTransactionException(job, Collections.singletonList(e));
					}
				}
			};
		}

		public static ITransactionStrategy backOffAndRetry(final long backOffTimeMs, final int retryCount) {
			return new ITransactionStrategy() {

				@Override
				public <To> To execute(IFunction1<IMonitor, To> job, IMonitor monitor) throws Exception {
					List<RedoTransactionException> exceptions = Lists.newList();
					while (true)
						try {
							return job.apply(monitor);
						} catch (RedoTransactionException e) {
							exceptions.add(e);
							Thread.sleep(backOffTimeMs);
							if (exceptions.size() >= retryCount)
								throw new PoisonedTransactionException(job, exceptions);
						}
				}
			};
		}
	}

}
