package org.softwarefm.core.composite;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.IHasSelectionBindingManager;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

abstract public class SoftwareFmComposite extends HasComposite implements IHasSelectionBindingManager {
	private final List<ISelectedBindingListener> listeners = Lists.newList();
	private final ISelectedBindingManager<?> selectionBindingManager;
	protected final IResourceGetter resourceGetter;
	protected final IUrlStrategy urlStrategy;
	protected ICallback2<Object, String> logger;
	protected final SoftwareFmContainer<?> container;

	public SoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent);
		this.container = container;
		this.selectionBindingManager = container.selectedBindingManager;
		this.resourceGetter = container.resourceGetter;
		urlStrategy = container.urlStrategy;
	}

	public void setLogger(ICallback2<Object, String> logger) {
		this.logger = logger;
	}

	protected void log(Object source, String message, Object...args) {
		if (logger != null)
			ICallback2.Utils.call(logger, source, MessageFormat.format(message, args));
	}

	protected void addListener(ISelectedBindingListener listener) {
		listeners.add(listener);
		selectionBindingManager.addSelectedArtifactSelectionListener(listener);
	}

	public String msg(String key, Object... args) {
		return IResourceGetter.Utils.getMessageOrException(resourceGetter, key, args);
	}

	public String searchingMsg() {
		return Strings.oneLine(msg(TextKeys.msgSearching));
	}

	public String unknownDigestMsg(String key, FileAndDigest fileAndDigest) {
		assert fileAndDigest.digest != null : fileAndDigest;
		String result = msg(key, fileAndDigest.file.getPath(), fileAndDigest.digest, Strings.firstNCharacters(fileAndDigest.digest, 6));
		return result;
	}

	public String digestDeterminedMsg(String key, FileAndDigest fileAndDigest) {
		return msg(key, //
				fileAndDigest.file, //
				fileAndDigest.digest,//
				Strings.firstNCharacters(fileAndDigest.digest, 6));
	}

	public String artifactDeterminedMsg(String key, ArtifactData artifactData) {
		FileAndDigest fileAndDigest = artifactData.fileAndDigest;
		return msg(key, //
				fileAndDigest.file,//
				fileAndDigest.digest,//
				Strings.firstNCharacters(fileAndDigest.digest, 6), //
				artifactData,//
				artifactData.groupId, artifactData.artifactId, artifactData.version);

	}

	public String notInAJarMsg(File file) {
		return msg(TextKeys.msgSharedNotAJar, file);
	}

	@Override
	public void dispose() {
		for (ISelectedBindingListener listener : listeners)
			selectionBindingManager.removeSelectedArtifactSelectionListener(listener);
		super.dispose();
	}

	@SuppressWarnings("unchecked")
	public <S> ISelectedBindingManager<S> getBindingManager() {
		return (ISelectedBindingManager<S>) selectionBindingManager;

	}

	protected String digestUrl(FileAndDigest fileAndDigest) {
		assert fileAndDigest.digest != null : fileAndDigest;
		return urlStrategy.digestUrl(fileAndDigest.digest).getHostAndUrl();
	}

	public String notJavaElementMsg() {
		return msg(TextKeys.msgSharedNotJavaElement);
	}

}
