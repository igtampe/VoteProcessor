package election;
import IO.TextFileManager;
import ciic4020.list.ArrayList;
import ciic4020.list.DoublyLinkedList;

/**
 * ---[USED DATA STRUCTURES]---
 * Arraylists were used for everything that was defined. Given their low access times and relative simplicity, I considered them the best fit.
 * I mean I could've used any data structure, really. Even a set. As long as it's itterable, we can do. There's need for a sense
 * of order. As long as it's dynamic, we can use it.
 * 
 * DoublyLinkedLists could work, but the arraylists we create have a defined, known size already, so ReAllocate doesn't need to be run.
 * The only time we don't is for Print, but it's ok. 
 * 
 * The only times we don't know what the size of the structure will be is when we read files, hence the TextFileManager uses a DoublyLinked List.
 * 
 * The Election class also incldues the private internal class Candidate, which holds a candidate's ID, Name, and the ballots where he is numero uno.
 * 
 * @author igtampe
 *
 */
public class election {

	//PLEASE FOR THE LOVE OF GOD DO NOT FORGET TO REPLACE THIS TO THE RIGHT COSOS WHEN WE SUBMIT
	public final static String CandidatesFile = "candidatesORIG.csv";
	public final static String BallotsFile = "ballotsORIG.csv";
	//DO NOT FORGET IGNACIO. AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

	private static ArrayList<Candidate> ActiveCandidates;
	public static ArrayList<ballot> AllValidBallots;

	public static int totalInvalidBallots=0;
	public static int totalBlankBallots=0;
	public static int totalValidBallots=0;

