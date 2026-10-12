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
 /*
 *     TO CORPORATE ARCHITECTS, ENGINEERING TEAMS, AND INDEPENDENT RE-IMPLEMENTORS: <br>
 *   ⚠️Read Legal And Commercial Compliance Notice from link below⚠️ <br>
 *     https://github.com/vivekFuneesh/VId/blob/master/Legal%20and%20Commercial%20Compliance%20Notice.md
 */

package com.vivek.vVidLib.service;

import com.vivek.vVidLib.models.VId;

public interface HandleBackwardClockDrift {

  /*
  * Possible implementations : 
  *   A.) Initially check with external service for time correctness.
  *       If external service is not reachable then
  *         Fetch time from hardware's embedded clock machine.
  *         Fetch from D.B./Hard-Disk the mapping of virtual time to hardware clock (to be stored
  *            and updated by either using a separate programm or in @PostConstruct of VIdGenerator).
  *         Update Virtual/system clock's precision.
  *       Else Fetch and correct;
  *       Return;
  *   B.) 
  *   1. throw exception
  *   2. wait untill correct time is restored in computing machine.
  *   3. fetch correct time from external service and update computing system's clock.
  *   4. untill clock is corrected, 
  *        generate from skipped domains and ranges, if available in DB.
  *        else if not available then choose any from above 3.
  *
  */
	public void handleBackDrift();
}
