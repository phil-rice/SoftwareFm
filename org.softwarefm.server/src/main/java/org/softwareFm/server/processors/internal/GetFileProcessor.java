package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.http.RequestLine;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class GetFileProcessor implements IProcessCall {
	private final File root;
	private final Set<String> knownExtensions;

	public GetFileProcessor(File root, String... knownExtensions) {
		this.root = root;
		this.knownExtensions = Sets.makeSet(knownExtensions);
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.GET.equals(requestLine.getMethod())) {
			String url = requestLine.getUri();
			String extension = Files.extension(url);
			if (knownExtensions.contains(extension)) {
				File file = new File(root, url);
				if (file.isFile())
					return IProcessResult.Utils.processFile(file);
			}
		}
		return null;
	}

}
