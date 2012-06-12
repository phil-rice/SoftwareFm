::This populates the org.softwarefm.softwarefm lib with all the maven dependencies
::I would like to do this automatically, but it is quite hard to do that
::To populate this file, go to the package explorer / org.softwarefm.eclipse / Maven Dependencies
::Select all the jars
::You only need to do this when you have changed the maven dependencies
::Drop in here and use search and replace to make them look like the examples below
::Example: copy %m2%\junit\junit\4.10\junit-4.10.jar lib
::
::IMPORTANT ensure you remove
::::copy %m2%\org\eclipse\equinox\common\3.3.0-v20070426\common-3.3.0-v20070426.jar lib
::::this is effectively the core runtime, and clashes with maven


copy %m2%\org\hamcrest\hamcrest-core\1.1\hamcrest-core-1.1.jar lib
copy %m2%\org\apache\httpcomponents\httpclient\4.1.1\httpclient-4.1.1.jar lib
copy %m2%\org\apache\httpcomponents\httpcore\4.1\httpcore-4.1.jar lib
copy %m2%\commons-logging\commons-logging\1.1.1\commons-logging-1.1.1.jar lib
copy %m2%\commons-codec\commons-codec\1.4\commons-codec-1.4.jar lib
copy %m2%\org\jdom\jdom\1.1.3\jdom-1.1.3.jar lib
copy %m2%\org\eclipse\swt\org.eclipse.swt.win32.win32.x86_64\3.7.2\org.eclipse.swt.win32.win32.x86_64-3.7.2.jar lib
copy %m2%\org\eclipse\jface\3.3.0-I20070606-0010\jface-3.3.0-I20070606-0010.jar lib
copy %m2%\org\eclipse\swt\3.3.0-v3346\swt-3.3.0-v3346.jar lib
copy %m2%\org\eclipse\core\commands\3.3.0-I20070605-0010\commands-3.3.0-I20070605-0010.jar lib
copy %m2%\org\eclipse\equinox\common\3.3.0-v20070426\common-3.3.0-v20070426.jar lib
copy %m2%\org\easymock\easymock\2.4\easymock-2.4.jar lib
copy %m2%\org\apache\maven\maven-plugin-api\3.0\maven-plugin-api-3.0.jar lib
copy %m2%\org\apache\maven\maven-model\3.0\maven-model-3.0.jar lib
copy %m2%\org\codehaus\plexus\plexus-utils\2.0.4\plexus-utils-2.0.4.jar lib
copy %m2%\org\apache\maven\maven-artifact\3.0\maven-artifact-3.0.jar lib
copy %m2%\org\sonatype\sisu\sisu-inject-plexus\1.4.2\sisu-inject-plexus-1.4.2.jar lib
copy %m2%\org\codehaus\plexus\plexus-component-annotations\1.5.4\plexus-component-annotations-1.5.4.jar lib
copy %m2%\org\codehaus\plexus\plexus-classworlds\2.2.3\plexus-classworlds-2.2.3.jar lib
copy %m2%\org\sonatype\sisu\sisu-inject-bean\1.4.2\sisu-inject-bean-1.4.2.jar lib
copy %m2%\org\sonatype\sisu\sisu-guice\2.1.7\sisu-guice-2.1.7-noaop.jar lib


