// Kanpitcha Assawavinijkulchai 6288064 sec.1

public class Player {

	public enum PlayerType {Healer, Tank, Samurai, BlackMage, Phoenix, Cherry};
	
	private PlayerType type; 	//Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix
	private double maxHP;		//Max HP of this player
	private double currentHP;	//Current HP of this player 
	private double atk;			//Attack power of this player
	private int numSpecialTurns; // # of Special turn
	
	private Arena.Team T; //Team of this player. {A,B}
	private Arena.Row R; //Row of this player. {Front,Back}
	private int P; //Position of this player. {1 - number of players in each row}
	private int countTurn = 1; //the internal turn count of this player.
	private boolean taunt = false; //When this player is taunting, taunt = true.
	private boolean curse = false; //When this player is cursed, curse = true.
	private Player curser = null; //To keep cursed player.
	private Player revivablePlayer = null; //To keep revived player.
	private boolean cookie = false; //When this player is sleeping, cookie = true.
	
	/**
	 * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns, 
	 * as specified in the given table. It also reset the internal turn count of this player. 
	 * @param _type
	 */
	public Player(PlayerType _type)
	{	
		//INSERT YOUR CODE HERE
		switch (_type){
	        case Healer : 
	               type = PlayerType.Healer;
	               maxHP = 4790;
	               currentHP = maxHP;
	               atk = 238; 
	               numSpecialTurns = 4; break;
	        case Tank : 
	               type = PlayerType.Tank;
	               maxHP = 5340;
	               currentHP = maxHP;
	               atk = 255; 
	               numSpecialTurns = 4; break;
	        case Samurai : 
	               type = PlayerType.Samurai;
	               maxHP = 4005;
	               currentHP = maxHP;
	               atk = 368; 
	               numSpecialTurns = 3; break;
	        case BlackMage : 
	               type = PlayerType.BlackMage;
	               maxHP = 4175;
	               currentHP = maxHP;
	               atk = 303; 
	               numSpecialTurns = 4; break;
	        case Phoenix : 
	               type = PlayerType.Phoenix;
	               maxHP = 4175;
	               currentHP = maxHP;
	               atk = 209; 
	               numSpecialTurns = 8; break;
	        case Cherry : 
	               type = PlayerType.Cherry;
	               maxHP = 3560;
	               currentHP = maxHP;
	               atk = 198; 
	               numSpecialTurns = 4; break;
		}
	}
	
	
	/**
	 * Returns the current HP of this player
	 * @return
	 */
	public double getCurrentHP()
	{
		//INSERT YOUR CODE HERE
		return currentHP;
	}
	
	/**
	 * Returns type of this player
	 * @return
	 */
	public Player.PlayerType getType()
	{
		//INSERT YOUR CODE HERE
		return type;
	}
	
	/**
	 * Returns max HP of this player. 
	 * @return
	 */
	public double getMaxHP()
	{
		//INSERT YOUR CODE HERE
		return maxHP;
	}
	/**
	 * Set team of this player.
	 * @param team
	 */
	public void setTeam(Arena.Team team) {
		T = team;
	}
	/**
	 * Set row of this player.
	 * @param row
	 */
	public void setRow(Arena.Row row) {
		R = row;
	}
	/**
	 * Set position of this player.
	 * @param position
	 */
	public void setPostion(int position) {
		P = position;
	}
	/**
	 * Returns whether this player is sleeping.
	 * @return
	 */
	public boolean isSleeping()
	{
		//INSERT YOUR CODE HERE
		if(cookie) {return true;}
		return false;
	}
	
	/**
	 * Returns whether this player is being cursed.
	 * @return
	 */
	public boolean isCursed()
	{
		//INSERT YOUR CODE HERE
		if(curse) {return true;}
		return false;
	}
	
	/**
	 * Returns whether this player is alive (i.e. current HP > 0).
	 * @return
	 */
	public boolean isAlive()
	{
		//INSERT YOUR CODE HERE
		if(currentHP > 0) {return true;}
		return false;
	}
	
