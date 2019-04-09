package hangman;

import java.io.*;
import java.util.*;
import hangman.EvilHangman;
import hangman.IEvilHangmanGame.GuessAlreadyMadeException;

public class Main {
	public static void main(String args[]) throws GuessAlreadyMadeException {
		File dictionaryName = new File(args[0]);
		int wordLength = Integer.valueOf(args[1]);
		int numGuesses = Integer.valueOf(args[2]);
		char guess;
		Scanner userInput = new Scanner(System.in);

		EvilHangman game = new EvilHangman();
		game.startGame(dictionaryName, wordLength);
		game.numGuesses = numGuesses;
		while (game.numGuesses > 0) {
			printTurn(game.numGuesses, game.guessedLetters,
					game.partWord);
			guess = Character.toLowerCase(userInput.next().charAt(0));
			game.makeGuess(guess);
			// check if the player won
			if (game.youWin == true) {
				Iterator<String> iter = game.dict.iterator();
				String winWord = iter.next();
				System.out.println("You win!");
				System.out.println("The word was: " + winWord);
			}
			if (game.youLose == true) {
				Iterator<String> iter = game.dict.iterator();
				String winWord = iter.next();
				System.out.println("You lose!");
				System.out.println("The word was: " + winWord);
			}
			int letterCount = 0;
			if (!game.youLose && !game.youWin && Character.isAlphabetic(guess)) {
				if (game.prevWord.equals(game.partWord)) {
					System.out.println("Sorry, there are no " + guess + "'s");
				} else {
					if (Character.isAlphabetic(guess)) {
						for (int i = 0; i < wordLength; i++) {
							if (game.partWord.charAt(i) == guess) {
								letterCount++;
							}
						}
						System.out.println("Yes, there is " + letterCount + " " + guess);
					}
				}
			}
		}
		userInput.close();
	}

	private static String printSet(Set<Character> guessedLetters2) {
		StringBuilder temp = new StringBuilder();
		for (Character s : guessedLetters2) {
			temp.append(s);
			temp.append(" ");
		}
		return temp.toString();
	}

	public static void printTurn(int numGuesses, Set<Character> guessedLetters, String curWord) {
		System.out.println("\nYou have " + numGuesses + " guesses left");
		System.out.println("Used letters: " + printSet(guessedLetters));
		System.out.println("Word: " + curWord);
		System.out.print("Enter guess: ");
	}
}
