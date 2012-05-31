package org.softwarefm.eclipse.selection;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jface.text.ITextSelection;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.selection.internal.EclipseSelectedBindingStrategy;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectHtmlRipper;
import org.softwarefm.eclipse.selection.internal.SoftwareFmProjectStrategy;
import org.softwarefm.utilities.constants.CommonConstants;
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
public interface ISelectedBindingStrategy<S, N> extends IProjectStrategy {

	/** May return null */
	N findNode(S selection, int selectionCount);

	ExpressionData findExpressionData(S selection, N node, int selectionCount);

	/** The filename/digest will be null if the strategy cannot work out file. The digest will be null if the filename isn't a jar */
	FileNameAndDigest findFileAndDigest(S selection, N node, int selectionCount);

	public static class Utils {
		public static ISelectedBindingStrategy<ITextSelection, Expression> eclipseSelectedBindingStrategy() {
			return new EclipseSelectedBindingStrategy(softwareFmProjectStrategy());
		}

		public static IProjectStrategy softwareFmProjectStrategy() {
			return new SoftwareFmProjectStrategy(IHttpClient.Utils.builder(), CommonConstants.softwareFmHost, new SoftwareFmProjectHtmlRipper());
		}
	}
}
