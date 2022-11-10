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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class LicenseAdder {

	private static final String licenseText = "" + "/**\r\n"
			+ " * VVId (VId), Vivek's Id or Vivek's virtual Id is a technique  or algorithm to generate \r\n"
			+ " * unique, random, trackless & storageless(no storage is required to keep track of them) IDs (strings, numbers, symbols etc.).\r\n"
			+ " *     Copyright (C) 2020  Vivek Mangla\r\n" + " * \r\n"
			+ " *     This program is free software: you can redistribute it and/or modify\r\n"
			+ " *     it under the terms of the GNU General Public License as published by\r\n"
			+ " *     the Free Software Foundation, either version 3 of the License, or\r\n"
			+ " *     (at your option) any later version.\r\n" + " * \r\n"
			+ " *     This program is distributed in the hope that it will be useful,\r\n"
			+ " *     but WITHOUT ANY WARRANTY; without even the implied warranty of\r\n"
			+ " *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\r\n"
			+ " *     GNU General Public License for more details.\r\n" + " * \r\n"
			+ " *     You should have received a copy of the GNU General Public License\r\n"
			+ " *     along with this program.  If not, see <https://www.gnu.org/licenses/>.\r\n" + " * \r\n"
			+ " *     To connect with author, possible way to reach is via email \r\n"
			+ " *     vivek.funeesh@gmail.com \r\n" + " * */    \r\n" + "\r\n";

	public LicenseAdder() {
	}

	public static void main(String[] args) {
		try {
			traverseAndPrepend(new File("D:\\dell\\Software\\ProjectsAfterSSD\\vVidLib"), licenseText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void traverseAndPrepend(File dir, String toPrepend) throws Exception {

		if (dir.isDirectory()) {

			String[] subDir = dir.list();
			for (String subDirContent : subDir) {
				traverseAndPrepend(new File(dir, subDirContent), toPrepend);
			}
		} else if (dir.isFile() && dir.getName().endsWith(".java")) {
			System.out.println(dir.getAbsoluteFile());
			prependInBufferMode(dir, toPrepend);

		}

	}

	public static void prependInBufferMode(File file, String toAppend) throws IOException {
		File temp = File.createTempFile(file.getName(), ".java");
		BufferedWriter writer = new BufferedWriter(new FileWriter(temp, true));
		writer.write(toAppend);

		BufferedReader reader = new BufferedReader(new FileReader(file));

		while (reader.ready()) {
			writer.write(reader.readLine());
			writer.newLine();
		}
		reader.close();
		writer.close();

		Files.delete(file.toPath());
		Files.copy(temp.toPath(), file.toPath());

	}

}
