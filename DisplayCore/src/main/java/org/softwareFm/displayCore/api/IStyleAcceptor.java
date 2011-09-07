package org.softwareFm.displayCore.api;

/** Can this style deal with this entity/editKey/clazz combination */
public interface IStyleAcceptor {

	boolean canDealWith(String entity, String key, Class<?> clazz);

	public static class Utils {

		public static IStyleAcceptor allways() {
			return new IStyleAcceptor() {
				@Override
				public boolean canDealWith(String entity, String key, Class<?> clazz) {
					return true;
				}
			};
		}

		public static IStyleAcceptor never() {
			return new IStyleAcceptor() {
				@Override
				public boolean canDealWith(String entity, String key, Class<?> clazz) {
					return false;
				}
			};
		}

	}
}
