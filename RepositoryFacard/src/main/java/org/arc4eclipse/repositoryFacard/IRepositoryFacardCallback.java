package org.arc4eclipse.repositoryFacard;

import java.util.Map;

import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;

public interface IRepositoryFacardCallback<Key, Thing, Aspect, Data> {

	void process(Key key, Thing thing, Aspect aspect, Data data);

	public static class Utils {

		public static IRepositoryFacardCallback<Object, IJarDetails, IEntityType, Map<Object, Object>> sysoutDoneCallback() {
			return new IRepositoryFacardCallback<Object, IJarDetails, IEntityType, Map<Object, Object>>() {
				@Override
				public void process(Object key, IJarDetails thing, IEntityType aspect, Map<Object, Object> data) {
					System.out.println("Done: " + data);
				}
			};
		}

	}
}
