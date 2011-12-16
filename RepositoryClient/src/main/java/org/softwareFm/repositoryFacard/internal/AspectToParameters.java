/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.repositoryFacard.IAspectToParameters;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.json.Json;

public class AspectToParameters implements IAspectToParameters {

	static abstract class Adder<T> {
		Class<T> markerClass;

		public Adder(Class<T> markerClass) {
			this.markerClass = markerClass;
		}

		@SuppressWarnings("unchecked")
		void add(List<NameValuePair> list, String name, Object value) {
			if (markerClass == value.getClass())
				addPrim(list, name, (T) value);
		}

		String typeHint(String name) {
			return name + "@TypeHint";
		}

		abstract void addPrim(List<NameValuePair> list, String name, T value);
	}

	@SuppressWarnings("rawtypes")
	private final List<Adder> adders;

	@SuppressWarnings("rawtypes")
	public AspectToParameters() {
		adders = Arrays.<Adder> asList(new Adder<String>(String.class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, String value) {
				list.add(pair(name, value));
				list.add(pair(typeHint(name), "String"));
			}
		}, new Adder<String[]>(String[].class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, String[] value) {
				for (String v : value)
					list.add(pair(name, v));
				list.add(pair(typeHint(name), "String[]"));
			}
		}, new Adder<Long>(Long.class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, Long value) {
				list.add(pair(name, value.toString()));
				list.add(pair(typeHint(name), "Long"));
			}
		}, new Adder<Long[]>(Long[].class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, Long[] value) {
				for (Long v : value)
					list.add(pair(name, v.toString()));
				list.add(pair(typeHint(name), "Long[]"));
			}
		}, new Adder<Integer>(Integer.class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, Integer value) {
				list.add(pair(name, value.toString()));
				list.add(pair(typeHint(name), "Long"));
			}
		}, new Adder<Integer[]>(Integer[].class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, Integer[] value) {
				for (Integer v : value)
					list.add(pair(name, v.toString()));
				list.add(pair(typeHint(name), "Long[]"));
			}
		}, new Adder<Boolean>(Boolean.class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, Boolean value) {
				list.add(pair(name, value.toString()));
				list.add(pair(typeHint(name), "Boolean"));
			}
		}, new Adder<Boolean[]>(Boolean[].class) {
			@Override
			void addPrim(List<NameValuePair> list, String name, Boolean[] value) {
				for (Boolean v : value)
					list.add(pair(name, v.toString()));
				list.add(pair(typeHint(name), "Boolean[]"));
			}
		});
	}

	@Override
	public List<NameValuePair> makeParameters(Map<String, Object> data) {
		List<NameValuePair> result = Lists.newList();
		for (Entry<String, Object> entry : data.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			add(result, name, value);
		}
		return result;
	}

	private void add(List<NameValuePair> result, String name, Object value) {
		for (Adder<Object> adder : adders)
			if (adder.markerClass == value.getClass()) {
				adder.add(result, name, value);
				return;
			}
		throw new IllegalArgumentException(MessageFormat.format(RepositoryFacardConstants.cannotEncode, value.getClass(), value));
	}

	private NameValuePair pair(String name, String value) {
		return new BasicNameValuePair(name, value);
	}

	@Override
	public Map<String, Object> makeFrom(String string) {
		Map<String, Object> map = Json.mapFromString(string);
		return map;
	}

}