package com.utils.input;

import java.util.Scanner;

import com.utils.text.TextColor;
import com.utils.text.TextHandler;

public class InputParser {
	
	public static String inputFor(String msg, String regEx, Scanner sc) {
		boolean match = !(regEx==null);
		String input=null;
		while (true) {
			System.out.print(msg);
			input = sc.nextLine();
			if (!match||TextHandler.strMatches(input, regEx)) {break;}
			else {
				System.out.println(
					TextColor.reset()+
					TextColor._FG_$RED.applyOn("\nIncorrect input (RegEx: \"")+
					TextColor._TEXT_$BOLD.applyOn(regEx)+
					TextColor._FG_$RED.applyOn("\"), please try again...")
				);
			}
		}
		return input;
	}
	
	public static int inputForInt(String msg, Scanner sc) {
		String input = inputFor(msg, "^\\d*$", sc);
		return input.isBlank()?0:Integer.parseInt(input);
	}
	
	public static double inputForDouble(String msg, Scanner sc) {
		String input = inputFor(msg, "^\\d*\\.?\\d*$", sc);
		return input.isBlank()?0:Double.parseDouble(input);
	}

}
