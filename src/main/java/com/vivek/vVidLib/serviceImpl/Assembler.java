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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.AssemblingChoice.MIXABLE;
import com.vivek.vVidLib.models.AssemblingChoice;
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
		
		
		/**
		 * call assembly functions only for those allParts whih are active i.e. assemblingOrder !=-1
		*/
		List<CommonFields> parts= Arrays.asList(vId.getTimer(), vId.getSequencer(), vId.getRandomizer());
		
		remapAllPartsValuesToVIdSymbolTable(vId, parts);

		switch (vId.getAssemblingChoice()) {
		case NO_MIXING:
			noMixing(vId, parts);
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
	private void remapAllPartsValuesToVIdSymbolTable(VId vId, List<CommonFields> parts) {
		
		parts.stream()
			.filter(part->vId.getAssemblingOrder().get(part.getIdentitySymbol()).intValue()!=-1)
			.forEach(part -> {

			List<String> indicesInFinalTable = new ArrayList<>();
//			System.out.println("Processing "+part.hashCode()+"->"+ part+"\n"
//					+"but vId timer part is:"+"\n"+
//					vId.getTimer().hashCode()+"->"+vId.getTimer());
			List<String> indicesInLocalTable = VIdUtility.convertTo1stBasedListOfNumbersFromBitsWithSecondBase(
					part.getValueInBits(), part.getBitLengthConstraint(), part.getSecondBase());
//			 System.out.println(indicesInLocalTable+" \n"+ part.getValueInBits()+" \n "
//			 		+ "bitLenConstraint: " + part.getBitLengthConstraint());

			indicesInLocalTable.stream().forEach(index -> {
				String symbols = part.getSymbolTable().get(Integer.valueOf(index));
//				 System.out.println("Calling for "+ symbols +" symbolTable size="
//				 +part.getSymbolTable().size());
				indicesInFinalTable.add("" + vId.getReverseSymbolTableMapping().get(symbols));

			});

			part.setRemappedValueInBitsToVIdSymbolTableInAssembly(indicesInFinalTable.stream()
					.map(str -> VIdUtility.convertToBitsWithBaseFromNumbers(new BigInteger(str),
							BigInteger.valueOf(part.getSecondBase())).stream().collect(Collectors.joining()))
					.map(bits -> {
						return VIdUtility.convertToDefaultValueFromNumber(part.getBitLengthConstraint() - bits.length(),
								0) + bits;
					}).collect(Collectors.joining()));
//			System.out.println("\n seq= "+vId.getSequencer().getValueInBits() +" rect= "+vId.getSequencer().getRemappedValueInBitsToVIdSymbolTableInAsembly()
//					+ "\n ran= "+vId.getRandomizer().getValueInBits() +" rect= "+vId.getRandomizer().getRemappedValueInBitsToVIdSymbolTableInAsembly()
//					+ "\n tim= "+vId.getTimer().getValueInBits() +" rect= "+vId.getTimer().getRemappedValueInBitsToVIdSymbolTableInAsembly()
//					);

		});
	}

	
	private void mixAndAssemble(VId vId) {
		
		//shift through mixable and assemblingOrder!=-1 (means active) 
		//case 0 mixable then call NO  MIXING
		//case 2 mixable then mix then using LSB over mSB mixer and concatenate others according to assemblingOrder
		//case 3 or more :
		//		if Randomizer is active then 
		//				spread randomizer over other components
		//				use lsb over msb mixer in individual spreaded parts
		//				concatenate them according to individual part's assembling order
		//				use lsb over msb mixer on whole
		//				
		//			
		
		
	}
	
	
	private void mixAll(VId vId) {

		Timer timer = vId.getTimer();
		Randomizer ran = vId.getRandomizer();
		Sequencer seq = vId.getSequencer();

		switch (vId.getShufflingChoiceInAssembly()) {
		case FIXED_POSITIONS: {
			String part1 = timer.getRemappedValueInBitsToVIdSymbolTableInAssembly() + ran.getRemappedValueInBitsToVIdSymbolTableInAssembly()
					.substring(0, ran.getRemappedValueInBitsToVIdSymbolTableInAssembly().length() / 2);
			String part2 = seq.getRemappedValueInBitsToVIdSymbolTableInAssembly() + ran.getRemappedValueInBitsToVIdSymbolTableInAssembly()
					.substring(ran.getRemappedValueInBitsToVIdSymbolTableInAssembly().length() / 2);

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

	private void mixOnlyTwoParts(VId vId, List<CommonFields> allParts) {
		
		
		int count = (int)allParts.stream().filter(part->part.isToBeMixed()).count();
		if( count>=3 && 
				allParts.stream().anyMatch(part->part.isToBeMixed() && 
						part.getIdentitySymbol().equals(vId.getRandomizer().getIdentitySymbol())) 
				) {
			
		}
		
		AtomicInteger lowestOrderInMixed = new AtomicInteger(Integer.MAX_VALUE);
		
		
		
		//1st concatenate parts that are to be mixed with each other
		String result = allParts.stream().filter(part->part.isToBeMixed())
		.sorted((a,b)->{
			lowestOrderInMixed.set(Math.min(
						Math.min(vId.getAssemblingOrder().get(b.getIdentitySymbol()).intValue(), 
					vId.getAssemblingOrder().get(a.getIdentitySymbol()).intValue()), lowestOrderInMixed.get()
											)
									);
			return vId.getAssemblingOrder().get(a.getIdentitySymbol()).intValue() -
				vId.getAssemblingOrder().get(b.getIdentitySymbol()).intValue();
		})
		.map(part->part.getRemappedValueInBitsToVIdSymbolTableInAssembly())
		.collect(Collectors.joining());
		
		//mix all of these 
		switch (vId.getShufflingChoiceInAssembly()) {
			case FIXED_POSITIONS: {
				result = Stream.of(VIdUtility.lsbOverMsbMixer(new char[result.length()], result)).map(String::valueOf)
						.collect(Collectors.joining());
				break;
			}
			case RANDOM_PLACE_MAPPINGS: {
				
				break;
			}
		}
		//now concatenate as : take lowest assemblingOrder from mixed ones and concatenate with others on ass.Order basis
		
		
		List<String> finalList = new ArrayList<>();
		
		result = Stream.concat(allParts.stream().filter(part->!part.isToBeMixed())
		.map(part-> 
		List.of(part.getRemappedValueInBitsToVIdSymbolTableInAssembly(),
				vId.getAssemblingOrder().get(part.getIdentitySymbol()).intValue()))
		, Stream.of(List.of(result,lowestOrderInMixed.get())))
		.sorted((a,b)->(Integer)a.get(1)-(Integer)b.get(1))
		.map(x->(String)x.get(0))
		.collect(Collectors.joining());
		
		
	}
	
	private void mixOnlyTimerAndSequencer(VId vId) {

		Timer timer = vId.getTimer();
		Randomizer ran = vId.getRandomizer();
		Sequencer seq = vId.getSequencer();

		switch (vId.getShufflingChoiceInAssembly()) {
		case FIXED_POSITIONS: {
			String result = timer.getRemappedValueInBitsToVIdSymbolTableInAssembly() + seq.getRemappedValueInBitsToVIdSymbolTableInAssembly();
			result = Stream.of(VIdUtility.lsbOverMsbMixer(new char[result.length()], result)).map(String::valueOf)
					.collect(Collectors.joining());
			result = result + ran.getRemappedValueInBitsToVIdSymbolTableInAssembly();
			vId.setValueInBitsPostAssembly(result);
			break;
		}
		case RANDOM_PLACE_MAPPINGS: {
			char[] result = assembleByPlaceMappings(vId, vId.getRandomizer());

			vId.setValueInBitsPostAssembly((IntStream.range(0, result.length).filter(num -> result[num] != 0)
					.mapToObj(num -> String.valueOf(result[num])).collect(Collectors.joining()))
					+ vId.getRandomizer().getRemappedValueInBitsToVIdSymbolTableInAssembly());
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
//				System.out.println(entry.getKey()+"->"+entry.getValue()+
//						"\n"+part.getRemappedValueInBitsToVIdSymbolTableInAsembly());

				result[entry.getValue()] = part.getRemappedValueInBitsToVIdSymbolTableInAssembly().charAt(entry.getKey());
			});
		});
		return result;
	}

	// t-s-r
	private void noMixing(VId vId, List<CommonFields> parts) {

//		Timer timer = vId.getTimer();
//		Randomizer ran = vId.getRandomizer();
//		Sequencer seq = vId.getSequencer();

//		vId.setValueInBitsPostAssembly(timer.getRemappedValueInBitsToVIdSymbolTableInAsembly()
//				+ seq.getRemappedValueInBitsToVIdSymbolTableInAsembly() + ran.getRemappedValueInBitsToVIdSymbolTableInAsembly());

//		String temp = vId.getValueInBitsPostAssembly();
		
		
		vId.setValueInBitsPostAssembly(parts.stream()
		.filter(part->vId.getAssemblingOrder().get(part.getIdentitySymbol()).intValue()!=-1)
		.sorted((a,b)->vId.getAssemblingOrder().get(a.getIdentitySymbol()).intValue() -
				vId.getAssemblingOrder().get(b.getIdentitySymbol()).intValue())
		.map(part-> part.getRemappedValueInBitsToVIdSymbolTableInAssembly())
		.collect(Collectors.joining())
		);
//		System.out.println(vId.getTimer().getRemappedValueInBitsToVIdSymbolTableInAsembly()+" "+
//		vId.getSequencer().getRemappedValueInBitsToVIdSymbolTableInAsembly()+" "+
//				vId.getRandomizer().getRemappedValueInBitsToVIdSymbolTableInAsembly()+"\n"+
//				vId.getValueInBitsPostAssembly()+"\n"+
//				temp);
		//ASSEMBLING_ORDER.values()[]
//
//		vId.setValueInBitsPostAssembly(map.get(vId.getAssemblySequence().name().substring(0, 1))
//				.getRemappedValueInBitsToVIdSymbolTableInAsembly()
//				+ map.get(vId.getAssemblySequence().name().substring(1, 2)).getRemappedValueInBitsToVIdSymbolTableInAsembly()
//				+ map.get(vId.getAssemblySequence().name().substring(2, 3)).getRemappedValueInBitsToVIdSymbolTableInAsembly());

//		if (!temp.equals(vId.getValueInBitsPostAssembly())) {
//			System.out.println("no-mixing assembled are not same..");
//			System.out.println(vId.getAssemblySequence().name() + "\n" + timer.getRemappedValueInBitsToVIdSymbolTableInAsembly()
//					+ "\n"
//					+ map.get(vId.getAssemblySequence().name().substring(0, 1)).getRemappedValueInBitsToVIdSymbolTableInAsembly());
//
//			System.out.println("\n" + seq.getRemappedValueInBitsToVIdSymbolTableInAsembly() + "\n"
//					+ map.get(vId.getAssemblySequence().name().substring(1, 2)).getRemappedValueInBitsToVIdSymbolTableInAsembly());
//
//			System.out.println("\n" + ran.getRemappedValueInBitsToVIdSymbolTableInAsembly() + "\n"
//					+ map.get(vId.getAssemblySequence().name().substring(2, 3)).getRemappedValueInBitsToVIdSymbolTableInAsembly());
//
//			System.out.println("exiting.");
//			System.out.println("printing\n" + temp + "\n" + vId.getValueInBitsPostAssembly() + "\n" + map + " \n"
//					+ vId.getAssemblySequence().name().substring(2, 3) + "\n" + map.keySet());
//			System.exit(0);
//		}

	}

}
