package com.utils.text;

/**
 * The {@code TextColor} enum provides a comprehensive set of ANSI escape codes 
 * used for coloring and formatting text output in terminal environments.
 * <p>
 * It supports foreground and background colors (both standard and light variants),
 * as well as special text styles such as bold, underlined, and reversed.
 * <ul>
 * 	<li>'FG'	Stands for FOREGROUND: The text itself</li>
 * 	<li>'BG'	Stands for BACKGROUND: The background of the line only</li>
 * </ul>
 * <p>
 * This enum also includes a set of utility methods to format strings dynamically 
 * using named color codes or raw ANSI sequences, allowing flexible, readable, 
 * and reusable terminal formatting in Java applications.
 * <p>
 * Key features include:
 * <ul>
 *   <li>Mapping between human-readable names and ANSI escape codes</li>
 *   <li>Support for resetting and combining multiple formatting styles</li>
 *   <li>Methods to apply, remove, or generate formatting from string inputs</li>
 *   <li>Resilient fallbacks when formatting strings contain invalid names/codes</li>
 * </ul>
 * 
 * <strong>Example usage:</strong>
 * <pre>{@code
 * System.out.println(TextColor._FG_$GREEN.applyOn("Success!"));
 * System.out.println(TextColor.format("Warning!", "FYellow,B.Red,Bold"));
 * }</pre>
 * 
 * @author Luna
 * @version 1.0.0
 * @see #format(String, String)
 * @see #getFormatFrom(String)
 * @see #reset()
 */
public enum TextColor {
	
/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||||  ENUMERATION VALUES  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

	/** DEFAULT FORMAT */
	_RESET				("\033[0m"	, ""			),
	_EMPTY				(""			, " "			),
	/** FOREGROUND COLORS */
	_FG_$BLACK			("\033[30m"	, "FBlack"		),
	_FG_$RED			("\033[31m"	, "FRed"		),
	_FG_$GREEN			("\033[32m"	, "FGreen"		),
	_FG_$YELLOW			("\033[33m"	, "FYellow"		),
	_FG_$BLUE			("\033[34m"	, "FBlue"		),
	_FG_$MAGENTA		("\033[35m"	, "FMagenta"	),
	_FG_$CYAN			("\033[36m"	, "FCyan"		),
	_FG_$WHITE			("\033[37m"	, "FWhite"		),
	_FG_$LIGHT_BLACK	("\033[90m"	, "F.Black"		),
	_FG_$LIGHT_RED		("\033[91m"	, "F.Red"		),
	_FG_$LIGHT_GREEN	("\033[92m"	, "F.Green"		),
	_FG_$LIGHT_YELLOW	("\033[93m"	, "F.Yellow"	),
	_FG_$LIGHT_BLUE		("\033[94m"	, "F.Blue"		),
	_FG_$LIGHT_MAGENTA	("\033[95m"	, "F.Magenta"	),
	_FG_$LIGHT_CYAN		("\033[96m"	, "F.Cyan"		),
	_FG_$LIGHT_WHITE	("\033[97m"	, "F.White"		),
	/** BACKGROUND COLORS */
	_BG_$BLACK			("\033[40m"	, "BBlack"		),
	_BG_$RED			("\033[41m"	, "BRed"		),
	_BG_$GREEN			("\033[42m"	, "BGreen"		),
	_BG_$YELLOW			("\033[43m"	, "BYellow"		),
	_BG_$BLUE			("\033[44m"	, "BBlue"		),
	_BG_$MAGENTA		("\033[45m"	, "BMagenta"	),
	_BG_$CYAN			("\033[46m"	, "BCyan"		),
	_BG_$WHITE			("\033[47m"	, "BWhite"		),
	_BG_$LIGHT_BLACK	("\033[100m", "B.Black"		),
	_BG_$LIGHT_RED		("\033[101m", "B.Red"		),
	_BG_$LIGHT_GREEN	("\033[102m", "B.Green"		),
	_BG_$LIGHT_YELLOW	("\033[103m", "B.Yellow"	),
	_BG_$LIGHT_BLUE		("\033[104m", "B.Blue"		),
	_BG_$LIGHT_MAGENTA	("\033[105m", "B.Magenta"	),
	_BG_$LIGHT_CYAN		("\033[106m", "B.Cyan"		),
	_BG_$LIGHT_WHITE	("\033[107m", "B.White"		),
	/** TEXT SPECIAL FORMAT */
	_TEXT_$BOLD			("\033[1m"	, "Bold"		),
	_TEXT_$UNDERLINED	("\033[4m"	, "Underlined"	),
	_TEXT_$REVERSED		("\033[7m"	, "Reversed"	);

/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||  ATTRIBUTES DEFINITION  |||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
	private final String code;
	private final String name;

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||  CONSTRUCTORS DEFINITION  ||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
	TextColor(String code, String name) {
		this.code = code;
		this.name = name;
	}

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||||  GETTERS AND SETTERS  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
	public String getCode() {return this.code;}
	
