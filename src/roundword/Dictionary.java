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
	
	private Set<String> dictionary;
	
	public Dictionary(String path) throws FileNotFoundException, IOException {
		loadDictionary(path);
	}
	
	/**
	 * Load the dictionary
	 * @param path the path of the dictionary
	 * @throws IllegalArgumentException if the file doesn't exists
	 */
	private void loadDictionary(String path) throws FileNotFoundException, IOException {
		dictionary = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String word = br.readLine();
		while(word != null) {
			dictionary.add(word.toUpperCase());
			word = br.readLine();
		}
		br.close();
		
		for (String w : dictionary) {
			Word w2 = new Word(w);
			System.out.println(w2.getLastSyllable());
		}
	}
	
	public boolean contains(String word) {
		return dictionary.contains(word);
	}

}