	/**
	 * Executes the election
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Candidate CurrentlyLeadingCandidate = new Candidate();
		Candidate CurrentlyTrailingCandidate = new Candidate();

		TextFileManager CandidatesCSV;
		TextFileManager BallotsCSV;
		TextFileManager ResultsTXT;

		ArrayList<String> Results = new ArrayList<String>(5);

		//Create our text file managers. Two will be used to read, and the other one will be used to write.
		CandidatesCSV = new TextFileManager(CandidatesFile);
		BallotsCSV = new TextFileManager(BallotsFile);
		ResultsTXT = new TextFileManager("results.txt",true); //this one we have to overwrite.

		//Read all of the files we're going to read.
		DoublyLinkedList<String> CandidatesAsStrings= CandidatesCSV.ToArray();
		DoublyLinkedList<String> BallotsAsStrings= BallotsCSV.ToArray();

		//LOAD CANDIDATES
		ActiveCandidates = new ArrayList<election.Candidate>(CandidatesAsStrings.size());

		//FOR EACH LINE CREATE A NEW CANDIDATE AND ADD IT TO THE ARRAYLIST OF CANDIDATES
		for (String string : CandidatesAsStrings) {
			String[] currentCandidate = string.split(",");
			ActiveCandidates.add(new Candidate(currentCandidate[0],Integer.parseInt(currentCandidate[1])));
			System.out.println("Added candidate '" + currentCandidate[0] + "' with ID " + currentCandidate[1]);
		}

		if (BallotsAsStrings.size()==0) {
			//Handle an exception if there is no file, or if the file is ```v a c i a```
			Results.add("Number of ballots: " + (totalValidBallots+totalBlankBallots+totalInvalidBallots));
			Results.add("Number of blank ballots: " + totalBlankBallots);
			Results.add("Number of invalid ballots: " + totalInvalidBallots);

			Results.add("Winner: " + ActiveCandidates.first() + " wins with 0 #1's");

			ResultsTXT.print(Results);
			return;
		}

		//LOAD BALLTOS
		AllValidBallots = new ArrayList<ballot>(BallotsAsStrings.size());

		//FOR EACH LINE IN THE ARRAY, CREATE A NEW BALLOT AND INCREMENT THE APPROPRIATE totalBallot VARIABLE (Invalid, Blank, Valid)		
		for (String string : BallotsAsStrings) {
			ballot currentBallot = new ballot(string); //Create a new ballot
			if(currentBallot.isBlank()) {totalBlankBallots++; System.out.println("Ballot " + currentBallot.getBallotNum() + " is blank. We cannot use it.");} //It's blank, we cannot use it.
			else if (currentBallot.isInvalid()||currentBallot.getNumberOfCandidates()>ActiveCandidates.size()) {totalInvalidBallots++; System.out.println("Ballot " + currentBallot.getBallotNum() + " is invalid. We cannot use it");} //It's invalid, we cannot use it. We added a check to see if there are more candidates in this ballot.
			else{
				//DETERMINE WHO IS NUMBER ONE, AND ADD IT TO THE APPROPRIATE SET FOR THAT CANDIDATE.
				getCandidate(currentBallot.getFirstChoice()).addBallot(currentBallot);
				totalValidBallots++;

				//Add it to all ballots (this will be used for Tie deciding)
				AllValidBallots.add(currentBallot);

			}
		}

		if (totalValidBallots==0) {
			//Handle a possibility that there are no valid ballots
			//(this Election is screwed if everything is invalid wow)
			Results.add("Winner: " + leadingCandidate() + " wins with 0 #1's");

			//Should we add a little warning like
			//Results.add("ALL BALLOTS WERE INVALID. ALGO PASO, AND YOU SHOULD PROBABLY CHECK IT OUT");
			//or algo asi

			ResultsTXT.print(Results);
			return;
		}

		int Rounds=1;

		//CHECK WHO IS THE CURRENTLY LEADING AND TRAILING CANDIDATE
		CurrentlyLeadingCandidate=leadingCandidate();
		CurrentlyTrailingCandidate=trailingCandidate();

		System.out.println("ROUND 0, LEADING: " + CurrentlyLeadingCandidate + " (" + ((100*CurrentlyLeadingCandidate.getCount())/totalValidBallots) + "%, " + CurrentlyLeadingCandidate.getCount() + " #1s), TRAILING: " + CurrentlyTrailingCandidate + " (" + ((100*CurrentlyTrailingCandidate.getCount())/totalValidBallots) + "%, " + CurrentlyTrailingCandidate.getCount() + " #1s)");

		//WHILE NOBODY HAS A MAJORITY (osea if for each candidate, there's nobody with count > .5*totalBallots) REMOVE THE ONE WITH THE MOST VOTES, AND GET THE NEW LEADING CANDIDATE
		while (CurrentlyLeadingCandidate.getCount() < (totalValidBallots * 0.5)) {
			//Eliminate the candidate
			CurrentlyTrailingCandidate.eliminate();

			//Add some info
			Results.add("Round " + Rounds + ": " + CurrentlyTrailingCandidate.getName() + " was eliminated with " + CurrentlyTrailingCandidate.getCount() + " #1's");

			//Clear and remove this candidate.
			ActiveCandidates.remove(CurrentlyTrailingCandidate);
			CurrentlyTrailingCandidate.Clear();
			CurrentlyTrailingCandidate=null;

			//Get the new candidate.
			CurrentlyLeadingCandidate=leadingCandidate();
			CurrentlyTrailingCandidate=trailingCandidate();

			//KK done, time for another round.
			System.out.println("ROUND " + Rounds + ", LEADING: " + CurrentlyLeadingCandidate + " (" + ((100*CurrentlyLeadingCandidate.getCount())/totalValidBallots) + "%, " + CurrentlyLeadingCandidate.getCount() + " #1s), TRAILING: " + CurrentlyTrailingCandidate + " (" + ((100*CurrentlyTrailingCandidate.getCount())/totalValidBallots) + "%, " + CurrentlyTrailingCandidate.getCount() + " #1s)");
			Rounds++;
		}

		//Save the total data now as it may have shifted during flight.
		Results.add(0,"Number of invalid ballots: " + totalInvalidBallots);
		Results.add(0,"Number of blank ballots: " + totalBlankBallots);
		Results.add(0,"Number of ballots: " + (totalValidBallots+totalBlankBallots+totalInvalidBallots));


		//If we've made it this far, CurrentlyLeadingCandidate is the leading candidate of this election. He's won.
		Results.add("Winner: " + CurrentlyLeadingCandidate.getName() + " wins with " + CurrentlyLeadingCandidate.getCount() + " #1's");

		//P R O N T
		ResultsTXT.print(Results);

		//And that's that bobitos. We are done!

	}

	/**
	 * @return The currently leading candidate (Most #1s)
	 */
	private static Candidate leadingCandidate() {
		ArrayList<Candidate> LeadingCandidates = new ArrayList<election.Candidate>(ActiveCandidates.size());
		Candidate CurrentlyLeadingCandidate = new Candidate();

		//comb through all the candidates.
		for (Candidate candidate : ActiveCandidates) {
			//Check if the current candidates has the same number of votes as the leading candidate.
			if(CurrentlyLeadingCandidate.getCount() == candidate.getCount() && !CurrentlyLeadingCandidate.isDummy() ) {
				LeadingCandidates.add(candidate);; //This will be used when we check for a tie.
			}
			//if the currently leading candidate has less #1 votes update the currently leading candidate			
			if(CurrentlyLeadingCandidate.getCount() < candidate.getCount() || CurrentlyLeadingCandidate.isDummy() ) {
				CurrentlyLeadingCandidate=candidate;
				LeadingCandidates.clear();
				LeadingCandidates.add(candidate);
			}
		}

		//TIEBREAKER
		if(LeadingCandidates.size()>1) {return LeadingTieBreaker(LeadingCandidates);}
		return CurrentlyLeadingCandidate;
	}


