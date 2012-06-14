package org.softwarefm.eclipse.composite;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.MessageKeys;
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
		return Strings.oneLine(msg(MessageKeys.search));
	}

	public String digestDeterminedMsg(FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest != null : fileNameAndDigest;
		return msg(MessageKeys.foundDigest, fileNameAndDigest.file.getPath(), Strings.firstNCharacters(fileNameAndDigest.digest, 6), fileNameAndDigest.digest);
	}

	public String unknownDigestMsg(FileNameAndDigest fileNameAndDigest) {
		return unknownDigestMsg(MessageKeys.unrecognisedDigestForProject, fileNameAndDigest);
	}
	public String unknownDigestMsg(String key, FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest != null : fileNameAndDigest;
		String result = msg(key, fileNameAndDigest.file.getPath(), Strings.firstNCharacters(fileNameAndDigest.digest, 6), fileNameAndDigest.digest);
		return result;
	}

	public String projectDeterminedMsg(ProjectData projectData) {
		FileNameAndDigest fileNameAndDigest = projectData.fileNameAndDigest;
		return msg(MessageKeys.foundProjectData, //
				fileNameAndDigest.file, Strings.firstNCharacters(fileNameAndDigest.digest, 6), fileNameAndDigest.digest,//
				projectData,//
				projectData.groupId, projectData.artifactId, projectData.version);

	}

	public String notInAJarMsg(FileNameAndDigest fileNameAndDigest) {
		assert fileNameAndDigest.digest == null : fileNameAndDigest;
		return msg(MessageKeys.notAJar, fileNameAndDigest.file);

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
		return msg(MessageKeys.notJavaElement);
	}

}