	/**
	 * Returns whether this player is taunting the other team.
	 * @return
	 */
	public boolean isTaunting()
	{
		//INSERT YOUR CODE HERE
		if(taunt) {return true;}
		return false;
	}
	/**
	 * Reset taunt statue of this player.
	 */
	public void resetTaunting() {
		taunt = false;
	}
	/**
	 * Reduce currentHP of the opposite team player(target) by atk of this player to attack.
	 * If its currentHP is negative, sets it as 0.
	 * @param target
	 */
	public void attack(Player target)
	{	
		//INSERT YOUR CODE HERE
		target.currentHP -= atk;
		if(target.currentHP < 0 ) {
			target.currentHP = 0;
		}
	}
	/**
	 * Check all alive players if they still have MaxHp.
	 * To tell healer player to do nothing in case of all allies still have MaxHp.
	 * @param team
	 * @return
	 */
	public boolean isAllAliveMaxHp(Player[][] team) {
		for(Player[] row: team) {
			for(Player each: row) {
				if(each.isAlive() && each.getMaxHP() > each.getCurrentHP()) {return false;}
			}
		}
		return true;
	}
	/**
	 * Check all front row player if they already dead.
	 * To help getting target in getTarget method.
	 * @param team
	 * @return
	 */
	public boolean allFrontRowDead(Player[][] team) {
		for(int j=0; j<team[0].length; j++) {
			if(team[0][j].isAlive()) { return false; }
		}
		return true;
	}
	/**
	 * Return target to attack.
	 * 1. Player of the opposite team who is alive and taunting. Or
	 * 2. Player of the opposite team who is alive and having lowestHP.
	 * @param team
	 * @return
	 */
	public Player getTarget(Player[][] team) {
		Player lowestHP = team[0][0];
		for(Player[] row: team) {
			for(Player each: row){
				if(each.isTaunting() && each.isAlive()) { return each; }
			}
		}
		//All front row players are dead, then find target from the back row players
		if(allFrontRowDead(team)) {
			for(int i=0; i<team[1].length; i++) {
				if(team[1][i].isAlive()) {
					lowestHP = team[1][i]; 
					for(int j=0; j<team[1].length; j++) {
						if(lowestHP.getCurrentHP() > team[1][j].getCurrentHP() && team[1][j].isAlive()) {
							lowestHP = team[1][j]; 
						}
					}
					return lowestHP;
				}
			}
		}else {
			for(int i=0; i<team[0].length; i++) {
				if(team[0][i].isAlive()) {
					lowestHP = team[0][i]; 
					for(int j=0; j<team[0].length; j++) {
						if(lowestHP.getCurrentHP() > team[0][j].getCurrentHP() && team[0][j].isAlive() ) {
							lowestHP = team[0][j]; 
						}
					}
					return lowestHP;
				}
			}
		}
		return lowestHP;
	}
	
