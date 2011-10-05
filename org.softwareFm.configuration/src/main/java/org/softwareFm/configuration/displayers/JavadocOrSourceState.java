package org.softwareFm.configuration.displayers;

public class JavadocOrSourceState {

	public final String eclipseValue;
	public final String softwareFmValue;
	public final boolean hasEclipse;
	public final boolean hasSoftwareFm;
	public final boolean matches;
	public final boolean eclipseValueIsJar;
	public final boolean eclipseValueIsHttp;

	public JavadocOrSourceState(String eclipseValue, String softwareFmValue) {
		this.eclipseValue = eclipseValue;
		this.softwareFmValue = softwareFmValue;
		hasEclipse = eclipseValue != null && eclipseValue.length() > 0;
		hasSoftwareFm = softwareFmValue != null && softwareFmValue.length() > 0;
		matches = hasEclipse && hasSoftwareFm && eclipseValue.equals(softwareFmValue);
		eclipseValueIsHttp = hasEclipse&& eclipseValue.startsWith("http://");
		eclipseValueIsJar = hasEclipse && eclipseValue.endsWith("jar");
	}

}
