package org.softwareFm.server.internal;

import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitLocal;
import org.softwareFm.server.IUserReader;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.url.IUrlGenerator;

public class LocalUserReader implements IUserReader {

	private final IUrlGenerator userGenerator;
	private final IGitLocal gitLocal;

	public LocalUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
		this.gitLocal = gitLocal;
		this.userGenerator = userGenerator;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(Map<String, Object> userDetails, String cryptoKey, String property) {
		String url = userGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		Map<String, Object> data = gitLocal.getFile(fileDescription);
		if (data == null)
			throw new NullPointerException(userDetails + ", " + property);
		return (T) data.get(property);
	}

}
