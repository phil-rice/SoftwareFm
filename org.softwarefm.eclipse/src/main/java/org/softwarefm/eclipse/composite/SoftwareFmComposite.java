package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.IHasSelectionBindingManager;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.swt.HasComposite;
import org.softwarefm.eclipse.url.IUrlStrategy;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

abstract public class SoftwareFmComposite extends HasComposite implements IHasSelectionBindingManager {
	private final List<ISelectedBindingListener> listeners = Lists.newList();
	private final ISelectedBindingManager<?> selectionBindingManager;
	protected final IResourceGetter resourceGetter;
	protected final IUrlStrategy urlStrategy;

	public SoftwareFmComposite(Composite parent, SoftwareFmContainer<?> container) {
		super(parent);
		this.selectionBindingManager = container.selectedBindingManager;
		this.resourceGetter = container.resourceGetter;
		urlStrategy = container.urlStrategy;
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

	public String unknownDigestMsg(String key, FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest != null : fileNameAndDigest;
		String result = msg(key, fileNameAndDigest.file.getPath(),fileNameAndDigest.digest, Strings.firstNCharacters(fileNameAndDigest.digest, 6) );
		return result;
	}

	public String digestDeterminedMsg(String key, FileNameAndDigest fileNameAndDigest) {
		return msg(key, //
				fileNameAndDigest.file, //
				fileNameAndDigest.digest,//
				Strings.firstNCharacters(fileNameAndDigest.digest, 6));
	}

	public String projectDeterminedMsg(String key, ProjectData projectData) {
		FileNameAndDigest fileNameAndDigest = projectData.fileNameAndDigest;
		return msg(key, //
				fileNameAndDigest.file,//
				fileNameAndDigest.digest,//
				Strings.firstNCharacters(fileNameAndDigest.digest, 6), //
				projectData,//
				projectData.groupId, projectData.artifactId, projectData.version);

	}

	public String notInAJarMsg(FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest == null : fileNameAndDigest;
		return msg(TextKeys.msgSharedNotAJar, fileNameAndDigest.file);

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

	protected String digestUrl(FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest != null : fileNameAndDigest;
		return urlStrategy.digestUrl(fileNameAndDigest.digest).getHostAndUrl();
	}

	public String notJavaElementMsg() {
		return msg(TextKeys.msgSharedNotJavaElement);
	}

}
