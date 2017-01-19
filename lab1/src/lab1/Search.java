package lab1;

public class Search {
	
	public static int search(int[] A, int[] B){
		int m = A.length;
		int n = B.length;
		boolean forstop = false;
		
		for(int i = 0; i <= n - m && forstop == false; i++){
			boolean same = true;
			for(int j = 0; j < m; j++){
				if(A[j]!= B[i + j])
					same = false;
			}
			if(same==true){
				forstop = true;
				return i;
			}
		}

	    return -1;
	}
	
	public static void main(String[] args){
		int[] A = {1, 2, 3};
		int[] B = {2, 3, 1, 2, 3, 4};
		System.out.println(search(A, B));
	}

}