	public String getName() {return this.name;}

/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||||  METHODS DEFINITION  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/

/*
    +                             ===============================                             +    
                                        CHECKING VALIDATION                                   
    +                             ===============================                             +    
 */

	/** 
	 * Checks if a given 'codeList' can be interpreted as COLORS.RESET, see below for the authorized format for a COLORS.RESET equivalent:
	 * Whether 'codeList' is null, a blank (empty or contains only white spaces) string or is the equivalent to COLORS.reset()
	 * @param 	codeList	A string listing all COLORS name and/or code you'd like to add into the format string (separated by ',')
	 * @return 	(boolean)	Returns true if 'codeList' is indeed one of the above equivalent of COLORS.reset(), false otherwise
	 * @author	Luna
	 * @version	1.0.0
	 */
	private static boolean isReset(String codeList) {
		return	codeList == null ||
				codeList.isBlank() ||
				codeList.equalsIgnoreCase(TextColor.reset())
		;
	}

/*
    +                             ===============================                             +    
                                      LOOKUP / FIND UTILITIES                                 
    +                             ===============================                             +    
 */

	/** 
	 * This static method allows to find a COLORS based on its 'code' (getCode()).
	 * @param 	code		A string representing the code of one and only one COLORS
	 * @return 	(COLORS)	The COLORS found, if not returns COLORS.EMPTY
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static TextColor findColorByCode(String code) {
		for (TextColor c : TextColor.values()) {
			if (code.trim().equalsIgnoreCase(c.code.trim())) return c; 
		}
		return TextColor._EMPTY;
	}

	/** 
	 * This static method allows to find a COLORS based on its 'name' (getName()).
	 * @param 	name		A string representing the name of one and only one COLORS
	 * @return 	(COLORS)	The COLORS found, if not returns COLORS.EMPTY
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static TextColor findColorByName(String name) {
		for (TextColor c : TextColor.values()) {
			if (name.trim().equalsIgnoreCase(c.name)) return c; 
		}
		return TextColor._EMPTY;
	}

	/** 
	 * This static method allows to find a COLORS based on its 'code' (findColorByCode()) or 'name' (findColorByName()).
	 * @param 	codeOrName	A string representing the name or the code of one and only one COLORS
	 * @return 	(COLORS)	The COLORS found, if not returns COLORS.EMPTY
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static TextColor findColorBy(String codeOrName) {
		codeOrName = codeOrName.trim();
		if (codeOrName.startsWith("[")) return findColorByCode(codeOrName);
		else return findColorByName(codeOrName);
	}

/*
    +                             ===============================                             +    
                                          RESET HANDLING                                      
    +                             ===============================                             +    
 */

