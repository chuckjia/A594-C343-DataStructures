import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class Testing {
	private static Random random = new Random();
	private static String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";

	/*
	 * My Own Tests
	 */
	@Test
	public void testMatchNaive(){
		Result res;
		String pattern, text;

		// Test 1 : Empty strings
		pattern = "";
		text = "";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(0, res.comps);

		// Test 2 : Empty pattern
		pattern = "";
		text = "ABDdmodpad";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(0, res.comps);

		// Test 3 : Empty text
		pattern = "alkdsa";
		text = "";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(-1, res.pos);
		assertEquals(0, res.comps);

		// Test 4 : Pattern longer than text
		pattern = "INDDAFS";
		text = "ABDDS";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(-1, res.pos);
		assertEquals(0, res.comps);

		// Test 5 : Pattern is not found
		pattern = "Idsa";
		text = "asdfasdfasdfas";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(-1, res.pos);
		assertEquals(text.length() - pattern.length() + 1, res.comps);

		// Test 6 : Small cases: 1 character
		pattern = "I";
		text = "I";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(1, res.comps);

		// Test 7 : Small cases: 2 characters
		pattern = "In";
		text = "In";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(2, res.comps);

		// Test 8 : Small cases: 3 characters
		pattern = "Ins";
		text = "yesIns";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(3, res.pos);

		// Test 9 : Medium size
		pattern = "great";
		text = "This has been a great day";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(16, res.pos);

		// Test 10 : Medium size with non-alphabets
		pattern = "great";
		text = "Hey! Hello? This has been a great day!";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(28, res.pos);

		// Test 11 : Medium size with repeat
		pattern = "great";
		text = "This has been a great great day";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(16, res.pos);

		// Test 12 : Medium size with partial repeat
		pattern = "great";
		text = "This has been a grea great day";
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(21, res.pos);

		// Test 13 : Large size
		pattern = "hello";
		StringBuilder textBuilder = new StringBuilder();
		for (int i = 0; i < 50; i++)
			textBuilder.append("abdedisp");
		textBuilder.append(pattern);
		for (int i = 0; i < 50; i++)
			textBuilder.append("qwmoded");
		text = textBuilder.toString();
		res = StringMatch.matchNaive(pattern, text);
		assertEquals(400, res.pos);
	}


	@Test
	public void testBuildKMP(){
		String pattern, text;
		int[] flink;

		// Test 1 : Empty strings
		pattern = "";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1 }));

		// Test 2 : Small strings
		pattern = "A";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0 }));

		// Test 3 : Small strings with repeat
		pattern = "AA";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 1 }));

		// Test 4 : Small strings with repeat
		pattern = "AAA";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 1, 2 }));

		// Test 5 : Small strings
		pattern = "AABC";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 1, 0, 0 }));

		// Test 6 : Medium sized strings
		pattern = "ABAAB";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 0, 1, 1, 2 }));

		// Test 7 : Medium sized strings: partial repeat
		pattern = "ABCDABD";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 0, 0, 0, 1, 2, 0 }));

		// Test 8 : Medium sized strings: general 
		pattern = "ABACABABC";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 0, 1, 0, 1, 2, 3, 2, 0 }));

		// Test 9 : Larger sized strings
		pattern = "PARTICIPATE IN PARACHUTE";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		assertTrue(Arrays.equals(flink, new int[] { -1, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 0, 0 }));
	}

	@Test
	public void testDelta1(){
		String pattern;
		int[] delta1;

		// Test 1: Small string
		pattern = "a";
		delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		assertEquals(delta1['a'], 0);

		// Test 2: Small string
		pattern = "ab";
		delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		assertEquals(delta1['a'], 1);
		assertEquals(delta1['b'], 0);

		// Test 3: Small string with repeats
		pattern = "aba";
		delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		assertEquals(delta1['a'], 0);
		assertEquals(delta1['b'], 1);

		// Test 4: Small string with repeats
		pattern = "abac";
		delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		assertEquals(delta1['a'], 1);
		assertEquals(delta1['b'], 2);
		assertEquals(delta1['c'], 0);

		// Test 5: Medium size string
		pattern = "abacbacd";
		delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		assertEquals(delta1['a'], 2);
		assertEquals(delta1['b'], 3);
		assertEquals(delta1['c'], 1);
		assertEquals(delta1['d'], 0);

		// Test 6: Medium size string with lots of repeats
		pattern = "abacbacdabcdabcd";
		delta1 = new int[Constants.SIGMA_SIZE];
		StringMatch.buildDelta1(pattern, delta1);
		assertEquals(delta1['a'], 3);
		assertEquals(delta1['b'], 2);
		assertEquals(delta1['c'], 1);
		assertEquals(delta1['d'], 0);
	}

	@Test
	public void testRunKMP(){
		Result res;
		String pattern, text;
		int[] flink; 

		// Test 1 : Empty strings
		pattern = "";
		text = "";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(0, res.pos);
		assertEquals(0, res.comps);

		// Test 2 : Empty pattern
		pattern = "";
		text = "ABDdmodpad";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(0, res.pos);
		assertEquals(0, res.comps);

		// Test 3 : Empty text
		pattern = "alkdsa";
		text = "";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(-1, res.pos);
		assertEquals(0, res.comps);

		// Test 4 : Pattern longer than text
		pattern = "INDDAFS";
		text = "ABDDS";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(-1, res.pos);

		// Test 5 : Pattern is not found
		pattern = "Idsa";
		text = "asdfasdfasdfas";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(-1, res.pos);

		// Test 6 : Small cases: 1 character
		pattern = "I";
		text = "I";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(0, res.pos);
		assertEquals(1, res.comps);

		// Test 7 : Small cases: 2 characters
		pattern = "In";
		text = "In";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(0, res.pos);
		assertEquals(2, res.comps);

		// Test 8 : Small cases: 3 characters
		pattern = "Ins";
		text = "yesIns";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(3, res.pos);

		// Test 9 : Medium size
		pattern = "great";
		text = "This has been a great day";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(16, res.pos);

		// Test 10 : Medium size with non-alphabets
		pattern = "great";
		text = "Hey! Hello? This has been a great day!";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(28, res.pos);

		// Test 11 : Medium size with repeat
		pattern = "great";
		text = "This has been a great great day";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(16, res.pos);

		// Test 12 : Medium size with partial repeat
		pattern = "great";
		text = "This has been a grea great day";
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(21, res.pos);

		// Test 13 : Large size
		pattern = "hello";
		StringBuilder textBuilder = new StringBuilder();
		for (int i = 0; i < 50; i++)
			textBuilder.append("abdedisp");
		textBuilder.append(pattern);
		for (int i = 0; i < 50; i++)
			textBuilder.append("qwmoded");
		text = textBuilder.toString();
		flink = new int[pattern.length() + 1];
		StringMatch.buildKMP(pattern, flink);
		res = StringMatch.runKMP(pattern, text, flink);
		assertEquals(400, res.pos);
	}



	@Test
	public void testRunBoyerMoore(){
		Result res;
		String pattern, text;
		int[] flink; 

		// Test 1 : Empty strings
		pattern = "";
		text = "";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(0, res.comps);

		// Test 2 : Empty pattern
		pattern = "";
		text = "ABDdmodpad";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(0, res.comps);

		// Test 3 : Empty text
		pattern = "alkdsa";
		text = "";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(-1, res.pos);
		assertEquals(0, res.comps);

		// Test 4 : Pattern longer than text
		pattern = "INDDAFS";
		text = "ABDDS";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(-1, res.pos);

		// Test 5 : Pattern is not found
		pattern = "Idsa";
		text = "asdfasdfasdfas";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(-1, res.pos);

		// Test 6 : Small cases: 1 character
		pattern = "I";
		text = "I";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(1, res.comps);

		// Test 7 : Small cases: 2 characters
		pattern = "In";
		text = "In";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(0, res.pos);
		assertEquals(2, res.comps);

		// Test 8 : Small cases: 3 characters
		pattern = "Ins";
		text = "yesIns";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(3, res.pos);

		// Test 9 : Medium size
		pattern = "great";
		text = "This has been a great day";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(16, res.pos);

		// Test 10 : Medium size with non-alphabets
		pattern = "great";
		text = "Hey! Hello? This has been a great day!";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(28, res.pos);

		// Test 11 : Medium size with repeat
		pattern = "great";
		text = "This has been a great great day";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(16, res.pos);

		// Test 12 : Medium size with partial repeat
		pattern = "great";
		text = "This has been a grea great day";
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(21, res.pos);

		// Test 13 : Large size
		pattern = "hello";
		StringBuilder textBuilder = new StringBuilder();
		for (int i = 0; i < 50; i++)
			textBuilder.append("abdedisp");
		textBuilder.append(pattern);
		for (int i = 0; i < 50; i++)
			textBuilder.append("qwmoded");
		text = textBuilder.toString();
		res = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(400, res.pos);
	}


	/*
	 * Tests Given
	 */

	@Test 
	public void testEmpty() {
		System.out.println("testEmpty");
		match("", "");
		match("", "ab");
		System.out.println();
	}

	@Test 
	public void testOneChar() {
		System.out.println("testOneChar");
		match("a", "a");
		match("a", "b");
		System.out.println();
	}

	@Test 
	public void testRepeat() {
		System.out.println("testRepeat");
		match("aaa", "aaaaa");
		match("aaa", "abaaba");
		match("abab", "abacababc");
		match("abab", "babacaba");
		System.out.println();
	}

	@Test 
	public void testPartialRepeat() {
		System.out.println("testPartialRepeat");
		match("aaacaaaaac", "aaacacaacaaacaaaacaaaaac");
		match("ababcababdabababcababdaba", "ababcababdabababcababdaba");
		System.out.println();
	}

	@Test 
	public void testRandomly() {
		System.out.println("testRandomly");
		for (int i = 0; i < 100; i++) {
			String pattern = makeRandomPattern();
			for (int j = 0; j < 100; j++) {
				String text = makeRandomText(pattern);
				match(pattern, text);
			}
		}
		System.out.println();
	}

	/*
	 * Tests From Piaaza
	 */

	@Test 
	public void smallMachines() {
		String[] pats = new String[] {
				"",
				"A",
				"AB",
				"AA",
				"AAAA",
				"BAAA",
				"AAAB",
				"AAAC",
				"ABAB",
				"ABCD",
				"ABBA",
				"AABC",
				"ABAAB",
				"AABAACAABABA",
				"ABRACADABRA",
		};
		int[][] flinks = new int[][] {
			{ -1 },
			{ -1, 0 },
			{ -1, 0, 0 },
			{ -1, 0, 1 },
			{ -1, 0, 1, 2, 3 },
			{ -1, 0, 0, 0, 0 },
			{ -1, 0, 1, 2, 0 },
			{ -1, 0, 1, 2, 0 },
			{ -1, 0, 0, 1, 2 },
			{ -1, 0, 0, 0, 0 },
			{ -1, 0, 0, 0, 1 },
			{ -1, 0, 1, 0, 0 },
			{ -1, 0, 0, 1, 1, 2 },
			{ -1, 0, 1, 0, 1, 2, 0, 1, 2, 3, 4, 0, 1 },
			{ -1, 0, 0, 0, 1, 0, 1, 0, 1, 2, 3, 4 },
		};
		int[] comps = new int[] { 0, 0, 1, 1, 3, 3, 5, 5, 3, 3, 3, 4, 5, 16, 12 };
		int i = 0;
		for (String pat : pats) {
			int[] flink = new int[pat.length() + 1];
			assertEquals(comps[i], StringMatch.buildKMP(pat, flink));
			assertArrayEquals(flinks[i], flink);
			i++;
		}
	}

	@Test
	public void lec13bKMP() {
		String[] pats = new String[] {
				"AABC",
				"ABCDE",
				"AABAACAABABA",
				"ABRACADABRA",
		};
		int[][] flinks = new int[][] {
			{ -1, 0, 1, 0, 0 },
			{ -1, 0, 0, 0, 0, 0 },
			{ -1, 0, 1, 0, 1, 2, 0, 1, 2, 3, 4, 0, 1 },
			{ -1, 0, 0, 0, 1, 0, 1, 0, 1, 2, 3, 4 },
		};
		String text = "AAAAAABRACADABAAAAAAAAAAAAAAAAAAAAAAABCAAAAAAAAAAABAABAAAAAAAAAAAAAAA";
		Result results[] = new Result[] {
				new Result(35, 68),
				new Result(-1, 128),
				new Result(-1, 123),
				new Result(-1, 126),
		};
		int i = 0;
		for (String pat : pats) {
			Result res = StringMatch.runKMP(pat, text, flinks[i]);
			assertEquals(results[i].pos, res.pos);
			assertEquals(results[i].comps, res.comps);
			i++;
		}
	}

	/* Helper functions */

	private static String makeRandomPattern() {
		StringBuilder sb = new StringBuilder();
		int steps = random.nextInt(10) + 1;
		for (int i = 0; i < steps; i++) {
			if (sb.length() == 0 || random.nextBoolean()) {  // Add literal
				int len = random.nextInt(5) + 1;
				for (int j = 0; j < len; j++)
					sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
			} 
			else {  // Repeat prefix
				int len = random.nextInt(sb.length()) + 1;
				int reps = random.nextInt(3) + 1;
				if (sb.length() + len * reps > 1000)
					break;
				for (int j = 0; j < reps; j++)
					sb.append(sb.substring(0, len));
			}
		}
		return sb.toString();
	}

	private static String makeRandomText(String pattern) {
		StringBuilder sb = new StringBuilder();
		int steps = random.nextInt(100);
		for (int i = 0; i < steps && sb.length() < 10000; i++) {
			if (random.nextDouble() < 0.7) {  // Add prefix of pattern
				int len = random.nextInt(pattern.length()) + 1;
				sb.append(pattern.substring(0, len));
			} 
			else {  // Add literal
				int len = random.nextInt(30) + 1;
				for (int j = 0; j < len; j++)
					sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
			}
		}
		return sb.toString();
	}

	private static void match(String pattern, String text) {
		// run all three algorithms and test for correctness
		Result ansNaive = StringMatch.matchNaive(pattern, text);
		int expected = text.indexOf(pattern);
		assertEquals(expected, ansNaive.pos);

		Result ansKMP = StringMatch.matchKMP(pattern, text);
		assertEquals(expected, ansKMP.pos);


		Result ansBoyerMoore = StringMatch.matchBoyerMoore(pattern, text);
		assertEquals(expected, ansBoyerMoore.pos);
		System.out.println(String.format("%5d %5d %5d : %s", 
				ansNaive.comps, ansKMP.comps, ansBoyerMoore.comps,
				(ansNaive.comps < ansKMP.comps && ansNaive.comps < ansBoyerMoore.comps) ?
						"Naive" :
							(ansKMP.comps < ansNaive.comps && ansKMP.comps < ansBoyerMoore.comps) ?
									"KMP" : "Boyer-Moore"));

	}
}
