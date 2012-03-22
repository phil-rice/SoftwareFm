package org.softwareFm.crowdsource.api.user;

public class GroupOperationResult {

	public static GroupOperationResult error(String errorMessage) {
		return new GroupOperationResult(errorMessage, null);
	}

	public static GroupOperationResult groupId(String groupId) {
		return new GroupOperationResult(null, groupId);
	}

	public final String errorMessage;
	public final String groupId;

	private GroupOperationResult(String errorMessage, String groupId) {
		this.errorMessage = errorMessage;
		this.groupId = groupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
		GroupOperationResult other = (GroupOperationResult) obj;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GroupOperationResult [errorMessage=" + errorMessage + ", groupId=" + groupId + "]";
	}

}
