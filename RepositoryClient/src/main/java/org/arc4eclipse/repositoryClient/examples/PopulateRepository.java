package org.arc4eclipse.repositoryClient.examples;

import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.repositoryClient.api.IEntityType;
import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;

public class PopulateRepository {

	public static void main(String[] args) throws SecurityException, NoSuchMethodException {
		IJarDetails jarDetails = IJarDetails.Utils.makeJarDetails("jdk1.6.0_24_jre_lib_rt.jar", "1.6.0_24");
		IRepositoryClient<Object, IEntityType> repositoryClient = IRepositoryClient.Utils.repositoryClientForClassAndMethod(jarDetails);
		IResponseCallback<Object, IEntityType> callback = IResponseCallback.Utils.sysoutStatusCallback();

		Object thing = String.class.getMethod("toUpperCase");
		repositoryClient.setDetails(thing, IEntityType.METHOD, callback, "comment", "The toUpperCase method");
		repositoryClient.setDetails(thing, IEntityType.PROJECT, callback, "comment", "A very jdk / rt jar");
		repositoryClient.setDetails(thing, IEntityType.RELEASE, callback, "comment", "The release");
		repositoryClient.setDetails(thing, IEntityType.PACKAGE, callback, "comment", "The utils package");
		repositoryClient.setDetails(thing, IEntityType.CLASS, callback, "comment", "The String class");
	}
}
