package org.arc4eclipse.repositoryFacardConstants;

import java.util.Set;

import org.arc4eclipse.utilities.collections.Sets;

public class RepositoryFacardConstants {

	public static final String cannotEncode = "Cannot encode values of {0}. {1}";
	public static final String json = "json";
	public static final String contentType = ":contentType";
	public static final String content = ":content";
	public static final String operation = ":operation";
	public static final String importOperation = "import";
	public static final Set<Integer> okStatusCodes = Sets.makeSet(200, 201);

}
