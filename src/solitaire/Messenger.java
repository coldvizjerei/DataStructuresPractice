package solitaire;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Messenger {
	public static void main(String[] args) throws IOException {
		
		Solitaire ss = new Solitaire();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("You can press return to quit anytime during the program");
		System.out.print("Enter deck file name => ");
		
		String deck = br.readLine();
		if (deck.length() == 0){
			System.exit(0);
		}
		
		//still needs error check for deck potentially not existing
		Scanner sc = new Scanner(new File(deck));		
		ss.makeDeck(sc);
		
		System.out.print("Encrypt or decrypt? (e/d), press return to quit => ");
		String inp = br.readLine();
		while( (!(inp.charAt(0) == 'e')) && (!(inp.charAt(0) == 'd')) ){
			if (inp.length() == 0) {
				System.exit(0);
			}
			System.out.print("Please enter either 'e' or 'd' => ");
			inp = br.readLine();
		}
				
		char ed = inp.charAt(0);
		System.out.print("Enter message => ");
		String message = br.readLine();
		if (ed == 'e') {
			System.out.println("Encrypted message: " + ss.encrypt(message));
		} else {
			System.out.println("Decrypted message: " + ss.decrypt(message));
		}
	}
}