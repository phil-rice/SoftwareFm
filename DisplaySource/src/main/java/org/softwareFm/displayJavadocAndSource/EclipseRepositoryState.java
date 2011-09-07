package org.softwareFm.displayJavadocAndSource;

public class EclipseRepositoryState {

	public boolean eclipsePresent;
	public boolean repositoryPresent;
	public String eclipseValue;
	public String repositoryValue;
	private final String tooltipIfNotEclipseNotPresent;
	private final String tooltipIfRepositoryNotPresent;

	public EclipseRepositoryState(String eclipseValue, String repositoryValue, String tooltipIfNotEclipseNotPresent, String tooltipIfRepositoryNotPresent) {
		this.tooltipIfNotEclipseNotPresent = tooltipIfNotEclipseNotPresent;
		this.tooltipIfRepositoryNotPresent = tooltipIfRepositoryNotPresent;
		this.eclipsePresent = eclipseValue != null && eclipseValue.length() > 0;
		this.repositoryPresent = repositoryValue != null && repositoryValue.length() > 0;
		this.eclipseValue = eclipseValue;
		this.repositoryValue = repositoryValue;
	}

	public String repositoryTooltip() {
		return repositoryPresent ? repositoryValue : tooltipIfRepositoryNotPresent;
	}

	public String eclipseTooltip() {
		return eclipsePresent ? eclipseValue : tooltipIfNotEclipseNotPresent;
	}

	@Override
	public String toString() {
		return "EclipseRepositoryState [eclipseValue=" + eclipseValue + ", repositoryValue=" + repositoryValue + "]";
	}

	public EclipseRepositoryState withEclipseValue(String eclipseValue) {
		return new EclipseRepositoryState(repositoryValue, eclipseValue, tooltipIfNotEclipseNotPresent, tooltipIfRepositoryNotPresent);
	}

}
