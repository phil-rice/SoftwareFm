package org.softwareFm.common.internal;

import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.url.IUrlGenerator;

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
		Map<String, Object> data = getUserData(userDetails, cryptoKey);
		if (data == null)
			throw new NullPointerException(userDetails + ", " + property);
		return (T) data.get(property);
	}

	protected Map<String, Object> getUserData(Map<String, Object> userDetails, String cryptoKey) {
		String url = userGenerator.findUrlFor(userDetails);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		Map<String, Object> data = gitLocal.getFile(fileDescription);
		return data;
	}



}