	/**
	 * Breaks a tie between leading candidates
	 * @param LeadingCandidates
	 * @return The actual leading candidate
	 */
	private static Candidate LeadingTieBreaker(ArrayList<Candidate> LeadingCandidates) {
		System.out.println("TIE DETECTED BETWEEN: " + ArraylistToString(LeadingCandidates));
		int N=1;
		ArrayList<Candidate> LLC = new ArrayList<Candidate>(LeadingCandidates.size());
		Candidate CurrentlyLeadingCandidate = LeadingCandidates.first();
		int CurrentlyLeadingCount=CountAtPosition(CurrentlyLeadingCandidate, 2);


		//While there's more than 1 leading candidate.
		do {
			N++;
			System.out.println("Checking Position " + N);
			for (Candidate candidate : LeadingCandidates) {
				//GET THEIR Nth PLACE POSITIONS
				int CurCandidateCount = CountAtPosition(CurrentlyLeadingCandidate, N);

				//IF THEY STILL HAVE THE SAME AMOUNT, ADD THEM FOR ANOTHER ROUND.
				if(CurrentlyLeadingCount == CurCandidateCount && !LLC.contains(candidate)) {
					LLC.add(candidate);
				}

				//if the currently leading candidate has less N votes, or is a dummy, update the currently leading candidate			
				if(CurrentlyLeadingCount < CurCandidateCount) {
					CurrentlyLeadingCandidate=candidate;
					CurrentlyLeadingCount=CurCandidateCount;
					LLC.clear();
					LLC.add(candidate);
				}
			} 


		} while(LLC.size()>1 && N<=ActiveCandidates.size());
		return CurrentlyLeadingCandidate;

	}

	/**
	 * Breaks a tie between trailing candidates
	 * @param TrailingCandidates
	 * @return The actual trailing candidate
	 */
	private static Candidate TrailingTieBreaker(ArrayList<Candidate> TrailingCandidates) {
		System.out.println("TIE DETECTED BETWEEN: " + ArraylistToString(TrailingCandidates));
		int N=1;
		ArrayList<Candidate> LLC = new ArrayList<Candidate>(TrailingCandidates.size());
		Candidate CurrentlyTrailingCandidate = TrailingCandidates.first();
		int CurrentlyTrailingCount=CountAtPosition(CurrentlyTrailingCandidate, 2);


		//While there's more than 1 leading candidate.
		do {
			N++;
			System.out.println("Checking Position " + N);
			for (Candidate candidate : TrailingCandidates) {
				//GET THEIR Nth PLACE POSITIONS
				int CurCandidateCount = CountAtPosition(candidate, N);

				//IF THEY STILL HAVE THE SAME AMOUNT, ADD THEM FOR ANOTHER ROUND.
				if(CurrentlyTrailingCount == CurCandidateCount && !LLC.contains(candidate)) {
					LLC.add(candidate);
				}

				//if the currently leading candidate has more N votes update the currently leading candidate			
				if(CurrentlyTrailingCount > CurCandidateCount) {
					CurrentlyTrailingCandidate=candidate;
					CurrentlyTrailingCount=CurCandidateCount;
					LLC.clear();
					LLC.add(candidate);
				}
			} 


		} while(LLC.size()>1 && N<=ActiveCandidates.size());

		//Special case, if there's still people, return with the highest candidate ID
		if(LLC.size()>1) {return LLC.last();}
		return CurrentlyTrailingCandidate;

	}

	/**
	 * @return The candidate with the least 1s
	 */
	private static Candidate trailingCandidate() {
		Candidate CurrentlyTrailingCandidate = new Candidate();
		ArrayList<Candidate> TrailingCandidates=new ArrayList<election.Candidate>(ActiveCandidates.size());

		//comb through all the candidates.
		for (Candidate candidate : ActiveCandidates) {
			if(CurrentlyTrailingCandidate.getCount() == candidate.getCount() && !CurrentlyTrailingCandidate.isDummy() ) {
				TrailingCandidates.add(candidate);; //This will be used when we check for a tie.
			}

			//if the currently trailing candidate has less #1 votes, or is a dummy, update the currently trailing candidate			
			if(CurrentlyTrailingCandidate.getCount() > candidate.getCount() || CurrentlyTrailingCandidate.isDummy() ) {
				CurrentlyTrailingCandidate=candidate;
				TrailingCandidates.clear();
				TrailingCandidates.add(candidate);
			}
		}

		//TIEBREAKER
		if(TrailingCandidates.size()>1) {return TrailingTieBreaker(TrailingCandidates);}
		return CurrentlyTrailingCandidate;	
	}

