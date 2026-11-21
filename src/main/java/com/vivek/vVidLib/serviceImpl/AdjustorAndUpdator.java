/*
*     TO CORPORATE ARCHITECTS, ENGINEERING TEAMS, AND INDEPENDENT RE-IMPLEMENTORS: <br>
*   ⚠️Read Legal And Commercial Compliance Notice from link below⚠️ <br>
*     https://github.com/vivekFuneesh/VId/blob/master/Legal%20and%20Commercial%20Compliance%20Notice.md
*/

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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.CommonFields;
import com.vivek.vVidLib.models.Randomizer;
import com.vivek.vVidLib.models.Sequencer;
import com.vivek.vVidLib.models.SequencerChoice;
import com.vivek.vVidLib.models.ShufflingChoice;
import com.vivek.vVidLib.models.Timer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.utility.VIdUtility;

/**
 * TODO: Instead of digitsCount(), check for increment in Bits-Length because
 * new symbols will be introduced upon bits-addition. TODO: As Increment in bits
 * will lead to increment in length of VId -> so add mappings at that point with
 * usual re-shuffle used! and for sequencer, as previous length is to be
 * used(use previous bitsLength) and reduce mappings accordingly!!
 */
@Component
public class AdjustorAndUpdator {

	/**
	 * parts places in argument-list as :-> < Timer(0) , Sequencer(1) ,
	 * Randomizer(2) > oldTimer is less than new Timer or if sequencer has increased
	 * in length. Preference given to value change to avoid unnecessary length
	 * increment[TODO: can be made flexible]! if timer's value change:-> reset
	 * shuffling mappings within (if applicable), reset symbol-store, can
	 * interchange seq with ran place mappings.
	 * 
	 * If length is incrementing then internalPlaceMappings to include more mappings
	 * for new digit (digit-wise or bitwise) and in VIdPlaceMappings.
	 */
	public void process(ServiceCaller sc, VId currVId, VId oldVId, List<CommonFields> parts) {

		Timer currTimer = currVId.getTimer();

		// It also addresses the case when timer also increased in value simultaneously
		// when sequencer
		// exhausted and increased in length... Timer is given preference as it is
		// continuously increasing.

		if (oldVId.getTimer().getPrecisedValue().compareTo(currVId.getTimer().getPrecisedValue()) < 0) {

			/*
			 * ----------1----------- reset length of sequencerBits to old, shuffle
			 * symbol-store for sequencer and randomizer, regenerate sequencer and
			 * randomizer, reshuffle them with new shuffling mappings(if mappings used), if
			 * in Vid - mappings are used then reallocate positions to both( may interchange
			 * places )
			 */

			handleIncrementInTimerValueWithLengthCheck(sc, currVId, oldVId, parts);

		} else if (oldVId.getSequencer().getCurrentBitsLength() < currVId.getSequencer().getCurrentBitsLength()) {

			if (oldVId.getSequencer().getSeqC() == SequencerChoice.SLOW) {
				/**
				 * Manipulating using Precised Value because oldtimer's Value has been shuffled
				 */
				// wait for timer to increment
				BigInteger oldTimeValue = oldVId.getTimer().getPrecisedValue();

				/** HandleBackwardClockDrift logic should be included here too. */

				while (oldTimeValue.compareTo(currTimer.getPrecisedValue()) == 0) {
//					System.out.println("oldtimer= "+oldVId.getTimer()+"\ncurTimer= "+currVId.getTimer());
//					System.out.println("oldVIdr= "+oldVId+"\ncurVId= "+currVId);
					long waitTime = oldTimeValue.add(BigInteger.ONE)
							.multiply(BigInteger.TEN.pow(currTimer.getPrecision())).longValueExact()
							- currTimer.getActualValue().longValueExact();
					try {
						synchronized (currVId.getTimer().getSynchronizingObject()) {
//							System.out.println("Waiting for " + waitTime + " milliSeconds");
							if (waitTime > 0) {
//								System.out.println("\n--waiting\n");
								currVId.getTimer().getSynchronizingObject().wait(waitTime);

							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						throw new RuntimeException("Unable to increment timer, waiting-problem");
					}
					sc.getTimerGenerator().generate(currVId);
				}

				// same as 1 by waiting for timer to increment in value by resetting sequencer's
				// length to that in old.

//				System.out.println("out of wait");
				handleIncrementInTimerValueWithLengthCheck(sc, currVId, oldVId, parts);

			} else {
				/*
				 * In this case, entire sequencer range has been exhausted for a given timer
				 * value.
				 */
				addInternalAndVIdMappings(oldVId.getSequencer(), currVId.getSequencer(), currVId, oldVId);
				// both 1 and 2 except for the changes in length of sequencer in 1
				currVId.getSequencer().setInitialNumberOfSymbols(currVId.getSequencer().getCurrentNumberOfSymbols());

				resetRegenerateReshuffleAndRemapOfInternalAndVIdMappingsForNonTimerParts(sc, currVId,
						parts.stream().filter(part -> part != currVId.getTimer()).toList());
				shuffleTimer(sc, currVId, parts);

			}
		}

	}

	/*
	 * TODO: Check if entire sequencer range has not been exhausted then check if
	 * rectified symbol mappings is required, if required then pull from assembler
	 * or make here. Save current state and old state both to DB to be retrieved
	 * later. Ensure which symbols have been mapped to a particular place, to
	 * properly exhaust complete domain as well.
	 */
	private void handleIncrementInTimerValueWithLengthCheck(ServiceCaller serviceCaller, VId currVId, VId oldVId,
			List<CommonFields> parts) {
		// System.out.println(currVId.getSequencer().getValue());
		currVId.getSequencer().setInitialNumberOfSymbols(oldVId.getSequencer().getCurrentNumberOfSymbols());
		resetRegenerateReshuffleAndRemapOfInternalAndVIdMappingsForNonTimerParts(serviceCaller, currVId,
				parts.stream().filter(part -> part != currVId.getTimer()).toList());

		if (oldVId.getTimer().getCurrentNumberOfSymbols() < currVId.getTimer().getCurrentNumberOfSymbols()) {

			addInternalAndVIdMappings(oldVId.getTimer(), currVId.getTimer(), currVId, oldVId);
			// --------2----------
			// shuffle timer symbol store, reallocate positions in VId, reshuffle with new
			// place mappings within(if applicable)

			shuffleTimer(serviceCaller, currVId, parts);

		} else if (oldVId.getTimer().getCurrentBitsLength() != currVId.getTimer().getCurrentBitsLength()) {
			// just a precautionary check!
			throw new RuntimeException("Illegal state of Timer, valueInBits length and no. of Symbols mismatch");
		}
	}

	private void addInternalAndVIdMappings(CommonFields oldPart, CommonFields newPart, VId currVId, VId oldVId) {

		// adding VidMappings.

		int extraBits = newPart.getValueInBits().length() - oldPart.getCurrentBitsLength();
		int oldPartBitsLen = oldPart.getCurrentBitsLength();
		int oldVidLengthInBits = oldVId.getBitsLengthPostAssembly();

		// in assembly line if shuffl-choice is randomPlaceMapping and this part is
		// mixable then update it's mappings
		// or simply put if it's mappings is not null then update as it's in middle so
		// no need to validate.
		// if (currVId.getShufflingChoiceInAssembly() ==
		// ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
		if (newPart.getIndexToVidPlaceMappings() != null) {
			for (int i = 0; i < extraBits; i++) {
				newPart.getIndexToVidPlaceMappings().put(oldPartBitsLen + i, oldVidLengthInBits + i);
			}
		}
		// }

		// adding internal mappings.

		if (newPart.getShufflingChoice() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {

			for (int i = 0; i < extraBits; i++) {

				newPart.getBitsInternalPlaceMappings().put(oldPartBitsLen + i, oldPartBitsLen + i);
			}
		}
	}

	private void shuffleTimer(ServiceCaller serviceCaller, VId currVId, List<CommonFields> parts) {

		Timer currTimer = currVId.getTimer();
		VIdUtility.shuffleSymbolStore(currTimer.getSymbolTable(), ThreadLocalRandom.current());

//		these 2 statements will reset timer-counter while maintaining length/no_of_symbols_in_timer
		currTimer.setPrecisedValue(null);
		currTimer.setInitialNumberOfSymbols(currTimer.getCurrentNumberOfSymbols());

		serviceCaller.getTimerGenerator().generate(currVId);

		VIdUtility
				.modifyAggregatedPlaceMappings(parts.stream().filter(part -> part.getIndexToVidPlaceMappings() != null)
						.map(part -> part.getIndexToVidPlaceMappings()).toList());

		/*if (currVId.getShufflingChoiceInAssembly() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
			if (currVId.getAssemblingChoice() == AssemblingChoice.MIX_TIMER_SEQUENCER_RANDOMIZER) {
				VIdUtility.modifyAggregatedPlaceMappings(
						Arrays.asList(currVId.getSequencer().getIndexToVidPlaceMappings(),
								currVId.getRandomizer().getIndexToVidPlaceMappings(),
								currVId.getTimer().getIndexToVidPlaceMappings()));
			} else if (currVId.getAssemblingChoice() == AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER) {
				VIdUtility.modifyAggregatedPlaceMappings(
						Arrays.asList(currVId.getSequencer().getIndexToVidPlaceMappings(),
								currVId.getTimer().getIndexToVidPlaceMappings()));
			}
		}*/

		if (currTimer.getShufflingChoice() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
			currTimer.setBitsInternalPlaceMappings(
					VIdUtility.modifyPlaceMappings(currTimer.getBitsInternalPlaceMappings()));
		}

	}

	private void resetRegenerateReshuffleAndRemapOfInternalAndVIdMappingsForNonTimerParts(ServiceCaller serviceCaller,
			VId currVId, List<CommonFields> parts) {
		Sequencer currSeq = currVId.getSequencer();
		currSeq.setValue(null);

		Randomizer currRan = currVId.getRandomizer();

		// shuffle sequencer symbol store
		VIdUtility.shuffleSymbolStore(currSeq.getSymbolTable(), ThreadLocalRandom.current());
		VIdUtility.shuffleSymbolStore(currRan.getSymbolTable(), ThreadLocalRandom.current());

		serviceCaller.getSequenceGenerator().generate(currVId);
		serviceCaller.getRandomizerGenerator().generate(currVId);

		// shuffle sequencer Place Mappings within, if they are used.
		if (currSeq.getShufflingChoice() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
			currSeq.setBitsInternalPlaceMappings(
					VIdUtility.modifyPlaceMappings(currSeq.getBitsInternalPlaceMappings()));
		}

		// reallocate positions in VId, if placeMappings is used.
		VIdUtility
				.modifyAggregatedPlaceMappings(parts.stream().filter(part -> part.getIndexToVidPlaceMappings() != null)
						.map(part -> part.getIndexToVidPlaceMappings()).toList());
		/*
		if (currVId.getShufflingChoiceInAssembly() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
			if (currVId.getAssemblingChoice() == AssemblingChoice.MIX_TIMER_SEQUENCER_RANDOMIZER) {
				VIdUtility.modifyAggregatedPlaceMappings(
						Arrays.asList(currVId.getSequencer().getIndexToVidPlaceMappings(),
								currVId.getRandomizer().getIndexToVidPlaceMappings()));
			} else if (currVId.getAssemblingChoice() == AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER) {
				VIdUtility.modifyAggregatedPlaceMappings(
						Arrays.asList(currVId.getSequencer().getIndexToVidPlaceMappings()));
			}
		}
		*/

	}

}
