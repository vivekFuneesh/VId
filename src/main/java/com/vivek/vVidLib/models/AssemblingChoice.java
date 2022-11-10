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

public enum AssemblingChoice {
	NO_MIXING, // TBD: IT CAN CONTAIN UPTO 6 USER-DEFINED SCENARIOS: random-timer-sequencer,
				// r-s-t, t-s-r, t-r-s, s-t-r, s-r-t.
	MIX_ONLY_TIMER_SEQUENCER, // IT can be R-(ts) or (ts)-Random or (st)-R or R-(st)
	MIX_TIMER_SEQUENCER_RANDOMIZER
}
