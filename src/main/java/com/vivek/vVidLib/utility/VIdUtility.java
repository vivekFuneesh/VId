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

package com.vivek.vVidLib.utility;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vivek.vVidLib.models.CommonFields;
import com.vivek.vVidLib.models.VId;

public class VIdUtility {

	public static Random getRandom() {
		return ThreadLocalRandom.current();
	}

	public static int calculateBitLengthConstraint(int firstBase, int secondBase) {
		return convertToBitsWithBaseFromNumbers(BigInteger.valueOf(firstBase), BigInteger.valueOf(secondBase)).size();
	}

	public static void populateValueAndLengths(CommonFields field, String valueAsString) {
		String valueInBits = VIdUtility.getBitiedStringPerBaseWithLengthAdjusted(valueAsString, field.getFirstBase(),
				field.getBitLengthConstraint(), field.getSecondBase());

		if (valueInBits.length() < field.getCurrentBitsLength()) {
			valueInBits = VIdUtility.convertToDefaultValueFromNumber(
					field.getCurrentBitsLength() - valueInBits.length(), 0) + valueInBits;
		} else {
			field.setCurrentBitsLength(valueInBits.length());

		}
		field.setCurrentNumberOfSymbols(VIdUtility.calculateNumberOfSymbolsFrom2ndBasedBitsCount(valueInBits.length(),
				field.getBitLengthConstraint()));
		field.setValueInBits(valueInBits);
	}

	/**
	 * For every increment in new symbol, increment of total bitLengthConstraint
	 * number of bits would be there.
	 */
	public static String getNext2ndBasedBits(String initialBits, int bitLengthConstraint) {
		char[] result = initialBits.toCharArray();

		int index = initialBits.length() - 1;
		while (index >= 0 && result[index] == '1') {
			result[index] = '0';
			index--;
		}

		if (index == -1) {
			result = (convertToDefaultValueFromNumber(bitLengthConstraint, 0).substring(0, bitLengthConstraint - 1)
					+ "1" + String.valueOf(result)).toCharArray();
		} else {
			result[index] = '1';
		}

		return String.valueOf(result);
	}

	public static int calculateNumberOf2ndBasedBitsFromNumberOfSymbols(int numberOfSymbols, int bitLengthConstraint) {

		return numberOfSymbols * bitLengthConstraint;
	}

	public static int calculateNumberOfSymbolsFrom2ndBasedBitsCount(int numberOfBits, int bitLengthConstraint) {
		if (Math.floorMod(numberOfBits, bitLengthConstraint) != 0) {
			throw new RuntimeException("Invalid number of 2ndBased bits and bitLengthConstraint pair.. < "
					+ numberOfBits + " , " + bitLengthConstraint + " >");
		}
		return numberOfBits / bitLengthConstraint;
	}

	public static void mapBitsWithSecondBaseToSymbols(VId vId) {
		List<String> indices = convertTo1stBasedListOfNumbersFromBitsWithSecondBase(vId.getValueInBitsPostAssembly(),
				vId.getTimer().getBitLengthConstraint(), vId.getTimer().getSecondBase());
		vId.setVid(indices.stream().map(index -> {
			return vId.getTimer().getSymbolTable().get(Integer.valueOf(index));
		}).collect(Collectors.joining()));

		vId.setNumberOfSymbolsPostAssembly(vId.getVid().length());
	}

	public static String convertToDefaultValueFromNumber(int numberOfDigits, int defaultValue) {
		StringBuffer sb = new StringBuffer();
		IntStream.range(0, numberOfDigits).forEach(x -> {
			sb.append(defaultValue);
		});

		return sb.toString();
	}

	// TODO: modify to cover domain of 0 as m.s.b too, i.e. 8 with base 8 -> 00, not
	// 10.
	public static List<String> convertToBitsWithBaseFromNumbers(BigInteger number, BigInteger base) {
		ArrayList<String> result = new ArrayList<>();
		while (number.compareTo(base) >= 0) {
			result.add(number.mod(base).toString());
			number = number.divide(base);
		}

		if (number.compareTo(base) < 0)
			result.add(number.toString());
		Collections.reverse(result);
		return result;
	}

	// TODO: modify to cover domain of 0 as m.s.b too, i.e. 00 with base 8 -> 8 as
	// final number and not 0.
	private static String convertToNumberFromBitsWithBase(String bits, int base) {

		BigInteger result = BigInteger.ZERO;

		for (int i = bits.length() - 1; i >= 0; i--) {
			result = result.add((new BigInteger("" + bits.charAt(i)))
					.multiply((BigInteger.valueOf(base)).pow(bits.length() - i - 1)));
		}

		return String.valueOf(result);
	}

