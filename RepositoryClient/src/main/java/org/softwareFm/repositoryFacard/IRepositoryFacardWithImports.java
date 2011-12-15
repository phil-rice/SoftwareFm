package org.softwareFm.repositoryFacard;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.httpClient.requests.IResponseCallback;

public interface IRepositoryFacardWithImports extends IRepositoryFacard{
	/**
	 * This creates one or more nodes underneath the parentNode<br />
	 * assume parentUrl='content',<br />
	 * map = {'sample', {'propOne':'propOneValue, 'childOne':{'childPropOne': true}}}' would make content/sample and content/sample/childOne
	 */
	Future<?> postMany(String parentUrl, Map<String, Object> map, IResponseCallback callback);

}
