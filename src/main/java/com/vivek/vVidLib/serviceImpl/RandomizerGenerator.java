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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.vivek.vVidLib.models.Randomizer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.service.Generator;
import com.vivek.vVidLib.utility.VIdUtility;

@Component
public class RandomizerGenerator implements Generator {

	public void generate(VId randomizer) {

		Randomizer ran = randomizer.getRandomizer();

		if (ran.getCurrentNumberOfSymbols() == 0) {
			ran.setCurrentBitsLength(0);
			ran.setValueInBits("");

		} else {
			ran.setValueInBits(getRandom(ran.getCurrentBitsLength()));
			ran.setCurrentBitsLength(ran.getValueInBits().length());
			ran.setCurrentNumberOfSymbols(VIdUtility.calculateNumberOfSymbolsFrom2ndBasedBitsCount(
					ran.getCurrentBitsLength(), ran.getBitLengthConstraint()));
			ran.setValue(new BigInteger(VIdUtility.convertToNumberFromBitsWithSecondBase(ran.getValueInBits(),
					ran.getFirstBase(), ran.getBitLengthConstraint(), ran.getSecondBase())));
		}
		// System.out.println("Randomizer generated: "+ ran.getValueInBits());
	}

	private String getRandom(int bitsCount) {

		List<Integer> randomList = new ArrayList<>();
		for (int i = 0; i < bitsCount; i++) {
			randomList.add(ThreadLocalRandom.current().nextInt(0, 2));
		}
		Collections.shuffle(randomList, ThreadLocalRandom.current());

		return StringUtils.collectionToDelimitedString(randomList, "");
	}

}
