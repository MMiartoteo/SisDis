package roundword;


public class Word {
	
	public static final int[] letterValues = {1, 5, 2, 5, 1, 5, 8, 8, 1, 10, 5, 3, 3, 3, 1, 5, 10, 2, 2, 2, 3, 5, 10, 8, 4, 8}; 
	
	String word;
	
	public Word(String word) {
		this.word = word;
	}
	
	private boolean isVowel(char c) {
		return ("AEIOUçƒêîòËéíñô".indexOf(c) != -1);
	}
	
	/**
	 * get the last syllable
	 * */
	public String getLastSyllable() {
		String a = word.toUpperCase();
		StringBuilder result = new StringBuilder();
		int s = 0;
		while (s < a.length()) {
			if (!isVowel(a.charAt(s))) {
				result.append(word.charAt(s)); s++;
			} else if (s+1 < a.length() && !isVowel(a.charAt(s+1))) {
				if (s+2 >= a.length()) {
					result.append(word.substring(s, s+2) + "-"); s += 2;
				} else if (isVowel(a.charAt(s+2))) {
					result.append(word.charAt(s) + "-"); s++;
				} else if (a.charAt(s+1) == a.charAt(s+2)) {
					result.append(word.substring(s,s+2) + "-"); s += 2;
				} else if ("SG".indexOf(a.charAt(s+1)) != -1) {
					result.append(word.charAt(s) + "-"); s++;
				} else if ("RLH".indexOf(a.charAt(s+2)) != -1) {
					result.append(word.charAt(s) + "-"); s++;
				} else {
					result.append(word.substring(s,s+2) + "-"); s+=2;
				}
			} else if (s+1 < a.length() && "Iêí".indexOf(a.charAt(s+1)) != -1) {
				if (s>1 && a.substring(s-1,s+1)=="QU" && isVowel(a.charAt(s+2))) {
					result.append(word.substring(s,s+2)); s += 2;
				} else if (s+2 < a.length() && isVowel(a.charAt(s+2))) {
					result.append(word.charAt(s) + "-"); s++;
				} else {
					result.append(word.charAt(s)); s++;
				}
			} else if ("IêíUòô".indexOf(a.charAt(s))!=-1) {
				result.append(word.charAt(s)); s++;
			} else {
				result.append(word.charAt(s) + "-"); s++;
			}
		}
		
		if (result.charAt(result.length() - 1) == '-')
			return result.substring(0, result.length() - 1);
		return result.toString();
		
	}
	
	/**
	 * get the points for the word, depending on the length
	 * and the used letters
	 * */
	public int getValue() {
		int wordPoints = 0;
		for (int j = 0; j < word.length(); j++) {
			wordPoints += Word.letterValues[word.charAt(j) - 'a'];
		}
		if (word.length() >= 5) wordPoints += 5 * (word.length() - 4);
		return wordPoints;
	}

}
