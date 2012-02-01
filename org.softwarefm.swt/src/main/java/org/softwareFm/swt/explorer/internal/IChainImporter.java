package org.softwareFm.swt.explorer.internal;

import java.util.concurrent.Future;

import org.softwareFm.swt.explorer.internal.NewJarImporter.ImportStage;

interface IChainImporter {

	Future<?> process(Runnable afterOk, ImportStage... stages);

}