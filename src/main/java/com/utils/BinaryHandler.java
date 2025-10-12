package com.utils;

import java.util.ArrayList;
import java.util.List;

public class BinaryHandler {
	
	public static int bitOf(int bit) {
		return (int) Math.pow(2,bit);
	}
	
	public static boolean bitEquals(int flag, int bit) {
		bit=bitOf(bit);
		return (flag&bit)==bit;
	}
	
	public static List<Integer> bitsIn(int flag) {
		int bit=0;
		List<Integer> bits = new ArrayList<>();
		while(!(bitOf(bit++)<flag)) {if (bitEquals(flag,bit)) {bits.add(bitOf(bit));}}
		return bits;
	}

}
