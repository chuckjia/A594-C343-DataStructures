import java.util.Arrays;
import java.util.HashSet;

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
				// If corresponding characters in pattern and text match 
				if (count++ > -1 && pattern.charAt(i) == text.charAt(j)) { // count++ here to increment count
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
		if (m == 0)
			return count;
		flink[1] = 0;		
		for (int i = 2; i <= m; i++){
			int j = flink[i - 1];
			char nextChar = pattern.charAt(i - 1);
			while (j > -1 && count++ > -1 && pattern.charAt(j) != nextChar)
				j = flink[j];
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
			if (state == -1 || count++ >= 0 && ch == pattern.charAt(state)) {
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
		for (char ch = 'A'; ch <= 'z'; ch++)
			delta1[ch] = m;
		
		HashSet<Character> s = new HashSet<>();
		for (int i = m - 1; i >= 0; i--){
			char ch = pattern.charAt(i);
			if (!s.contains(ch)){
				s.add(ch);
				delta1[ch] = m - 1 - i;
			}
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
			int slide = Math.max(1, delta1[(int) text.charAt(j)] - partialMatch);
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
		String pattern = "AABC";
		int[] flink = new int[5];
		buildKMP(pattern, flink);
		System.out.println(Arrays.toString(flink));
	}

}
