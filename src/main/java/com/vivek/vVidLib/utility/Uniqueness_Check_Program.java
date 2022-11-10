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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Uniqueness_Check_Program {

	public static void main(String[] arg) {

		String[] files = { ((arg != null && arg.length > 0) ? arg[0]
				: "D:\\dell\\Software\\ProjectsAfterSSD\\vVidLib\\SF2Fi_2000.txt") };
		try {
			checkUniqueness(files);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void checkUniqueness(String[] list) throws Exception {

		File file[] = new File[list.length];

		for (int i = 0; i < file.length; i++) {
			file[i] = new File(list[i]);
			if (!file[i].exists()) {
				System.out.println("File doesn't exist " + file[i]);
				System.exit(0);
			}
		}

		Map<String, Integer> map = new HashMap<>();
		String str;
		int count = 0;

		BufferedReader br[] = new BufferedReader[file.length];
		for (int i = 0; i < file.length; i++) {
			br[i] = new BufferedReader(new InputStreamReader(new FileInputStream(file[i].getAbsoluteFile())));
			while (br[i].ready()) {
				str = br[i].readLine();
				if (str == null || str.trim().length() == 0) {
					System.out.println("----read string is -- " + str + " in " + i + "th file");
				} else if (!str.startsWith("Total Database")) {
					str = str.split(" ")[0];

					if (map.get(str) != null) {
						System.out.println("--FATAL ERROR--\n\tDuplicate VId:: " + str + " count = " + count);
						// System.exit(0);
					} else {
						map.put(str, 1);
						count++;
					}

				}
				//
			}
			System.out.println(map.size() + " " + map.entrySet().stream().findFirst().get());
			br[i].close();
			System.out.println("File processed is " + file[i] + " total count till now is " + count);

		}
		System.out.println("\n--All are Unique--");

	}

}
