import java.util.Arrays;
import java.util.Random;

/**
 * TODO: Implement the fillCache(), getResult(), and traceback() methods, in
 * that order. This is the biggest part of this project.
 * 
 * @author <Chuck Jia>
 */

public class SequenceAligner {
	private static Random gen = new Random();

	private String x, y;
	private int n, m;
	private String alignedX, alignedY;
	private Result[][] cache;
	private Judge judge;

	/**
	 * Generates a pair of random DNA strands, where x is of length n and
	 * y has some length between n/2 and 3n/2, and aligns them using the 
	 * default judge.
	 */
	public SequenceAligner(int n) {
		this(randomDNA(n), randomDNA(n - gen.nextInt(n / 2) * (gen.nextInt(2) * 2 - 1)));
	}

	/**
	 * Aligns the given strands using the default judge.
	 */
	public SequenceAligner(String x, String y) {
		this(x, y, new Judge());
	}

	/**
	 * Aligns the given strands using the specified judge.
	 */
	public SequenceAligner(String x, String y, Judge judge) {
		this.x = x.toUpperCase();
		this.y = y.toUpperCase();
		this.judge = judge;
		n = x.length();
		m = y.length();
		cache = new Result[n + 1][m + 1];
		fillCache();
		traceback();
	}

	/**
	 * Returns the x strand.
	 */
	public String getX() {
		return x;
	}

	/**
	 * Returns the y strand.
	 */
	public String getY() {
		return y;
	}

	/**
	 * Returns the judge associated with this pair.
	 */
	public Judge getJudge() {
		return judge;
	}

	/**
	 * Returns the aligned version of the x strand.
	 */
	public String getAlignedX() {
		return alignedX;
	}

	/**
	 * Returns the aligned version of the y strand.
	 */
	public String getAlignedY() {
		return alignedY;
	}

	/**
	 *  TODO: Solve the alignment problem using bottom-up dynamic programming
	 *  algorithm described in lecture. When you're done, cache[i][j] will hold
	 *  the result of solving the alignment problem for the first i characters
	 *  in x and the first j characters in y.
	 *  
	 *  Your algorithm must run in O(n * m) time, where n is the length of x
	 *  and m is the length of y.
	 *  
	 *  Ordering convention: So that your code will identify the same alignment
	 *  as is expected in Testing, we establish the following preferred order
	 *  of operations: M (diag), I (left), D (up). This only applies when you
	 *  are picking the operation with the biggest payoff and two or more  
	 *  operations have the same max score. 
	 */

	private void fillCache() {
		cache[0][0] = new Result(0, Direction.NONE); // Filling cache[0][0]
		for (int i = 1; i <= n; i++){ // Fill the first column
			int score = cache[i - 1][0].getScore() + judge.score(x.charAt(i - 1), Constants.GAP_CHAR);
			/* Note on the method that we calculate score:
			 *   For the purpose of solving this particular problem under this specific setting, we can 
			 *   fill in the first column and row by directly using gapCost value in the Judge class or 
			 *   even directly using the score -1, e.g. we can calculate the score by using
			 * 		int score = -1 * i;
			 *   However, we chose the method above in the calculation of score in order to achieve 
			 *   higher generality. This way, we would be able to easily accommodate more general scoring 
			 *   mechanisms, allowing our program to apply to different models. 
			 */  
			cache[i][0] = new Result(score, Direction.UP);
			// Note on the above 2 lines: The above two lines can be combined to one. But to improve
			// readability, we chose to use 2 lines (for the purpose of creating a more readable homework
			// assignment). If optimal efficiency is needed, 1 line version can be used. Same as below.
		}
		for (int j = 1; j <= m; j++){ // Fill the first row
			int score = cache[0][j - 1].getScore() + judge.score(Constants.GAP_CHAR, y.charAt(j - 1));
			cache[0][j] = new Result(score, Direction.LEFT);
		}

		for (int i = 1; i <= n; i++)
			for (int j = 1; j <= m; j++){
				int diag = cache[i - 1][j - 1].getScore() + judge.score(x.charAt(i - 1), y.charAt(j - 1));
				int left = cache[i][j - 1].getScore() + judge.score(Constants.GAP_CHAR, y.charAt(j - 1));
				int up = cache[i - 1][j].getScore() + judge.score(x.charAt(i - 1), Constants.GAP_CHAR);
				// Note: Same as before, for higher generality, Judge.score() is used instead of Judge.gapCost 

				// Compare the three values
				if (diag >= left && diag >= up) // Case 1: diag is the largest
					cache[i][j] = new Result(diag, Direction.DIAGONAL);
				else if (left >= up) // Case 2: left is the largest
					cache[i][j] = new Result(left, Direction.LEFT);
				else // Case 3: up is the largest
					cache[i][j] = new Result(up, Direction.UP); 

			}
	}


