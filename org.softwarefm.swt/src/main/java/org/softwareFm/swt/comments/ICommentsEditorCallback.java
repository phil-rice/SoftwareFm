package org.softwareFm.swt.comments;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.swt.comments.internal.CommentsEditorCallbackThatWritesComment;

public interface ICommentsEditorCallback {

	void youComment(String url, String text);

	void groupComment(String url, int groupIndex, String text);

	void everyoneComment(String url, String text);

	void cancel();

	public static class Utils {
		public static ICommentsEditorCallback writeComments(final IUserReader userReader, final IGroupsReader groupReader, final String softwareFmId, final String userCrypto, final Iterable<Map<String,Object>> groupsData, final ICommentWriter commentWriter, final Runnable whenFinished){
			return new CommentsEditorCallbackThatWritesComment(userReader, groupReader, commentWriter, softwareFmId, userCrypto, groupsData, whenFinished);
		}
		
		public static ICommentsEditorCallback sysout() {
			return new ICommentsEditorCallback() {
				@Override
				public void youComment(String url, String text) {
					System.out.println("you: " + text);
				}

				@Override
				public void groupComment(String url, int groupIndex, String text) {
					System.out.println("group: " + groupIndex + ", " + text);
				}

				@Override
				public void everyoneComment(String url, String text) {
					System.out.println("everyoneComment: " +  text);
				}

				@Override
				public void cancel() {
				}
			};
		}

		public static ICommentsEditorCallback withCount(final ICommentsEditorCallback delegate, final AtomicInteger integer) {
			return new ICommentsEditorCallback() {

				@Override
				public void youComment(String url, String text) {
					integer.incrementAndGet();
					delegate.youComment(url, text);
				}

				@Override
				public void groupComment(String url, int groupIndex, String text) {
					integer.incrementAndGet();
					delegate.groupComment(url, groupIndex, text);

				}

				@Override
				public void everyoneComment(String url, String text) {
					integer.incrementAndGet();
					delegate.everyoneComment(url, text);

				}

				@Override
				public void cancel() {
					integer.incrementAndGet();
					delegate.cancel();
				}
			};
		}

		public static ICommentsEditorCallback exceptionCallback() {
			return new ICommentsEditorCallback() {

				@Override
				public void cancel() {
					throw new IllegalArgumentException();
				}

				@Override
				public void youComment(String url, String text) {
					throw new IllegalArgumentException();
				}

				@Override
				public void groupComment(String url, int groupIndex, String text) {
					throw new IllegalArgumentException();
				}

				@Override
				public void everyoneComment(String url, String text) {
					throw new IllegalArgumentException();
				}
			};
		}
	}

}
