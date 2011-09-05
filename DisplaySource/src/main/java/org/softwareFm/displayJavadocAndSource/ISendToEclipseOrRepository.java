package org.softwareFm.displayJavadocAndSource;

public interface ISendToEclipseOrRepository {

	void sendToEclipse(String value);

	void clearEclipseValue();

	void sendToRepository(String value) throws Exception;
}
