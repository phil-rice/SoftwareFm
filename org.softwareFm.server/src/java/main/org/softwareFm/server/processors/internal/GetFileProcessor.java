package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;

public class GetFileProcessor implements IProcessCall {
	private final File root;

	public GetFileProcessor(File root) {
		this.root = root;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (ServerConstants.GET.equals(requestLine.getMethod())) {
			String url = requestLine.getUri();
			String extension = Files.extension(url);
			if (extension.length()>0){
				File file = new File(root, url);
				return IProcessResult.Utils.processFile(file);
			}
		}
		return null;
	}

}
