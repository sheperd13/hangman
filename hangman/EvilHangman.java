package hangman;

import java.io.*;
import java.util.*;

public class EvilHangman implements IEvilHangmanGame {
	public int wordLength;
	public int numGuesses;
	public String partWord;
	public String prevWord;
	public boolean youWin;
	public boolean youLose;
	public Set<String> dict = new HashSet<String>();
	public Set<Character> guessedLetters = new TreeSet<Character>();

	public EvilHangman() {
		wordLength = 2;
		numGuesses = 1;
		partWord = "";
		prevWord = "";
	}

	private void useDictionary(File fileName) throws FileNotFoundException {
		Scanner fin = new Scanner(new BufferedReader(new FileReader(fileName)));
		while (fin.hasNext()) {
			this.dict.add(fin.next().toLowerCase());
		}
		fin.close();
	}

	@Override
	public void startGame(File dictionary, int wordLength) {
		this.wordLength = wordLength;
		StringBuilder partialWord = new StringBuilder();
		try {
			useDictionary(dictionary);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < wordLength; i++) {
			partialWord.append('-');
		}
		this.partWord = partialWord.toString();
	}

	@Override
	public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
		if (!Character.isAlphabetic(guess)) {
			System.out.println("Invalid input");
		} else if (guessedLetters.contains(guess)) {
			System.out.println("You already used that letter");
		} else {
			this.guessedLetters.add(guess);
			return hangmanAlgorithm(guess);
		}
		return null;
	}

	// makes the key to be used for the map
	private String makeKey(char guess, String word, String partWord) {
		StringBuilder curKey = new StringBuilder();
		for (int i = 0; i < wordLength; i++) {
			if (partWord.charAt(i) == '-' && word.charAt(i) == guess) {
				curKey.append(guess);
			} else if (partWord.charAt(i) != '-') {
				curKey.append(partWord.charAt(i));
			} else {
				curKey.append('-');
			}
		}
		return curKey.toString();
	}

	private Set<String> hangmanAlgorithm(char guess) {
		Set<String> potentialWords = new HashSet<String>();
		HashMap<String, Set<String>> wordMap = new HashMap<String, Set<String>>();
		Map.Entry<String, Set<String>> tempEntry = null;
		String curKey;
		this.prevWord = partWord;
		int maxSize = 0;

		// get all words of the proper length
		for (String s : dict) {
			if (s.length() == wordLength) {
				potentialWords.add(s);
			}
		}

		// create the map using the given guess
		for (String s : potentialWords) {
			Set<String> tempSet = new HashSet<String>();
			curKey = makeKey(guess, s, partWord);
			if (wordMap.containsKey(curKey))
				tempSet = wordMap.get(curKey);
			tempSet.add(s);
			wordMap.put(curKey, tempSet);
		}

		// iterate through the map to find the entry with the longest
		// wordlist and return that list
		for (Map.Entry<String, Set<String>> wordEntry : wordMap.entrySet()) {
			int curSize = wordEntry.getValue().size();

			// if the size of the current wordlist is bigger
			// than the previous list, then use the current list
			if (curSize > maxSize) {
				tempEntry = wordEntry;
				maxSize = curSize;
				// if they are the same size
			} else if (curSize == maxSize) {
				// if the current key doesn't have the guess letter in it
				// then use that one
				//loop through to check 
				boolean curContainsGuess = false;
				boolean tempContainsGuess = false;
				int curContainsGuessCount = 0;
				int tempContainsGuessCount = 0;
				for (int i = 0; i < wordLength; i++) {
					if (wordEntry.getKey().charAt(i) == guess) {
						curContainsGuess = true;
						curContainsGuessCount++;
					}
					if(tempEntry.getKey().charAt(i) == guess){
						tempContainsGuess = true;
						tempContainsGuessCount++;
					}
				}
				if (!curContainsGuess) {
					tempEntry = wordEntry;
				} else if(!tempContainsGuess){
					//do nothing because w already have the correct value
				}else if(curContainsGuessCount < tempContainsGuessCount){
					tempEntry = wordEntry;
				}else if(curContainsGuessCount > tempContainsGuessCount){
					
				}else{
					
					// check which one has less letters and use whichever one
					// has less
					int curDashCount = 0;
					int tempDashCount = 0;
					String curKeyDash = wordEntry.getKey();
					String tempKeyDash = tempEntry.getKey();
					for (int i = 0; i < wordLength; i++) {
						if (curKeyDash.charAt(i) == '-') {
							curDashCount++;
						}
						if (tempKeyDash.charAt(i) == '-') {
							tempDashCount++;
						}
					}
					if (tempDashCount < curDashCount) {
						tempEntry = wordEntry;
					} else if(tempDashCount > curDashCount) {
					//check which one has the guessed char
					//further to the right
					} else {
						boolean curKeyGuessRight = false;
						boolean tempKeyGuessRight = false;
						for(int i = wordLength-1; i > 0; i--){
							if(curKeyDash.charAt(i) == guess){
								curKeyGuessRight = true;
							}
							if(tempKeyDash.charAt(i) == guess){
								tempKeyGuessRight = true;
							}
							if(curKeyGuessRight == true && tempKeyGuessRight == false){
								tempEntry= wordEntry;
								break;
							}else if(!curKeyGuessRight && tempKeyGuessRight){
								break;
							}
						}
						//else take whichever one has any characters
						//further to the right
						boolean curKeyRight = false;
						boolean tempKeyRight = false;
						for (int i = wordLength - 1; i > 0; i--) {
							if (Character.isAlphabetic(curKeyDash.charAt(i))) {
								curKeyRight = true;
							}
							if (Character.isAlphabetic(tempKeyDash.charAt(i))) {
								tempKeyRight = true;
							}
							if (curKeyRight == true && tempKeyRight == false) {
								tempEntry = wordEntry;
								break;
							}else if(!curKeyRight && tempKeyRight){
								break;
							}
						}
					}
				}
			}
		}
		potentialWords = tempEntry.getValue();
		this.partWord = tempEntry.getKey();

		// check for end of game requirements
		this.youWin = true;
		this.youLose = false;
		for (int i = 0; i < wordLength; i++) {
			if (partWord.charAt(i) == '-') {
				this.youWin = false;
			}
		}
		if (youWin == true) {
			this.numGuesses = 0;
		}

		// check if the player lost
		if (numGuesses <= 1 && prevWord.equals(partWord)) {
			for (int i = 0; i < wordLength; i++) {
				if (partWord.charAt(i) == '-') {
					this.youLose = true;
					this.numGuesses--;
				}
			}
		}
		// if the game isn't over, do all this stuff
		if (!youLose && !youWin) {
			if (prevWord.equals(partWord)) {
				this.numGuesses--;
			} 
		}
		// narrow down the dictionary and return it
		this.dict = potentialWords;
		return potentialWords;
	}

}// e a i o u s m l r t k f n g d p b