	/**
	 * @return Ballots with the specified candidate at the specified position.
	 */
	private static int CountAtPosition(Candidate cand, int Position) {
		//I know this is much less efficient, but it only runs during tie-breakers, so its ok.
		int count = 0;
		for (ballot ballot : AllValidBallots) {if(ballot.getRankByCandidate(cand.getID())==Position) {count++;}}
		return count;
	}

	/**
	 * Returns the candidate with the specified ID
	 * @param ID The ID u want
	 * @return THe candidate u need
	 */
	private static Candidate getCandidate(int ID) {
		for (Candidate candidate : ActiveCandidates) {if(candidate.ID.equals(ID)) {return candidate;}}
		return null;
	}

	/**
	 * Returns a joined Arraylist string
	 * @return All elements in the arraylist, separated by a comma
	 */
	public static String ArraylistToString(ArrayList<Candidate> a) {
		String returnString = "";
		for (Object object : a) {returnString+=object.toString() + ", ";}
		return returnString;
	}

	/**
	 * This is the class for a candidate
	 * @author igtampe
	 */
	private static class Candidate {
		private boolean dummy=false;
		private ArrayList<ballot> myBallots;
		private String Name;
		private Integer ID;

		/**
		 * Creates a new candidate
		 * @param myName Name of the candidate
		 * @param myID ID of the candidate
		 */
		public Candidate(String myName, Integer myID) {
			Name=myName;
			ID=myID;
			myBallots=new ArrayList<ballot>(1);
		}

		/**
		 * Creates a dummy candidate.
		 */
		public Candidate() {
			dummy=true;
			Name="I'm a dummy candidate. I shouldn't have won";
			ID=-1;
		}

		/**
		 * @return Name of the candidate
		 */
		public String getName() {return Name;}

		/**
		 * @return Name of the candidate
		 */
		public int getID() {return ID;}

		/**
		 * @return Returns whether or not this candidate is a dummy candidate 
		 */
		public boolean isDummy() {return dummy;}

		/**
		 * @return The number of ballots that have this candidate as Number 1
		 */
		public int getCount() {
			if (dummy) { return 0;} //If it's dummy this coso should not be used.
			return myBallots.size();
		}

		/**
		 * Adds the specified ballot to the collection of ballots that have this candidate as number 1
		 * @param newBallot A new candidate.
		 */
		public void addBallot(ballot newBallot) {myBallots.add(newBallot);}

		/**
		 * Eliminates this candidate 
		 */
		private void eliminate() {
			int newTopChoice;

			//Comb through all of my ballots and move them to the appropriate new candidates
			for (ballot currentBallot : myBallots) {
				currentBallot.eliminate(this.ID); //Eliminate myself from each of my ballots

				newTopChoice=currentBallot.getFirstChoice(); //Get their new top choice
				if(newTopChoice==-1) {throw new IllegalStateException("This ballot has no top choice. " + currentBallot.getBallotNum());}

				//To make sure their new top choice is in the list of active ballots, if their top choice is not in the list...
				while (getCandidate(newTopChoice)==null||newTopChoice==-1) {

					//Eliminate their top choice
					currentBallot.eliminate(newTopChoice);

					//Get their new top choice
					newTopChoice=currentBallot.getFirstChoice();
				}

				if(newTopChoice==-1) {
					//There's no new top choice, so this coso is invalid
					currentBallot.markInvalid();
					totalInvalidBallots++;
					totalValidBallots--;
					AllValidBallots.remove(currentBallot);

				}else {
					//Move this ballot to their new top candidate
					moveToCandidate(currentBallot, getCandidate(newTopChoice));
				}

			}
		}

		/**
		 * Moves a ballot to another candidate
		 * @param moveBallot The ballot to move
		 * @param TheCandidate The candidate this ballot needs to be moved to.
		 */
		private void moveToCandidate(ballot moveBallot, Candidate TheCandidate) {TheCandidate.addBallot(moveBallot);}

		/**
		 * Clears a candidate in preparation for deletion
		 */
		public void Clear() {
			myBallots.clear();
			myBallots=null;
			Name=null;
			ID=null;
		}

		/**
		 * Returns the ID and Name
		 */
		public String toString() {return "(" + ID + ") " + Name;}

	}
}
