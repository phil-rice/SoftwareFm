package org.softwareFm.server;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.processors.FindRootProcessor;
import org.softwareFm.server.processors.MakeRootProcessor;
import org.softwareFm.server.processors.PostProcessor;

public interface IProcessCall {
	String process(RequestLine requestLine, Map<String, Object> parameters);

	public static class Utils {

		public static IProcessCall chain(final IProcessCall... processors) {
			return new IProcessCall() {
				@Override
				public String process(RequestLine requestLine, Map<String, Object> parameters) {
					for (IProcessCall processor : processors) {
						String result = processor.process(requestLine, parameters);
						if (result != null)
							return result;
					}
					throw new IllegalArgumentException(Utils.toString(requestLine, parameters));
				}
			};
		}

		public static IProcessCall toStringProcessCall() {
			return new IProcessCall() {

				@Override
				public String process(RequestLine requestLine, Map<String, Object> parameters) {
					return Utils.toString(requestLine, parameters);
				}

			};
		}

		private static String toString(RequestLine requestLine, Map<String, Object> parameters) {
			return "<" + requestLine + ", " + parameters + ">";
		}

		public static IProcessCall softwareFmProcessCall(IGitServer server) {
			return chain(new FindRootProcessor(server), //
					new MakeRootProcessor(server), //
					new PostProcessor(server));// this sweeps up any posts, so ensure that commands appear in chain before it
		}
	}
}