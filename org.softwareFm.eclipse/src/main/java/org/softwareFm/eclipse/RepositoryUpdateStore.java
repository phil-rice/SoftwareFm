package org.softwareFm.eclipse;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.editor.IUpdateStore;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.PreAndPost;
import org.softwareFm.utilities.strings.Strings;

public class RepositoryUpdateStore implements IUpdateStore {

	private final IRepositoryFacard repository;
	private final IStoreUpdatedCallback callback;

	public RepositoryUpdateStore(IRepositoryFacard repository, IStoreUpdatedCallback callback) {
		this.repository = repository;
		this.callback = callback;
	}

	@Override
	public void update(ActionData actionData, String key, final Object newValue) {
		if (!key.startsWith("data."))
			throw new IllegalArgumentException(MessageFormat.format(EclipseConstants.keyMustStartWithDataDot, actionData, key, newValue));
		PreAndPost entityAndAttribute = Strings.split(key.substring(5), '.');
		final String entity = entityAndAttribute.pre;
		final String attribute = entityAndAttribute.post;
		final String url = actionData.url.get(entity);
		Map<String, Object> data = Maps.<String, Object> makeMap(attribute, newValue);
		repository.post(url, data, new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				System.out.println("Updated: " + response);
				System.out.println( "...status" + response.statusCode());
				callback.storeUpdates(url, entity, attribute, newValue);
			}
		});

	}

}
