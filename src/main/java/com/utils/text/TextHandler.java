package com.utils.text;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextHandler {
	
	/**
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
		return capitalize(str, "(?!)", 0);
	}
	
	/**
	 * @param str
	 * @param sep
	 * @return
	 */
	public static String capitalize(String str, String sep) {
		return capitalize(str, sep, 0);
	}
	
	/**
	 * @param str
	 * @param sep
	 * @param limit
	 * @return
	 */
	public static String capitalize(String str, String sep, int limit) {
		if (!str.isBlank()) {
			limit=limit>0?limit:0;
			String[] strList = str.splitWithDelimiters(sep, limit);
			str="";
			for (String s : strList) {
				if (s.equals(sep)) {str+=s;}
				else {
					str+=s.substring(0,1).toUpperCase();
					str+=s.substring(1).toLowerCase();
				}
			}
		}
		return str;
	}
	
	public static String strCutBefore(String str, int limit) {
		String result = "";
		for (String s : str.lines().collect(Collectors.toList())) {
			s=s.replaceAll("\t"," ");
			if (s.length()>limit) {
				int ind = s.lastIndexOf(" ",limit);
				if (ind==-1) {
					result+=s.substring(0,limit-1)+"-";
					ind=limit-1;
				}
				else {result+=s.substring(0,ind);}
				result+="\n"+strCutBefore(s.substring(ind),limit);
			}
			else {result+=s;}
			result+="\n";
		}
		return result.stripTrailing();
	}
	
	public static boolean strMatches(String str, String pattern) {
		return Pattern.compile(pattern).matcher(str).find();
	}
	
	public static int strCount(String search, String str) {
		int count = 0;
		int pos=-1;
		while (true&&!str.isEmpty()) {
			pos = str.indexOf(search);
			if (pos==-1) {break;}
			count+=1;
			str=pos<str.length()?str.substring(pos+1):"";
		}
		return count;
	}
	
	/**
	 * @param date
	 * @param lc
	 * @param minLength
	 * @return
	 */
	public static String fullDate(LocalDate date, Locale lc, int minLength) {
		String str = "";
		String code = lc.toString();
		String day = capitalize(date.getDayOfWeek().getDisplayName(TextStyle.FULL, lc));
		int num = date.getDayOfMonth();
		String month = capitalize(date.getMonth().getDisplayName(TextStyle.FULL, lc));
		int year = date.getYear();
			// If the Language is "fr", then display the date in French format
		if (code.matches("^fr")) {str="%s %02d %s %d".formatted(day,num,month,year);}
			// If the Language is "en" and the country "US", then display the date in American format
		else if (code.matches("^en_US")) {str="%s, %s %02d, %d".formatted(day,month,num,year);}
			// If the Locale doesn't match the previous statements, then display the date in British format
		else {str="%s, %02d %s %d".formatted(day,num,month,year);}
		return String.format(str, "%-"+minLength+"s");
	}
	
	/**
	 * @param date
	 * @param minLength
	 * @return
	 */
	public static String fullDate(LocalDate date, int minLength) {
		return fullDate(date, Locale.getDefault(), minLength);
	}

}
