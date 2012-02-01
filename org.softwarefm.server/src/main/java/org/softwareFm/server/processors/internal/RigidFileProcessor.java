package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.http.RequestLine;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class RigidFileProcessor implements IProcessCall {

	private final Set<String> roots;
	private final File fileRoot;

	public RigidFileProcessor(File fileRoot, String... roots) {
		this.fileRoot = fileRoot;
		this.roots = Sets.makeSet(roots);
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		String method = requestLine.getMethod();
		if (CommonConstants.GET.equals(requestLine.getMethod()) || CommonConstants.HEAD.equals(method)) {
			String url = requestLine.getUri().substring(1);
			int index = url.indexOf('/');
			if (index != -1) {
				String root = url.substring(0, index);
				if (roots.contains(root)) {
					File file = new File(fileRoot, url);
					if (file.isFile())
						if (method.equals("HEAD"))
							return IProcessResult.Utils.processString("Found");
						else
							return IProcessResult.Utils.processFile(file);
					else
						return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, CommonMessages.notFoundMessage);
				}
			}
		}
		return null;
	}

}
