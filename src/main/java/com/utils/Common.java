package com.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.utils.text.TextColor;

public class Common {

	public static int maxLength(List<String> lst) {
		int i = 0;
		for (String str : lst) {
			i=Math.max(i, str.length());
		}
		return i;
	}
	
	public static List<String> indexOfArray(List<Object[]> array, int ind) {
		return (List<String>) array.stream().map(x -> x[ind<x.length?ind:ind<0?0:x.length].toString()).collect(Collectors.toList());
	}
	
	/*
	 * Example for single line prompt :
	 * 	pre = "["
	 * 	sep = "/"
	 * 	suf = "]"
	 * Example for multiline line prompt :
	 * 	pre = ":"
	 * 	sep = "\n  | "
	 * 	suf = "\n  |"
	 */
	public static String showMenu(	List<String[]> menu, String msg, String pre, String sep, String suf, int flag,
									TextColor color0, TextColor color1, TextColor color2, TextColor colorInput) {
		// flag:
		// 1 (bit 0) -> Display the full name of the command
		// 2 (bit 1) -> Display the short name of the command, if (bit 0) placed between {}
		// 4 (bit 2) -> Display the /command name between ()
		boolean newLine = sep.contains("\n");
		String str = TextColor.reset();
		str+=msg+pre;
		for (String[] command : menu) {
			boolean last=menu.getLast().equals(command);
			String[] command0 = command[0].splitWithDelimiters("[A-Z]+",0);
			String s="";
			for (String x : command0) {s+=(x.matches("[A-Z]+")?color1.applyOn(x):color0.applyOn(x));}
			command[0]=s+(newLine&&BinaryHandler.bitsIn(flag).size()>1?" -> ":"");
			command[1]=color1.applyOn(BinaryHandler.bitEquals(flag,0)?"{"+command[1]+"}":command[1]);
			command[2]=color2.applyOn("("+command[2]+")");
			int bit=0;
			s+=newLine?sep:"";
			if (BinaryHandler.bitEquals(flag,bit)) {s+=command[bit]+" ";bit++;}
			if (BinaryHandler.bitEquals(flag,bit)) {s+=command[bit]+" ";bit++;}
			if (BinaryHandler.bitEquals(flag,bit)) {s+=command[bit]+" ";bit++;}
			s=s.stripTrailing();
			s+=newLine||last?"":sep;
		}
		return str+suf+colorInput.applyOn("\n> ",false);
	}

}
