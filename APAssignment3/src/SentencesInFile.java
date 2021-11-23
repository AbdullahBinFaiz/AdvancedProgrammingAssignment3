import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.StringTokenizer;
import java.util.Vector;

public class SentencesInFile implements Runnable {
	private Vector<String> wordsList;
	private String filename;
	
	SentencesInFile() {
		wordsList = null;
		filename = null;
	}
	
	SentencesInFile(String filename) {
		wordsList = null;
		this.filename = filename;
	}
	
	// this "run" function will construct a vector of words for each file read in "files" folder, so a thread for each text file
	public void run() {
		wordsList = new Vector<String>();
		// first, read contents of file
		String entireFile = "";
		FileInputStream fIn = null;
		FileChannel fC = null;
		ByteBuffer bBuffer = null;
		try {
			fIn = new FileInputStream("files\\" + filename + ".txt");
			fC = fIn.getChannel();
			bBuffer = ByteBuffer.allocate((int)fC.size());
			while (fC.read(bBuffer) > 0) {
				bBuffer.flip();
				for (int i = 0; i < bBuffer.limit(); i++) {
					entireFile += (char)bBuffer.get();
				}
				bBuffer.clear();
			}
			fC.close();
			fIn.close();
		}
		catch (IOException e) {
			return;
		}
		// now, convert uppercase to lowercase and replace any character other than lowercase letters with whitespace
		for (int i = 0; i < entireFile.length(); i++) {
			if (entireFile.charAt(i) >= 'A' && entireFile.charAt(i) <= 'Z') { // if character is uppercase, convert to lowercase
				entireFile = entireFile.substring(0, i) + (char)(entireFile.charAt(i) + 32) + entireFile.substring(i + 1);
			} else if ( !(entireFile.charAt(i) >= 'a' && entireFile.charAt(i) <= 'z') && entireFile.charAt(i) != '\'' ) { // if character is not lowercase letter, replace with whitespace
				entireFile = entireFile.substring(0, i) + ' ' + entireFile.substring(i + 1);
			}
		}
		// finally, tokenize the sentences into list of words to insert in vector
		StringTokenizer token = new StringTokenizer(entireFile, " ");
		while (token.hasMoreTokens()) {
			wordsList.add(token.nextToken());
		}
		System.out.println("Thread " + Thread.currentThread().getName() + " closing.\n");
	}
	
	public Vector<String> getVectorOfFile() {
		return wordsList;
	}
	
	public String getFilename() {
		return filename;
	}
}
