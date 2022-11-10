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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.Randomizer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.service.Shuffler;

@Component
public class RandomizerShuffler implements Shuffler {

	// TODO: can include both fixed and random place-mappings.. if using any complex
	// randomizer..
	@Override
	public void bitwiseShuffle(VId randomizer) {

		Randomizer ran = randomizer.getRandomizer();

		List<Character> randomList = ran.getValueInBits().chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		Collections.shuffle(randomList, ThreadLocalRandom.current());

		ran.setValueInBits(randomList.stream().map(String::valueOf).collect(Collectors.joining()));

	}

}
