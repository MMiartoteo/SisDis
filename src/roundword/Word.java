package roundword;


import java.io.Serializable;

public class Word implements Serializable {

	// ------------------------------------------------------------------------
	// FIELDS
	// ------------------------------------------------------------------------

	public static final int[] letterValues = {1, 5, 2, 5, 1, 5, 8, 8, 1, 10, 5, 3, 3, 3, 1, 5, 10, 2, 2, 2, 3, 5, 10, 8, 4, 8}; 
	
	String word;
	int lastSyllableIndex;

	// ------------------------------------------------------------------------
	// METHODS
	// ------------------------------------------------------------------------

	public Word(String word) {
		if (word == null) throw new NullPointerException("word is null");
		if (word.length() == 0) throw new IllegalArgumentException("word lenght is 0");

		this.word = word.toUpperCase();
		lastSyllableIndex = calculateLastSyllableIndex();
	}
	
	private boolean isVowel(char c) {
		return ("AEIOUÁÉÍÓÚÀÈÌÒÙ".indexOf(c) != -1);
	}
	
	/**
	 * calculate the index where the last syllable starts
	 * Inspired by from: http://ready64.it/ccc/pagina.php?ccc=09&pag=036.jpg
	 * */
	private int calculateLastSyllableIndex() {
		int length = 0;
		int tempLength = 0;
		int s = 0;
		while (s < word.length()) {
			if (!isVowel(word.charAt(s))) {
				tempLength++;
				s++;
			} else if (s+1 < word.length() && !isVowel(word.charAt(s+1))) {
				if (s+2 >= word.length()) {
					length = tempLength + 2;
					tempLength = 0;
					s += 2;
				} else if (isVowel(word.charAt(s+2))) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else if (word.charAt(s+1) == word.charAt(s+2)) {
					length = tempLength + 2;
					tempLength = 0;
					s += 2;
				} else if ("SG".indexOf(word.charAt(s+1)) != -1) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else if ("RLH".indexOf(word.charAt(s+2)) != -1) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else {
					length = tempLength + 2;
					tempLength = 0;
					s+=2;
				}
			} else if (s+1 < word.length() && "IÍÌ".indexOf(word.charAt(s+1)) != -1) {
				if (s > 1 && word.substring(s-1,s+1)=="QU" && isVowel(word.charAt(s+2))) {
					tempLength += 2; 
					s += 2;
				} else if (s+2 < word.length() && isVowel(word.charAt(s+2))) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else {
					tempLength++;
					s++;
				}
			} else if ("IÍÌUÚÙ".indexOf(word.charAt(s)) != -1) {
				tempLength++;
				s++;
			} else {
				length = tempLength + 1;
				tempLength = 0;
				s++;
			}
		}
		
		return word.length() - length;
		
	}
	
	public int getLastSyllableIndex() {
		return lastSyllableIndex;
	}
	
	public String getLastSyllableSubWord() {
		return word.substring(lastSyllableIndex);
	}
	
	public String getSubWordBeforeLastSyllable() {
		return word.substring(0, lastSyllableIndex);
	}
	
	/**
	 * get the points for the word, depending on the length
	 * and the used letters
	 * */
	public int getValue() {
		int wordPoints = 0;
		for (int j = 0; j < word.length(); j++) {
			wordPoints += Word.letterValues[word.charAt(j) - 'A'];
		}
		if (word.length() >= 5) wordPoints += 5 * (word.length() - 4);
		return wordPoints;
	}
	
	@Override
	public String toString() {
		return word;
	}

}