	public void useSpecialAbility(Player[][] myTeam, Player[][] theirTeam)
	{	
		//INSERT YOUR CODE HERE
		Player target = getTarget(theirTeam);
		switch(type) {
			case Healer: 
				if(isAllAliveMaxHp(myTeam)) {break;}
				Player receiver = myTeam[0][0];
				int c=0;
				for(int i=0; i<myTeam.length; i++) {
					for(int j=0; j<myTeam[i].length; j++) {
						if(myTeam[i][j].isAlive() && !myTeam[i][j].isCursed()) {
							receiver = myTeam[i][j];
							for(int k=j; k<myTeam[0].length; k++) {
								if(myTeam[i][k].isAlive() && (myTeam[i][k].getCurrentHP()*100/myTeam[i][k].getMaxHP()) < (receiver.getCurrentHP()*100/receiver.getMaxHP()) ) {
									receiver = myTeam[i][k];
								}
							}c=1; //Already get the proper receiver(get healing), then break to get out.
						}if(c==1) {break;}
					}if(c==1) {break;}
				}
				receiver.currentHP += 0.25*receiver.getMaxHP();
				//currentHP cannot more than its maxHP.
				if(receiver.getCurrentHP() > receiver.getMaxHP() ) { receiver.currentHP = receiver.getMaxHP(); }
				System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Heals "+receiver.T+"["+receiver.R+"]"+"["+receiver.P+"]"+" {"+receiver.type+"}");
			break;
			case Tank : 
				taunt = true;
				System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" is Taunting");
			break;
	        case Samurai : 
	        	//Check if there is alive enemy to attack
	        	if(Arena.getSumHP(theirTeam) != 0) {
		        	attack(target);
		        	attack(target);
	        		System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Double-Slashes "+target.T+"["+target.R+"]"+"["+target.P+"]"+" {"+target.type+"}");
	        	}
	        break;
	        case BlackMage : 
	        	//Check if there is alive enemy to attack
	        	if(Arena.getSumHP(theirTeam) != 0) {
	        		target.curse = true;
	        		curser = target;
	        		System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Curses "+target.T+"["+target.R+"]"+"["+target.P+"]"+" {"+target.type+"}");
	        	}
			break;
	        case Phoenix :
	        	int a=0;
	        	for(int i=0; i<myTeam.length; i++) {
	        		for(int j=0; j<myTeam[i].length; j++) {
	        			if(!myTeam[i][j].isAlive()) {
	        				revivablePlayer = myTeam[i][j]; //keep proper player to be revived.
	        				revivablePlayer.currentHP += 0.3*revivablePlayer.getMaxHP();
	        				revivablePlayer.countTurn = 1; //reset the internal count turn.
	        				revivablePlayer.resetTaunting(); //reset taunt statue.
	        				System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Revives "+myTeam[i][j].T+"["+myTeam[i][j].R+"]"+"["+myTeam[i][j].P+"]"+" {"+myTeam[i][j].type+"}");
	        				a=1;
	        				break;
	        			}
	        		} if(a==1) {break;}
	        	}if(a==1) {break;}
	        break;
	        case Cherry : 
	        	//Check if there is alive enemy to attack
	        	if(Arena.getSumHP(theirTeam) != 0) {
	        		for(Player[] row: theirTeam) {
		        		for(Player each: row) {
		        			if(each.isAlive()) {
		        				each.cookie = true;
		        				System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Feed a Fortue Cookie to "+each.T+"["+each.R+"]"+"["+each.P+"]"+" {"+each.type+"}");
		        			}
		        		}
		        	}
	        	}
	        break;	
		}
		
	}
	
	
	/**
	 * This method is called by Arena when it is this player's turn to take an action. 
	 * By default, the player simply just "attack(target)". However, once this player has 
	 * fought for "numSpecialTurns" rounds, this player must perform "useSpecialAbility(myTeam, theirTeam)"
	 * where each player type performs his own special move. 
	 * @param arena
	 */
	public void takeAction(Arena arena)
	{	
		//INSERT YOUR CODE HERE
		if(arena.isMemberOf(this, Arena.Team.A)) {
			if(type.equals(PlayerType.Tank)) { taunt = false; } //Reset statue,when turn of this player who is tank come.
			if(type.equals(PlayerType.BlackMage) && curser != null) { curser.curse = false; } //Reset cursed statue of curser. when BlackMage's turn come.

			Player target = getTarget(arena.getTeamB());
			
			if(countTurn != numSpecialTurns && !isSleeping()) {
				//Check if there is alive enemy to attack
				if(Arena.getSumHP(arena.getTeamB()) != 0) {
					attack(target);
					System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Attacks "+target.T+"["+target.R+"]"+"["+target.P+"]"+" {"+target.type+"}");
					countTurn++;
				}
			}else if(countTurn == numSpecialTurns && !isSleeping()) {
				useSpecialAbility(arena.getTeamA(),arena.getTeamB());
				countTurn = 1; //reset its internal turn count
			}else if(isSleeping()) {
				cookie = false;
			}
		}else if(arena.isMemberOf(this, Arena.Team.B)) {
			if(type.equals(PlayerType.Tank)) { taunt = false; }
			if(type.equals(PlayerType.BlackMage) && curser != null) { curser.curse = false; }

			Player target = getTarget(arena.getTeamA());
			
			if(countTurn != numSpecialTurns && !isSleeping()) {
				if(Arena.getSumHP(arena.getTeamA()) != 0) {
					attack(target);
					System.out.println("# "+T+"["+R+"]"+"["+P+"]"+" {"+type+"}"+" Attacks "+target.T+"["+target.R+"]"+"["+target.P+"]"+" {"+target.type+"}");
					countTurn++;
				}
			}else if(countTurn == numSpecialTurns && !isSleeping()) {
				useSpecialAbility(arena.getTeamB(), arena.getTeamA());
				countTurn = 1;
			}else if(isSleeping()) {
				cookie = false;
			}
		}
	}
	
	/**
	 * This method overrides the default Object's toString() and is already implemented for you. 
	 */
	@Override
	public String toString()
	{
		return "["+this.type.toString()+" HP:"+this.currentHP+"/"+this.maxHP+" ATK:"+this.atk+"]["
				+((this.isCursed())?"C":"")
				+((this.isTaunting())?"T":"")
				+((this.isSleeping())?"S":"")
				+"]";
	}
	
	
}
