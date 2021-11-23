import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.lang.Thread;
import java.io.File;

public class Controller implements Runnable {
	private String[] args; // save all filenames to be used in file reading
	private BinarySearchTree tree; // construct BST tree for vocabulary
	private SentencesInFile[] wordsInVectors; // vectors for storing words from other input files
	private boolean vectorsFlag; //a safety flag for exception
	private ArrayList<ArrayList<Word>> matchedWords; // arraylist for storing matched words in all input files
	
	Controller(String[] args) {
		this.args = args;
		tree = null;
		wordsInVectors = null;
		matchedWords = null;
		vectorsFlag = false;
	}
	
	// this controller function will create threads in parallel to construct our list
	public void run() { // with using "args", args[0] will be vocabulary file, and args[1..n-1] will be input files
		if (args.length > 0) { // we need to make sure at least one file parameter is passed
			synchronized(this) { // this will be kept synchronized to complete all child threads from this controller thread
				String[] filenamesWithoutExtensions = new String[args.length];
				for (int i = 0; i < args.length; i++) {
					StringTokenizer token = new StringTokenizer(args[i], ".");
					filenamesWithoutExtensions[i] = token.nextToken();
				}
				File filespath = new File(System.getProperty("user.dir") + "\\files");
				if (filespath.isDirectory()) { // if directory found, initialize tree
				} else {
					throw new InitializationException("\"Files\" folder does not exist; add the folder in " + System.getProperty("user.dir") + " and restart program to fix problem.");
				}
				
				// initialize vectors
				if (args.length > 1) { // if any other .txt file exists other than our vocabulary text file, continue
					vectorsFlag = true;
					wordsInVectors = new SentencesInFile[args.length - 1]; // construct the array size (of length depending on text files in "files" folder)
					for (int i = 1; i < args.length; i++) {
						wordsInVectors[i-1] = new SentencesInFile(filenamesWithoutExtensions[i]); // initialize each array index with name of file
						Thread wordsThread = new Thread(wordsInVectors[i-1]);
						wordsThread.setName(filenamesWithoutExtensions[i]); // set name of thread
						wordsThread.start(); // start the thread
						//wordsInVectors[i].join();
					}

					tree = new BinarySearchTree(filenamesWithoutExtensions[0]); // initialize Thread-extended BST class
					Thread treeThread = new Thread(tree);
					treeThread.setName(filenamesWithoutExtensions[0]); // set name of thread
					treeThread.start(); // start BST thread
					
					try {
						treeThread.join();
					}
					catch (InterruptedException e) {
						System.out.println(e);
						e.printStackTrace();
					}
					
					// wait for BST thread to finish so we can proceed with matching words
					
					// now that our reading is done in parallel multithreading, search for words in BST from each vector one by one
					matchedWords = new ArrayList<ArrayList<Word>>();
					for (int i = 0; i < args.length - 1; i++) {
						ArrayList<Word> newList = new ArrayList<Word>(); // initialize ArrayList to insert into ArrayList of ArrayLists
						Iterator<String> it = wordsInVectors[i].getVectorOfFile().iterator();
						while (it.hasNext()) {
							String wordFromVector = it.next();
							if (tree.searchWord(wordFromVector)) { // if the word exists in BST, continue
								boolean newWord = true;
								int j = 0;
								for (; j < newList.size(); j++) {
									if (newList.get(j).getWord().equals(wordFromVector)) {
										newWord = false;
										break;
									}
								}
								if (newWord) { // if it is a new matched word, insert in ArrayList
									Word addWord = new Word(wordFromVector);
									newList.add(addWord);
								} else { // else, increment it at index
									newList.get(j).increaseFrequency();
								}
							}
						}
						matchedWords.add(newList);
					}
				} else { // else, terminate program
					System.out.println("ERROR: No input textfile found to read from. It's recommended to insert at least 1 textfile [filled with sentence(s)] in \"\\files\" subdirectory and restart program.");
				}
				this.notify();
			}
		} else {
			throw new InitializationException("No arguments passed in command-line interface.");
		}
	}
	
	public void displayBST() { // Menu's option 1
		if (!tree.getInitFlag()) {
			throw new InitializationException("BST not initialized as \"vocabulary.txt\" not found during time of initialization.\n");
		}
		tree.displayTree();
	}
	
