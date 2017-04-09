package com.afrikawood.banguiwood.utils;

import java.math.BigDecimal;

public class MathUtilities {
	
	public static double round(double unrounded, int precision, int roundingMode) {
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}

}
