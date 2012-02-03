package org.softwareFm.common;

import java.util.Map;

public interface IGitWriter {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);
	
	void delete(IFileDescription fileDescription);
	
	public static class Utils{
	
		public static IGitWriter noWriter() {
			return new IGitWriter() {
				@Override
				public void put(IFileDescription fileDescription, Map<String, Object> data) {
					throw new IllegalArgumentException();
				}
				
				@Override
				public void init(String url) {
					throw new IllegalArgumentException();
				}
				
				@Override
				public void delete(IFileDescription fileDescription) {
					throw new IllegalArgumentException();
				}
			};
		}
	}
}
