package org.softwarefm.eclipse.selection;

import java.io.File;
import java.util.Map;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectHtmlRipper;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectStrategy;
import org.softwarefm.eclipse.url.IUrlStrategy;
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
public interface ISelectedBindingStrategy<S, N> extends IProjectStrategy<S> {

	/** May return null */
	N findNode(S selection, int selectionCount);

	ExpressionData findExpressionData(S selection, N node, int selectionCount);

	/** The filename/digest will be null if the strategy cannot work out file. The digest will be null if the filename isn't a jar */
	File findFile(S selection, N node, int selectionCount);

	@SuppressWarnings("Change this to return null to be consistant with others")
	FileAndDigest findDigest(S selection, N node, File file, int selectionCount);

	public static class Utils {
		public static <S> IProjectStrategy<S> softwareFmProjectStrategy(IUrlStrategy urlStrategy) {
			return new SoftwareFmProjectStrategy<S>(IHttpClient.Utils.builder(), new SoftwareFmProjectHtmlRipper(), urlStrategy);
		}

		public static ISelectedBindingStrategy<Map<String, Object>, Map<String, Object>> fromMap() {
			return new ISelectedBindingStrategy<Map<String, Object>, Map<String, Object>>() {
				public Map<String, Object> findNode(Map<String, Object> selection, int selectionCount) {
					return selection;
				}

				public ExpressionData findExpressionData(Map<String, Object> selection, Map<String, Object> node, int selectionCount) {
					return new ExpressionData((String) selection.get("package"), (String) selection.get("class"), (String) selection.get("method"));
				}

				public ProjectData findProject(Map<String, Object> selection, FileAndDigest fileAndDigest, int selectionCount) {
					String groupid = (String) selection.get("groupid");
					String artefactId = (String) selection.get("artifactid");
					String version = (String) selection.get("version");
					if (groupid == null)
						return null;
					else
						return new ProjectData(fileAndDigest, groupid, artefactId, version);
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
