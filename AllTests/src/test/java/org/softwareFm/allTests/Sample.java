package org.softwareFm.allTests;

import java.io.File;

import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Ant;
import org.codehaus.cake.forkjoin.RecursiveTask;

public class Sample {
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(Sample.class);
		new Ant();
		new TestSuite();
		File file = new File("");
		RecursiveTask<String> task;
	}
}
