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

import java.math.BigInteger;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.Sequencer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.service.Generator;
import com.vivek.vVidLib.utility.VIdUtility;

@Component
public class SequencerGenerator implements Generator {

	public void generate(VId vId) {

		Sequencer seq = vId.getSequencer();

		if (seq.getValue() == null) {

			seq.setValue(BigInteger.ZERO);

			seq.setCurrentNumberOfSymbols(seq.getInitialNumberOfSymbols());

			seq.setValueInBits(VIdUtility.convertToDefaultValueFromNumber(
					VIdUtility.calculateNumberOf2ndBasedBitsFromNumberOfSymbols(seq.getInitialNumberOfSymbols(),
							seq.getBitLengthConstraint()),
					0));

			seq.setCurrentBitsLength(seq.getValueInBits().length());
		} else {
			seq.setValue(seq.getValue().add(BigInteger.ONE));

			VIdUtility.populateValueAndLengths(seq, seq.getValue().toString());
		}
	}
}