	// TODO: modify to cover domain of 0 as m.s.b too, i.e. 00 with base 8 -> 8 as
	// final number and not 0.
	private static String convertToNumberFromNonBinaryBitsWithBase(List<String> bits, int base) {

		BigInteger result = BigInteger.ZERO;

		for (int i = bits.size() - 1; i >= 0; i--) {
			result = result.add(
					(new BigInteger("" + bits.get(i))).multiply((BigInteger.valueOf(base)).pow(bits.size() - i - 1)));
		}

		return String.valueOf(result);
	}

	public static String convertToNumberFromBitsWithSecondBase(String bits, int firstBase, int bitLengthConstraint,
			int secondBase) {
		List<String> numbers = new ArrayList<>();
		if (secondBase > 1) {
			numbers = VIdUtility.convertToListOfNumbersFromBitsWithBaseAndLengthPerDigit(bits, secondBase,
					bitLengthConstraint);
		}

		return VIdUtility.convertToNumberFromNonBinaryBitsWithBase(numbers, firstBase);
	}

	public static List<String> convertTo1stBasedListOfNumbersFromBitsWithSecondBase(String bits,
			int bitLengthConstraint, int secondBase) {
		List<String> numbers = new ArrayList<>();
		if (secondBase > 1) {
			numbers = VIdUtility.convertToListOfNumbersFromBitsWithBaseAndLengthPerDigit(bits, secondBase,
					bitLengthConstraint);
		} else
			throw new RuntimeException("Second Base Not set");
		return numbers;

	}

	private static List<String> convertToListOfNumbersFromBitsWithBaseAndLengthPerDigit(String bits, int base,
			int lengthConstraint) {
		List<String> sb = new ArrayList<>();

		int i = 0;
		while (i < bits.length()) {
			sb.add(convertToNumberFromBitsWithBase(bits.substring(i, i + lengthConstraint), base));
			i += lengthConstraint;
		}
		return sb;
	}

	// TODO: needs to be checked for shortening lengths as every single digit is
	// covering entire symbol table
	// at line 67 but symbol table should cover maximum digits possible
	/*
	 * public static String getBitiedStringWithLengthForEveryDigit(String string,
	 * int symbolListBase, int lengthConstraint) { StringBuffer sb = new
	 * StringBuffer(); for (int i = 0; i < string.length(); i++) { StringBuffer bits
	 * = new StringBuffer(
	 * VIdUtility.convertToBitsWithBaseFromNumbers(Long.valueOf("" +
	 * string.charAt(i)), symbolListBase)); StringBuffer revBits = bits.reverse();
	 * IntStream.range(0, lengthConstraint - bits.length()).forEach(x -> {
	 * revBits.append('0'); }); bits = revBits.reverse(); sb.append(bits); } return
	 * sb.toString(); }
	 */
	// use chaining pattern to modify it to have multiple bases.
	/** secondBase is MUST.. make sure of that while initializing vId. */

	// bitleengthConstraint(x) will be according to scndBase(b2) s.t. b2^x ==
	// symbolTableLeeength - 1.
	// secndBase is too iincrease rrndooomness achiivable by shuffling bits... it
	// shooould be used as to increease
	// the number oof bits generated..!!
	/**
	 * for the time being- taking as second base is mandatory otherwise use length
	 * constraint for 1st base too to remove out-of-bounds in table after mixing
	 **/
	// TODO: Instead of symbolTable Length, use other less numbered value.
	// Info: Irrespective of vIdLengthChoice, it will check and append 0s at front.
	public static String getBitiedStringPerBaseWithLengthAdjusted(String string, int firstBase, int bitLengthConstraint,
			int secondBase) {
		BigInteger strNum = new BigInteger(string);
		List<String> bitList = convertToBitsWithBaseFromNumbers(strNum, BigInteger.valueOf(firstBase));
		// System.out.println("list of bitieed with 1st base is: " + bitList);
		return secondBase > 1 ? bitList.stream()
				.map(str -> convertToBitsWithBaseFromNumbers(new BigInteger(str), BigInteger.valueOf(secondBase))
						.stream().collect(Collectors.joining()))
				.map(bits -> {
					return convertToDefaultValueFromNumber(bitLengthConstraint - bits.length(), 0) + bits;
				}).collect(Collectors.joining()) : bitList.stream().collect(Collectors.joining());
	}

	public static char[] lsbOverMsbMixer(char[] result, String strToProcess) {

		int k = 0, i = 0, j = strToProcess.length() - 1;
		while (i <= j) {
			result[k++] = strToProcess.charAt(j--);
			if (k < strToProcess.length()) {
				result[k++] = strToProcess.charAt(i++);
			}
		}

		return result;
	}

