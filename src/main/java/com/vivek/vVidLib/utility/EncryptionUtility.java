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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtility {

	public static void main(String[] arg) {
		String key = "password";
//	        File inputFile = new File("D:\\dell\\Software\\ProjectsAfterSSD\\vVidLib\\src\\main\\java\\com\\vivek\\vVidLib\\ServletInitializer.java");
//	        File encryptedFile = new File("ServletInitializer.encrypted");
//	        File decryptedFile = new File("ServletInitializer.decrypted");
//	         
		try {
			// for encrypt
//		        File inputDir = new File("D:\\dell\\Software\\ProjectsAfterSSD\\vVidLib");
//		        File copiedDir = new File("D:\\dell\\Software\\ProjectsAfterSSD\\encrypted");
//		        System.out.println(copiedDir.mkdir()+" = "+ copiedDir);
//		        copiedDir = new File(copiedDir, "vVidLib");
//		        System.out.println(copiedDir.mkdir());
			// ----

			// for decrypt
			File inputDir = new File("D:\\dell\\Software\\ProjectsAfterSSD\\encrypted");
			File copiedDir = new File("D:\\dell\\Software\\ProjectsAfterSSD\\decrypted");
			System.out.println(copiedDir.mkdir() + " = " + copiedDir);

			// ----

			// true for encrypt, false for decrypt
			traverseDirectory(inputDir, key, copiedDir, false);

			// encrypt(key, inputFile, encryptedFile);
			// decrypt(key, encryptedFile, decryptedFile);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void traverseDirectory(File dir, String key, File toCopy, boolean toEncrypt) throws Exception {

		if (dir.isDirectory()) {
			if (!toCopy.exists()) {
				boolean created = toCopy.mkdir();
				System.out.println(created + " = " + toCopy);
			}

			String[] subDir = dir.list();
			for (String subDirContent : subDir) {
				traverseDirectory(new File(dir, subDirContent), key, new File(toCopy, subDirContent), toEncrypt);
			}
		} else if (dir.isFile() && dir.getName().endsWith(".java")) {
			System.out.println(dir.getAbsoluteFile() + " \ntoCopy = " + toCopy);
			if (!toCopy.exists()) {
				toCopy.createNewFile();
			}

			System.out.println("Inside" + toCopy.getParent() + " " + toCopy + " ::");
			// if(!out.exists())out.createNewFile();
			if (toEncrypt)
				encrypt(key, dir, toCopy);
			else {
				decrypt(key, dir, toCopy);
			}
		}

	}

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";

	public static void encrypt(String key, File inputFile, File outputFile) throws Exception {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
	}

	public static void decrypt(String key, File inputFile, File outputFile) throws Exception {
		doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
	}

	private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {

		Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(cipherMode, secretKey);

		FileInputStream inputStream = new FileInputStream(inputFile);
		byte[] inputBytes = new byte[(int) inputFile.length()];
		inputStream.read(inputBytes);

		byte[] outputBytes = cipher.doFinal(inputBytes);

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		outputStream.write(outputBytes);

		inputStream.close();
		outputStream.close();

	}

}