	/**
	 * TODO: Returns the result of solving the alignment problem for the 
	 * first i characters in x and the first j characters in y. You can
	 * find the result in O(1) time by looking in your cache.
	 */
	public Result getResult(int i, int j) {
		return cache[i][j];
	}

	/**
	 * TODO: Mark the path by tracing back through parent pointers, starting
	 * with the Result in the lower right corner of the cache. Run Result.markPath()
	 * on each Result along the path. The GUI will highlight all such marked cells
	 * when you check 'Show path'. As you're tracing back along the path, build 
	 * the aligned strings in alignedX and alignedY (using Constants.GAP_CHAR
	 * to denote a gap in the strand).
	 * 
	 * Your algorithm must run in O(n + m) time, where n is the length of x
	 * and m is the length of y. 
	 */
	private void traceback() {
		StringBuilder xBuilder = new StringBuilder(); // To improve efficiency, StringBuilder are used instead of String
		StringBuilder yBuilder = new StringBuilder();
		tracebackHelper(n, m, xBuilder, yBuilder);
		alignedX = xBuilder.reverse().toString(); // Complexity: O(n)
		alignedY = yBuilder.reverse().toString(); // Complexity: O(m)
	}

	private void tracebackHelper(int i, int j, StringBuilder xBuilder, StringBuilder yBuilder) {
		Result curr = cache[i][j];
		curr.markPath();

		switch (curr.getParent()){
		case DIAGONAL:
			xBuilder.append(x.charAt(i - 1));
			yBuilder.append(y.charAt(j - 1));
			tracebackHelper(i - 1, j - 1, xBuilder, yBuilder);
			break;
		case LEFT:
			xBuilder.append(Constants.GAP_CHAR);
			yBuilder.append(y.charAt(j - 1));
			tracebackHelper(i, j - 1, xBuilder, yBuilder);
			break;
		case UP:
			xBuilder.append(x.charAt(i - 1));
			yBuilder.append(Constants.GAP_CHAR);
			tracebackHelper(i - 1, j, xBuilder, yBuilder);
			break;
		case NONE: // Do nothing, base case
			return;
		}
	}

	/**
	 * Returns true iff these strands are seemingly aligned.
	 */
	public boolean isAligned() {
		return alignedX != null && alignedY != null &&
				alignedX.length() == alignedY.length();
	}

	/**
	 * Returns the score associated with the current alignment.
	 */
	public int getScore() {
		if (isAligned())
			return judge.score(alignedX, alignedY);
		return 0;
	}

	/**
	 * Returns a nice textual version of this alignment.
	 */
	public String toString() {
		if (!isAligned())
			return "[X=" + x + ",Y=" + y + "]";
		final char GAP_SYM = '.', MATCH_SYM = '|', MISMATCH_SYM = ':';
		StringBuilder ans = new StringBuilder();
		ans.append(alignedX).append('\n');
		int n = alignedX.length();
		for (int i = 0; i < n; i++)
			if (alignedX.charAt(i) == Constants.GAP_CHAR || alignedY.charAt(i) == Constants.GAP_CHAR)
				ans.append(GAP_SYM);
			else if (alignedX.charAt(i) == alignedY.charAt(i))
				ans.append(MATCH_SYM);
			else
				ans.append(MISMATCH_SYM);
		ans.append('\n').append(alignedY).append('\n').append("score = ").append(getScore());
		return ans.toString();
	}

	/**
	 * Returns a DNA strand of length n with randomly selected nucleotides.
	 */
	private static String randomDNA(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
			sb.append("ACGT".charAt(gen.nextInt(4)));
		return sb.toString();
	}

}
