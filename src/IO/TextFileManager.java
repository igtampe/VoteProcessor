package IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ciic4020.list.ArrayList;
import ciic4020.list.DoublyLinkedList;

/**
 * A class to read and write to text files. I made this separately to not have to repeat this ever, and to re-use it in future projects. It'll probably grow when I need to do more things.
 * @author igtampe
 * 
 */
public class TextFileManager {
	private String filename;
	private File theFile;

	/**
	 * Creates a text file manager
	 * @param File The file u want to manage
	 * @throws IOException if it cannot create it.
	 */
	public TextFileManager(String Filename) throws IOException {
		filename=Filename;
		theFile = new File(filename);

		//If the file doesn't exist then let's create it
		if(!theFile.exists()) {theFile.createNewFile();}
	}

	/**
	 * Creates a text file manager
	 * @param Filename The file u want to manage
	 * @param Overwrite	If u want to erase it and re-write it.
	 * @throws IOException
	 */
	public TextFileManager(String Filename, Boolean Overwrite) throws IOException {
		filename=Filename;
		theFile = new File(filename);

		//If the file exists, delete it and remake it.
		if(theFile.exists() && Overwrite) {
			theFile.delete();
			theFile.createNewFile();
		}

		//If the file doesn't exist, then make it.
		if(!theFile.exists()) {theFile.createNewFile();}
	}


	/**
	 * Spits out the contents of the file as a list.
	 * @return The file as an array.
	 * @throws Exception If something happens (Possibly that it doesn't exist)
	 */
	public DoublyLinkedList<String> ToList() throws Exception {

		//Time to read using this program's "eyes" 
		BufferedReader Eyes = new BufferedReader(new FileReader(filename));

		//Create the arraylist we're going to return
		DoublyLinkedList<String> Temp = new DoublyLinkedList<String>();

		//Read the first line
		String currentLine = Eyes.readLine();

		//Continue to read until there is nothing more to read
		while(currentLine!=null) {
			Temp.add(currentLine);
			currentLine=Eyes.readLine();
		}

		//Close our eyes. They are done.
		Eyes.close();

		//Boop we're done
		return Temp;
	}

	/**
	 * Prints each string in the list out into a file (with each element representing a line) <b>(will overwrite!)</b>
	 * @param StuffToPrint
	 */
	public void print(ArrayList<String> StuffToPrint) throws Exception {
		//Time to write
		BufferedWriter Pen = new BufferedWriter(new FileWriter(filename));

		for (String string : StuffToPrint) {
			//Write
			Pen.write(string);
			Pen.newLine();
		}

		//we're done writing a line. Put the cap on the pen and close it.
		Pen.close();

	}
	
}
