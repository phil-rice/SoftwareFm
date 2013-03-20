package org.softwarefm.helloannotations.annotations;

public interface IAnnotationFoundCallback {

	void process(String sfmId, String value) throws Exception;

}
