package com.saga.opencms.util

import org.apache.commons.lang3.RandomStringUtils

public class SgRandom {

	/**
	 * Generate code using length parameters and separator:
	 * (5, "-", 3) -> asdfg-few
	 * @param rnd1
	 * @param sep
	 * @param rnd2
	 * @return
	 */
	public static String genCode(int rnd1, String sep, int rnd2){
		return RandomStringUtils.randomAlphanumeric(rnd1) + sep + RandomStringUtils.randomAlphanumeric(rnd2);
	}
}