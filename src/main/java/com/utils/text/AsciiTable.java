package com.utils.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.utils.Range;

/**
 * The {@code AsciiTable} enum provides predefined groups of ASCII codes and characters,
 * covering the standard ASCII table (0–127), the extended ASCII table (0–255), and
 * useful subsets such as digits, letters, symbols, punctuation, control characters,
 * and more.
 *
 * <p>Each enum constant encapsulates a sorted list of integer code points and their
 * corresponding character representations. It also provides methods for set operations
 * (merge, remove, intersection), range queries, and string formatting.</p>
 *
 * <p><strong>Examples:</strong></p>
 * <pre>{@code
 * // Get uppercase ASCII codes
 * List<Integer> uppercaseCodes = AsciiTable.UPPERCASE.getIntList();
 *
 * // Convert to characters
 * List<Character> uppercaseChars = AsciiTable.UPPERCASE.getCharList();
 *
 * // Check membership
 * boolean isDigit = AsciiTable.DIGITS.contains('5'); // true
 *
 * // Merge tables
 * List<Integer> lettersAndDigits = AsciiTable.merge(AsciiTable.UPPERCASE, AsciiTable.LOWERCASE, AsciiTable.DIGITS);
 *
 * // String representation
 * System.out.println(AsciiTable.DIGITS.toString(true));
 * }</pre>
 *
 * @author Luna
 * @version 1.1.0
 */
public enum AsciiTable {
	
/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||||  ENUMERATION VALUES  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
    // The whole Standard ASCII Table (0-127), including control characters and printable characters.
    ASCII				(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127),
    
    // The whole Extended ASCII Table (0-255), including non-printable characters from 128 to 255.
    ASCII_EXTENDED		(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255),
    
    // Control characters (0-31) and DEL (127), typically used for non-printing purposes like line breaks, tabs, etc.
    CONTROL_CHARACTERS	(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,127,160),
    
    // Digits (48-57), representing '0' to '9'.
    DIGITS				(48,49,50,51,52,53,54,55,56,57),
    
    // Uppercase letters (65-90), representing 'A' to 'Z'.
    UPPERCASE			(65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90),
    
    // Lowercase letters (97-122), representing 'a' to 'z'.
    LOWERCASE			(97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122),
    
    // Combination of uppercase and lowercase letters.
    CHARACTERS			(65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122),
    
    // Paired symbols like brackets and parentheses: '()', '{}', '[]', and more.
    PAIRS				(40,41,60,62,91,93,123,125,171,187,47,92),
    
    // Separators like space, commas, periods, and common delimiters.
    SEPARATORS			(32,44,45,46,47,58,59,64,95,124),
    
    // Math operators such as +, -, *, /, <, =, and others.
    MATHS_OPERATORS		(42,43,45,47,60,61,62,177,178,179,185,215,247),
    
    // Currency symbols like $, €, ¥, £, etc.
    MONEY				(36,128,163,162,165),
    
    // Latin characters with diacritics (accents, etc.) used in many European languages.
    LATIN_CHARACTERS	(131,138,140,142,154,156,158,159,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,248,249,250,251,252,253,254,255),
    
    // Non-printable characters, including control characters and other non-visual codes.
    NOT_PRINTABLE		(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160),
    
    // Punctuation marks: '!', '"', '#', '$', etc.
    PUNCTUATION			(32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47),
    
    // Various symbols like ':', ';', '<', '=', '>', '@', '[', '\', and others.
    SYMBOLS				(58,59,60,61,62,63,64,91,92,93,94,95,96),
    
    // Miscellaneous symbols and other special characters (123-159).
    MISCELLANEOUS		(123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159),
    
    // Diacritical marks and characters with accents (160-191).
    DIACRITICAL			(160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191),
    
    // Unused ASCII code in the Extended ASCII Table
    UNUSED				(141,143,144,157);

/* 
    +-----------------------------------------------------------------------------------------+    
    |||||||||||||||||||||||||||||||||  ATTRIBUTES DEFINITION  |||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
	private final List<Integer> intList = new ArrayList<>();
	private final List<Character> charList;
	private final Range<Integer> range;
	private static boolean printOnEachLine = false;
	private static final Range<Integer> asciiRange = new Range<>(0,255);

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||  CONSTRUCTORS DEFINITION  ||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
    /**
     * Constructs an {@code AsciiTable} constant from a list of integer codes.
     * Duplicates are removed and the list is sorted.
     *
     * @param codeList the ASCII codes represented by this table
     */
	AsciiTable(int... codeList) {
		for (int code : codeList) {
			if(!intList.contains(code)) intList.add(code);
		}
		Collections.sort(intList);
		this.range = new Range<Integer>(intList.getFirst(),intList.getLast());
		this.charList = toCharList();
	}

/* 
    +-----------------------------------------------------------------------------------------+    
    ||||||||||||||||||||||||||||||||||  GETTERS AND SETTERS  ||||||||||||||||||||||||||||||||||    
    +-----------------------------------------------------------------------------------------+    
*/
	
    /**
     * Defines whether {@code toString()} outputs one entry per line or inline.
     *
     * @param value {@code true} for one entry per line, {@code false} for inline
     */
	public static void setToStringOnEachLine(boolean value) {
		printOnEachLine = value;
	}

    /** @return the full ASCII range (0–255) */
	public static Range<Integer> getAsciiRange() {return asciiRange;}

