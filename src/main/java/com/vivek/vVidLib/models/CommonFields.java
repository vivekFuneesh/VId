/**
 * VVId (VId), Vivek's Id or Vivek's virtual Id is a technique  or algorithm to generate 
 * unique, random, trackless & storageless(no storage is required to keep track of them) IDs (strings, numbers, symbols etc.).
 *     Copyright (C) 2020  Vivek Mangla
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 *     To connect with author, possible way to reach is via email 
 *     vivek.funeesh@gmail.com 
 * */

package com.vivek.vVidLib.models;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonFields {

	private String valueInBits;

	/**
	 * CHECK_REASON:: If Keeping it separate because oldValueInBits is used for
	 * processing in AdjustorAndUpdator
	 */
	private String rectifiedValueInBitsDuringAsembly;

	private int currentNumberOfSymbols;

	private int currentBitsLength;

	/**
	 * For eg. table Length is 8, firstBase will be 8, let's take secondBase to be
	 * 2(because it's completing firstBase) so bitLengthCOnstraint would be 3 here
	 * .. 2^2 x 1 + 2^1 x 1 + 2^0 x 1
	 */
	private int secondBase; // to maintain bit-values after firstBase modulus.

	private int firstBase; // symbolTableLength

	private int bitLengthConstraint; // to be used for secondBase bits length

	/**
	 * For a given index, what are the possible values.. as the length can be
	 * adjusted so one index may contain multiple values. To accomodate this,
	 * hashmap is used.
	 */
	private Map<Integer, String> symbolTable;

	private Map<Integer, Integer> bitsInternalPlaceMappings; // map original bit positions to random position within

	private Map<Integer, Integer> indexToVidPlaceMappings; // map position within generated&shuffled to position in VId

	private ShufflingChoice shufflingChoice;

}
