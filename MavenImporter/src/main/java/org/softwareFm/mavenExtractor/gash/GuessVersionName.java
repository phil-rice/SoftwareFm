package org.softwareFm.mavenExtractor.gash;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwareFm.collections.unrecognisedJar.GuessArtifactAndVersionDetails;
import org.softwareFm.mavenExtractor.IExtractorCallback;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.mavenExtractor.git.ExtractProjectStuff;
import org.softwareFm.utilities.callbacks.ICallback;

public class GuessVersionName implements IExtractorCallback {

	private final AtomicInteger succeeded = new AtomicInteger();
	private final AtomicInteger failed = new AtomicInteger();

	public GuessVersionName() {
	}

	public static void main(String[] args) {
		new ExtractProjectStuff().walk(MavenImporterConstants.dataSource, //
				new GuessVersionName(), //
				ICallback.Utils.sysErrCallback());
	}

	public void process(String project, String version, String jarUrl, Model model) throws Exception {
		if (jarUrl.equals("http://repo1.maven.org/maven2/"))
			return;
		GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
		String guessed = guesser.guessVersion(new File(jarUrl));
		String actual = getVersion(model);
		if (guessed.equals(actual))
			succeeded.incrementAndGet();
		else{
			failed.incrementAndGet();
			System.out.println("Failed: " + jarUrl + " actual: " + actual +" guessed: " + guessed);
		}
	}

	public void finished() {
		System.out.println("Succeeded: " + succeeded.get());
		System.out.println("Failed: " + failed.get());
	}

	private String getVersion(Model model) {
		String version = model.getVersion();
		if (version != null)
			return version;
		Parent parent = model.getParent();
		return parent.getVersion();
	}
}