    /** @return an unmodifiable view of the integer ASCII codes of this table */
	public List<Integer> getIntList() {return Collections.unmodifiableList(this.intList);}

    /** @return an unmodifiable view of the character representations of this table */
	public List<Character> getCharList() {return Collections.unmodifiableList(this.charList);}

    /** @return a mutable copy of the integer ASCII codes of this table */
	public List<Integer> copyIntList() {return new ArrayList<>(this.intList);}

    /** @return a mutable copy of the character representations of this table */
	public List<Character> copyCharList() {return new ArrayList<>(this.charList);}

    /** @return the range covered by this table (min and max ASCII code) */
	public Range<Integer> getRange() {return this.range;}

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

    /** @return the number of ASCII codes in this table */
	public int size() {
		return this.intList.size();
	}

    /** @param i ASCII code to check @return true if the table contains it */
	public boolean contains(int i) {
		return this.intList.contains(i);
	}

    /** @param c character to check @return true if the table contains it */
	public boolean contains(char c) {
		return this.charList.contains(c);
	}

    /** @param ii ASCII codes @return true if all codes are in the table */
	public boolean containsAll(int... ii) {
		boolean result = true;
		for (int i : ii) {
			if (!this.contains(i)) {result = false; break;}
		}
		return result;
	}

    /** @param cc characters @return true if all characters are in the table */
	public boolean containsAll(char... cc) {
		boolean result = true;
		for (char c : cc) {
			if (!this.contains(c)) {result = false; break;}
		}
		return result;
	}

/*
    +                             ===============================                             +    
                                            CONVERSION                                        
    +                             ===============================                             +    
 */
	
	/** @return	the equivalent of {@code intList} but filled with {@code Character} */
	private List<Character> toCharList() {return copyIntList().stream().map(c -> (char) c.intValue()).toList();}

/*
    +                             ===============================                             +    
                                          SET OPERATIONS                                      
    +                             ===============================                             +    
 */
	
    /**
     * Removes the characters defined in other tables from a base table.
     *
     * @param fromBase the base table
     * @param tables   the tables whose characters will be removed
     * @return a list of ASCII codes after removal
     */
	public static List<Integer> removeFrom(AsciiTable fromBase, AsciiTable... tables) {
		List<Integer> result = fromBase.copyIntList();
		for (AsciiTable table : tables) {result.removeAll(table.getIntList());}
		return result;
	}

    /**
     * Removes the characters of the given tables from this table.
     *
     * @param tables tables whose characters will be removed
     * @return a list of ASCII codes after removal
     */
	public List<Integer> remove(AsciiTable... tables) {
		return removeFrom(this, tables);
	}

    /**
     * Merges multiple tables into a single sorted list of ASCII codes.
     * Duplicates are removed.
     *
     * @param tables the tables to merge
     * @return a sorted list of merged ASCII codes
     */
	public static List<Integer> merge(AsciiTable... tables) {
		List<Integer> result = new ArrayList<>();
		for (AsciiTable table : tables) {
			for (int i : table.getIntList()) {
				if (!result.contains(i)) result.add(i);
			}
		}
		Collections.sort(result);
		return result;
	}

    /**
     * Computes the intersection of this table with another.
     *
     * @param that another ASCII table
     * @return a list of common ASCII codes between the two tables
     */
	public List<Integer> intersect(AsciiTable that) {
		List<Integer> result = new ArrayList<>();
		List<Integer> lst = (this.size() > that.size()) ? that.getIntList() : this.getIntList();
		for (int i : lst) {
			if (lst.size()==this.size()) {
				if (that.contains(i)) result.add(i);
			} else if (this.contains(i)) result.add(i);
		}
		return result;
	}

/*
    +                             ===============================                             +    
                                       DISPLAY / TOSTRING()                                   
    +                             ===============================                             +    
 */

    /**
     * Converts a list of ASCII codes into a formatted string representation.
     *
     * @param table the list of ASCII codes
     * @param onEachLine whether to print entries line by line or inline
     * @return formatted string
     * @throws ArrayIndexOutOfBoundsException if any code is outside the ASCII range (0–255)
     */
	public static String toString(List<Integer> table, boolean onEachLine) throws ArrayIndexOutOfBoundsException {
		if (!asciiRange.containsAll(table)) throw new ArrayIndexOutOfBoundsException();
		String str = "";
		String sep = onEachLine ? "\n" : ", ";
		String format = sep+"%d: %s";
		for (int code : table) {
			str+= format.formatted(code, String.valueOf((char) code));
		}
		return (onEachLine?"%s":"[%s]").formatted(str.substring(sep.length()));
	}

    /** Same as {@link #toString(List, boolean)} but for an {@code AsciiTable} */
	public static String toString(AsciiTable table, boolean onEachLine) {
		return toString(table.getIntList(), onEachLine);
	}

    /** Uses the global formatting setting {@link #setToStringOnEachLine(boolean)} */
	public static String toString(List<Integer> table) throws ArrayIndexOutOfBoundsException {
		return toString(table, printOnEachLine);
	}

    /** Same as {@link #toString(List)} but for an {@code AsciiTable} */
	public static String toString(AsciiTable table) {
		return toString(table.getIntList());
	}

    /** Instance variant of {@link #toString(List, boolean)} */
	public String toString(boolean onEachLine) {
		return toString(this.getIntList(), onEachLine);
	}

    /** Default string representation using global formatting rules */
	@Override
	public String toString() {
		return toString(this.getIntList());
	}

}
