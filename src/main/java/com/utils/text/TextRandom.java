package com.utils.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TextRandom {
	
	private int strLength = 0;
	private List<Integer> charList = new ArrayList<>();
	
	public TextRandom() {}
	
	public TextRandom(int strLength, List<Integer> charList) {
		setStrLength(strLength);
		setCharList(charList);
	}
	
	public TextRandom(int strLength, AsciiTable... tables) {
		setStrLength(strLength);
		setCharList(tables);
	}
	
	public int getStrLength() {return this.strLength;}
	public void setStrLength(int value) {
		this.strLength = Math.abs(value);
	}
	
	public List<Integer> getCharList() {return this.charList;}
	public void setCharList(List<Integer> value) {
		this.charList = AsciiTable.getAsciiRange().containsAll(value)?value:AsciiTable.ASCII.copyIntList();
	}
	public void setCharList(AsciiTable... tables) {
		this.charList = AsciiTable.merge(tables);
	}
	
	public static String generate(int strLength, List<Integer> charList) {
		String str = "";
		Random random = new Random();
		for (int i=0; i<strLength; i++) {
			int index = random.nextInt(charList.size());
			str+= (char) charList.get(index).intValue();
		}
		return str;
	}
	
	public static String generate(int strLength, AsciiTable... tables) {
		return generate(strLength, AsciiTable.merge(tables));
	}
	
	public String generate() {
		return generate(this.strLength, this.charList);
	}

}
