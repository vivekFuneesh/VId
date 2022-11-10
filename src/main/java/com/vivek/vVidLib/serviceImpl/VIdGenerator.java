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
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.vivek.vVidLib.models.AssemblingChoice;
import com.vivek.vVidLib.models.CommonFields;
import com.vivek.vVidLib.models.InitializerDto;
import com.vivek.vVidLib.models.Randomizer;
import com.vivek.vVidLib.models.RandomizerState;
import com.vivek.vVidLib.models.Sequencer;
import com.vivek.vVidLib.models.SequencerState;
import com.vivek.vVidLib.models.ShufflingChoice;
import com.vivek.vVidLib.models.SynchronizingObject;
import com.vivek.vVidLib.models.Timer;
import com.vivek.vVidLib.models.TimerState;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.models.VIdState;
import com.vivek.vVidLib.repository.VIdStateRepo;
import com.vivek.vVidLib.utility.VIdUtility;

/**
 * Questions::
 * 1. sequencer digit count -> start from 1000.. 
 * 2. randomizer digit count -> shuffle might convert value from 1928455.. to 00012, same for sequencer
 * 3. bit-conversion & shuffle will also do same as above along with digits shuffle
 * 
 * Another possible view: VId of length L, expecting x digits from Timer, y from seq. and L-(x+y) from random.
 * */

/**
 * Possible answer apart from required or NO modification::
 * Length of VId will vary from 1 digit to number of digits possible from a given resultant number which has all 9
 * for required purpose, a Default_UMPAPPED_Symbold (reserved-symbol) can  be used that can be appended to resultant
 * VId in-case if length is less than expected with 00s in b/w!!
 * 
 * 	--> Now, after all the observations and work done inside InitializerAndVerifier, a default symbol is not required
 * rather 000s can be parsed using string form and further processing will be in bits form only!.
 * 
 * ---------TO AVOID VARIABLE LENGTH----
 * Generate(generated numbers are indices), then convert to bits.
 * Shuffle only bits(bitwise, not basic) and do not re-process the corresponding string to bits again rather work only in bits!!
 * From the shuffled bits, process the resultant 1stBased indices to get vId
 * */

/**
 * ToCheck::- must use synchronized calls to every getNextId() otherwise IDs would be inconsistent state!
 * 
 * As further extension, can also do final shuffling using predefined placeMappings for VId bits postAssembly
 * */
/**
 * For converting it to UUID, put MAC address along with others and do work... this way it will become UUID with no duplicacy
 * across the entire world. Assuming that MAC address is unique across all network-devices. OR the devices on which this 
 * vVId is installed. 
 * */
/**
 * 1.) Check for data via repo(can be any other method of saving, a generalized
 * and user-choiced-implementations-for-various options like randomizer,
 * generation techniques, shuffling-patterns etc. 2.) If data available --> that
 * means server is restarting so:- load data-> populate into oldVId wait for
 * precision populate updated oldVId into currVId Else --> default initialize
 * and process.
 *
 * 3.) when length in bits increase --> A.) save state in
 * DB/any-other-system-specified-by-user. B.) If above successful then generate
 * final currVId and return that otherwise return oldVId. (make sure oldVId is
 * not modified during processing).
 * 
 * 
 * Flexibilities::-- 1. symbol table can be used of any number of symbols: if
 * count_of_symbols is out of 1st an 2nd base coverage/calculation of all bits
 * then list of values can be used at a particular index with precaution: values
 * at same index on part's-table can be at different index in final-table but
 * values at different index in part's-symbol-table must be at different index
 * in final-table.
 * 
 * 2. dependency on state/key-of-switches&gears can be removed if randomizer's &
 * sequencer's length to be fixed, upon exhaustion-> timer's value to be
 * updated, baseTime to be same.. In this case of switches and gears, even
 * without dynamic-state generation of VIds will be unique and random every time
 * after stop/crash.
 * 
 * 3. skipped domains can be tracked for a particular length and later-on they
 * can be exhausted too.
 * 
 * 4. Can use varying random, not just java implementation of
 * ThreadLocalRandom.. or use multiple random.. doesn't matter.
 * 
 */

@Component
@RequestScope
public class VIdGenerator {

	@Autowired
	ServiceCaller serviceCaller;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired(required = false)
	VIdStateRepo vIdStateRepo;

	@Autowired
	MongoClient client;

	ModelMapper mm = new ModelMapper();

	volatile VIdState state;

	volatile VId currentVId;

	private int numberOfDatabaseCalls;

	public int getNumberOfDatabaseCalls() {
		return this.numberOfDatabaseCalls;
	}

