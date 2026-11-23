package com.vivek.vVidLib.models;

public enum KeyChoice {

	PROFILES_WITH_KEY,

	/**
	 * For profiles without key (when no data is saved anywhere : 
	 * 	1. SymbolTable of Timer and VId must not be altered unless length changes:
	 * 		SymbolTable of Timer and VId must be hard-coded for length change scenarios to keep 
	 * 		state as consistent. Either hard-code or populate from encrypted and private properties-file
	 *  2. Timer's Internal shuffling must be Fixed Positions as positions are not tracked after system restart.
	 *  3. Similarly, Shuffling choice in Assembly must be Fixed Positions, unless positions are
	 *  	being tracked through properties file.
	 *  4. Fast Sequencer is not allowed unless sequencer's number of symbols are being tracked 
	 *  	through encrypted properties file.
	 * */
	PROFILES_WITHOUT_KEY;

}
