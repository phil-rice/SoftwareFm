package org.arc4eclipse.arc4eclipseRepository.api.impl;

import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;

public class StatusAndData {
	public final String url;
	public final Class<?> clazz;
	public final RepositoryDataItemStatus status;
	public final IRepositoryDataItem data;

	public StatusAndData(String url, Class<?> clazz, RepositoryDataItemStatus status, IRepositoryDataItem data) {
		super();
		this.url = url;
		this.clazz = clazz;
		this.status = status;
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
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
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
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
		return "StatusAndData [url=" + url + ", clazz=" + clazz + ", status=" + status + ", data=" + data + "]";
	}

}
