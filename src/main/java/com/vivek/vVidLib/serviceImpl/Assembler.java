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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.CommonFields;
import com.vivek.vVidLib.models.Randomizer;
import com.vivek.vVidLib.models.Sequencer;
import com.vivek.vVidLib.models.Timer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.utility.VIdUtility;

/**
 * Further choices can be done on basis of :-> <br>
 * if Digits to be mixed (assemblyShufflingLevel is DIGITS) or if Bits to be
 * mixed (assemblyShufflingLevel is BITWISE) if(digits to be mixed) then take
 * care of VIdLengthChoice.. convert bits to value_without_mapped_to_symbols
 * then mix value.. this values as rectifiedValueDuringAssembly
 */
@Component
public class Assembler {

	public void assemble(VId vId) {
//		System.out.println("Assembling for T,S,R= "+ vId.getTimer().getValueInBits() +" "+ vId.getSequencer().getValueInBits()+ 
//				" "+ vId.getRandomizer().getValueInBits());
//		System.out.println("Timer: prec, actual: "+ vId.getTimer().getPrecisedValue()+" "+ vId.getTimer().getActualValue()+" "
//				+ " Sequencer: "+ vId.getSequencer().getValue());
		rectifyIndividualSymbolTableMappings(vId);

		switch (vId.getAssemblingChoice()) {
		case NO_MIXING:
			noMixing(vId);
			break;
		case MIX_ONLY_TIMER_SEQUENCER:
			mixOnlyTimerAndSequencer(vId);
			break;
		case MIX_TIMER_SEQUENCER_RANDOMIZER:
			mixAll(vId);
			break;
		default:
			throw new RuntimeException("Invalid Option for Assembly");
		}
		vId.setBitsLengthPostAssembly(vId.getValueInBitsPostAssembly().length());

	}

	/**
	 * This also plays a little part of symbolMapper (while deciding a random symbol
	 * from a given list)!
	 */
	private void rectifyIndividualSymbolTableMappings(VId vId) {
		List<CommonFields> parts = Arrays.asList(vId.getTimer(), vId.getSequencer(), vId.getRandomizer());

		parts.stream().forEach(part -> {

			List<String> indicesInFinalTable = new ArrayList<>();

			List<String> indicesInLocalTable = VIdUtility.convertTo1stBasedListOfNumbersFromBitsWithSecondBase(
					part.getValueInBits(), part.getBitLengthConstraint(), part.getSecondBase());
			// System.out.println(indicesInLocalTable+" \n"+ part.getValueInBits()+" \n
			// bitLenConstraint: " + part.getBitLengthConstraint());

			indicesInLocalTable.stream().forEach(index -> {
				String symbols = part.getSymbolTable().get(Integer.valueOf(index));
				// System.out.println("Calling for "+ symbols +" symbolTable size="
				// +part.getSymbolTable().size());
				indicesInFinalTable.add("" + vId.getReverseSymbolTableMapping().get(symbols));

			});

			part.setRectifiedValueInBitsDuringAsembly(indicesInFinalTable.stream()
					.map(str -> VIdUtility.convertToBitsWithBaseFromNumbers(new BigInteger(str),
							BigInteger.valueOf(part.getSecondBase())).stream().collect(Collectors.joining()))
					.map(bits -> {
						return VIdUtility.convertToDefaultValueFromNumber(part.getBitLengthConstraint() - bits.length(),
								0) + bits;
					}).collect(Collectors.joining()));

		});
	}

	private void mixAll(VId vId) {

		Timer timer = vId.getTimer();
		Randomizer ran = vId.getRandomizer();
		Sequencer seq = vId.getSequencer();

		switch (vId.getShufflingChoiceInAssembly()) {
		case FIXED_POSITIONS: {
			String part1 = timer.getRectifiedValueInBitsDuringAsembly() + ran.getRectifiedValueInBitsDuringAsembly()
					.substring(0, ran.getRectifiedValueInBitsDuringAsembly().length() / 2);
			String part2 = seq.getRectifiedValueInBitsDuringAsembly() + ran.getRectifiedValueInBitsDuringAsembly()
					.substring(ran.getRectifiedValueInBitsDuringAsembly().length() / 2);

			part1 = Stream.of(VIdUtility.lsbOverMsbMixer(new char[part1.length()], part1)).map(String::valueOf)
					.collect(Collectors.joining());
			part2 = Stream.of(VIdUtility.lsbOverMsbMixer(new char[part2.length()], part2)).map(String::valueOf)
					.collect(Collectors.joining());

			String result = part1 + part2;

			result = Stream.of(VIdUtility.lsbOverMsbMixer(new char[result.length()], result)).map(String::valueOf)
					.collect(Collectors.joining());

			vId.setValueInBitsPostAssembly(result);
			break;
		}
		case RANDOM_PLACE_MAPPINGS: {
			char[] result = assembleByPlaceMappings(vId, null);
			vId.setValueInBitsPostAssembly(Stream.of(result).map(String::valueOf).collect(Collectors.joining()));
			break;
		}
		default:
			break;
		}
	}

	private void mixOnlyTimerAndSequencer(VId vId) {

		Timer timer = vId.getTimer();
		Randomizer ran = vId.getRandomizer();
		Sequencer seq = vId.getSequencer();

		switch (vId.getShufflingChoiceInAssembly()) {
		case FIXED_POSITIONS: {
			String result = timer.getRectifiedValueInBitsDuringAsembly() + seq.getRectifiedValueInBitsDuringAsembly();
			result = Stream.of(VIdUtility.lsbOverMsbMixer(new char[result.length()], result)).map(String::valueOf)
					.collect(Collectors.joining());
			result = ran.getRectifiedValueInBitsDuringAsembly() + result;
			vId.setValueInBitsPostAssembly(result);
			break;
		}
		case RANDOM_PLACE_MAPPINGS: {
			char[] result = assembleByPlaceMappings(vId, vId.getRandomizer());

			vId.setValueInBitsPostAssembly((vId.getRandomizer().getRectifiedValueInBitsDuringAsembly()
					+ IntStream.range(0, result.length).filter(num -> result[num] != 0)
							.mapToObj(num -> String.valueOf(result[num])).collect(Collectors.joining())));
			break;
		}
		default:
			break;
		}

	}

	private char[] assembleByPlaceMappings(VId vId, CommonFields partToFilter) {

		Timer timer = vId.getTimer();
		Randomizer ran = vId.getRandomizer();
		Sequencer seq = vId.getSequencer();
		// System.out.println(ran.getValueInBits().length() );
		// length of both valueInBits and rectifiedValueInBitsDuringAsembly must be
		// same!!
		char[] result = new char[ran.getValueInBits().length() + seq.getValueInBits().length()
				+ timer.getValueInBits().length()];

		List<CommonFields> partsOfVId = Arrays.asList(timer, seq, ran);
		partsOfVId.stream().filter(part -> part != partToFilter).forEach(part -> {
			part.getIndexToVidPlaceMappings().entrySet().stream().forEach(entry -> {
				result[entry.getValue()] = part.getRectifiedValueInBitsDuringAsembly().charAt(entry.getKey());
			});
		});
		return result;
	}

	private void noMixing(VId vId) {

		Timer timer = vId.getTimer();
		Randomizer ran = vId.getRandomizer();
		Sequencer seq = vId.getSequencer();

		vId.setValueInBitsPostAssembly(ran.getRectifiedValueInBitsDuringAsembly()
				+ seq.getRectifiedValueInBitsDuringAsembly() + timer.getRectifiedValueInBitsDuringAsembly());
	}

}
