package com.utils.table;

import java.util.List;
import java.util.stream.Collectors;

import com.utils.BinaryHandler;
import com.utils.Common;
import com.utils.text.TextAlign;
import com.utils.text.TextColor;
import com.utils.text.TextHandler;

public class TableUI {

	private static int 			dimension		= 80;
	private static String 		formatLine		= "";
	private static String 		formatFillTitle	= "";
	private static String 		formatFillKey	= "";
	private static String		formatFillVal	= "";
	private static TextAlign 	alignTitle		= TextAlign.CENTER;
	private static TextAlign 	alignKey		= TextAlign.LEFT;
	private static TextAlign 	alignValue		= TextAlign.LEFT;
	
	/*
	public TableUI() {}

	public TableUI(int dimension, String formatLine, String formatFillTitle, String formatFillKey,
			String formatFillVal, TextAlign alignTitle, TextAlign alignKey, TextAlign alignValue) {
		setDimension(dimension);
		setFormatLine(formatLine);
		setFormatFillTitle(formatFillTitle);
		setFormatFillKey(formatFillKey);
		setFormatFillVal(formatFillVal);
		setAlignTitle(alignTitle);
		setAlignKey(alignKey);
		setAlignValue(alignValue);
	}
	*/

	public static int getDimension() {
		return dimension;
	}

	public static void setDimension(int dimension) {
		TableUI.dimension = dimension;
	}

	public static String getFormatLine() {
		return formatLine;
	}

	public static void setFormatLine(String formatLine) {
		TableUI.formatLine = formatLine;
	}

	public static String getFormatFillTitle() {
		return formatFillTitle;
	}

	public static void setFormatFillTitle(String formatFillTitle) {
		TableUI.formatFillTitle = formatFillTitle;
	}

	public static String getFormatFillKey() {
		return formatFillKey;
	}

	public static void setFormatFillKey(String formatFillKey) {
		TableUI.formatFillKey = formatFillKey;
	}

	public static String getFormatFillVal() {
		return formatFillVal;
	}

	public static void setFormatFillVal(String formatFillVal) {
		TableUI.formatFillVal = formatFillVal;
	}

	public static TextAlign getAlignTitle() {
		return alignTitle;
	}

	public static void setAlignTitle(TextAlign alignTitle) {
		TableUI.alignTitle = alignTitle;
	}

	public static TextAlign getAlignKey() {
		return alignKey;
	}

	public static void setAlignKey(TextAlign alignKey) {
		TableUI.alignKey = alignKey;
	}

	public static TextAlign getAlignValue() {
		return alignValue;
	}

	public static void setAlignValue(TextAlign alignValue) {
		TableUI.alignValue = alignValue;
	}
	
	public static String titledTable(String title, List<String[]> data, String sep) {
		return 
			tableTitle(title,3)+
			tableDataRows(data,sep,2)
		;
	}
	
	public static String tableTitle(String str, int flag) {
		int tableWidth = dimension-2;
		String s = "";
		int bit = 0;
		if (BinaryHandler.bitEquals(flag,bit++)) {
			s+=TextColor.format("\n+"+"-".repeat(tableWidth)+"+", formatLine);
		}
		for (String tmp : TextHandler.strCutBefore(str,tableWidth-2).lines().collect(Collectors.toList())) {
			s+=TextColor.format("\n|", formatLine);
			s+=TextColor.format(alignTitle.justify(tmp, tableWidth), formatFillTitle);
			s+=TextColor.format("|", formatLine);
		}
		if (BinaryHandler.bitEquals(flag,bit++)) {
			s+=TextColor.format("\n+"+"-".repeat(tableWidth)+"+",formatLine);
		}
		return s;
	}
	
	public static String tableDataRows(List<String[]> data, String sep, int flag) {
		int tableWidth = dimension-2;
		String s = "";
		int bit = 0;
		int keyWidth = Common.maxLength(Common.indexOfArray(data, 0));
		int valWidth = tableWidth-(1+keyWidth+1+sep.length()+2); // +1 added for space on keyWidth, and -1 added for space on valWidth
		if (BinaryHandler.bitEquals(flag,bit++)) {
			s+=TextColor.format("\n+"+"-".repeat(tableWidth)+"+", formatLine);
		}
		for (String[] row : data) {
			String key = row.length>0?row[0]:"";
			String val = row.length>1?TextHandler.strCutBefore(row[1],valWidth):""; // deleted -1 on valWidth
			if (key.isBlank()&&val.isBlank()) {s+=tableDataDelimiter(sep);continue;}
			s+=TextColor.format("\n|", formatLine);
			s+=TextColor.format(" "+alignKey.justify(key, keyWidth)+" ", formatFillKey);
			//s+=COLORS.format(formatFillKey, String.format(" %-"+keyWidth+"s",key));
			s+=TextColor.format(sep, formatLine);
			if (val.contains("\n")) {
				int pos = val.indexOf("\n");
				s+=TextColor.format(" "+alignValue.justify(val.substring(0,pos), valWidth)+" ", formatFillVal);
				//s+=COLORS.format(formatFillVal, String.format(" %-"+valWidth+"s", val.substring(0,pos)));
				s+=TextColor.format("|", formatLine);
				s+=tableDataMLines(keyWidth+2, val.substring(pos+1), sep);
			}
			else {
				s+=TextColor.format(" "+alignValue.justify(val, valWidth)+" ", formatFillVal);
				//s+=COLORS.format(formatFillVal, String.format(" %-"+valWidth+"s", val));
				s+=TextColor.format("|", formatLine);
			}
		}
		if (BinaryHandler.bitEquals(flag,bit++)) {
			s+=TextColor.format("\n+"+"-".repeat(tableWidth)+"+", formatLine);
		}
		return s;
	}
	
	private static String tableDataMLines(int keyWidth, String value, String sep) {
		int tableWidth = dimension-2;
		int leftSpace = tableWidth-(keyWidth+sep.length()+1);
		String s = "";
		for (String str : value.lines().collect(Collectors.toList())) {
			s+=TextColor.format("\n|", formatLine);
			s+=TextColor.format(" ".repeat(keyWidth), formatFillKey);
			s+=TextColor.format(sep, formatLine);
			s+=TextColor.format(String.format(" %-"+leftSpace+"s", str), formatFillVal);
			s+=TextColor.format("|", formatLine);
		}
		return s;
	}
	
	private static String tableDataDelimiter(String sep) {
		return TextColor.format("\n+-"+" ".repeat(dimension-4)+"-+", formatLine);
	}

}
