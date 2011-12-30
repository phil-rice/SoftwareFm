package org.softwareFm.utilities.collections;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class SampleFile {
	public static void main(String args[]) throws IOException {

		java.io.BufferedInputStream in = new java.io.BufferedInputStream(new

		java.net.URL("someurl").openStream());
		java.io.FileOutputStream fos = new java.io.FileOutputStream("sometargetfile");
		java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
		byte data[] = new byte[1024];
		while (in.read(data, 0, 1024) >= 0) {
			bout.write(data);
		}
		bout.close();
		in.close();
	}
}