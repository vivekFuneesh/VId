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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.AssemblingChoice;
import com.vivek.vVidLib.models.CommonFields;
import com.vivek.vVidLib.models.Randomizer;
import com.vivek.vVidLib.models.Sequencer;
import com.vivek.vVidLib.models.SequencerChoice;
import com.vivek.vVidLib.models.ShufflingChoice;
import com.vivek.vVidLib.models.SynchronizingObject;
import com.vivek.vVidLib.models.Timer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.utility.VIdUtility;

/**
 * Question: For variable length VId, as bits length will also be affected and
 * hence their mapping will be affected too.
 * 
 * ToCheck: How will bits length be affected?--> As sequencer will start from 0,
 * and 0000 wil not be parsable to digits
 * 
 * Solution: For Variable Length VId, use only fixed-Mapping and
 * fixed-shuffling. Revert Question: --> Is variable length possible? -> because
 * sequencer starting from 0 so if timer increases in length then let's say t =
 * 4 digits, seq start from 1 digit so t+s = 5 digit. now s goes till 2 digits
 * so t+s is 6 digits. Now t increases so even if s starts with 1 digit, t+s = 6
 * digits.
 * 
 * --> Sol. to check: In this case then upon length increment, shuffling of
 * timer wont be there!! --> seems incorrect sol. check once!! --> even if
 * shuffling is not there same numbers can easily (or will to be precise) appear
 * again! --------------------------------------------- TODO: for creating
 * default mappings-> create a temporary default vId and from those number of
 * bits do work..
 * 
 * Inside AdjustorAndUpdator --> as number of bits increases then mappings will
 * be modified accordingly and checks will be for bits-length, not
 * digits-length. Timer should start (if starting or restarting for 1st
 * time-after server crashes) with a precision lapse.!
 * 
 * create a FIXED_LENGTH_VERSION_2 that will have 0000 appended and bits for 0
 * as separate. These can have sequencer value restart and start from 0. Check
 * for the same in AdjustorAndUpdator!
 * 
 * Server-DB should be updated upon increment in bits length and digits length
 * both.
 * 
 * Question: As final version's assembly and manipulation is in bits so db
 * should be notified only upon change in bits length and not for
 * digits-length!! 1st Explain--> NO:: say t = 100, firstBase = 8, secondBase =
 * 2, lengthCOnstraint = 3, so seq start from initialDigitdsCount = 1 from 0
 * till 8, bits remain same(1 after 1st base) .. upon 8- bits become 2(after 1st
 * base) so server is notified.. now it will not be notified till seq = 63 (bits
 * required after 1st base are 2 only). at that point generation stops and
 * restarts at t=1100 so before (seq + t) = (1 _ 100 .. 11 _ 100 .. 63 _ 100) ..
 * and now ( 1 _ 1100 .. 63 _ 1100 ).
 * 
 * 2nd Explain: but if we parse saved state before restarting via bits only
 * then.. as 2 bits & 1 digit(8 value) were saved so new number of digits_count
 * should restart from 1 but value should start from 8. HOW_TO_PROVE: BASE^(N)
 * -1 .. BASE^(N-1) _ T = BASE^N -1 .. BASE^(N-1) _ T+X ; at-least one +ve
 * number N, BASE and T, X to satisfy this eq. T = a*10^b + c S.T. NUMBER OF
 * DIGITS IN T ARE b T+X = a*10^b + c +X S.T. NUMBER OF DIGITS IN T+X ARE b Num1
 * = BASE^(N) -1 .. BASE^(N-1) _ T = M1*10^b + a*10^b + c Num2 = BASE^N -1 ..
 * BASE^(N-1) _ T+X = M2*10^b + a*10^b + c +X I.E. M1*10^b = M2*10^b +X I.E.
 * (M1-M2)*10^b = X I.E. M1>M2 FOR X TO BE +VE; AS T+X IS ALWAYS WHEN X IS +VE.
 * but no. of digits in X would become more than b i.e. b+1, so it contradicts
 * above assumption. hence, no such number exists s.t. above equation holds!!
 * 
 * --confusions to resolve via below--
 * 
 * 3 systems are here:: digits system, bits system, symbolTable system (length
 * of symbolTable). as timer value in raw form is generated in digits system, so
 * easy to manipulate via that otherwise directly after generation- if map to
 * bits then manipulation can be done via bits system, if map to symbolTable
 * then manipulation can be done via symbolTable system. ::::::Ultimately,
 * symbolTable system is in dominance because final results will be mapped to
 * symbolTable system.
 * 
 * So when value of timer changes in digit-form then -> 1.) reset sequencer in
 * digits form with value from 0 and length from old one. 2.) --same as above
 * for randomizer-- 3.) shuffle sequencer's symbol store, internal mappings(acc.
 * to shuf. level & choice) and indexToVId mappings. 4.) if(bits-length in timer
 * are incrementing after value changes) --> as bit-length is incrementing, So
 * add new internal mappings and reshuffle them. Add new indexToVId mappings and
 * reshuffle them. This will remain unique because it will be mapped to
 * symbolTable system and not to digits system, so 0, 00, 000 will be unique.
 * 
 * $ But if timer's digits increase in length, then bits length may not increase
 * for eg. with base as 8 digitValue from 8 till 63, number of bits will remain
 * same i.e. 2. So if bit length is same that mean domain of 2 digits in
 * symbolTable system has not been completed OR if we shuffle symbolTable or
 * placeMappings then duplicate values may come up. eg: shuffle symbolTable[ 10
 * was pointing to AB ,.. 11,12 now table shuffled so even 12 or 13 is also
 * pointing to AB]. { 10 corresponds to 8 of digitsSystem with base as 8 and 11
 * to 9, 12 to 10 and 13 to 11 } eg: shuffle internalMappings[ 10 = 001000, 11=
 * 001001, 12=001010-> now shuffling so 12 might map back to 001001]
 * 
 * So upon increment in timer's length -> must not be the deciding criteria!
 * Only valueInBits() value or length should be the deciding criteria!!
 * ValueInBits() value will always change with change in timer's digits value
 * but for the sake of consistency, better to keep checks based on
 * valueInBits().
 * 
 * $ but when seq is fast and timer value has not changed then if bits-length of
 * sequencer is changing then same as above:
 * 
 * $ because change(incrementing) in bits-length is leading to incrementing in
 * VID's length, so indexToVId mappings will be added and reshuffled too.
 * 
 * $ because manipulation is done in bits-form so digitsInternalMappings will be
 * shuffled only when timer's bits-value are changing(ran and seq) or
 * bits-length is increasing (for all)
 * 
 * Apply this for AdjustorAndUpdator, Shuffler, Assembler.
 * 
 * :::::::::::Final resolute::::::::::: For randomizer: As value can change
 * randomly and it's not guaranteed to have constantly increase in bits length
 * like in sequencer, so either keep bits length of randomizer to be highest
 * with probability of all values OR --below is also faulty, not possible--
 * process all 3 in digits form (i.e. generation, adjust&Update, shuffle and
 * assemble then after assemble convert to bits form and do the shuffling!!-->
 * NOT POSSIBLE as in this case too randomizer's value would go for varying
 * length of bits.
 * 
 * For Sequencer and Timer: Basic Shuffling(in digits domain) same problem is
 * there.. shuffling digits instead of bits might lead to increment in number of
 * digits or decrement in number of digits. So basic shuffling is to be
 * removed!! Otherwise just like randomizer, maximum number of bits to be taken
 * but that would increase the size of vID unnecessarily at the start of
 * generation for the choice of basicShuffling!!
 * 
 * $Make corresponding changes in generators, shuffler, adjust&update, assemble.
 * restrictedBitsLength must be initialized to appropriate value.
 * 
 * $Every manipulation must be in bits-level, no meaning for sequencer to start
 * from 0000 OR 1000 as length will remain same so, do this at bits level
 * instead of digits-length level.
 * 
 * $For ran, seq, timer: generate 2nd based bits except timer (which to be
 * converted to 2nd based bits after generation in digits form). $For a VId of
 * given length L, x symbols for timer(timer's relative time-value(parent's
 * birth date&Time) to be adjusted accordingly), y symbols for randomizer and L
 * - (x+y) symbols for sequencer. So for generating for y and for L-(x+y)..
 * calculate this and proceed accordingly. For x(timer) number of symbols can be
 * pre-checked by user by calculating using VIdUtility tool.
 * 
 * $For generator and randomizer, generate bits instead of number
 */
