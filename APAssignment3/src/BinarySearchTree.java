import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.BufferedReader;
import java.io.FileInputStream;

public class BinarySearchTree implements Runnable {
	class BinaryTreeNode {
		public String word;
		public BinaryTreeNode leftNode, rightNode;
		
		BinaryTreeNode() {
			word = null;
			leftNode = null;
			rightNode = null;
		}
		
		BinaryTreeNode(String word) {
			this.word = word;
			leftNode = null;
			rightNode = null;
		}
	}
	
	private BinaryTreeNode root;
	private boolean init;
	private String filename;
	
	BinarySearchTree() {
		root = null;
		init = false;
		filename = null;
	}
	
	BinarySearchTree(String filename) {
		root = null;
		init = false;
		this.filename = filename;
	}
	
	public BinaryTreeNode getRoot() {
		return root;
	}
	
	public boolean isEmpty() {
		return (root == null);
	}
	
	public boolean getInitFlag() {
		return init;
	}
	
	// this "run" function will construct a BST in a thread
	public void run() {
		try {
			constructBinarySearchTreeNIO();
		}
		catch (IOException e) {
			System.out.println("ERROR: file not found. It's recommended to insert a vocabulary file into \"\\files\" subdirectory and restart program.");
		}
	}
	
	public void constructBinarySearchTreeIO() throws IOException {
		FileReader fIn = new FileReader("files\\vocabulary.txt");
		BufferedReader bIn = new BufferedReader(fIn);
		String word = null;
		while ( (word = bIn.readLine()) != null ) {
			insert(word);
		}
		bIn.close();
		fIn.close();
	}
	
	public void constructBinarySearchTreeNIO() throws IOException { // runs without blocking IO; should run alongside multiple threads simultaneously
		FileInputStream fIn = new FileInputStream("files\\" + filename + ".txt");
		FileChannel fC = fIn.getChannel();
		ByteBuffer bBuffer = ByteBuffer.allocate((int)fC.size());
		while (fC.read(bBuffer) > 0) {
			String word = "";
			bBuffer.flip();
			for (int i = 0; i < bBuffer.limit(); i++) {
				int val = bBuffer.get();
				if (val == 13 || val == 10) { // if line feed or carriage return detected, skip
					if (word.length() > 0) {
						insert(word);
						word = "";
					}
				} else {
					word += (char)val;
				}
			}
		}
		fC.close();
		fIn.close();
		System.out.println("Thread " + Thread.currentThread().getName() + " closing.\n");
		init = true;
	}
	
	public void insert(String word) {
		if (root == null) {
			root = new BinaryTreeNode(word);
		} else {
			BinaryTreeNode node = root;
			while (true) {
				if (node.word.compareTo(word) > 0) { // if word at node is greater than word being inserted, go left
					if (node.leftNode == null) {
						node.leftNode = new BinaryTreeNode(word);
						return;
					} else {
						node = node.leftNode;
					}
				} else if (node.word.compareTo(word) < 0) { // if word at node is less than word being inserted, go right
					if (node.rightNode == null) {
						node.rightNode = new BinaryTreeNode(word);
						return;
					} else {
						node = node.rightNode;
					}
				} else {
					return;
				}
			}
		}
	}
	
	private void displayNode(BinaryTreeNode node, int height) {
		if (node != null) {
			displayNode(node.leftNode, height + 1);
			System.out.println("Word: \"" + node.word + "\" at height " + height);
			displayNode(node.rightNode, height + 1);
		}
	}
	
	public void displayTree() {
		displayNode(root, 0);
		System.out.println();
	}
	
	public boolean searchWord(String word) {
		BinaryTreeNode node = root;
		while (true) {
			if (node == null) {
				return false;
			} else if (node.word.equals(word)) {
				return true;
			} else if (node.word.compareTo(word) > 0) {
				node = node.leftNode;
			} else if (node.word.compareTo(word) < 0) {
				node = node.rightNode;
			}
		}
	}
}
