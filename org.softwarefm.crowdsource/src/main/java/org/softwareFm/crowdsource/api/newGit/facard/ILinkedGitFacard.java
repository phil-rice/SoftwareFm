package org.softwareFm.crowdsource.api.newGit.facard;

/**
 * The operations required on the client git facard, includes the ability to <br />
 * This is just Git stuff, it doesn't interact with the server
 */
public interface ILinkedGitFacard extends IGitFacard {

	void setConfigForRemotePull(String repoRl);

	void pull(String repoRl);

}
