package roundword;

import java.io.*;
import java.util.*;

/**
 * Represent the dictionary of words. With it one can check if
 * an inserted word is valid or not.
 * 
 * The dictionary must be a plain text file were the words are
 * separated by the '\n' character
 */
public class Dictionary {
	
	private Set<String> wordSet;
	
	public Dictionary(String path) throws FileNotFoundException, IOException {
		loadDictionary(path);
	}
	
	/**
	 * Load the dictionary
	 * @param path the path of the dictionary
	 * @throws IllegalArgumentException if the file doesn't exists
	 */
	private void loadDictionary(String path) throws FileNotFoundException, IOException {
		wordSet = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String word = br.readLine();
		while(word != null) {
			wordSet.add(word.toUpperCase());
			word = br.readLine();
		}
		br.close();
	}
	
	public Set<String> getWordSet() {
		return wordSet;
	}
	
	public boolean contains(String word) {
		return wordSet.contains(word);
	}

}
