package org.softwarefm.server.friend.internal;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.strings.Strings;

public class FriendServerHandler implements HttpRequestHandler {

	private final IFriendAndFriendManager manager;
	private final IUsagePersistance usagePersistance;

	public FriendServerHandler(IUsagePersistance usagePersistance, IFriendAndFriendManager manager) {
		this.usagePersistance = usagePersistance;
		this.manager = manager;
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		try {
			String method = request.getRequestLine().getMethod();
			String uri = request.getRequestLine().getUri();
			List<String> fragments = Strings.splitIgnoreBlanks(uri, "/");
			if (method.equalsIgnoreCase("GET")) {
				int index = findMarker(method, fragments, "friendsUsage", 1);
				String user = fragments.get(index+1);
				ISimpleMap<String, IUsageStats> friendsUsage = manager.friendsUsage(user);
				String text = usagePersistance.save(friendsUsage);
				response.setEntity(new StringEntity(text));

			} else {
				int index = findMarker(method, fragments, "user", 2);
				String user = fragments.get(index + 1);
				String friend = fragments.get(index + 2);
				if (method.equalsIgnoreCase("POST") || method.equals("PUT")) {
					manager.add(user, friend);
				} else if (method.equalsIgnoreCase("DELETE")) {
					manager.delete(user, friend);

				} else
					throw new HttpException("Illegal method " + method);
			}
		} catch (HttpException e) {
			throw e;
		} catch (Throwable e) {
			throw new HttpException("Unexpected error ", e);
		}
	}

	private int findMarker(String method, List<String> fragments, String marker, int sizeAfterMarker) throws HttpException {
		int index = fragments.indexOf(marker);
		if (index == -1 || fragments.size() != index + sizeAfterMarker + 1)
			throw new HttpException("Malformed Url. Method " + method + " Url: " + fragments);
		return index;
	}

}
