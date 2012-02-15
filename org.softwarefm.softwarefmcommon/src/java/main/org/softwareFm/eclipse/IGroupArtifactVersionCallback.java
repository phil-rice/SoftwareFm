package org.softwareFm.eclipse;

public interface IGroupArtifactVersionCallback {

	void process(String groupId, String artifactId, String version);

	void noData();
public static class Utils{
	public static IGroupArtifactVersionCallback sysout(){
		return new IGroupArtifactVersionCallback() {
			@Override
			public void process(String groupId, String artifactId, String version) {
				System.out.println(groupId +", " + artifactId +", " + version);
			}

			@Override
			public void noData() {
				System.out.println("No data");
			}
		};
	}
}

}
