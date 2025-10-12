package com.utils.text;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code TextAlign} enum provides utilities for formatting and justifying text
 * into fixed-width columns. It supports the four common alignment modes:
 * <ul>
 *   <li>{@link #LEFT}   – Aligns text flush left, padding with spaces on the right.</li>
 *   <li>{@link #RIGHT}  – Aligns text flush right, padding with spaces on the left.</li>
 *   <li>{@link #CENTER} – Centers text, balancing spaces on both sides.</li>
 *   <li>{@link #FULL}   – Fully justifies text, distributing spaces between words.</li>
 * </ul>
 *
 * <p>Each mode ensures that every line of text is formatted to fit within a
 * given {@code maxWidth}. Paragraphs separated by line breaks are preserved,
 * and each line is independently justified.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * String paragraph = "This is an example of text justification.";
 * System.out.println(TextAlign.LEFT.justify(paragraph, 30));
 * System.out.println(TextAlign.RIGHT.justify(paragraph, 30));
 * System.out.println(TextAlign.CENTER.justify(paragraph, 30));
 * System.out.println(TextAlign.FULL.justify(paragraph, 30));
 * }</pre>
 *
 * <p>The {@code FULL} justification mode is adapted from an implementation
 * shared on StackOverflow by Shahroz Saleem
 * (<a href="https://stackoverflow.com/a/51118373">source</a>).</p>
 *
 * @author  Luna
 * @version 1.0.0
 */
public enum TextAlign {

	/** Align text flush left, padding with spaces on the right. */
	LEFT,
	
	/** Align text flush right, padding with spaces on the left. */
	RIGHT,
	
	/** Align text centered between left and right padding. */
	CENTER,
	
	/** Fully justify text, distributing spaces between words. */
	FULL;

	/**
	 * Justify a block of text according to the current alignment mode.
	 *
	 * @param str       the input string (may contain multiple paragraphs separated by {@code \n})
	 * @param maxWidth  the maximum width of each line
	 * @return the justified text as a single string with preserved line breaks
	 */
	public String justify(String str, int maxWidth) {
		switch(this) {
		case LEFT	: return toLeft(str,maxWidth);
		case RIGHT	: return toRight(str,maxWidth);
		case CENTER	: return toCenter(str,maxWidth);
		case FULL	: return toFull(str,maxWidth);
		}
		return str;
	}

	/**
	 * Aligns text flush left across multiple paragraphs.
	 *
	 * @param str    the input text
	 * @param limit  the maximum width of each line
	 * @return left-aligned text
	 */
	public static String toLeft(String str, int limit) {
		StringBuilder text = new StringBuilder();
		String[] paragraph = str.split("\n", -1);
		for (String currParagraph : paragraph) {
			text.append("\n");
			text.append(String.join("\n",leftJustify(currParagraph.split("\\h"),limit)));
		}
		return text.toString().substring(1);
	}

	/**
	 * Aligns text flush right across multiple paragraphs.
	 *
	 * @param str    the input text
	 * @param limit  the maximum width of each line
	 * @return right-aligned text
	 */
	public static String toRight(String str, int limit) {
		StringBuilder text = new StringBuilder();
		String[] paragraph = str.split("\n", -1);
		for (String currParagraph : paragraph) {
			text.append("\n");
			text.append(String.join("\n",rightJustify(currParagraph.split("\\h"),limit)));
		}
		return text.toString().substring(1);
	}

	/**
	 * Centers text across multiple paragraphs.
	 *
	 * @param str    the input text
	 * @param limit  the maximum width of each line
	 * @return centered text
	 */
	public static String toCenter(String str, int limit) {
		StringBuilder text = new StringBuilder();
		String[] paragraph = str.split("\n", -1);
		for (String currParagraph : paragraph) {
			text.append("\n");
			text.append(String.join("\n",centerJustify(currParagraph.split("\\h"),limit)));
		}
		return text.toString().substring(1);
	}

	/**
	 * Fully justifies text across multiple paragraphs, distributing spaces
	 * evenly between words so that each line (except the last) reaches the
	 * specified {@code maxWidth}.
	 *
	 * @param str    the input text
	 * @param limit  the maximum width of each line
	 * @return fully justified text
	 */
	public static String toFull(String str, int limit) {
		StringBuilder text = new StringBuilder();
		String[] paragraph = str.split("\n", -1);
		for (String currParagraph : paragraph) {
			text.append("\n");
			text.append(String.join("\n",fullJustify(currParagraph.split("\\h"),limit)));
		}
		return text.toString().substring(1);
	}
	
	
	/* ----------------------------------------------------------------------------------------------------------------
	 * Posted on StackOverflow (30/06/2018) by Shahroz Saleem
	 * https://stackoverflow.com/questions/8524979/justify-text-in-java/51118373#51118373
	 * 
	 * Below is the sample conversion where maxWidth was 80 characters:
	 * The following paragraph contains 115 words exactly and it took 55 ms to write the converted text to external file.
	 * I've tested this code for a paragraph of about 70k+ words and it took approx 400 ms to write the converted text to
	 * a file.
	 * 
	 * Input:
	 * 
	 * These features tend to make legal writing formal. 
	 * his formality can take the form of long sentences, complex constructions, archaic and hyper-formal vocabulary, 
	 * and a focus on content to the exclusion of reader needs. Some of this formality in legal writing is necessary and 
	 * desirable, given the importance of some legal documents and the seriousness of the circumstances in which some
	 * legal documents are used. Yet not all formality in legal writing is justified. To the extent that formality
	 * produces opacity and imprecision, it is undesirable. To the extent that formality hinders reader comprehension,
	 * it is less desirable. In particular, when legal content must be conveyed to nonlawyers, formality should give
	 * way to clear communication.
	 * 
	 * Output:
	 * These  features  tend  to make legal writing formal. This formality can take the
	 * form   of  long  sentences,  complex  constructions,  archaic  and  hyper-formal
	 * vocabulary,  and  a  focus  on content to the exclusion of reader needs. Some of
	 * this formality in legal writing is necessary and desirable, given the importance
	 * of  some  legal documents and the seriousness of the circumstances in which some
	 * legal  documents  are used. Yet not all formality in legal writing is justified.
	 * To   the   extent  that  formality  produces  opacity  and  imprecision,  it  is
	 * undesirable.  To  the  extent that formality hinders reader comprehension, it is
	 * less   desirable.  In  particular,  when  legal  content  must  be  conveyed  to
	 * nonlawyers, formality should give way to clear communication.                   
	 * 
	 */
	/**
	 * Splits words into fully justified lines.
	 *
	 * @param words     array of words to format
	 * @param maxWidth  maximum width of each line
	 * @return list of justified lines
	 */
	private static List<String> fullJustify(String[] words, int maxWidth) {
	    int n = words.length;
	    List<String> justifiedText = new ArrayList<>();
	    int currLineIndex = 0;
	    int nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	    while (currLineIndex < n) {
	        StringBuilder line = new StringBuilder();
	        for (int i = currLineIndex; i < nextLineIndex; i++) {
	            line.append(words[i] + " ");
	        }
	        currLineIndex = nextLineIndex;
	        nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	        justifiedText.add(line.toString());
	    }
	    for (int i = 0; i < justifiedText.size() - 1; i++) {
	        String fullJustifiedLine = getFullJustifiedLine(justifiedText.get(i).trim(), maxWidth);
	        justifiedText.remove(i);
	        justifiedText.add(i, fullJustifiedLine);
	    }
	    String leftJustifiedLine = getLeftJustifiedLine(justifiedText.get(justifiedText.size() - 1).trim(), maxWidth);
	    justifiedText.remove(justifiedText.size() - 1);
	    justifiedText.add(leftJustifiedLine);
	    return justifiedText;
	}

	/**
	 * Determines where a line should end based on the max width.
	 *
	 * @param currLineIndex current starting index
	 * @param maxWidth      maximum width of line
	 * @param words         word array
	 * @return the next line index
	 */
	private static int getNextLineIndex(int currLineIndex, int maxWidth, String[] words) {
	    int n = words.length;
	    int width = 0;
	    while (currLineIndex < n && width < maxWidth) {
	    	width += words[currLineIndex++].length() + 1;
	    }
	    if (width > maxWidth + 1)
	        currLineIndex--;
	    return currLineIndex;
	}
	
	/**
	 * Creates a single fully justified line by distributing spaces between words.
	 *
	 * @param line     unformatted line
	 * @param maxWidth maximum width of the line
	 * @return fully justified line
	 */
	private static String getFullJustifiedLine(String line, int maxWidth) {
	    StringBuilder justifiedLine = new StringBuilder();
	    String[] words = line.split(" ");
	    int occupiedCharLength = 0;
	    for (String word : words) {
	        occupiedCharLength += word.length();
	    }
	    int remainingSpace = maxWidth - occupiedCharLength;
	    int spaceForEachWordSeparation = words.length > 1 ? remainingSpace / (words.length - 1) : remainingSpace;
	    int extraSpace = remainingSpace - spaceForEachWordSeparation * (words.length - 1);
	    for (int j = 0; j < words.length - 1; j++) {
	        justifiedLine.append(words[j]);
	        for (int i = 0; i < spaceForEachWordSeparation; i++)
	            justifiedLine.append(" ");
	        if (extraSpace > 0) {
	            justifiedLine.append(" ");
	            extraSpace--;
	        }
	    }
	    justifiedLine.append(words[words.length - 1]);
	    for (int i = 0; i < extraSpace; i++)
	        justifiedLine.append(" ");
	    return justifiedLine.toString();
	}

	/**
	 * Creates a left-justified line by padding with spaces to the right.
	 */
	private static String getLeftJustifiedLine(String line, int maxWidth) {
	    int lineWidth = line.length();
	    StringBuilder justifiedLine = new StringBuilder(line);
	    for (int i = 0; i < maxWidth - lineWidth; i++)
	        justifiedLine.append(" ");
	    return justifiedLine.toString();
	}
	/* ---------------------------------------------------------------------------------------------------------------- */

	/**
	 * Creates a right-justified line by padding with spaces to the left.
	 */
	private static String getRightJustifiedLine(String line, int maxWidth) {
		int lineWidth = line.length();
		StringBuilder justifiedLine = new StringBuilder(line);
		for (int i = 0; i < maxWidth - lineWidth; i++) {
			justifiedLine.insert(0," ");
		}
		return justifiedLine.toString();
	}

	/**
	 * Creates a center-justified line by balancing spaces on both sides.
	 */
	private static String getCenterJustifiedLine(String line, int maxWidth) {
		int lineWidth = line.length();
		StringBuilder justifiedLine = new StringBuilder(line);
		for (int i = 0; i < (maxWidth - lineWidth)/2; i++) {
			justifiedLine.insert(0," ");
			justifiedLine.append(" ");
		}
		if (justifiedLine.length()<maxWidth) {justifiedLine.append(" ");}
		return justifiedLine.toString();
	}

	/** Breaks and aligns words into left-justified lines. */
	private static List<String> leftJustify(String[] words, int maxWidth) {
	    int n = words.length;
	    List<String> justifiedText = new ArrayList<>();
	    int currLineIndex = 0;
	    int nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	    while (currLineIndex < n) {
	        StringBuilder line = new StringBuilder();
	        for (int i = currLineIndex; i < nextLineIndex; i++) {
	            line.append(words[i] + " ");
	        }
	        currLineIndex = nextLineIndex;
	        nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	        justifiedText.add(line.toString());
	    }
	    for (int i = 0; i < justifiedText.size(); i++) {
	        String leftJustifiedLine = getLeftJustifiedLine(justifiedText.get(i).trim(), maxWidth);
	        justifiedText.remove(i);
	        justifiedText.add(i, leftJustifiedLine);
	    }
	    return justifiedText;
	}

	/** Breaks and aligns words into right-justified lines. */
	private static List<String> rightJustify(String[] words, int maxWidth) {
	    int n = words.length;
	    List<String> justifiedText = new ArrayList<>();
	    int currLineIndex = 0;
	    int nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	    while (currLineIndex < n) {
	        StringBuilder line = new StringBuilder();
	        for (int i = currLineIndex; i < nextLineIndex; i++) {
	            line.append(words[i] + " ");
	        }
	        currLineIndex = nextLineIndex;
	        nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	        justifiedText.add(line.toString());
	    }
	    for (int i = 0; i < justifiedText.size(); i++) {
	        String rightJustifiedLine = getRightJustifiedLine(justifiedText.get(i).trim(), maxWidth);
	        justifiedText.remove(i);
	        justifiedText.add(i, rightJustifiedLine);
	    }
	    return justifiedText;
	}

	/** Breaks and aligns words into center-justified lines. */
	private static List<String> centerJustify(String[] words, int maxWidth) {
	    int n = words.length;
	    List<String> justifiedText = new ArrayList<>();
	    int currLineIndex = 0;
	    int nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	    while (currLineIndex < n) {
	        StringBuilder line = new StringBuilder();
	        for (int i = currLineIndex; i < nextLineIndex; i++) {
	            line.append(words[i] + " ");
	        }
	        currLineIndex = nextLineIndex;
	        nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
	        justifiedText.add(line.toString());
	    }
	    for (int i = 0; i < justifiedText.size(); i++) {
	        String centerJustifiedLine = getCenterJustifiedLine(justifiedText.get(i).trim(), maxWidth);
	        justifiedText.remove(i);
	        justifiedText.add(i, centerJustifiedLine);
	    }
	    return justifiedText;
	}

}
