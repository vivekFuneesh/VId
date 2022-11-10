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

package com.vivek.vVidLib;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.vVidLib.models.AssemblingChoice;
import com.vivek.vVidLib.models.InitializerDto;
import com.vivek.vVidLib.models.SequencerChoice;
import com.vivek.vVidLib.models.ShufflingChoice;
import com.vivek.vVidLib.repository.VIdStateRepo;
import com.vivek.vVidLib.service.SampleVIdService;
import com.vivek.vVidLib.utility.Uniqueness_Check_Program;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class VVidLibApplicationTests {
	
	@Autowired
	SampleVIdService sampleService;

	InitializerDto dto;
	
	@Autowired
	VIdStateRepo repo;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	ObjectMapper om;
	
	
	@Test
	void contextLoads() {
	}
	
	@BeforeEach
	void initialize() {
		dto = new InitializerDto();
		//F
		dto.setSequencerChoice(SequencerChoice.FAST);
		
		//{P,Fi,Fi}
		dto.setSequencerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setRandomizerShufflingChoice(ShufflingChoice.FIXED_POSITIONS);
		dto.setTimerShufflingChoice(ShufflingChoice.FIXED_POSITIONS);
		
		//2
		dto.setAssemblingChoice(AssemblingChoice.MIX_TIMER_SEQUENCER_RANDOMIZER);
		
		//P
		dto.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
	}
	
	@Test
	@Order(1)
	void testSF2P() {
		repo.deleteAll();
		dto.setSaveIntoFile("SF2P");
		dto.setSequencerChoice(SequencerChoice.SLOW);
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();

		Uniqueness_Check_Program.main(new String[]{"SF2P_2000.txt"});
	}
	
	@Test
	@Order(2)
	void testSF2Fi() {
		repo.deleteAll();
		
		dto.setSaveIntoFile("SF2Fi");
		dto.setSequencerChoice(SequencerChoice.SLOW);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.FIXED_POSITIONS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"SF2Fi_2000.txt"});
	}


	@Test
	@Order(3)
	void testSF2Fi_2Nd_Call() {
		
		dto.setSaveIntoFile("SF2Fi");
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"SF2Fi_2000.txt"});
	}
	
	@Test
	@Order(4)
	void testFF2Fi() {
		repo.deleteAll();
		dto.setSaveIntoFile("FF2Fi");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setRandomizerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.FIXED_POSITIONS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FF2Fi_2000.txt"});
	}
	
	@Test
	@Order(5)
	void testFF2P() {
		repo.deleteAll();
		dto.setSaveIntoFile("FF2P");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FF2P_2000.txt"});
	}
	
	@Test
	@Order(6)
	void testSP1Fi() {
		repo.deleteAll();
		dto.setSaveIntoFile("SP1Fi");
		dto.setSequencerChoice(SequencerChoice.SLOW);
		dto.setAssemblingChoice(AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.FIXED_POSITIONS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"SP1Fi_2000.txt"});
	}
	
	@Test
	@Order(7)
	void testSP1P() {
		repo.deleteAll();
		dto.setSaveIntoFile("SP1P");
		dto.setSequencerChoice(SequencerChoice.SLOW);
		dto.setAssemblingChoice(AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"SP1P_2000.txt"});
	}
	
	@Test
	@Order(8)
	void testFP1P() {
		repo.deleteAll();
		dto.setSaveIntoFile("FP1P");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setAssemblingChoice(AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);

		dto.setTimerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FP1P_2000.txt"});
	}

	@Test
	@Order(9)
	void testFP1P_2nd_Call() {
		
		dto.setSaveIntoFile("FP1P");
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FP1P_2000.txt"});
	}
	
	@Test
	@Order(10)
	void testFP1Fi() {
		repo.deleteAll();
		dto.setSaveIntoFile("FP1Fi");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setAssemblingChoice(AssemblingChoice.MIX_ONLY_TIMER_SEQUENCER);
		dto.setShufflingChoiceInAssembly(ShufflingChoice.FIXED_POSITIONS);

		dto.setSequencerShufflingChoice(ShufflingChoice.FIXED_POSITIONS);
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FP1Fi_2000.txt"});
	}
	
	@Test
	@Order(11)
	void testFP1Fi_2nd_Call() {
		dto.setSaveIntoFile("FP1Fi");
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FP1Fi_2000.txt"});
	}
	
	@Test
	@Order(12)
	void testFP0() {
		repo.deleteAll();
		dto.setSaveIntoFile("FP0");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setAssemblingChoice(AssemblingChoice.NO_MIXING);
		//below should be converted to null, check in system.out logs
		dto.setShufflingChoiceInAssembly(ShufflingChoice.FIXED_POSITIONS);

		dto.setSequencerShufflingChoice(ShufflingChoice.FIXED_POSITIONS);
		dto.setTimerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setRandomizerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FP0_2000.txt"});
	}

	@Test
	@Order(13)
	void testFP0_2Nd_Call() {
		dto.setSaveIntoFile("FP0");
				
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"FP0_2000.txt"});
	}
	
	@Test
	@Order(14)
	void testSP0() {
		repo.deleteAll();
		dto.setSaveIntoFile("SP0");
		dto.setSequencerChoice(SequencerChoice.SLOW);
		dto.setAssemblingChoice(AssemblingChoice.NO_MIXING);
		//below should be converted to null, check in system.out logs
		dto.setShufflingChoiceInAssembly(ShufflingChoice.FIXED_POSITIONS);

		dto.setSequencerShufflingChoice(ShufflingChoice.FIXED_POSITIONS);
		dto.setTimerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setRandomizerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"SP0_2000.txt"});
	}
	
	@Test
	@Order(15)
	void testSP0_2Nd_Call() {
		dto.setSaveIntoFile("SP0");
				
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"SP0_2000.txt"});
	}
	
	@Test
	@Order(16)
	void test_MaxLenT_FFi2P_Precision_0_BaseTime_0_Max_Length_Timer() {
		repo.deleteAll();
		dto.setSaveIntoFile("MaxLenT-FFi2P");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setAssemblingChoice(AssemblingChoice.MIX_TIMER_SEQUENCER_RANDOMIZER);
		
		dto.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);

		dto.setSequencerShufflingChoice(ShufflingChoice.FIXED_POSITIONS);
		dto.setTimerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setRandomizerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"MaxLenT-FFi2P_2000.txt"});
	}
	
	@Test
	@Order(17)
	void test_MaxLenT_FP2P_Precision_0_BaseTime_0_Max_Length_Timer() {
		repo.deleteAll();
		dto.setSaveIntoFile("MaxLenT-FP2P");
		dto.setSequencerChoice(SequencerChoice.FAST);
		dto.setAssemblingChoice(AssemblingChoice.MIX_TIMER_SEQUENCER_RANDOMIZER);
		
		dto.setShufflingChoiceInAssembly(ShufflingChoice.RANDOM_PLACE_MAPPINGS);

		dto.setSequencerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setTimerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		dto.setRandomizerShufflingChoice(ShufflingChoice.RANDOM_PLACE_MAPPINGS);
		
		dto.setVIdCountToGenerate(2000);
		assertThat(sampleService.getVId(dto, context, om)).isNotNull();
		Uniqueness_Check_Program.main(new String[]{"MaxLenT-FP2P_2000.txt"});
	}
}
