import java.awt.Color;

/**
 * TODO
 * @ Chuck Jia
 */

public class Driver {
  
  private static int numCollisions;
  private static double rehashThreshold = 0.49;
  private static int collisionStrategy = Constants.LINEAR;
  
  /**
   * TODO
   * 
   * Return the ColorTable associated with this image, assuming the color key space
   * is restricted to bitsPerChannel. Increment numCollisions after each increment.
   */
  public static ColorTable vectorize(Image image, int bitsPerChannel) {
	  int w = image.getWidth();
	  int h = image.getHeight();
	  ColorTable table = new ColorTable(3, bitsPerChannel, collisionStrategy, rehashThreshold);

	  for(int i = 0; i < w; i++){
		  for(int j = 0; j < h; j++){
			  table.increment(image.getColor(i, j));
		  }
	  }
	  return table;
  }

  /**
   * TODO
   * 
   * Return the result of running Util.cosineSimilarity() on the vectorized images.
   * 
   * Note: If you compute the similarity of an image with itself, it should be close to 1.0.
   */
  public static double similarity(Image image1, Image image2, int bitsPerChannel) {
	  ColorTable A = vectorize(image1, bitsPerChannel);
	  ColorTable B = vectorize(image2, bitsPerChannel);

	  return Util.cosineSimilarity(A, B);
  }

  /**
   * Uses the Painting images and all 8 bitsPerChannel values to compute and print 
   * out a table of collision counts.
   */
  public static void allPairsTest() {
    Painting[] paintings = Painting.values();
    int n = paintings.length;
    for (int y = 0; y < n; y++) {
      for (int x = y + 1; x < n; x++) {
        System.out.println(paintings[y].get().getName() + 
            " and " + 
            paintings[x].get().getName() + ":");
        for (int bitsPerChannel = 1; bitsPerChannel <= 8; bitsPerChannel++) {
          numCollisions = 0;
          System.out.println(String.format("   %d: %.2f %d", 
              bitsPerChannel,
              similarity(paintings[x].get(), paintings[y].get(), bitsPerChannel),
              numCollisions));
        }
        System.out.println();
      }
    }
  }

  /**
   * Simple testing
   */  
  public static void main(String[] args) {
    System.out.println(Constants.TITLE);
    Image mona = Painting.MONA_LISA.get();
    Image starry = Painting.STARRY_NIGHT.get();
    Image christina = Painting.CHRISTINAS_WORLD.get();
    System.out.println("It looks like all three test images were successfully loaded.");
    System.out.println("mona's dimensions are " + 
        mona.getWidth() + " x " + mona.getHeight());
    System.out.println("starry's dimenstions are " + 
        starry.getWidth() + " x " + starry.getHeight());
    System.out.println("christina's dimensions are " + 
        christina.getWidth() + " x " + christina.getHeight());
    
    System.out.println("");
    // Calculating the number of black pixels 
    ColorTable tableMona = vectorize(mona,2);
    ColorTable tableStarry = vectorize(starry,2);    
    System.out.println("Count of black pixels in Davinci's Mona Lisa using " + tableMona.getBitsPerChannel() + " bits per channel is: " + tableMona.get(Color.BLACK));
    System.out.println("Count of black pixels in Van Gogh's Starry Night using " + tableMona.getBitsPerChannel() + " bits per channel is: " + tableStarry.get(Color.BLACK));
    System.out.println("");
    
    // allPairsTest();
    
    class ImageArrElem{
    	public String name;
    	public Image image;
    	
    	ImageArrElem(String name, Image image){
    		this.name = name;
    		this.image = image;
    	}
    }
    
    ImageArrElem[] imageArr = new ImageArrElem[9];
    String[] imageNames = {"cezanne", "davinci", "degas", "homer", "kahlo", "picasso", "renoir", "vangogh", "wyeth"};
    
    for(int i = 0; i < 9; i++){
    	Image tempImage = new Image(Constants.IMAGE_DIR + "/" + imageNames[i] + ".jpg");
        imageArr[i] = new ImageArrElem(imageNames[i], tempImage);
    } 
    
    /* Testing
     * 
      for(int i = 0; i < 9; i++){
    	  System.out.println(i + ". " + imageArr[i].name + ": " + imageArr[i].image.getWidth() + "x" + imageArr[i].image.getHeight());
      }
    */
	
    int thisBitsPerChannel = 4;
    int count = 0;
    double max = 0;
    int maxi = 0;
    int maxj = 0;
    
    for(int i = 0; i <= 7; i++){
    	for(int j = i + 1; j <= 8; j++){
    		double temp = similarity(imageArr[i].image, imageArr[j].image, thisBitsPerChannel);
    		count++;
    		if(temp > max){
    			max = temp;
    			maxi = i;
    			maxj = j;
    		}
    		System.out.println(count + ". " + "Similarity between " + imageArr[i].name + " and " + imageArr[j].name + " is " + temp);
    	}
    }
    System.out.println("The most similar pair is " + imageArr[maxi].name + " and " + imageArr[maxj].name + " with similarity " + max);
    
  }
}
