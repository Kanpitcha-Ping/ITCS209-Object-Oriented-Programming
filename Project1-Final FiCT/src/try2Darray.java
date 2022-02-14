
public class try2Darray {
	private Player[][] teamA = null;	//two dimensional array representing the players of Team A
	public static double[][] teamB = null;	//two dimensional array representing the players of Team B
	private int numRowPlayers = 3;	
	
	public try2Darray() {
		 Player[][] teamA = new Player[2][numRowPlayers];
		 double[][] teamB = {{1.0,1.0},{1.0,1.0},{1.0,1.0}};
	}
	public static double getSumHP(double[][] team)
	{
		//INSERT YOUR CODE HERE
		double sumHP = 0;
		for(int i=0; i<2; i++) {
			for(int j=0; j<team.length; j++) {
				sumHP += team[i][j];
			}
		}
		System.out.println(team.length);
		return sumHP;
	}
	
	public static void main(String[] args) {
		try2Darray test = new try2Darray();
		test.getSumHP(teamB);
	}
}