@Component
public class InitializerAndVerifier {

	public static void initializeAndVerifyVId(VId vId) {

	}

	public static VId defaultInitializeVId(VId vId) {
		if (vId == null)
			vId = new VId();
		if (vId.getSequencer() == null)
			vId.setSequencer(new Sequencer());
		if (vId.getTimer() == null)
			vId.setTimer(new Timer());
		if (vId.getRandomizer() == null)
			vId.setRandomizer(new Randomizer());

		defaultInitializeSequencer(vId);
		defaultInitializeRandomizer(vId);
		defaultInitializeTimer(vId);

		defaultVIdSettings(vId);

		return vId;
	}

	public static void defaultVIdSettings(VId vId) {
		if (vId.getAssemblingChoice() == null)
			vId.setAssemblingChoice(AssemblingChoice.MIX_TIMER_SEQUENCER_RANDOMIZER);

		if (vId.getShufflingChoiceInAssembly() == null)
			vId.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);

		Map<String, Integer> reverseSymbolsMapping = new HashMap<>();

		vId.getTimer().getSymbolTable().entrySet().stream().forEach(entry -> {
			reverseSymbolsMapping.put(entry.getValue(), entry.getKey());
		});

		vId.setReverseSymbolTableMapping(reverseSymbolsMapping);
	}

	/**
	 * 
	 * */
	public static void defaultInitializeSequencer(VId vId) {
		Sequencer seq = vId.getSequencer();

		if (seq.getSeqC() == null)
			seq.setSeqC(SequencerChoice.SLOW);
		seq.setInitialNumberOfSymbols(1);

		setDefaultCommonFields(seq);

		if (vId.getSequencer().getShufflingChoice() == ShufflingChoice.RANDOM_PLACE_MAPPINGS)
			defaultInternalPlaceMappings(seq, seq.getInitialNumberOfSymbols());
	}

	public static void defaultInitializeRandomizer(VId vId) {
		Randomizer ran = vId.getRandomizer();
		ran.setCurrentNumberOfSymbols(1);

		setDefaultCommonFields(ran);
		if (vId.getRandomizer().getShufflingChoice() == ShufflingChoice.RANDOM_PLACE_MAPPINGS)
			defaultInternalPlaceMappings(ran, ran.getCurrentNumberOfSymbols());

		ran.setCurrentBitsLength(VIdUtility.calculateNumberOf2ndBasedBitsFromNumberOfSymbols(
				ran.getCurrentNumberOfSymbols(), ran.getBitLengthConstraint()));
	}

	public static void defaultInitializeTimer(VId vId) {
		Timer timer = vId.getTimer();

		setDefaultCommonFields(timer);

		timer.setPrecision(0);

		timer.setBaseTime(BigInteger.valueOf(0));// BigInteger.valueOf(Instant.now().toEpochMilli()));

		SynchronizingObject synchronizingObject = new SynchronizingObject();
		synchronizingObject.setId("default-ID");
		timer.setSynchronizingObject(synchronizingObject);
	}

	public static void setDefaultCommonFields(CommonFields part) {

		part.setSymbolTable(getDefaultRandomSymbolTable(32));

		part.setFirstBase(part.getSymbolTable().size());

		part.setSecondBase(2);

		part.setBitLengthConstraint(
				VIdUtility.calculateBitLengthConstraint(part.getFirstBase() - 1, part.getSecondBase()));

		if (part.getShufflingChoice() == null)
			part.setShufflingChoice(ShufflingChoice.FIXED_POSITIONS);

	}

	public static void defaultIndexToVIdPlaceMappings(VId vId, int totalBitsLength) {
		List<CommonFields> parts = Arrays.asList(vId.getTimer(), vId.getSequencer(), vId.getRandomizer());

		List<Integer> shuffledVIdIndices = IntStream.range(0, totalBitsLength).boxed().collect(Collectors.toList());
		Collections.shuffle(shuffledVIdIndices, ThreadLocalRandom.current());

		AtomicInteger vIdIndex = new AtomicInteger(0);

		parts.stream().forEach(part -> {
			Map<Integer, Integer> indexToVidMappings = new HashMap<>();

			for (int i = 0; i < part.getValueInBits().length(); i++) {
				indexToVidMappings.put(i, shuffledVIdIndices.get(vIdIndex.getAndIncrement()));
			}
			part.setIndexToVidPlaceMappings(indexToVidMappings);

		});
	}

	public static void defaultInternalPlaceMappings(CommonFields part, int numberOfSymbols) {

		Map<Integer, Integer> internalMappings = new HashMap<>();

		List<Integer> shuffledIndices = IntStream.range(0, VIdUtility
				.calculateNumberOf2ndBasedBitsFromNumberOfSymbols(numberOfSymbols, part.getBitLengthConstraint()))
				.boxed().collect(Collectors.toList());

		Collections.shuffle(shuffledIndices, ThreadLocalRandom.current());

		for (int i = 0; i < shuffledIndices.size(); i++) {
			internalMappings.put(i, shuffledIndices.get(i));
		}

		part.setBitsInternalPlaceMappings(internalMappings);

	}

	public static Set<Character> getDefaultSymbols() {
		Set<Character> set = new HashSet<>();

		// IntStream.range(65, 91).forEach(num -> {set.add((char)num);}); //A Z
		// IntStream.range(33, 39).forEach(num -> {set.add((char)num);}); //! &
		IntStream.range(48, 54).forEach(num -> {
			set.add((char) num);
		});
//		set.add((char)43); //+
//		set.add((char)45); //-
//		set.add((char)61); //=
//		set.add((char)64); //@
//		set.add((char)63); //?
//		set.add((char)59); //;
//		set.add((char)91);
//		set.add((char)93);
//		set.add((char)123);
//		set.add((char)125);
//		set.add((char)40);
//		set.add((char)41);
//		set.add((char)60);
//		set.add((char)62);

		IntStream.range(97, 123).forEach(num -> {
			set.add((char) num);
		}); // a z

		return set;
	}

	public static Map<Integer, String> getDefaultRandomSymbolTable(int defaultLength) {
		Map<Integer, String> symbolTable = new HashMap<>();
		List<Character> defaultSymbols = new ArrayList<>(getDefaultSymbols());

		Collections.shuffle(defaultSymbols, ThreadLocalRandom.current());

		for (int i = 0; i < defaultLength; i++) {
			symbolTable.put(i, "" + defaultSymbols.get(i));
		}

		return symbolTable;
	}

	public static void main(String[] str) {
		System.out.println(getDefaultSymbols().size());
		long tableSize = getDefaultRandomSymbolTable(32).entrySet().stream().count();
		System.out.println(tableSize);
	}

}
