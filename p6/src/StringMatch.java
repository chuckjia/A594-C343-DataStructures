import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * TODO #1
 */

public class StringMatch {

	/**
	 * TODO
	 * 
	 * Returns the result of running the naive algorithm to match pattern in text.
	 */
	public static Result matchNaive(String pattern, String text) {  
		int count = 0;
		int m = pattern.length();
		if (m == 0)
			return new Result(0, count);

		int n = text.length();
		if (n >= m){ // Make sure pattern is shorter than text
			int pos = 0, i = 0, j = 0;
			while (pos <= n - m){
				count++; // Increment count
				// If corresponding characters in pattern and text match
				if (pattern.charAt(i) == text.charAt(j)) {
					if (i == m - 1)
						return new Result(pos, count);
					else {
						i++; 
						j++;
					}
				} else { // If corresponding characters do NOT match 
					// slide pattern forward and start over
					pos++;
					i = 0;
					j = pos;
				}
			}	
		}
		return new Result(-1, count);
	}

	/**
	 * TODO
	 * 
	 * Populates flink with the failure links for the KMP machine associated with the
	 * given pattern, and returns the cost in terms of the number of character comparisons.
	 */
	public static int buildKMP(String pattern, int[] flink) {
		int m = pattern.length();
		int count = 0;
		flink[0] = -1;
		if (m == 0) // Empty pattern
			return count;
		flink[1] = 0;		
		for (int i = 2; i <= m; i++){
			int j = flink[i - 1];
			char nextChar = pattern.charAt(i - 1);
			while (j > -1){
				count++; // Incrementing count
				if (pattern.charAt(j) != nextChar)
					j = flink[j];
				else
					break;
			}
			flink[i] = j + 1;
		}
		return count;
	}

	/**
	 * TODO
	 * 
	 * Returns the result of running the KMP machine specified by flink (built for the
	 * given pattern) on the text.
	 */
	public static Result runKMP(String pattern, String text, int[] flink) {
		int m = pattern.length(), n = text.length();
		int count = 0;

		int j = -1, state = -1;
		char ch = '\0';
		while (true) {
			if (state == -1 || count++ >= 0 && ch == pattern.charAt(state)) { // Count is incremented within the condition
				state++;
				if (state == m)
					return new Result(j - m + 1, count);
				j = j + 1;
				if (j == n)
					return new Result(-1, count);
				ch = text.charAt(j);
			} else 
				state = flink[state];
		}  
	}

	/**
	 * Returns the result of running the KMP algorithm to match pattern in text. The number
	 * of comparisons includes the cost of building the machine from the pattern.
	 */
	public static Result matchKMP(String pattern, String text) {
		int m = pattern.length();
		int[] flink = new int[m + 1];
		int comps = buildKMP(pattern, flink);
		Result ans = runKMP(pattern, text, flink);
		return new Result(ans.pos, comps + ans.comps);
	}

	/**
	 * TODO
	 * 
	 * Populates delta1 with the shift values associated with each character in the
	 * alphabet. Assume delta1 is large enough to hold any ASCII value.
	 */
	public static void buildDelta1(String pattern, int[] delta1) {
		int m = pattern.length();
		int l = delta1.length;
		// Our program is compatible with empty input
		for (int i = 0; i < l; i++) // Populates with largest number
			delta1[i] = m;

		for (int i = m - 1; i >= 0; i--){
			char ch = pattern.charAt(i);
			if (delta1[ch] == m)
				delta1[ch] = m - 1 - i;
		}
	}

	/**
	 * TODO
	 * 
	 * Returns the result of running the simplified Boyer-Moore algorithm using the
	 * delta1 table from the pre-processing phase.
	 */
	public static Result runBoyerMoore(String pattern, String text, int[] delta1) {
		int m = pattern.length(), n = text.length();

		// Our program is compatible with cases where m == 0 and/or n == 0
		if (m > n)
			return new Result(-1, 0);

		int count = 0;
		int t = m - 1;
		while (t < n){
			int i = m - 1;
			int j = t;
			while (i >= 0 && count++ >= 0 && pattern.charAt(i) == text.charAt(j)){
				i--;
				j--;
			}
			if (i < 0)
				return new Result(t - m + 1, count);
			int partialMatch = m - i - 1;
			char ch = text.charAt(j);
			int delta1Val;
			if (ch >= 0 && ch <= 127) // Make sure that our program is compatible with characters outside of ASCII
				delta1Val = delta1[text.charAt(j)];
			else
				delta1Val = m;
			int slide = Math.max(1, delta1Val - partialMatch); // Check if char is in ASCII
			t = t + slide;
		}

		return new Result(-1, count);
	}

	/**
	 * Returns the result of running the simplified Boyer-Moore algorithm to match 
	 * pattern in text. 
	 */
	public static Result matchBoyerMoore(String pattern, String text) {
		int[] delta1 = new int[Constants.SIGMA_SIZE];
		buildDelta1(pattern, delta1);
		return runBoyerMoore(pattern, text, delta1);
	}


