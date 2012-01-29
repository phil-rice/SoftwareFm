package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.Future;

import org.softwareFm.collections.explorer.internal.NewJarImporter.ImportStage;

interface IChainImporter {

	Future<?> process(Runnable afterOk, ImportStage... stages);

}