package Control;

import java.util.Scanner;

public class MasterControl {
	public static void main(String[] args){Scanner scstdin = new Scanner(System.in);
		while(true){
			//options here
			System.out.println("\n1) Solitaire Encryption");
			System.out.println("2) Expression Evaluator");
			System.out.println("3) Quit");

			System.out.println("\nChoose an option, or hit return to quit => ");
			String line = scstdin.nextLine();
			if (line.length() == 0){
				return;
			}

			int intLine = Integer.parseInt(line);
			
			switch(intLine) {
			case 1:
				
				break;
				
			case 2:
				
				break;
				
			case 3:
			
				break;
			}
		}
	}
}