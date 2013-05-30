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
	
	private boolean isVowel(String ch) {
		return ("AEIOUÁÉÍÓÚÀÈÌÒÙ".indexOf(ch) != -1);
	}

	/** Returns the {@code word.charAt(pos)} if {@code pos} is a valid position, else it returns "" */
	private String charAtOrEmpty(int pos) {
		if (pos >= word.length()) return "";
		return String.valueOf(word.charAt(pos));
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
			if (!isVowel(charAtOrEmpty(s))) {
				tempLength++;
				s++;
			} else if (!isVowel(charAtOrEmpty(s+1))) {
				if (s+2 >= word.length()) {
					length = tempLength + 2;
					tempLength = 0;
					s += 2;
				} else if (isVowel(charAtOrEmpty(s+2))) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else if (charAtOrEmpty(s+1).compareTo(charAtOrEmpty(s+2)) == 0) {
					length = tempLength + 2;
					tempLength = 0;
					s += 2;
				} else if ("SG".indexOf(charAtOrEmpty(s+1)) != -1) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else if ("RLH".indexOf(charAtOrEmpty(s+2)) != -1) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else {
					length = tempLength + 2;
					tempLength = 0;
					s+=2;
				}
			} else if ("IÍÌ".indexOf(charAtOrEmpty(s+1)) != -1) {
				if (s > 1 && (word.substring(s-1,s+1).compareTo("QU") == 0) && isVowel(charAtOrEmpty(s+2))) {
					tempLength += 2; 
					s += 2;
				} else if (isVowel(charAtOrEmpty(s+2))) {
					length = tempLength + 1;
					tempLength = 0;
					s++;
				} else {
					tempLength++;
					s++;
				}
			} else if ("IÍÌUÚÙ".indexOf(charAtOrEmpty(s)) != -1) {
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
