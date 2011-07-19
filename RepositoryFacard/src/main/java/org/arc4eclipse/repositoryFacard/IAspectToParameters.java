package org.arc4eclipse.repositoryFacard;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

public interface IAspectToParameters {

	List<NameValuePair> makeParameters(Map<String, Object> data);

	Map<String, Object> makeFrom(String string);

}
