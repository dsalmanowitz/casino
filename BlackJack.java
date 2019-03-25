import java.util.ArrayList;
import java.util.Scanner;

public class BlackJack {
	Scanner in = new Scanner(System.in);
	private int chips;
	private int original;
	private Deck deck;
	private ArrayList<Card> hand;
	private ArrayList<Card> dhand;
	private boolean stand;
	private int bet;
	private boolean split;
	private boolean doubledown;
	private int scount;
	private Card s;
	
	private static final String[] RANKS =
		{"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
	private static final String[] SUITS =
		{"spades", "hearts", "diamonds", "clubs"};
	private static final int[] POINT_VALUES =
		{11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
	
	public void start() {
		deck = new Deck(RANKS, SUITS, POINT_VALUES);
		bet = 0;
		System.out.println("How much money do you have?");
		String response = in.nextLine();
		boolean ok = true;
		if (response.equals("")) {
			ok = false;
		}
		for (int i = 0; i < response.length(); i++) {
			if(!Character.isDigit(response.charAt(i))) {
				ok = false;
			}
		}
		if (ok && Integer.parseInt(response) < 1) {
			ok = false;
		}
		while (!ok) {
			System.out.println("Invalid response. How much money do you have?");
			response = in.nextLine();
			ok = true;
			if (response.equals("")) {
				ok = false;
			}
			for (int i = 0; i < response.length(); i++) {
				if(!Character.isDigit(response.charAt(i))) {
					ok = false;
				}
			}
			if (ok && Integer.parseInt(response) < 1) {
				ok = false;
			}
		}
		chips = Integer.parseInt(response);
		newGame();
	}
	
	public void newGame() {
		scount = 0;
		split = false;
		original = chips;
		deck.shuffle();
		hand = new ArrayList<Card>(0);
		hand.add(deck.deal());
		hand.add(deck.deal());
		bet();
		dhand = new ArrayList<Card>(0);
		dhand.add(deck.deal());
		dhand.add(deck.deal());
		stand = false;
		play();
	} 
	
	public void bet() {
		System.out.println("How much would you like to bet?");
		String response = in.nextLine();
		boolean ok = true;
		if (response.equals("")) {
			ok = false;
		}
		for (int i = 0; i < response.length(); i++) {
			if(!Character.isDigit(response.charAt(i))) {
				ok = false;
			}
		}
		if (ok && Integer.parseInt(response) < 1) {
			ok = false;
		}
		while (!ok) {
			System.out.println("Invalid response. How much would you like to bet?");
			response = in.nextLine();
			ok = true;
			if (response.equals("")) {
				ok = false;
			}
			for (int i = 0; i < response.length(); i++) {
				if(!Character.isDigit(response.charAt(i))) {
					ok = false;
				}
			}
			if (ok && Integer.parseInt(response) < 1) {
				ok = false;
			}
		}
		if (Integer.parseInt(response) > chips) {
			System.out.println("Cannot bet more than you have.");
			bet();
		}
		bet = Integer.parseInt(response);
	}
	
	public void print(ArrayList<Card> list) {
		System.out.print("Current Hand: ");
		ArrayList<Card> sorted = sort(hand);
		for (Card c : sorted) {
			System.out.print(c.rank() + " ");
		}
		System.out.println("\nValue of Hand: " + value(sorted));
	}
	
	public void dprint() {
		if (value(dhand) == 21 && (dhand.get(0).pointValue() == 10 || dhand.get(0).pointValue() == 11)) {
			stand = true;
			isWin();
		} else {
			System.out.println("\nDealer's Hand: " + dhand.get(0).rank() + " _");
		}
	}
	
	public ArrayList<Card> sort(ArrayList<Card> list) {
		boolean sorted = false;
		Card temp;
		while (!sorted) {
			sorted = true;
			for (int i = 1; i < list.size(); i++) {
				if(list.get(i).compareTo(list.get(i-1)) == -1) {
					temp = list.get(i);
					list.set(i, list.get(i-1));
					list.set(i-1, temp);
					sorted = false;
				}
			}
		}
		return list;
	}
	
	public int value(ArrayList<Card> list) {
		int value = 0;
		ArrayList<Card> sorted = sort(list);
		for (int i = 0; i < sorted.size(); i++) {
			value += sorted.get(i).pointValue();
			if (value > 21 && sorted.get(i).rank() == "A") {
				value -= 10;
			}
		}
		return value;
	}
	
	public void dealer() {
		if (split && stand) {
			dhand = new ArrayList<Card>(0);
			dhand.add(deck.deal());
			dhand.add(deck.deal());
		}
		stand = true;
		while (value(dhand) < 17) {
			dhand.add(deck.deal());
		}
		print(dhand);
	}
	
	public void isWin() {
		if (stand) {
			if (dhand.size() == 2 && value(dhand) == 21) {
				print(hand);
			}
			System.out.print("Dealer's Hand: ");
			for (Card c : dhand) {
				System.out.print(c.rank() + " ");
			}
			System.out.println("\nValue of Dealer's Hand: " + value(dhand) + "\n");
			
			if (value(hand) == value(dhand)) {
				System.out.println("It is a tie. No money is gained or lost.");
			} else {
				if (value(dhand) > 21 || value(hand) > value(dhand)) {
					System.out.println("Congratulations, you beat the dealer.");
					chips += bet;
				} else {
					System.out.println("Sorry, the dealer beat you.");
					chips -= bet;
				}
			}
		} else if (value(hand) == 21) {
			System.out.print("Hand: ");
			for (Card c : hand) {
				System.out.print(c.rank() + " ");
			}
			System.out.println("\nValue of Hand: " + value(hand));
			System.out.println("\nCongratulations, you win.");
			chips += bet;
		} else if (value(hand) > 21){
			print(hand);
			System.out.println("\nBust. Game Over.");
			chips -= bet;
		} 
		end();
	}
	
	public void end() {
		int win = original - chips;
		if (win < 1) {
			System.out.println("You won $" + (win*-1) + ". You currently have $" + chips + ".");
		} else {
			System.out.println("Sorry, you lost $" + win + ". You currently have $" + chips + ".");
		}
		
		if (chips > 0 && (scount > 1 || !split)) {
			System.out.println("Would you like to play again? (y/n)");
			String response = in.nextLine().toLowerCase();
			while (!response.equals("y") && !response.equals("n")) {
				System.out.println("Invalid input. Would you like to play again? (y/n)");
				response = in.nextLine().toLowerCase();
			}
			if (response.equals("y")) {
				newGame();
			} else {
				System.out.println("Have a nice day!");
			}
		} else if (chips > 0) {
			scount++;
			play();
		} else {
			System.out.println("Sorry, you're broke. Welcome to Vegas!");
		}
	}
	
	public void doubledown() {
		doubledown = true;
		hand.add(deck.deal());
		bet *= 2;
		if (value(hand) < 21) {
			dealer();
		}
		isWin();
	}
	
	public void split() {
		split = true;
		s = hand.get(0);
		hand.remove(1);
		hand.add(deck.deal());
		scount++;
	}
	
	public void play() {
		dprint();
		String response = "";
		if (split) {
			System.out.println("// SECOND HAND //");
			hand = new ArrayList<Card>(0);
			hand.add(s);
			hand.add(deck.deal());
			original = chips;
			if (doubledown) {
				bet /= 2;
			}
			stand = false;
			doubledown = false;
		}
		while (value(hand) < 21 && !stand && chips > 0) {
			print(hand);
			if (bet*2 > chips) {
				response = in.nextLine().toLowerCase();
				System.out.println("Would you like to hit or stand?");
				while (!response.equals("hit") && !response.equals("stand")) {
					System.out.println("Invalid input. Would you like to hit or stand?");
					response = in.nextLine().toLowerCase();
				}
			} else if (scount==0 && hand.get(0).pointValue() == hand.get(1).pointValue() && hand.size() == 2) {
				System.out.println("Would you like to hit, stand, double down, or split?");
				response = in.nextLine().toLowerCase();
				while (!response.equals("hit") && !response.equals("stand") && !response.equals("double down") && !response.equals("split")) {
					System.out.println("Invalid input. Would you like to hit, stand, double down, or split?");
					response = in.nextLine().toLowerCase();
				}
			} else {
				System.out.println("Would you like to hit, stand, or double down?");
				response = in.nextLine().toLowerCase();
				while (!response.equals("hit") && !response.equals("stand") && !response.equals("double down")) {
					System.out.println("Invalid input. Would you like to hit, stand, or double down?");
					response = in.nextLine().toLowerCase();
				}
			}
			
			if (response.equals("hit")) {
				hand.add(deck.deal());
				System.out.print("You were dealt ");
				if (hand.get(hand.size()-1).rank() == "8" || hand.get(hand.size()-1).rank() == "A") {
					System.out.println("an " + hand.get(hand.size()-1).rank());
				} else {
					System.out.println("a " + hand.get(hand.size()-1).rank());
				}
			} else if (response.equals("stand")) {
				stand = true;	
				dealer();
			} else if (response.equals("double down")) {
				doubledown();
				return;
			} else {
				split();
				System.out.println("// FIRST HAND //");
			}
		}
		isWin();
	}
}
