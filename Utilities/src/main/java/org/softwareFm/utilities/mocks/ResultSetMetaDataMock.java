package org.softwareFm.utilities.mocks;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class ResultSetMetaDataMock implements ResultSetMetaData {

	private final List<String> labels;

	public ResultSetMetaDataMock(List<String> labels) {
		super();
		this.labels = labels;
	}

	public String getColumnLabel(int arg0) throws SQLException {
		return labels.get(arg0 - 1);
	}

	public int getColumnCount() throws SQLException {
		return labels.size();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getCatalogName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getColumnClassName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getColumnDisplaySize(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getColumnName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getColumnType(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getColumnTypeName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getPrecision(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getScale(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getSchemaName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getTableName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isAutoIncrement(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isCaseSensitive(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isCurrency(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int isNullable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isReadOnly(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isSearchable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isSigned(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isWritable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
