package election;

import ciic4020.list.ArrayList;

public class ballot {
	
	private int ballotID;
	private Boolean Blank=false;
	private Boolean Invalid=false;
	private int NumberofCandidates;
	private ArrayList<Vote> CandidateChoices;
	private String OriginalBallotString;
	
	/**
	 * Generates a ballot
	 * @param BallotString The Ballot String read from the CSV file
	 */
	public ballot(String BallotString) {
		OriginalBallotString=BallotString;
		
		//Example Ballot String:
		//ballot#,c1:r1,c2:r2, …,ck:rk
		String[] SplitString=BallotString.split(",");
		
		//SplitString[0] is the ballot number
		ballotID=Integer.parseInt(SplitString[0]);
		
		//assume the count of the array (minus the first one because that identifies this ballot) 
		NumberofCandidates=SplitString.length-1;
		
		if (CheckIfBlank(SplitString)) {
			Blank=true;
			return;
		}
		
		//The arraylist only has to handle the number of candidates so we should be ok with this.
		CandidateChoices=new ArrayList<Vote>(NumberofCandidates);
		
		for (int i = 1; i < SplitString.length; i++) {
			//SplitString[i].split(":") = {Ci,Ri}
			
			//Assuming this isn't a blank ballot, if we find an empty slot, we have an invalid ballot.
			if(SplitString[i].split(":")[1]=="") {
				markInvalid();
				return;
			}
			
			int candidateID=Integer.parseInt(SplitString[i].split(":")[0]);
			int Preference=Integer.parseInt(SplitString[i].split(":")[1]);
			
			//--->Preferences should not exceed number of candidates and should not be bellow 1			
			if(Preference<1 || Preference>NumberofCandidates ) {
				markInvalid();
				return;
			}

			//--->No repeated ranks
			for (Vote vote : CandidateChoices) {
				//Lets also make sure there's no repeated candidates *just* in case
				//Good Idea past me. That was also a reason to mark a candidate as invalid.
				if (vote.PreferenceLevel==Preference || vote.CandidateID==candidateID) {
					markInvalid();
					return;
				}
			}
			
			//Create a new vote
			Vote NewVote = new Vote(candidateID, Preference);
			
			//Add it to our list of choices
			CandidateChoices.add(NewVote);
			
		}
		
		//CHECK THAT ALL RELEVANT PLEVELS ARE THERE.
		int Candidate=0;
		int TestRank=1;
		while(TestRank<NumberofCandidates || Candidate==-1)	{
			//Check that each rank has a candidate. If the candidate is equal to -1, we don't have it.
			Candidate=getCandidateByRank(TestRank);
			TestRank++;
		}
		
	}
	
	@Override
	public String toString() {
		return OriginalBallotString;
	}
	
	/**
	 * Checks if a ballot is empty
	 * @param BallotString The complete ballot string, split into an array by ","
	 * @return If all of the array elements end with ":" (osea they are empty), returns TRUE
	 */
	public boolean CheckIfBlank(String[] BallotString) {
		//ok nevermind this isn't the way blank votes are strung. If BallotString has length of 1 it's blank
		return BallotString.length==1;
		
//		for (String string : BallotString) {
//			//if just one doesn't end with ":" return false
//			if(!string.endsWith(":")) {return false;}
//		}
//		
		//All votes ended with ":", therefore it's blank
		//return true;
		
	}
	
	/**
	 * Gets the number of candidates this ballot has votes for
	 * @return
	 */
	public int getNumberofCandidates() {return NumberofCandidates;} //this will be used by the ellection class for consistency.
	
	/**
	 * Returns wether this ballot was found to be invalid
	 * @return
	 */
	public boolean isInvalid() {return Invalid;}
	
	/**
	 * Returns wether this ballot was found to be blank
	 * @return
	 */
	public boolean isBlank() {return Blank;}
	
	/**
	 * Marks this ballot as invalid (This will be used by the election class.
	 */
	public void markInvalid() {Invalid=true;}
	
	/**
	 * Returns ballot number
	 * @return The number of this ballot
	 */
	 public int getBallotNum() {
		 return ballotID;
	 }

	 /**
	  * Returns this ballot's current leading choice.
	  * @return The CandidateID for the current leading choice.
	  */
	 public int getFirstChoice() {return getCandidateByRank(1);} 

	 /**
	  * @param I The candidate u want to find
	  * @return This ballot's rank of that candidate
	  */
	 public int getRankByCandidate(int I) {
		 for (Vote vote : CandidateChoices) {
			if (vote.getCandidate()==I) {return vote.getPLevel();}
		}
		 return -1;
	 } 

	 /**
	  * @param I The rank u want to find
	  * @return This ballot's candidate that has that rank.
	  */
	 public int getCandidateByRank(int I) {
		 for (Vote vote : CandidateChoices) {
			if (vote.getPLevel()==I) {return vote.getCandidate();}
		}
		 return -1;
	 } 
	 

	 /**
	  * Eliminates a candidate from this ballot
	  * @param candidateId The ID of the candidate you wish to remove.
	  * @return
	  */
	 public boolean eliminate(int candidateId) {
		 int PLevelLimit = getRankByCandidate(candidateId);
		 if (candidateId<1) {throw new IllegalArgumentException("Candidate to be removed isn't valid");}
		 //Comb through the array of choices, reduce their preference level by 1, and if their candidate ID matches, remove them.
		 for (Vote vote : CandidateChoices) {
			if(vote.getCandidate()==candidateId) {vote.setPLevel(0);} //this may not be possible, but I mean its ok. We can just set their PLevel to 0 instead.
			if(vote.getPLevel()>PLevelLimit) {vote.setPLevel(vote.getPLevel()-1);}
			if(vote.getPLevel()<0) {throw new IllegalStateException("Algo paso, and somehow a candidate has less than 0");}
		}
		 return false;
	 }; 
	 
	 /**
	  * Represents one of the votes in the ballot, tying a candidate and the voter's preference level for that candidate
	  * @author igtampe
	  */
	 private class Vote {
		 private int CandidateID;
		 private int PreferenceLevel;
		 
		 /**
		  * Creates a vote, tying a candidate with this voter's preference level.
		  * @param ID The ID of the candidate this vote goes to
		  * @param PLevel The Voter's preference level for this candidate.
		  */
		 public Vote(int ID, int PLevel) {
			 this.CandidateID=ID;
			 this.PreferenceLevel=PLevel;
		 }
		 
		 /**
		  * Gets this vote's tied candidate
		  * @return a CandidateID
		  */
		 public int getCandidate() {return CandidateID;}
		 
		 /**
		  * Gets this voter's preference level for the candidate this vote is tied to.
		  * @return
		  */
		 public int getPLevel() {return PreferenceLevel;}
		 
		 public boolean setPLevel(int NewPLevel) {
			 PreferenceLevel=NewPLevel;
			 return true;
		 }
		
		 @Override
		public String toString() {
			return CandidateID + ":" + PreferenceLevel;
		}
	 }
	
	

}
