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

package com.vivek.vVidLib.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.vVidLib.models.InitializerDto;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.serviceImpl.VIdGenerator;
import com.vivek.vVidLib.utility.Uniqueness_Check_Program;

public interface SampleVIdService {

	default String getVId(@Valid InitializerDto dto, ApplicationContext context, ObjectMapper objectMapper) {
		System.out.println("received dto =" + dto.getVIdCountToGenerate());
		VIdGenerator vIdGenerator = context.getBean(VIdGenerator.class);
		vIdGenerator = context.getBean(VIdGenerator.class);
		System.out
				.println("Prepopulated db-calls in generation-process is : " + vIdGenerator.getNumberOfDatabaseCalls());
		// return vIdGenerator.getNext().getVid();
		Map<String, VId> map = new HashMap<>();
		try {
			File file = new File(dto.getSaveIntoFile() + "_" + dto.getVIdCountToGenerate() + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

			for (int i = 0; i < Integer.valueOf(dto.getVIdCountToGenerate()); i++) {

				if (i % 500 == 0)
					System.out.println(" processed" + i);
				VId vId = vIdGenerator.getNext(dto);

				if (i == 0) {
					System.out.println("Sequencer choice" + vId.getSequencer().getSeqC());
					System.out.println("shuffling choice" + vId.getTimer().getShufflingChoice() + " "
							+ vId.getSequencer().getShufflingChoice() + " " + vId.getRandomizer().getShufflingChoice());
					System.out.println("assembling choice" + vId.getAssemblingChoice());
					System.out.println("shuffling choice in assembly" + vId.getShufflingChoiceInAssembly());
					System.out.println("assemblingChoice: " + dto.getAssemblingChoice());
				}

				// System.out.println("DB calls" +
				// String.valueOf(vIdGenerator.getNumberOfDatabaseCalls()));

				if (map.get(vId.getVid()) != null) {
					System.out.println("Duplicate: " + vId.getVid());

					System.out.println(objectMapper.writeValueAsString(map.get(vId.getVid())));
					System.out.println(objectMapper.writeValueAsString(vId));
					System.exit(0);
				} else {
					writer.newLine();
					writer.append(vId.getVid() + "    " + (i + 1));
					writer.flush();
					map.put(vId.getVid(), vId);
				}
			}
			System.out.println("All are unique" + String.valueOf(vIdGenerator.getNumberOfDatabaseCalls()));
			writer.newLine();
			writer.append("Total Database Calls Made (Dynamic Key Updated) : "
					+ String.valueOf(vIdGenerator.getNumberOfDatabaseCalls()));

			writer.flush();
			writer.close();
			Uniqueness_Check_Program.main(new String[] {file.toPath().toString()});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "DONE";
	}

}
