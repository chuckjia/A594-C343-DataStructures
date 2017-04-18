import java.util.ArrayList;
import java.util.List;

/**
 * TODO #2
 */

public class MatchBot extends TwitterBot {
	/**
	 * Constructs a MatchBot to operate on the last numTweets of the given user.
	 */
	public MatchBot(String user, int numTweets) {
		super(user, numTweets);
	}

	/**
	 * TODO
	 * 
	 * Employs the KMP string matching algorithm to add all tweets containing 
	 * the given pattern to the provided list. Returns the total number of 
	 * character comparisons performed.
	 */
	public int searchTweetsKMP(String pattern, List<String> ans) {
		int count = 0;
		int m = pattern.length();
		int[] flink = new int[m + 1];
		count += StringMatch.buildKMP(pattern, flink); // Update count
		
		for (String text : tweets){ // Iterate all tweets
			Result res = StringMatch.runKMP(pattern, text, flink);
			if (res.pos != -1)
				ans.add(text);
			count += res.comps;
		}

		return count;
	}

	/**
	 * TODO
	 * 
	 * Employs the naive string matching algorithm to find all tweets containing 
	 * the given pattern to the provided list. Returns the total number of 
	 * character comparisons performed.
	 */
	public int searchTweetsNaive(String pattern, List<String> ans) {
		int count = 0;
		int m = pattern.length();
		for (String text : tweets){ // Iterate all tweets
			Result res = StringMatch.matchNaive(pattern, text);
			if (res.pos != -1)
				ans.add(text);
			count += res.comps;
		}
		return count;
	}
	
	public int searchTweetsBoyerMoore(String pattern, List<String> ans) {
		int count = 0;
		int m = pattern.length();
		int[] delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		for (String text : tweets){ // Iterate all tweets
			Result res = StringMatch.runBoyerMoore(pattern, text, delta1);
			if (res.pos != -1)
				ans.add(text);
			count += res.comps;
		}
		return count;
	}
	

	public static void main(String... args) {
		String handle = "taylorswift13", pattern = "morning";
		MatchBot bot = new MatchBot(handle, 2000);

		// Search all tweets for the pattern.
		List<String> ansNaive = new ArrayList<>();
		int compsNaive = bot.searchTweetsNaive(pattern, ansNaive); 
		List<String> ansKMP = new ArrayList<>();
		int compsKMP = bot.searchTweetsKMP(pattern, ansKMP);  

		System.out.println("naive comps = " + compsNaive + ", KMP comps = " + compsKMP);

		for (int i = 0; i < ansKMP.size(); i++) {
			String tweet = ansKMP.get(i);
			assert tweet.equals(ansNaive.get(i));
			System.out.println(i++ + ". " + tweet);
			System.out.println(pattern + " appears at index " + 
					tweet.toLowerCase().indexOf(pattern.toLowerCase()));
		}
		
		System.out.println("\n----- ----- ----- ----- ----- ----- -----\n"
				+ "Below are results by running Boyer-Moore.\n"
				+ "----- ----- ----- ----- ----- ----- -----");
		// Do something similar for the Boyer-Moore matching algorithm.
		List<String> ansBoyerMoore = new ArrayList<>();
		int compsBoyerMoore = bot.searchTweetsBoyerMoore(pattern, ansBoyerMoore);
		System.out.println("Boyer-Moore comps = " + compsBoyerMoore);
		
		for (int i = 0; i < ansBoyerMoore.size(); i++) {
			String tweet = ansBoyerMoore.get(i);
			assert tweet.equals(ansNaive.get(i));
			System.out.println(i++ + ". " + tweet);
			System.out.println(pattern + " appears at index " + 
					tweet.toLowerCase().indexOf(pattern.toLowerCase()));
		}
	}
}
