package org.softwarefm.eclipse.annotations;

public interface IAnnotationFoundCallback {

	void process(String sfmId, String value) throws Exception;

}
