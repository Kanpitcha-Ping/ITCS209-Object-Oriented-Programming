//Kanpitcha Assawavinijkulchai 6288064 sec.1
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Arena {

	public enum Row {Front, Back};	//enum for specifying the front or back row
	public enum Team {A, B};		//enum for specifying team A or B
	
	private Player[][] teamA = null;	//two dimensional array representing the players of Team A
	private Player[][] teamB = null;	//two dimensional array representing the players of Team B
	private int numRowPlayers = 0;		//number of players in each row
	
	public static final int MAXROUNDS = 100;	//Max number of turn
	public static final int MAXEACHTYPE = 3;	//Max number of players of each type, in each team.
	private final Path logFile = Paths.get("battle_log.txt");
	
	private int numRounds = 0;	//keep track of the number of rounds so far
	
	/**
	 * Constructor. 
	 * @param _numRowPlayers is the number of players in each row.
	 */
	public Arena(int _numRowPlayers)
	{	
		//INSERT YOUR CODE HERE
		numRowPlayers = _numRowPlayers;
		teamA = new Player[2][numRowPlayers];
		teamB = new Player[2][numRowPlayers];
		
		
		////Keep this block of code. You need it for initialize the log file. 
		////(You will learn how to deal with files later)
		try {
			Files.deleteIfExists(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/////////////////////////////////////////
		
	}
	

	
	/**
	 * Returns true if "player" is a member of "team", false otherwise.
	 * Assumption: team can be either Team.A or Team.B
	 * @param player
	 * @param team
	 * @return
	 */
	public boolean isMemberOf(Player player, Team team)
	{
		//INSERT YOUR CODE HERE
		switch(team) {
			case A :
				for(int i=0; i<teamA.length; i++) {
					for(int j=0; j<numRowPlayers; j++) {
						if(player.equals(teamA[i][j])) { return true; }
					}
				}
				break;
			case B :
				for(int i=0; i<teamB.length; i++) {
					for(int j=0; j<numRowPlayers; j++) {
						if(player.equals(teamB[i][j])) { return true; }
					}
				}
				break;
		}
		return false;
	}
	
	
	
	/**
	 * This methods receives a player configuration (i.e., team, type, row, and position), 
	 * creates a new player instance, and places him at the specified position.
	 * @param team is either Team.A or Team.B
	 * @param pType is one of the Player.Type  {Healer, Tank, Samurai, BlackMage, Phoenix}
	 * @param row	either Row.Front or Row.Back
	 * @param position is the position of the player in the row. Note that position starts from 1, 2, 3....
	 */
	public void addPlayer(Team team, Player.PlayerType pType, Row row, int position)
	{	
		//INSERT YOUR CODE HERE
		//Player newPlayer = new Player(pType);
		switch(team) {
			case A :
				if(row.equals(Arena.Row.Front)) {
					teamA[0][position-1] = new Player(pType);
					teamA[0][position-1].setTeam(team.A);
					teamA[0][position-1].setRow(Row.Front);
					teamA[0][position-1].setPostion(position);
				}else if(row.equals(Arena.Row.Back)) {
					teamA[1][position-1] = new Player(pType);
					teamA[1][position-1].setTeam(team.A);
					teamA[1][position-1].setRow(Row.Back);
					teamA[1][position-1].setPostion(position);
				}
				break;
			case B :
				if(row.equals(Arena.Row.Front)) {
					teamB[0][position-1] = new Player(pType);
					teamB[0][position-1].setTeam(team.B);
					teamB[0][position-1].setRow(Row.Front);
					teamB[0][position-1].setPostion(position);
				}else if(row.equals(Arena.Row.Back)) {
					teamB[1][position-1] = new Player(pType);
					teamB[1][position-1].setTeam(team.B);
					teamB[1][position-1].setRow(Row.Back);
					teamB[1][position-1].setPostion(position);
				}
				break;
		}
	}
	
	
	/**
	 * Validate the players in both Team A and B. Returns true if all of the following conditions hold:
	 * 
	 * 1. All the positions are filled. That is, there each team must have exactly numRow*numRowPlayers players.
	 * 2. There can be at most MAXEACHTYPE players of each type in each team. For example, if MAXEACHTYPE = 3
	 * then each team can have at most 3 Healers, 3 Tanks, 3 Samurais, 3 BlackMages, and 3 Phoenixes.
	 * 
	 * Returns true if all the conditions above are satisfied, false otherwise.
	 * @return
	 */
	public boolean validatePlayers()
	{
		//INSERT YOUR CODE HERE
		int[] countA = {0,0,0,0,0,0};
		int[] countB = {0,0,0,0,0,0};
		/*checking teamA*/
		for(Player[] row: teamA) {
			for(Player each: row ) {
				if(each == null) {
					return false;
				}else {
					switch(each.getType()) {
						case Healer: countA[0]++; break;
						case Tank : countA[1]++; break;
				        case Samurai : countA[2]++; break;
				        case BlackMage : countA[3]++; break;
				        case Phoenix : countA[4]++; break;
				        case Cherry : countA[5]++; break;	
					}
				}
			}
		}
		for(int cA: countA) {
			if(cA>MAXEACHTYPE) { return false; }
		}
		/*checking teamB*/
		for(Player[] row: teamB) {
			for(Player each: row ) {
				if(each == null) {
					return false;
				}else {
					switch(each.getType()) {
						case Healer: countB[0]++; break;
						case Tank : countB[1]++; break;
				        case Samurai : countB[2]++; break;
				        case BlackMage : countB[3]++; break;
				        case Phoenix : countB[4]++; break;
				        case Cherry : countB[5]++; break;	
					}
				}
			}
		}
		for(int cB: countB) {
			if(cB>MAXEACHTYPE) { return false; }
		}	
		return true;
	}
	
	/** 
	 * Return teamA 
	 */
	public Player[][] getTeamA(){
		return teamA;
	}
	/** 
	 * Return teamB 
	 */
	public Player[][] getTeamB(){
		return teamB;
	}
	/**
	 * Returns the sum of HP of all the players in the given "team"
	 * @param team
	 * @return
	 */
	public static double getSumHP(Player[][] team)
	{
		//INSERT YOUR CODE HERE
		double sumHP = 0;
		for(Player[] row: team) {
			for(Player each: row ) {
				sumHP += each.getCurrentHP();
			}
		}
		return sumHP;
	}
	
	
	/**
	 * Return the team (either teamA or teamB) whose number of alive players is higher than the other. 
	 * 
	 * If the two teams have an equal number of alive players, then the team whose sum of HP of all the
	 * players is higher is returned.
	 * 
	 * If the sums of HP of all the players of both teams are equal, return teamA.
	 * @return
	 */
	public Player[][] getWinningTeam()
	{
		//INSERT YOUR CODE HERE	
		int alive[] = {0,0}; //index 0 for teamA,index 1 for teamB
		//Checking all alive players of TeamA
		for(Player[] row: teamA) {
			for(Player each: row) {
				if(each.isAlive()) {
					alive[0]++;
				}
			}
		}
		//Checking all alive players of TeamB
		for(Player[] row: teamB) {
			for(Player each: row) {
				if(each.isAlive()) {
					alive[1]++;
				}
			}
		}
		if(alive[0]>alive[1]) {
			return teamA;
		}else if(alive[0]<alive[1]) {
			return teamB;
		}else { //Both teams have an equal number of alive players, the compare sumHp of both.
			if(getSumHP(teamB) > getSumHP(teamA)) {
				return teamB;
			}else {
				return teamA;
			}
		}
	}
	
	/**
	 * This method simulates the battle between teamA and teamB. The method should have a loop that signifies
	 * a round of the battle. In each round, each player in teamA invokes the method takeAction(). The players'
	 * turns are ordered by its position in the team. Once all the players in teamA have invoked takeAction(),
	 * not it is teamB's turn to do the same. 
	 * 
	 * The battle terminates if one of the following two conditions is met:
	 * 
	 * 1. All the players in a team has been eliminated.
	 * 2. The number of rounds exceeds MAXROUNDS
	 * 
	 * After the battle terminates, report the winning team, which is determined by getWinningTeam().
	 */
	public void startBattle()
	{
		//INSERT YOUR CODE HERE
		numRounds = 1;
		while(numRounds <= MAXROUNDS) {
			System.out.println("@Round "+numRounds);
			//TeamA attacks
			for(int i=0; i<teamA.length; i++) {
				for(int j=0; j<numRowPlayers; j++) {
					if(teamA[i][j].isAlive() ) {
						teamA[i][j].takeAction(this);
					}else if(!teamA[i][j].isAlive() && teamA[i][j].isTaunting() ) {
						teamA[i][j].resetTaunting(); //reset taunting statue of dead tank.
					}
				}
			}
			//TeamB attacks
			for(int i=0; i<teamB.length; i++) {
				for(int j=0; j<numRowPlayers; j++) {
					if(teamB[i][j].isAlive()) {
						teamB[i][j].takeAction(this);
					}else if(!teamB[i][j].isAlive() && teamB[i][j].isTaunting() ) {
						teamB[i][j].resetTaunting(); //reset taunting statue of dead tank.
					}
				}
			}
			
			logAfterEachRound();
			displayArea(this, true);
			numRounds++;
			
			if(getSumHP(teamA) == 0) {
				break;
			}else if(getSumHP(teamB) == 0) {
				break;
			}
			
			
		}
		if(getWinningTeam().equals(teamA)) {
			System.out.println("@@@ Team A won.");
		}else {
			System.out.println("@@@ Team B won.");
		}
		
	}
	
	
	
	/**
	 * This method displays the current area state, and is already implemented for you.
	 * In startBattle(), you should call this method once before the battle starts, and 
	 * after each round ends. 
	 * 
	 * @param arena
	 * @param verbose
	 */
	public static void displayArea(Arena arena, boolean verbose)
	{
		StringBuilder str = new StringBuilder();
		if(verbose)
		{
			str.append(String.format("%43s   %40s","Team A","")+"\t\t"+String.format("%-38s%-40s","","Team B")+"\n");
			str.append(String.format("%43s","BACK ROW")+String.format("%43s","FRONT ROW")+"  |  "+String.format("%-43s","FRONT ROW")+"\t"+String.format("%-43s","BACK ROW")+"\n");
			for(int i = 0; i < arena.numRowPlayers; i++)
			{
				str.append(String.format("%43s",arena.teamA[1][i])+String.format("%43s",arena.teamA[0][i])+"  |  "+String.format("%-43s",arena.teamB[0][i])+String.format("%-43s",arena.teamB[1][i])+"\n");
			}
		}
	
		str.append("@ Total HP of Team A = "+getSumHP(arena.teamA)+". @ Total HP of Team B = "+getSumHP(arena.teamB)+"\n\n");
		System.out.print(str.toString());
		
		
	}
	
	/**
	 * This method writes a log (as round number, sum of HP of teamA, and sum of HP of teamB) into the log file.
	 * You are not to modify this method, however, this method must be call by startBattle() after each round.
	 * 
	 * The output file will be tested against the auto-grader, so make sure the output look something like:
	 * 
	 * 1	47415.0	49923.0
	 * 2	44977.0	46990.0
	 * 3	42092.0	43525.0
	 * 4	44408.0	43210.0
	 * 
	 * Where the numbers of the first, second, and third columns specify round numbers, sum of HP of teamA, and sum of HP of teamB respectively. 
	 */
	private void logAfterEachRound()
	{
		try {
			Files.write(logFile, Arrays.asList(new String[]{numRounds+"\t"+getSumHP(teamA)+"\t"+getSumHP(teamB)}), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
