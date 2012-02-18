package org.softwareFm.swt.comments;

import java.util.concurrent.atomic.AtomicInteger;

public interface ICommentsEditorCallback {

	void youComment(String text);

	void groupComment(int groupIndex, String text);

	void everyoneComment(String text);

	void cancel();

	public static class Utils {
		public static ICommentsEditorCallback sysout() {
			return new ICommentsEditorCallback() {
				@Override
				public void youComment(String text) {
					System.out.println("you: " + text);
				}

				@Override
				public void groupComment(int groupIndex, String text) {
					System.out.println("group: " + groupIndex + ", " + text);
				}

				@Override
				public void everyoneComment(String text) {
					System.out.println("everyoneComment: " +  text);
				}

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}
			};
		}

		public static ICommentsEditorCallback withCount(final ICommentsEditorCallback delegate, final AtomicInteger integer) {
			return new ICommentsEditorCallback() {

				@Override
				public void youComment(String text) {
					integer.incrementAndGet();
					delegate.youComment(text);
				}

				@Override
				public void groupComment(int groupIndex, String text) {
					integer.incrementAndGet();
					delegate.groupComment(groupIndex, text);

				}

				@Override
				public void everyoneComment(String text) {
					integer.incrementAndGet();
					delegate.everyoneComment(text);

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
				public void youComment(String text) {
					throw new IllegalArgumentException();
				}

				@Override
				public void groupComment(int groupIndex, String text) {
					throw new IllegalArgumentException();
				}

				@Override
				public void everyoneComment(String text) {
					throw new IllegalArgumentException();
				}
			};
		}
	}

}
