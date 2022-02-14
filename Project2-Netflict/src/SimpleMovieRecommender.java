import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

//Kanpitcha Assawavinijikulchai 6288064 sec.1

public class SimpleMovieRecommender implements BaseMovieRecommender {
	
	//LoadMovies
	private Map<Integer,Movie> movies; //Movies
	private Set<Integer> moviesID; //Load and sort movieID
	//LoadUsers
	private Map<Integer,User> users; //Users
	private List<Integer> usersList; //Load and sort userID
	//LoadModel
	private Map<Integer,Integer> user_map; //<userID, index>
	private Map<Integer,Integer> movie_map; //<movieID, index>
	private double[][] ratLoadModel; //rating of each user giving each movie
	private double[][] simLoadModel; //similarity
	
	public SimpleMovieRecommender() {
		movies = new HashMap<Integer,Movie>();
		moviesID = new TreeSet<Integer>();
		users = new HashMap<Integer,User>();
		usersList = new LinkedList<Integer>();
		user_map = new HashMap<Integer,Integer>();
		movie_map = new HashMap<Integer,Integer>();
	}
	
	
	@Override
	public Map<Integer, Movie> loadMovies(String movieFilename) {
		// TODO Auto-generated method stub
		Map<Integer, Movie> mload = new HashMap<Integer,Movie>();
		BufferedReader reader = null;
		File file = new File(movieFilename);
		
		try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = null;
				
				while((line = reader.readLine()) != null) {
					line = line.trim();
					if(line.isEmpty()) continue;
					
					String movieRegex;
					if(line.contains("\"")) {
						movieRegex = "(\\d+),\"(.+) \\((.+)\\)\",(.+)"; //title contains ','
					}else {
						movieRegex = "(\\d+),(.+) \\((.+)\\),(.+)"; //title doesn't contain ','
					}
					
					Pattern pattern = Pattern.compile(movieRegex);
					Matcher matcher = pattern.matcher(line);
				
					if(matcher.find()) {
						
						//System.out.println(matcher.group(1)+" : "+matcher.group(2)+" : "+matcher.group(3)+" : "+matcher.group(4));
						int mid = Integer.parseInt(matcher.group(1)); //group 1 is movieID
						String mTitle = matcher.group(2); //group 2 is title
						int mYear = Integer.parseInt(matcher.group(3)); //group 3 is year
						
						Movie m = new Movie(mid,mTitle,mYear); //create new movie
						
						String[] s = matcher.group(4).split("\\|"); //group 4 is tags that are separated by '|'
						for(int i=0; i<s.length; i++) {
							m.tags.add(s[i]); //add tags
						}
						
						mload.put(mid, m); //keep this new movie in mload HashMap
						moviesID.add(mid); //keep this new movie ID in moviesID TreeSet(sorted) -> Note: to use sorted ID when training data
					}
				}
				
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			try {
				if(reader != null) {
					reader.close();
				} 
			}catch (IOException e) {
					e.printStackTrace();
				}
		}
		return mload;
	}
	

	@Override
	public Map<Integer, User> loadUsers(String ratingFilename) {
		// TODO Auto-generated method stub
		Map<Integer, User> uload = new HashMap<Integer,User>();
		BufferedReader reader = null;
		File file = new File(ratingFilename);
		
		try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = null;
				
				while((line = reader.readLine()) != null) {
					line = line.trim();
					if(line.isEmpty() || line.contains("u")) continue; //skips empty line and head line
					
					//System.out.println("Problem here1: "+line);
					
					String[] sUser = line.split(","); //separate each line by ','
					
					int uid = Integer.parseInt(sUser[0]); //String 1 is userID
					if(uid < 0) continue;
					int mid = Integer.parseInt(sUser[1]); //String 2 is movieID
					double mRating = Double.parseDouble(sUser[2]); //String 3 is rating that this user gave this movie
					if(mRating < 0) mRating = 0; 
					long timestamp = Long.parseLong(sUser[3]); //String 4 is time stamp
					
					if(!uload.containsKey(uid)) { //new user that isn't added in uload HashMap yet
						User u = new User(uid);
						u.addRating(movies.get(mid), mRating, timestamp); 
						uload.put(uid, u);
						usersList.add(uid);
					}else { //this user is already in uload HashMap, just add movie, rating, and time stamp
						uload.get(uid).addRating(movies.get(mid), mRating, timestamp);
					}
					//System.out.println("uID: "+u+" mID: "+mid+" mRating: "+mRating+" TS: "+timestamp);
				}
				Collections.sort(usersList); //sort users's ID is used when training data
				
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			try {
				if(reader != null) {
					reader.close();
				} 
			}catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return uload;
	}

	
	@Override
	public void loadData(String movieFilename, String userFilename) {
		// TODO Auto-generated method stub
		movies = loadMovies(movieFilename); //keep list of movies in movies HashMap for using in this class
		users = loadUsers(userFilename); //keep list of users in users HashMap for using in this class
	}
	

	@Override
	public Map<Integer, Movie> getAllMovies() {
		// TODO Auto-generated method stub
		if(movies == null) return new HashMap<Integer, Movie>();
		return movies;
	}
	

	@Override
	public Map<Integer, User> getAllUsers() {
		// TODO Auto-generated method stub
		if(users == null) return new HashMap<Integer, User>();
		return users;
	}
	
	/*
	 * Finds similarity of userID u and userID v
	 * 
	 * If they are the same user which means u=v, return 1.0;
	 * @param u
	 * @param v
	 * @return similarity of userID u and userID v
	 */
	public double similarity(int u, int v) {
		if(u == v) return 1.0; //similarity of themselves is 1.0
		double rUV=0, rU=0, rV=0; 
		/* 
		 * rUV is sum of [(r(u,i) - meanRating(u))*(r(v,i) - meanRating(v))]
		 * rU is sum of (r(u,i) - meanRating(u))^2
		 * rV is sun of (r(v,i) - meanRating(v))^2
		 */
		int uID,vID; 
		double tempU=0, tempV=0;
		//to find least looping 
		if(users.get(u).ratings.size() < users.get(v).ratings.size()) {
			uID = u;
			vID = v;
		}else {
			uID = v;
			vID = u;
		}
		for(int mid: users.get(uID).ratings.keySet() ) { //set of movies that uID have rated
			if(users.get(vID).ratings.containsKey(mid)) { //compute only movies that uID and vID have rated
				tempU = users.get(uID).ratings.get(mid).rating - users.get(uID).getMeanRating(); //tempU is r(u,i) - meanRating(u)
				tempV = users.get(vID).ratings.get(mid).rating - users.get(vID).getMeanRating(); //tempV is r(v,i) - meanRating(v)
				rUV += tempU*tempV;
				rU += Math.pow(tempU, 2);
				rV += Math.pow(tempV, 2);
			}
		}
		double denominator = Math.sqrt(rU)*Math.sqrt(rV);
		return (denominator == 0) ?  0.0 : ( rUV/denominator );
	}
	
	
	@Override
	public void trainModel(String modelFilename) {
		// TODO Auto-generated method stub
		int i = 0;
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(new File(modelFilename)));
			writer.write("@NUM_USERS "+users.size()+"\n");
			writer.append("@USER_MAP {");
			for(int each: usersList) {
				writer.append(i+"="+each); 
				if(i != users.size()-1) writer.append(", ");
				i++;
			}
			writer.append("}\n");
			
			writer.append("@NUM_MOVIES "+movies.size()+"\n");
			writer.append("@MOVIE_MAP {");
			i = 0;
			for(int each: moviesID) {
				writer.append(i+"="+each); 
				if(i != movies.size()-1) writer.append(", ");
				i++;
			}
			writer.append("}\n");
			
			//Rating Matrix
			System.out.println("@@@ Computing user rating matrix");
			writer.append("@RATING_MATRIX\n");
			for(int uEachID: usersList) {
				for(int mid: moviesID) {
					if(users.get(uEachID).ratings.get(mid) != null) {
						writer.append(users.get(uEachID).ratings.get(mid).rating+" ");
					}else {
						writer.append("0.0 ");
					}
				}
				writer.append(users.get(uEachID).getMeanRating()+"\n");
			}
			
			//Finding similarity and keeping in s[][]
			System.out.println("@@@ Computing user sim matrix");
			double s[][] = new double[users.size()][users.size()];
			//s(u,v) = s(v,u) so these loops finding only one of them 
			//s(u,u) = 1.0 so it doesn't have to compute in these loops
			for(int k=0; k<users.size()-1; k++) {
				for(int j=k+1; j<users.size(); j++) {
					s[k][j] = similarity(usersList.get(k), usersList.get(j)); //usersList is sorted usersID list
					s[j][k] = s[k][j];		 
				}
			}
			System.out.println("@@@ Writing out model file");
			writer.append("@USERSIM_MATRIX\n");
			for(int k=0; k<users.size(); k++) {
				for(int j=0; j<users.size(); j++) {
					if(k == j) //s(u,u) = 1.0
						writer.append("1.0 ");
					else 
						writer.append(s[k][j]+" ");
				}
				writer.append("\n");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}finally {
			try {
				if(writer != null) {
					writer.close();
				} 
			}catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	
	@Override
	public void loadModel(String modelFilename) {
		// TODO Auto-generated method stub
		BufferedReader reader = null;
		File file = new File(modelFilename);
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			ratLoadModel = new double[users.size()][movies.size()+1];
			simLoadModel = new double[users.size()][users.size()];
			
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(line.isEmpty()) continue;
				
				if(line.contains("USER_MAP")) {
					String[] sLine = line.split(","); //Firstly, separating by ',' e.g.{0=1, 1=21, 2=475} -> [{0=1] [1=21] [2=475}]
					for(int i=0; i<sLine.length; i++) {
						String[] sNum = sLine[i].split("="); //Secondly, separating by '=' e.g.{0=1 -> [{0] [1], 1=21 -> [1] [21] and use only second element
						if(sNum[sNum.length-1].contains("}")) { //if it is the last element having '}', it need to be gotten out of '}'
							String[] s = sNum[sNum.length-1].split("}"); //e.g.475} -> [475]
							user_map.put(Integer.parseInt(s[0]), i); //keeping in user_ map that maps userID and index respectively
						}else {
							user_map.put(Integer.parseInt(sNum[sNum.length-1]), i); 
						}
					}
				}else if(line.contains("MOVIE_MAP")) { 
					String[] sLine = line.split(",");
					for(int i=0; i<sLine.length; i++) {
						String[] sNum = sLine[i].split("=");
						if(sNum[sNum.length-1].contains("}")) {
							String[] s = sNum[sNum.length-1].split("}");
							movie_map.put(Integer.parseInt(s[0]), i); 
						}else {
							movie_map.put(Integer.parseInt(sNum[sNum.length-1]), i); 
						}
					}
				}else if(line.contains("RATING")) {
					for(int i=0; i<users.size(); i++) { //each line is referred each user
						//System.out.println("Rating is found");
						line = reader.readLine();
						String[] sRating = line.split(" "); //separating each line using whitespace                                               
						for(int j=0; j<movies.size()+1; j++) { //last element is mean rating of each user (index: movies.size())
							if(Double.parseDouble(sRating[j]) != 0) 
								ratLoadModel[i][j] = Double.parseDouble(sRating[j]);
						}
					}
				}else if(line.contains("USERSIM")) {  // loadSimilarity
					//System.out.println("Similarity is found");
					for(int i=0; i<users.size(); i++) {
						line = reader.readLine();
						String[] sSIM = line.split(" ");//separating each line using whitespace 
						for(int j=0; j<sSIM.length; j++) {
							simLoadModel[i][j] = Double.parseDouble(sSIM[j]); 
						}
					}
				}
			}
			
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			try {
				if(reader != null) {
					reader.close();
				} 
			}catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	

	@Override
	public double predict(Movie m, User u) {
		// TODO Auto-generated method stub
		if(!users.containsKey(u.uid)) return u.getMeanRating(); //if user u doesn't exist in the training file(user_map: usersID loading from trainModel)
		
		double up = 0; //up is sum of s(u.u')*(rating(u',i)-meanRatingOf u') 
		double down = 0; // down is sum of s(u,u')
		
		for(User uEach: users.values()) {
			if(uEach.ratings.get(m.mid) != null && u.uid != uEach.uid) {
			up += simLoadModel[user_map.get(u.uid)][user_map.get(uEach.uid)]*( ratLoadModel[user_map.get(uEach.uid)][movie_map.get(m.mid)] - ratLoadModel[user_map.get(uEach.uid)][movie_map.size()] );
			down += Math.abs(simLoadModel[user_map.get(u.uid)][user_map.get(uEach.uid)]);
			}
		}
		
		if(down == 0) return ratLoadModel[user_map.get(u.uid)][movie_map.size()]; //return mean rating of user u that loaded from training file
		return ( (ratLoadModel[user_map.get(u.uid)][movie_map.size()]+(up/down)) > 5.0 )? 5.0 : (ratLoadModel[user_map.get(u.uid)][movie_map.size()]+(up/down)) ;
	}
	

	@Override
	public List<MovieItem> recommend(User u, int fromYear, int toYear, int K) {
		// TODO Auto-generated method stub
		List<MovieItem> mReleased = new LinkedList<MovieItem>(); //List of movies that released during fromYear to toYear
		List<MovieItem> topK = new LinkedList<MovieItem>();  //List of recommended movies 
		
		//Collecting all the movies released during fromYear to toYear : mReleased
		for(Movie each: movies.values()) {
			if(each.year >= fromYear && each.year <= toYear ) {
				mReleased.add(new MovieItem(each, predict(each, u)));
			}
		}
		
		Collections.sort(mReleased);
		
		if(mReleased.size() <= K) return mReleased; //If the number of movies is fewer than K, simply return the ranked list of the movies.

		for(int i=0; i<K; i++) {
			topK.add(mReleased.get(i));
		}
		
		return topK;
	}
	
}