	/** 
	 * This static method simply wrapped the 'str' passed in parameter between two COLORS.RESET code.
	 * It does not modify the 'str' string if it has already been formatted before.
	 * @param 	str 		The string that needs to be wrapped by COLORS.RESET
	 * @return 	(String)	The string wrapped by COLORS.RESET
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String resetWrapped(String str) {
		return TextColor.reset()+str+TextColor.reset();
	}

	/** 
	 * This static method simply return the COLORS.RESET.getCode() value as a string.
	 * @return 	(String)	COLORS.RESET.getCode() value
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String reset() {
		return TextColor._RESET.code;
	}

/*
    +                             ===============================                             +    
                                          FORMAT BUILDING                                     
    +                             ===============================                             +    
 */

	/** 
	 * This static method concatenate one or several COLORS code in a single string for manual formatting.
	 * @param 	codeList	A string listing all COLORS name and/or code you'd like to add into the format string (separated by ',')
	 * @return 	(String)	The format string to put wherever you want to apply the desired format
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String getFormatFrom(String codeList) {
		if (TextColor.isReset(codeList)) return TextColor.reset();
		String s="";
		String[] lst = codeList.split(",");
		for (String str : lst) {s+=findColorBy(str).code;}
		return s;
	}

	/** 
	 * This static method concatenate one or several COLORS code in a single string for manual formatting.
	 * @param 	colors		A list of COLORS passed in parameters (separated by commas ',')
	 * @return 	(String)	The format string to put wherever you want to apply the desired format
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String getFormatFrom(TextColor... colors) {
		String s = "";
		for (TextColor color : colors) {s+=color.code;}
		return s;
	}

/*
    +                             ===============================                             +    
                                       FORMATTING ON STRING                                   
    +                             ===============================                             +    
 */

	/** 
	 * This static method can apply one or several COLORS on the 'str' passed in parameter.
	 * It does not reset the text format before applying the new format, but does reset at the end of the 'str' if specified.
	 * In order to make sure your formatting will work properly, always start the 'codeList' with a comma (',') for a reset at the beginning
	 * @param 	str 		The string that needs to be formatted
	 * @param 	reset		Apply reset code at the end of the string or not
	 * @param 	codeList	A string listing all COLORS name and/or code you'd like to use for formatting the string (separated by ',')
	 * @return 	(String)	The string wrapped by formatting codes (ex: desiredColor"str"resetFormatCode)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String format(String str, boolean reset, String codeList) {
		return "%s%s%s".formatted(getFormatFrom(codeList), str, reset?TextColor.reset():"");
	}

	/** 
	 * This static method can apply one or several COLORS on the 'str' passed in parameter.
	 * It does not reset the text format before applying the new format, but does reset at the end of the 'str' if specified.
	 * In order to make sure your formatting will work properly, always start the 'codeList' with a comma (',') for a reset at the beginning
	 * @param 	str 		The string that needs to be formatted
	 * @param 	reset		Apply reset code at the end of the string or not
	 * @param 	colors		A list of COLORS passed in parameters (separated by commas ',')
	 * @return 	(String)	The string wrapped by formatting codes (ex: desiredColor"str"resetFormatCode)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String format(String str, boolean reset, TextColor... colors) {
		return "%s%s%s".formatted(getFormatFrom(colors), str, reset?TextColor.reset():"");
	}

	/** 
	 * This static method can apply one or several COLORS on the 'str' passed in parameter.
	 * It does not reset the text format before applying the new format, but does reset at the end of the 'str'.
	 * In order to make sure your formatting will work properly, always start the 'codeList' with a comma (',') for a reset at the beginning
	 * @param 	str 		The string that needs to be formatted
	 * @param 	codeList	A string listing all COLORS name and/or code you'd like to use for formatting the string (separated by ',')
	 * @return 	(String)	The string wrapped by formatting codes (ex: desiredColor"str"resetFormatCode)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String format(String str, String codeList) {
		return format(str,true,codeList);
	}

	/** 
	 * This static method can apply one or several COLORS on the 'str' passed in parameter.
	 * It does not reset the text format before applying the new format, but does reset at the end of the 'str'.
	 * In order to make sure your formatting will work properly, always start the 'codeList' with a comma (',') for a reset at the beginning
	 * @param 	str 		The string that needs to be formatted
	 * @param 	colors		A list of COLORS passed in parameters (separated by commas ',')
	 * @return 	(String) 	The string wrapped by formatting codes (ex: desiredColor"str"resetFormatCode)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String format(String str, TextColor... colors) {
		return format(str,true,colors);
	}
	
	/** 
	 * This method apply the COLORS Object (this) on the 'str' passed in parameter.
	 * It does not reset the text format before applying the new format, but does reset at the end of the 'str' if specified.
	 * @param 	str			The string that needs to be formatted
	 * @param 	reset		Apply reset code at the end of the string or not
	 * @return 	(String)	The string wrapped by formatting codes (ex: desiredColor"str"[resetFormatCode])
	 * @author	Luna
	 * @version	1.0.0
	 */
	public String applyOn(String str, boolean reset) {
		return format(str, reset, this.code);
	}
	
