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

package com.vivek.vVidLib.serviceImpl;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.Sequencer;
import com.vivek.vVidLib.models.ShufflingChoice;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.service.Shuffler;
import com.vivek.vVidLib.utility.VIdUtility;

/**
 * For Sequencer, Value will not change only valueAsString and valueInBits will
 * be processed. It's because the original value will be used to increment in
 * normal way by generator!
 */
@Component
public class SequencerShuffler implements Shuffler {

	@Override
	public void bitwiseShuffle(VId sequencer) {

		Sequencer seq = sequencer.getSequencer();

		char[] shuffledSequencer = new char[seq.getValueInBits().length()];

		shuffledSequencer = shuffleMe(seq.getShufflingChoice(), seq.getBitsInternalPlaceMappings(),
				seq.getValueInBits(), shuffledSequencer);

		seq.setValueInBits(Stream.of(shuffledSequencer).map(String::valueOf).collect(Collectors.joining()));

	}

	private char[] shuffleMe(ShufflingChoice choice, Map<Integer, Integer> placeMappings, String toShuffle,
			char[] shuffled) {
		if (choice == ShufflingChoice.FIXED_POSITIONS) {
			return VIdUtility.lsbOverMsbMixer(shuffled, toShuffle);
		} else {
			for (int i = 0; i < shuffled.length; i++) {
				shuffled[placeMappings.get(i)] = toShuffle.charAt(i);
			}
			return shuffled;
		}
	}

}