	@PostConstruct
	public void checkAndLoad() {
		System.out.println("Inside checkAndLoad");
		state = vIdStateRepo.findAll().stream().findFirst().orElse(null);

		if (state != null) {
			System.out.println("Loading form DB--");
			currentVId = mm.map(state, VId.class);
			currentVId.setTimer(mm.map(state.getTimerState(), Timer.class));
			currentVId.setSequencer(mm.map(state.getSequencerState(), Sequencer.class));
			currentVId.setRandomizer(mm.map(state.getRandomizerState(), Randomizer.class));

			// putting default value for the time being..
			SynchronizingObject synchronizingObject = new SynchronizingObject();
			synchronizingObject.setId("default-ID");
			currentVId.getTimer().setSynchronizingObject(synchronizingObject);

			BigInteger precisor = BigInteger.TEN.pow(currentVId.getTimer().getPrecision());
			BigInteger newMinimumTimer = BigInteger.valueOf(Instant.now().toEpochMilli())
					.subtract(currentVId.getTimer().getBaseTime());

			currentVId.getTimer().setPrecisedValue(newMinimumTimer.divide(precisor));

			newMinimumTimer = currentVId.getTimer().getPrecisedValue().add(BigInteger.ONE).multiply(precisor)
					.add(currentVId.getTimer().getBaseTime());

			long waitTime = newMinimumTimer.subtract(BigInteger.valueOf(Instant.now().toEpochMilli())).longValueExact();
			if (waitTime > 0) {
				System.out.println("Waiting Time  after loder: in ms = " + waitTime);
				try {
					synchronized (currentVId.getTimer().getSynchronizingObject()) {
						currentVId.getTimer().getSynchronizingObject().wait(waitTime);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public VId getNext(InitializerDto dto) {
		VId oldVId = currentVId;
		currentVId = getNext(oldVId, dto);

		if (oldVId == null || currentVId.getVid().length() > oldVId.getVid().length()) {
			VIdState newState = mm.map(currentVId, VIdState.class);

			if (oldVId != null) {
				newState.set_id(state.get_id());
			}

			newState.setTimerState(mm.map(currentVId.getTimer(), TimerState.class));
			newState.setSequencerState(mm.map(currentVId.getSequencer(), SequencerState.class));
			newState.setRandomizerState(mm.map(currentVId.getRandomizer(), RandomizerState.class));

			numberOfDatabaseCalls++;
			System.out.println("Calling DB-" + numberOfDatabaseCalls);

			state = vIdStateRepo.save(newState);

		}
		// System.out.println("After db call : "+ numberOfDatabaseCalls);
		return currentVId;
	}

	public void sampleGenerateVIds(InitializerDto dto) {
		VId currVId = null;
		for (int i = 0; i < 35; i++) {
			currVId = getNext(currVId, dto);
			System.out.println(currVId.getVid());
		}
	}

	/**
	 * if oldVId is null-> defaultInitialize currVId else load(deep-copy into
	 * currVId) and process currVId.
	 */
	public VId getNext(VId oldVId, InitializerDto dto) {

		VId currVId;

		if (oldVId == null) {
			currVId = new VId();
			currVId.setTimer(new Timer());
			currVId.setSequencer(new Sequencer());
			currVId.setRandomizer(new Randomizer());

			if (dto.getShufflingChoiceInAssembly() != null)
				currVId.setShufflingChoiceInAssembly(dto.getShufflingChoiceInAssembly());

			if (dto.getRandomizerShufflingChoice() != null)
				currVId.getRandomizer().setShufflingChoice(dto.getRandomizerShufflingChoice());

			if (dto.getSequencerChoice() != null)
				currVId.getSequencer().setSeqC(dto.getSequencerChoice());

			if (dto.getSequencerShufflingChoice() != null)
				currVId.getSequencer().setShufflingChoice(dto.getSequencerShufflingChoice());

			if (dto.getTimerShufflingChoice() != null)
				currVId.getTimer().setShufflingChoice(dto.getTimerShufflingChoice());

			if (dto.getAssemblingChoice() != null)
				currVId.setAssemblingChoice(dto.getAssemblingChoice());

			currVId = InitializerAndVerifier.defaultInitializeVId(currVId);

		} else {
			currVId = objectMapper.convertValue(oldVId, VId.class);
			// copy oldVId to temp (to act like old) and rename oldVId to currVId
		}

		List<CommonFields> parts = Arrays.asList(currVId.getTimer(), currVId.getSequencer(), currVId.getRandomizer());

		// Generate
		generate(currVId, parts);

		// if 1st time then after generate, produce internal & VId place mappings
		if (oldVId == null) {

			if (currVId.getAssemblingChoice() == AssemblingChoice.NO_MIXING) {
				parts.stream().forEach(part -> part.setIndexToVidPlaceMappings(null));
				currVId.setShufflingChoiceInAssembly(null);
			} else if (currVId.getShufflingChoiceInAssembly() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
				InitializerAndVerifier.defaultIndexToVIdPlaceMappings(currVId,
						parts.stream().mapToInt(part -> part.getValueInBits().length()).sum());

				if (currVId.getAssemblingChoice() == AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER) {
					currVId.getRandomizer().setIndexToVidPlaceMappings(null);
				}
			} else {
				parts.stream().forEach(part -> part.setIndexToVidPlaceMappings(null));
			}

			if (currVId.getTimer().getShufflingChoice() == ShufflingChoice.RANDOM_PLACE_MAPPINGS) {
				InitializerAndVerifier.defaultInternalPlaceMappings(currVId.getTimer(),
						currVId.getTimer().getCurrentNumberOfSymbols());
			}

			parts.stream().forEach(part -> {
				if (part.getShufflingChoice() == ShufflingChoice.FIXED_POSITIONS) {
					part.setBitsInternalPlaceMappings(null);
				}
			});
		}

		// AdjustAndUpdate (if temp is not null)
		if (oldVId != null) {
			// System.out.println(" sync Object= "+
			// currVId.getTimer().getSynchronizingObject());
			serviceCaller.getAdjustorAndUpdator().process(serviceCaller, currVId, oldVId);
		}

		// Shuffle
		shuffle(currVId, parts);

		// Assemble
		serviceCaller.getAssembler().assemble(currVId);

		// compile from bits to symbol-mapped-value.
		VIdUtility.mapBitsWithSecondBaseToSymbols(currVId);

		return currVId;
	}

	private void shuffle(VId vId, List<CommonFields> parts) {

		AtomicInteger index = new AtomicInteger(0);

		parts.stream().forEach(part -> {

			serviceCaller.getShuffler(index.getAndIncrement()).bitwiseShuffle(vId);
		});

	}

	private void generate(VId vId, List<CommonFields> parts) {

		AtomicInteger index = new AtomicInteger(0);

		parts.stream().forEach(part -> {
			serviceCaller.getGenerator(index.getAndIncrement()).generate(vId);
		});

	}

}
