package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 */
public class Solitaire {

	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;

	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}

		// shuffle the cards
		Random randgen = new Random();
		for (int i = 0; i < cardValues.length; i++) {
			int other = randgen.nextInt(28);
			int temp = cardValues[i];
			cardValues[i] = cardValues[other];
			cardValues[other] = temp;
		}

		// CREATION PART    
		// create a circular linked list from this deck and make deckRear point to its last node
		CardNode cn = new CardNode();
		cn.cardValue = cardValues[0];
		cn.next = cn;
		deckRear = cn;
		for (int i=1; i < cardValues.length; i++) {
			cn = new CardNode();
			cn.cardValue = cardValues[i];
			cn.next = deckRear.next;
			deckRear.next = cn;
			deckRear = cn;
		}
	}

	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
			throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
			cn.cardValue = scanner.nextInt();
			cn.next = cn;
			deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
			cn.cardValue = scanner.nextInt();
			cn.next = deckRear.next;
			deckRear.next = cn;
			deckRear = cn;
		}
	}

	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		if (deckRear == null){
			return;
		}
		CardNode node = deckRear;
		while (node.cardValue != 27){
			node = node.next;
		}	
		int temp = node.cardValue;
		node.cardValue = node.next.cardValue;	
		node.next.cardValue = temp;
		//checking to see if deck is right
		//		node = deckRear;
		//		for(int i = 0; i < 28; i++){
		//			node = node.next;
		//			System.out.print(node.cardValue + " ");
		//		}
		return;
	}

	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		if (deckRear == null){
			return;
		}
		CardNode node = deckRear;
		while(node.cardValue != 28){
			node = node.next;
		}
		for (int i = 0; i < 2 ;i++){
			int temp = node.cardValue;
			node.cardValue = node.next.cardValue;
			node.next.cardValue = temp;
			node = node.next;
		}
		//checking to see if deck is right
		//	    node = deckRear;
		//	    for(int i = 0; i < 28; i++){
		//			node = node.next;
		//			System.out.print(node.cardValue + " ");
		//		}
		return;
	}

	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		if(deckRear == null){
			return;
		}
		//case where no cards before first joker
		CardNode end = deckRear;
		CardNode front = end.next;
		if (front.cardValue == 27 || front.cardValue == 28){
			CardNode findjoker2 = front.next;
			while(findjoker2.cardValue != 27 && findjoker2.cardValue != 28){
				findjoker2 = findjoker2.next;
			}
			deckRear = findjoker2;
			return;
		}
		//case where no cards after second joker
		CardNode tail = deckRear;
		if(tail.cardValue == 27 || tail.cardValue == 28){
			CardNode findJoker1 = tail.next;
			CardNode previous = null;
			while(findJoker1.cardValue != 27 && findJoker1.cardValue != 28){
				previous = findJoker1;
				findJoker1 = findJoker1.next;
			}
			deckRear = previous;
			return;
		}

		CardNode node = deckRear.next;
		CardNode prevnode = null;
		while(node.cardValue != 27 && node.cardValue != 28){
			prevnode = node;
			node = node.next;
		}
		CardNode tail1 = prevnode;
		CardNode firstJoker = node;
		node = node.next;
		while(node.cardValue != 27 && node.cardValue != 28){
			node = node.next;
		}
		CardNode top2 = node.next;

		node.next = deckRear.next;
		deckRear.next = firstJoker;
		deckRear = tail1;
		tail1.next = top2;
		//checking to see if deck is right
		//		node = deckRear;
		//	    for(int i = 0; i < 28; i++){
		//			node = node.next;
		//			System.out.print(node.cardValue + " ");
		//		}
		return;
	}

	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		//finds second to last node and stores it in prev
		CardNode nextToLast = deckRear.next;
		while(nextToLast.next != deckRear){
			nextToLast = nextToLast.next;
		}
		//counts down that many from first card
		int i = deckRear.cardValue;
		if(i == 28){
			i = 27;
		}
		CardNode node = deckRear;
		for (int j = 0; j < i ; j++){
			node = node.next;
		}
		nextToLast.next = deckRear.next;
		CardNode newtop = node.next;
		node.next = deckRear;
		deckRear.next = newtop;
		//checking to see if deck is right
		//		node = deckRear;
		//	    for(int k = 0; k < 28; k++){
		//			node = node.next;
		//			System.out.print(node.cardValue + " ");
		//		}
		return;
	}

	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key, but if that value is 27 or 28, repeats the whole process.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		while(true){
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			int i = deckRear.next.cardValue;
			if(i == 28){
				i = 27;
			}
			CardNode node = deckRear;
			for(int j = 0; j < i; j++){
				node = node.next;
			}
			node = node.next;
			if(node.cardValue != 27 && node.cardValue != 28){
				return node.cardValue;
			}
		}
	}

	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {
		String message2 = message.toUpperCase();
		String encrypted = "";
		int length = message2.length();
		for (int i = 0; i < length; i++){
			char ch = message2.charAt(i);
			if(Character.isLetter(ch) == true){
				int key = getKey();
				int letter = ch - 'A' + 1;
				int sum = letter + key;
				if (sum > 26){
					sum = sum - 26;
				}
				char c = (char)(sum + 'A' - 1);
				encrypted = encrypted + c;
			}
		}
		return encrypted;
	}

	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		String message2 = message.toUpperCase();
		String dencrypted = "";
		int length = message2.length();
		for (int i = 0; i < length; i++){
			char ch = message2.charAt(i);
			if(Character.isLetter(ch) == true){
				int key = getKey();
				int letter = ch - 'A' + 1;
				if (letter <= key){
					letter = letter + 26;
				}
				int sum = letter - key;
				char c = (char)(sum + 'A' - 1);
				dencrypted = dencrypted + c;
			}
		}
		return dencrypted;
	}
}