	public static void main(String[] args){
		// Experiments
		System.out.println("----- ----- ----- ----- -----\n"
				+ "Experiments With English Text\n"
				+ "----- ----- ----- ----- -----");
		String text = "United Continental Holdings, struggling to quell fallout from "
				+ "the forcible removal of a passenger from a flight, said its chief executive, "
				+ "Oscar Munoz, would no longer become chairman next year as planned, according to "
				+ "a filing on Friday. United also said it would revise executive compensation based"
				+ " on the airline¡¯s customer performance. Having an independent chairman of the "
				+ "board is a means to ensure that Mr. Munoz is able to more exclusively focus on his"
				+ " role as chief executive officer,¡± the company said in the filing, made with the "
				+ "Securities and Exchange Commission.";
		System.out.println("Text length: " + text.length());
		int n = 3;
		List<String> patternList = new ArrayList<String>(n);
		patternList.add("in");
		patternList.add("officer");
		patternList.add("exclusively");
		for (int i = 0; i < n; i++){
			String pattern = patternList.get(i);
			int compNaive = matchNaive(pattern, text).comps;
			int compKMP = matchKMP(pattern, text).comps;
			int compBoyerMoore = matchBoyerMoore(pattern, text).comps;
			System.out.println("Experiment #" + (i + 1));
			System.out.println("Pattern: " + pattern);
			System.out.println("Comparisons: "
					+ "Naive " + compNaive 
					+ ", KMP " + compKMP
					+ ", BoyerMoore " + compBoyerMoore + "\n");
		}


		// Experiments
		System.out.println("\n----- ----- ----- ----- ----- -----\n"
				+ "Experiments With Large Random Text\n"
				+ "----- ----- ----- ----- ----- -----");
		Random rand = new Random();
		StringBuilder tBuilder = new StringBuilder();
		int m = 20000;
		String alpha = "abcdefghijklmnopqrstuvwxyz ";
		int alphaLength = alpha.length();
		for (int i = 0; i < m; i++){
			int k = rand.nextInt(alphaLength);
			tBuilder.append(alpha.charAt(k));
		}
		n = 3;
		patternList = new ArrayList<String>(n);
		patternList.add("yes");
		patternList.add("officer");
		patternList.add("exclusively");
		for (int i = 0; i < n; i++){
			String pattern = patternList.get(i);
			int compNaive = matchNaive(pattern, text).comps;
			int compKMP = matchKMP(pattern, text).comps;
			int compBoyerMoore = matchBoyerMoore(pattern, text).comps;
			System.out.println("Experiment #" + (i + 1));
			System.out.println("Pattern: " + pattern);
			System.out.println("Comparisons: "
					+ "Naive " + compNaive 
					+ ", KMP " + compKMP
					+ ", BoyerMoore " + compBoyerMoore + "\n");
		}

		// Experiments
		System.out.println("\n----- ----- ----- ----- -----\n"
				+ "Experiments With Simple Text\n"
				+ "----- ----- ----- ----- -----");
		rand = new Random();
		tBuilder = new StringBuilder();
		m = 20000;
		for (int i = 0; i < m; i++){
			int k = rand.nextInt(4);
			tBuilder.append(k);
		}
		text = tBuilder.toString();
		n = 3;
		patternList = new ArrayList<String>(n);
		patternList.add("01000201302");
		patternList.add("00200010203");
		patternList.add("02301123011");
		for (int i = 0; i < n; i++){
			String pattern = patternList.get(i);
			Result resNaive = matchNaive(pattern, text);
			Result resKMP = matchKMP(pattern, text);
			Result resBoyerMoore = matchBoyerMoore(pattern, text);
			System.out.println("Experiment #" + (i + 1));
			System.out.println("Pattern: " + pattern);
			System.out.println("Comparisons: "
					+ "Naive " + resNaive.comps 
					+ ", KMP " + resKMP.comps
					+ ", BoyerMoore " + resBoyerMoore.comps + "\n");

		}

		// Experiments
		System.out.println("\n----- ----- ----- ----- -----\n"
				+ "Experiments With Repeated Text\n"
				+ "----- ----- ----- ----- -----");
		rand = new Random();
		tBuilder = new StringBuilder();
		m = 20000;
		for (int i = 0; i < m; i++){
			int k = i % 2;
			tBuilder.append(k);
		}
		text = tBuilder.toString();
		n = 3;
		patternList = new ArrayList<String>(n);
		patternList.add("010101");
		patternList.add("1010010");
		patternList.add("0101011");
		for (int i = 0; i < n; i++){
			String pattern = patternList.get(i);
			Result resNaive = matchNaive(pattern, text);
			Result resKMP = matchKMP(pattern, text);
			Result resBoyerMoore = matchBoyerMoore(pattern, text);
			System.out.println("Experiment #" + (i + 1));
			System.out.println("Pattern: " + pattern);
			System.out.println("Comparisons: "
					+ "Naive " + resNaive.comps 
					+ ", KMP " + resKMP.comps
					+ ", BoyerMoore " + resBoyerMoore.comps + "\n");

		}

	}

}
