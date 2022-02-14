import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class pingTest {
	public static void main(String[] args) {
		SimpleMovieRecommender test1 = new SimpleMovieRecommender();
		
		//test1.loadData("movielisttest.txt","pingtestcase.txt");
		//Small
		//test1.loadData("D:\\1st Year\\Java\\project02\\small\\movies.csv", "D:\\1st Year\\Java\\project02\\small\\users.train.csv");
		//Micro
		//test1.loadData("D:\\1st Year\\Java\\project02\\micro\\movies.csv", "D:\\1st Year\\Java\\project02\\micro\\users.train.csv");
		
		Map<Integer,Movie> mtest = new HashMap<Integer,Movie>();
		mtest = test1.loadMovies("movielisttest.txt");
		//mtest = test1.loadMovies("D:\\1st Year\\Java\\project02\\micro\\movies.csv");
		
		//System.out.println("1 "+mtest.get(0));
		for(Movie each: mtest.values()) {
			System.out.println(each.toString());
		}
		/*
		Map<Integer,User> utest = new HashMap<Integer,User>();
		
		//utest = test1.loadUsers("pingtestcase.txt");
		utest = test1.loadUsers("D:\\1st Year\\Java\\project02\\micro\\users.train.csv");
		for(User each: utest.values()) {
			System.out.println(each.toString());
		}
		*/
		//Micro Model
		
		//test1.trainModel("micro.test.model");
		//test1.loadModel("micro.test.model");
		

	
		//Small Model
		/*System.out.println("Training");
		test1.trainModel("small.test.model");
		System.out.println("Loading");
		test1.loadModel("small.test.model");  */
		
		//System.out.println(test1.similarity(15, 19));
		/*System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(106)));
		System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(114)));
		System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(128)));
		System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(166)));
		System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(167)));
		System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(170)));
		System.out.println(test1.similarity(test1.getAllUsers().get(15), test1.getAllUsers().get(206)));
		//System.out.println(test1.predict(test1.getAllMovies().get(125914), test1.getAllUsers().get(362)));*/
		//System.out.println(test1.predict(test1.getAllMovies().get(99114), test1.getAllUsers().get(15)));
		/*System.out.println(test1.predict(test1.getAllMovies().get(88672), test1.getAllUsers().get(389)));
		List<MovieItem> testRecommend = test1.recommend(test1.getAllUsers().get(389),2010, 2015, 20);
		System.out.println(testRecommend);*/
		
	}
		
}
