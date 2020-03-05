package Testing;

import java.io.IOException;
import java.util.Random;

import IO.TextFileManager;
import ciic4020.list.ArrayList;

public class ballotCSVFactory {

	public final static int VALID=4;
	public final static int BLANK=0;
	public final static int INVALID=0;
	public final static Random Rand = new Random();

	public final static String InputFile="candidates.csv";
	public final static String OutputFile="ballots.csv";

	public static void main(String[] args ) throws Exception {
		TextFileManager NewBallots = new TextFileManager(OutputFile,true);

		System.out.println("Generating Ballots");

		//Replace this with a method that you made to get a file as an array
		ArrayList<String> AllCandidates = (new TextFileManager(InputFile)).ToArray();

		ArrayList<Integer> AllCandidateIDs = new ArrayList<Integer>(AllCandidates.size());
		for (String string : AllCandidates) {AllCandidateIDs.add(Integer.parseInt(string.split(",")[1]));}

		ArrayList<String> AllBallots = new ArrayList<String>(VALID+BLANK+INVALID);
		ArrayList<Integer> UsedPLevels = new ArrayList<Integer>(AllCandidateIDs.size());

		//Generate Valid ballots
		for (int i = 0; i < VALID; i++) {
			System.out.println("Generating Valid Ballot " + i);
			String currentBallot = "" + Rand.nextInt(100000);

			for (Integer candidate: AllCandidateIDs) {
				int nextPLevel;
				do {
					nextPLevel= Rand.nextInt(AllCandidateIDs.size()) + 1;
				} while (UsedPLevels.contains(nextPLevel));
				currentBallot+= "," + candidate + ":" + nextPLevel;
				UsedPLevels.add(nextPLevel);

			}

			AllBallots.add(currentBallot);
			UsedPLevels.clear();
		}

		//Blank Ballots
		for (int i = 0; i < BLANK; i++) {
			System.out.println("Generating Blank Ballot " + i);
			AllBallots.add(""+Rand.nextInt(100000));
		}

		//invalid ballots
		for (int i = 0; i < INVALID; i++) {
			System.out.println("Generating invalid Ballot " + i);
			String currentBallot = "" + Rand.nextInt(100000);

			for (Integer candidate: AllCandidateIDs) {
				int nextPLevel= Rand.nextInt(AllCandidateIDs.size()+30) -15;
				currentBallot+= "," + candidate + ":" + nextPLevel;
			}

			AllBallots.add(currentBallot);
		}

		NewBallots.print(AllBallots);



	}

}
