/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.constants;

public class UtilityMessages {

	public static final String cannotHaveNullReturnedBySplitFunction = "Cannot have a null pointed returned by 'Iterables.split'. The split function class was {0}. The parameter was {1}";
	public static final String cannotHaveNullInIterableBeingProcessedBySplit = "Cannot Have Null In Iterable Being Processed By Split";
	public static final String needPositivePartitionSize = "Need partition size > 0. Have {0}";
	public static final String duplicateKey = "Cannot have duplicate value for key {0}. Existing value {1}. New value {2}";
	public static final String illegalKey = "Illegal key {0}. Legal values are {1}";
	public static final String cannotSetLength = "Cannot set length. Desired value {0}. Max value {1}";
	public static final String tooManyBytes = "Too large a parameter from. Parameter length is {0}. Max length is {1}";
	public static final String indexOutOfBounds = "Index out of bounds. Asked for {0}. Size {1}";
	public static final String standardDeviation = "standardDeviation";
	public static final String duration = "duration";
	public static final String loopException = "Cannot add {0} as child of {1}, because of path {2}";
	public static final String cannotWorkOutValue = "Cannot work out value.\nMap: {0}\n Last key: {1}\nKeys: {2}";
	public static final String cannotFindMapForGet = "Cannot find map for get\nMap: {0}\n Keys: {2}";
	public static final String errorParsingJson = "Error parsing object of {0} with value {1}";
	public static final String missingResource = "Cannot find resource {0}";
	public static final String cannotFindOffset = "Cannot find offset. \nRoot {0}\nLeaf {1}";
	public static final String cannotGetDescendant = "Cannot find descendant in {0} index is {1} childIndicies are {1}";
	public static final String cannotCloseServer = "Cannot close server";
	public static final String expectedButGot = "Expected {0} but got {1}";
	public static final String cannotGetFirstNCharacters = "Cannot get first ({0}) characters";
	public static final String poisonedTransactionException = "Possible poisoned Transaction {0}\nHave executed it {1} times. Nested exceptions {2}";
}