import java.io.File;

import org.softwareFm.common.collections.Files;

public class time {

	public static void main(String[] args) {
		while (true) {
			long startTime = System.currentTimeMillis();
			Files.digest(new File("C:/Program Files/Java/jdk1.6.0_29/jre/lib/rt.jar"));
			System.out.println(System.currentTimeMillis() - startTime);
		}
	}

}