	public static Set<Integer> generateRandomPlaces(int startInclusive, int endExclusive,
			LinkedHashSet<Integer> placeList) {
		for (int i = startInclusive; i < endExclusive; i++) {
			int next = ThreadLocalRandom.current().nextInt(startInclusive, endExclusive);
			if (!placeList.contains(next))
				placeList.add(next);
		}
		for (int i = startInclusive; i < endExclusive; i++) {
			if (!placeList.contains(i))
				placeList.add(i);
		}

		return placeList;
	}

	public static void populateMapPlaces(Set<Integer> randomizedPlaces, Map<Integer, Integer> mapToFill,
			int placesCountToMap) {
		int i = 0;
		for (Iterator<Integer> itr = randomizedPlaces.iterator(); itr.hasNext() && i < placesCountToMap; i++) {
			mapToFill.put(i, itr.next());
			itr.remove();
		}
	}

	public static void shuffleSet(Set<Integer> set) {
		ArrayList<Integer> list = new ArrayList<Integer>(set);
		Collections.shuffle(list, ThreadLocalRandom.current());
		set.clear();
		set.addAll(list);
	}

	/**
	 * sorting is used here assuming that index(key) may not start from 0 or may not
	 * be sequentially increasing!! though, currently that is not the case.
	 */
	public static Map<Integer, Integer> modifyPlaceMappings(Map<Integer, Integer> originalMapping) {

		List<Integer> values = new ArrayList<>(originalMapping.size());

		List<Integer> keys = originalMapping.keySet().stream().collect(Collectors.toList());
		Collections.sort(keys);

		keys.stream().forEach(key -> values.add(originalMapping.get(key)));

		Collections.shuffle(values, ThreadLocalRandom.current());

		Map<Integer, Integer> newMapping = new HashMap<Integer, Integer>();

		AtomicInteger i = new AtomicInteger(0);

		keys.stream().forEach(key -> {
			newMapping.put(key, values.get(i.getAndIncrement()));
		});

		return newMapping;

	}

	/**
	 * This is only when key of multiple originalMapping is mapped to a unique value
	 * s.t. they are not clashed For eg. all keys(indices) from <0-9> of sequencer
	 * and randomizer is mapped to unique index in VId.
	 */
	public static void modifyAggregatedPlaceMappings(List<Map<Integer, Integer>> originalMappingsList) {

		List<Integer> values = new ArrayList<>();

		List<List<Integer>> keys = new ArrayList<>();
		originalMappingsList.stream().forEach(map -> {
			List<Integer> mapKeys = map.keySet().stream().collect(Collectors.toList());
			Collections.sort(mapKeys);
			keys.add(mapKeys);
			mapKeys.stream().forEach(mapKey -> values.add(map.get(mapKey)));
		});

		Collections.shuffle(values, ThreadLocalRandom.current());

		AtomicInteger valueIndex = new AtomicInteger(0);
		AtomicInteger mapIndex = new AtomicInteger(0);

		originalMappingsList.stream().forEach(map -> {
			keys.get(mapIndex.getAndIncrement()).stream()
					.forEach(mapKey -> map.put(mapKey, values.get(valueIndex.getAndIncrement())));

		});

	}

	public static void main(String[] arg) {

		System.out.println(getBitiedStringPerBaseWithLengthAdjusted("0009", 5, 3, 2));

		System.out.println(calculateNumberOf2ndBasedBitsFromNumberOfSymbols(3, 4));

		System.out.println(getNext2ndBasedBits("11", 2));
		// below functions not checked yet!!
		/*
		 * System.out.println(convertToBitsWithBaseFromNumbers(BigInteger.valueOf(6),
		 * BigInteger.valueOf(3)));
		 * System.out.println(convertToNumberFromBitsWithBase("1001", 3));
		 * System.out.println(getBitiedStringPerBaseWithLengthAdjusted("1230", 64, 4,
		 * 3)); VId vid = new VId(); vid.getRandomizer().setValueInBits("1101");
		 * vid.getTimer().setValueInBits("1100");
		 * vid.getSequencer().setValueInBits("0011"); Map<Integer, Integer> map = new
		 * HashMap<Integer, Integer>(); map.put(0, 3); map.put(1, 8); map.put(2, 11);
		 * map.put(3, 6); vid.getRandomizer().setIndexToVidPlaceMappings(map); //create
		 * indexToVIdPlace mappings for seq and timer below //mapTimerAndSequencer(vid);
		 */

	}

}