	/** 
	 * This method apply the COLORS Object (this) on the 'str' passed in parameter.
	 * It does not reset the text format before applying the new format, but does reset at the end of the 'str'.
	 * @param 	str 		The string that needs to be formatted
	 * @return 	(String)	The string wrapped by formatting codes (ex: desiredColor"str"resetFormatCode)
	 * @author	Luna
	 * @version	1.0.0
	 */
	public String applyOn(String str) {
		return this.applyOn(str,true);
	}

/*
    +                             ===============================                             +    
                                         REMOVE FORMATTING                                    
    +                             ===============================                             +    
 */

	/** 
	 * This static method can remove a COLORS ('color') code from an already formatted string ('str') if present.
	 * Removes every occurrence from the given string.
	 * @param 	str 		The string that needs to be modified
	 * @param 	color		The COLORS that needs to be removed from 'str'
	 * @return 	(String)	The string without the COLORS code
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String remove(String str, TextColor color) {
		return str.replace(color.code, "");
	}

	/** 
	 * This method can remove the COLORS (this) code from an already formatted string ('str') if present.
	 * Removes every occurrence from the given string.
	 * @param 	str 		The string that needs to be modified
	 * @return 	(String)	The string without the COLORS code
	 * @author	Luna
	 * @version	1.0.0
	 */
	public String removeFrom(String str) {
		return remove(str, this);
	}

	/** 
	 * This static method can remove one or several COLORS ('codeList') code from an already formatted string ('str') if present.
	 * Removes every occurrence from the given string.
	 * If 'codeList' = "*", removes absolutely every formatting COLORS code from the given 'str'.
	 * @param 	str 		The string that needs to be modified
	 * @param 	codeList	A string listing all COLORS name and/or code you'd like to remove from formatted string (separated by ',')
	 * @return 	(String)	The string without the COLORS codes
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String removeEach(String str, String codeList) {
		if (TextColor.isReset(codeList)) codeList = TextColor.reset();
		if (codeList.equals("*")) {
			for (TextColor color : TextColor.values()) {str = color.removeFrom(str);}
		} else {
			for (String color : codeList.split(",")) {str = findColorBy(color).removeFrom(str);}
		}
		return str;
	}

	/** 
	 * This static method can remove one or several COLORS ('colors') code from an already formatted string ('str') if present.
	 * Removes every occurrence from the given string.
	 * @param 	str 		The string that needs to be modified
	 * @param 	colors		A list of COLORS passed in parameters (separated by commas ',')
	 * @return 	(String)	The string without the COLORS codes
	 * @author	Luna
	 * @version	1.0.0
	 */
	public static String removeEach(String str, TextColor... colors) {
		for (TextColor color : colors) {str = color.removeFrom(str);}
		return str;
	}

/*
    +                             ===============================                             +    
                                       DISPLAY / TOSTRING()                                   
    +                             ===============================                             +    
 */

}
