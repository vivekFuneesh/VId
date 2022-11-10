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

package com.vivek.vVidLib.models;

import java.math.BigInteger;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Timer extends CommonFields {
	/** Original value generated by Timer */
	private BigInteger actualValue;

	private int precision;

	/** Original value, unmodified, unprocessed, precised. */
	private BigInteger precisedValue;

	private BigInteger baseTime;

	/**
	 * can be passed as an account's session(if deployed on a server where nextId
	 * request is user-specific) or any particular object that will be shared across
	 * multiple threads/accounts/sessions for getting next VId. Eg. session object
	 * for user-specific(as mentioned before), application-context for
	 * context-specific environment (where it's deployed on a server but nextId is
	 * required by anyone so a sync would be required for all requests.
	 */
	private SynchronizingObject synchronizingObject;
}