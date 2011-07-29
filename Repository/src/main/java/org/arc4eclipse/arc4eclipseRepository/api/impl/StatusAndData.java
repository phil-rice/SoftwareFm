package org.arc4eclipse.arc4eclipseRepository.api.impl;

import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;

public class StatusAndData {
	public final String url;
	public final RepositoryDataItemStatus status;
	private final Map<String, Object> data;
	private final Map<String, Object> context;

	public StatusAndData(String url, RepositoryDataItemStatus status, Map<String, Object> data, Map<String, Object> context) {
		super();
		this.url = url;
		this.status = status;
		this.data = data;
		this.context = context;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusAndData other = (StatusAndData) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (status != other.status)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StatusAndData [url=" + url + ", status=" + status + ", data=" + data + ", context=" + context + "]";
	}

}
