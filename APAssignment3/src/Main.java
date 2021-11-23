import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	public static void main(String args[]) {
		Controller controller = new Controller(args);
		Scanner scan = new Scanner(System.in);
		Thread controllerThread = new Thread(controller);
		try {
			// "Controller" thread will extend from main, and other threads will extend from "Controller" thread, performing multiparallelism calculations until "Controller" thread has performed all actions in its "run" function
			controllerThread.start();
			synchronized(controllerThread) {
				controllerThread.wait();
			}
		}
		catch (InitializationException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		int choice = 0;
		do {
			try {
				System.out.println("----MENU----");
				System.out.println("1. Display Binary Search Tree formed from the vocabulary file.");
				System.out.println("2. Display vectors of each input file.");
				System.out.println("3. View matching words and their frequencies.");
				System.out.println("4. List search queries.");
				System.out.println("5. Exit program.");
				System.out.println("------------");
				System.out.print("Enter: ");
				choice = scan.nextInt();
				if (choice == 5) { // Exit program
					System.out.println("Terminating program...");
					break;
				} else if (choice == 1) { // Display BST
					controller.displayBST();
				} else if (choice == 2) { // Display Vectors of all input files
					controller.displayWordsVectorsFromFiles();
				} else if (choice == 3) { // Display Matched words in vectors against BST from vocabulary file
					controller.displayMatchedVectorWordsFromBST();
				} else if (choice == 4) { // Search query - search for a word in either BST, Vectors, or Matched words list
					int extChoice = 0;
					System.out.println();
					do {
						System.out.println("---SUBMENU---");
						System.out.println("1. Search for word in BST.");
						System.out.println("2. Search for word in vector created from file.");
						System.out.println("3. Search for word in matched words list between vector and BST.");
						System.out.println("4. Display all files in \"files\" folder.");
						System.out.println("5. Return to MENU.");
						System.out.println("-------------");
						System.out.print("Enter: ");
						extChoice = scan.nextInt();
						scan.nextLine();
						if (extChoice == 5) {
							System.out.println("Returning to MENU...\n");
							break;
						} else if (extChoice == 1) {
							String word = "";
							System.out.print("Enter a word to search in BST (all lowercase): ");;
							word = scan.nextLine();
							controller.searchWordInBST(word);
						} else if (extChoice == 2) {
							String word = "";
							System.out.print("Enter a word to search in vector (all lowercase): ");
							word = scan.nextLine();
							controller.searchWordInVector(word);
						} else if (extChoice == 3) {
							String word = "";
							System.out.print("Enter a word to search in matched words list (all lowercase): ");
							word = scan.nextLine();
							controller.searchWordInMatchedWordsList(word);
						} else if (extChoice == 4) {
							controller.displayAllFilesInFolder();
						}
					} while (true);
				}
			}
			catch (InitializationException e) {
				System.out.println(e);
			}
			catch (InputMismatchException e) {
				System.out.println(e);
			}
		} while (true);
		scan.close();
	}
}
