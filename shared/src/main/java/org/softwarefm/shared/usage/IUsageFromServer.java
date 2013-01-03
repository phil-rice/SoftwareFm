package org.softwarefm.shared.usage;

import java.util.List;

import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.internal.UsageFromServer;

public interface IUsageFromServer {

 void getStatsFor(String name, IUsageFromServerCallback callback);

	 void getStatsFor(List<FriendData> friendDatas, IUsageFromServerCallback callback);
	 
	 public static class Utils{
		 public static IUsageFromServer usageFromServer(String host, int port, IUsagePersistance persistance){
			 return new UsageFromServer(host, port, persistance);
		 }
		 
	 }

}