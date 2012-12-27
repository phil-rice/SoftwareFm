package org.softwarefm.core.selection;

import java.io.File;
import java.util.Map;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.internal.SoftwareFmArtifactHtmlRipper;
import org.softwarefm.core.selection.internal.SoftwareFmArtifactStrategy;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.http.IHttpClient;

/**
 * <p>
 * S is the selection, N is some intermediate value
 * 
 * <p>
 * In eclipse S is ITextSelection. N is an ASTNode cast to an Expression
 * 
 * <p>
 * There are no assumptions about the threading model. Internally the strategy may be simple, or multi threaded.
 */
public interface ISelectedBindingStrategy<S, N> extends IArtifactStrategy<S> {

	/** May return null */
	N findNode(S selection, int selectionCount);

	CodeData findExpressionData(S selection, N node, int selectionCount);

	/** The filename/digest will be null if the strategy cannot work out file. The digest will be null if the filename isn't a jar */
	File findFile(S selection, N node, int selectionCount);

	FileAndDigest findDigest(S selection, N node, File file, int selectionCount);

	public static class Utils {
		public static <S> IArtifactStrategy<S> softwareFmProjectStrategy(IUrlStrategy urlStrategy) {
			return new SoftwareFmArtifactStrategy<S>(IHttpClient.Utils.builder(), new SoftwareFmArtifactHtmlRipper(), urlStrategy);
		}

		public static ISelectedBindingStrategy<Map<String, Object>, Map<String, Object>> fromMap() {
			return new ISelectedBindingStrategy<Map<String, Object>, Map<String, Object>>() {
				public Map<String, Object> findNode(Map<String, Object> selection, int selectionCount) {
					return selection;
				}

				public CodeData findExpressionData(Map<String, Object> selection, Map<String, Object> node, int selectionCount) {
					return new CodeData((String) selection.get("package"), (String) selection.get("class"), (String) selection.get("method"));
				}

				public ArtifactData findArtifact(Map<String, Object> selection, FileAndDigest fileAndDigest, int selectionCount) {
					String groupid = (String) selection.get("groupid");
					String artefactId = (String) selection.get("artifactid");
					String version = (String) selection.get("version");
					if (groupid == null)
						return null;
					else
						return new ArtifactData(fileAndDigest, groupid, artefactId, version);
				}

				public File findFile(Map<String, Object> selection, Map<String, Object> node, int selectionCount) {
					File file = new File((String) selection.get("filename"));
					return file;
				}

				public FileAndDigest findDigest(Map<String, Object> selection, Map<String, Object> node, File file, int selectionCount) {
					return new FileAndDigest(file, (String) selection.get("digest"));
				}
			};
		}
	}
}
