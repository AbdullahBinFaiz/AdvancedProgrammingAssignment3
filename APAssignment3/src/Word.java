
public class Word {
	private String word;
	private int frequency;
	
	Word() {
		word = null;
		frequency = 0;
	}
	
	Word(String word) {
		this.word = word;
		frequency = 1;
	}
	
	public void increaseFrequency() {
		frequency = frequency + 1;
	}
	
	public String getWord() {
		return word;
	}
	
	public int getFrequency() {
		return frequency;
	}
}
