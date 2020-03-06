package Testing;
import java.util.Random;

import IO.TextFileManager;
import ciic4020.list.ArrayList;
import ciic4020.list.DoublyLinkedList;

/**
 * This coso creates ballots. Maybe use it to create insane tests with hunderds of thousands of candidates and ballots!
 * @author igtampe
 *
 */
public class ballotCSVFactory {

	public final static int VALID=2000;
	public final static int BLANK=1;
	public final static int INVALID=1;
	public final static Random Rand = new Random();
	public final static String InputFile="candidates.csv";
	public final static String OutputFile="ballots.csv";

	public static void main(String[] args ) throws Exception {
		TextFileManager NewBallots = new TextFileManager(OutputFile,true);

		//Replace this with a method that you made to get a file as an array
		DoublyLinkedList<String> AllCandidates = (new TextFileManager(InputFile)).ToArray();

		//Get all candidates IDs
		ArrayList<Integer> AllCandidateIDs = new ArrayList<Integer>(AllCandidates.size());
		for (String string : AllCandidates) {AllCandidateIDs.add(Integer.parseInt(string.split(",")[1]));}

		ArrayList<String> AllBallots = new ArrayList<String>(VALID+BLANK+INVALID);
		ArrayList<Integer> UsedPLevels = new ArrayList<Integer>(AllCandidateIDs.size());

		//Generate Valid ballots
		for (int i = 0; i < VALID; i++) {
			String currentBallot = "" + Rand.nextInt(100000); //Generate ID

			for (Integer candidate: AllCandidateIDs) {
				int nextPLevel;
				do {nextPLevel= Rand.nextInt(AllCandidateIDs.size()) + 1;} while (UsedPLevels.contains(nextPLevel)); //Make sure we don't use the same PLevel
				currentBallot+= "," + candidate + ":" + nextPLevel;
				UsedPLevels.add(nextPLevel);
			}
			
			AllBallots.add(currentBallot);
			UsedPLevels.clear();
		}

		//Blank Ballots
		for (int i = 0; i < BLANK; i++) {AllBallots.add(""+Rand.nextInt(100000));} //We only need to generate IDs for these.

		//Invalid ballots
		for (int i = 0; i < INVALID; i++) {
			String currentBallot = "" + Rand.nextInt(100000);

			for (Integer candidate: AllCandidateIDs) {
				int nextPLevel= Rand.nextInt(AllCandidateIDs.size()+30) -15; //Random invalid PLevel
				currentBallot+= "," + candidate + ":" + nextPLevel;
			}
			
			AllBallots.add(currentBallot);
		}
		
		NewBallots.print(AllBallots); //Print it.
	}

}
