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

import org.springframework.stereotype.Component;

import com.vivek.vVidLib.models.Timer;
import com.vivek.vVidLib.models.VId;
import com.vivek.vVidLib.service.Generator;
import com.vivek.vVidLib.utility.VIdUtility;

@Component
public class TimerGenerator implements Generator {

	public void generate(VId timerz) {

		Timer timer = timerz.getTimer();

		timer.setActualValue(BigInteger.valueOf(getTimeInMillis()).subtract(timer.getBaseTime()));

		timer.setPrecisedValue(timer.getActualValue().divide(BigInteger.TEN.pow(timer.getPrecision())));

		VIdUtility.populateValueAndLengths(timer, timer.getPrecisedValue().toString());
	}

	private long getTimeInMillis() {
		return Instant.now().toEpochMilli();
	}
}
