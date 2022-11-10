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

/**
 * VIdGenerator or client must use synchronized calls to every getNextId()
 * otherwise IDs would be inconsistent state!
 * 
 * As further extension, can also use a separate symbolTable for VId that can
 * also be shuffled upon length changes.
 */
@Data
@NoArgsConstructor
public class VId {

	private String valueInBitsPostAssembly;

	private String vid;

	private int bitsLengthPostAssembly;

	private int numberOfSymbolsPostAssembly;

	private AssemblingChoice assemblingChoice;

	private ShufflingChoice shufflingChoiceInAssembly;

	private Timer timer;

	private Sequencer sequencer;

	private Randomizer randomizer;

	/**
	 * ***DEFAULTS TO TIMER'S SYMBOLTABLE**** For a given symbol, where is it
	 * present in final symbol table
	 */
	private Map<String, Integer> reverseSymbolTableMapping;

}