	public void displayWordsVectorsFromFiles() { // Menu's option 2
		if (vectorsFlag == false) {
			throw new InitializationException("No vectors formed as no textfile other than \"vocabulary.txt\" found in \"files\" folder.\n");
		}
		for (int i = 0; i < wordsInVectors.length; i++) {
			Iterator<String> it = wordsInVectors[i].getVectorOfFile().iterator();
			System.out.println("Words captured from file \"" + wordsInVectors[i].getFilename() + "\":\n--------");
			while (it.hasNext()) {
				System.out.println(it.next());
			}
			System.out.println("--------\n");
		}
	}
	
	public void displayMatchedVectorWordsFromBST() { // Menu's option 3
		if (!tree.getInitFlag()) {
			throw new InitializationException("BST not initialized as \"vocabulary.txt\" not found during time of initialization.\n");
		}
		if (vectorsFlag == false) {
			throw new InitializationException("No matched words formed as vectors were not formed during time of initialization.\n");
		}
		for (int i = 0; i < matchedWords.size(); i++) {
			System.out.println("Words from vectors matched in BST from file \"" + wordsInVectors[i].getFilename() + "\":\n--------");
			for (int j = 0; j < matchedWords.get(i).size(); j++) {
				System.out.println("Word: " + matchedWords.get(i).get(j).getWord() + " - frequency: " + matchedWords.get(i).get(j).getFrequency());
			}
			System.out.println("--------\n");
		}
	}
	
	// Menu's option 4 will have 4 parts: search word in BST, search word in specific vector (file), search matched word in arraylist (file), or search all files present in "files" folder
	public void searchWordInBST(String word) {
		if (!tree.getInitFlag()) {
			throw new InitializationException("BST not initialized as \"vocabulary.txt\" not found during time of initialization.\n");
		}
		if (tree.searchWord(word)) {
			System.out.println("Word is present in Binary Search Tree.\n");
			return;
		}
		System.out.println("Word is not present in Binary Search Tree.\n");
	}
	
	public void searchWordInVector(String word) {
		if (vectorsFlag == false) {
			throw new InitializationException("No vectors formed as no textfile other than \"vocabulary.txt\" found in \"files\" folder.\n");
		}
		int idx = 0;
		while (idx < wordsInVectors.length) {
			Iterator<String> it = wordsInVectors[idx].getVectorOfFile().iterator();
			boolean wordFound = false;
			while (it.hasNext()) {
				if (it.next().equals(word)) {
					wordFound = true;
					break;
				}
			}
			if (wordFound) {
				System.out.println("Word exists in file \"" + wordsInVectors[idx].getFilename() + "\"");
			}
			idx++;
		}
		System.out.println();
	}
	
	public void searchWordInMatchedWordsList(String word) {
		if (!tree.getInitFlag()) {
			throw new InitializationException("BST not initialized as \"vocabulary.txt\" not found during time of initialization.\n");
		}
		if (vectorsFlag == false) {
			throw new InitializationException("No matched words formed as vectors were not formed during time of initialization.\n");
		}
		int idx = 0;
		while (idx < matchedWords.size()) {
			ArrayList<Word> listOfWords = matchedWords.get(idx);
			boolean wordFound = false;
			int frequency = 0;
			for (int i = 0; i < listOfWords.size(); i++) {
				if (listOfWords.get(i).getWord().equals(word)) {
					frequency = listOfWords.get(i).getFrequency();
					wordFound = true;
					break;
				}
			}
			if (wordFound) {
				System.out.println("Word \"" + word + "\" in file \"" + wordsInVectors[idx].getFilename() + "\" was matched " + frequency + " times in BST.");
			}
			idx++;
		}
		System.out.println();
	}
	
	public void displayAllFilesInFolder() {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		System.out.println();
	}
	
	/*
	System.out.println("1. Display Binary Search Tree formed from the vocabulary file."); (done)
	System.out.println("2. Display vectors of each input file."); (done)
	System.out.println("3. View matching words and their frequencies."); (done)
	System.out.println("4. Search for a word in BST/Vector/Matched words."); (done)
	System.out.println("5. Exit program."); (done)
	*/
	
}
