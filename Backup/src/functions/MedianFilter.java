package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Adam Hayes on 9/25/2017 for project ImageProcessing.
 */
public class MedianFilter {
    int width, height;
    boolean isGreyscale;

    public MedianFilter(boolean isGreyscale) {
        this.isGreyscale = isGreyscale;
    }

    public void medianFilter(BufferedImage img) {
        width = img.getWidth();
        height = img.getHeight();
        int[][] pixels = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //gets blue value for pixels (grey value when in greyscale)
                pixels[i][j] = img.getRGB(i, j)&0xFF;
            }
        }

        //for a 3x3 neighborhood we need to start one row down and one col
        //across from the top left corner to assure we have a full 3x3
        //grid for the filter
        System.out.println("Applying Median Filter...");
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                //find the pixels in the 3x3 neighborhood
                //System.out.println("Test");
                int[] intensity = new int[9];
                intensity[0] = pixels[i-1][j-1];
                intensity[1] = pixels[i][j-1];
                intensity[2] = pixels[i+1][j-1];
                intensity[3] = pixels[i-1][j];
                intensity[4] = pixels[i][j];
                intensity[5] = pixels[i+1][j];
                intensity[6] = pixels[i-1][j+1];
                intensity[7] = pixels[i][j+1];
                intensity[8] = pixels[i+1][j+1];

                intensity = bubbleSort(intensity);

                /*System.out.print("Intensity Array: ");
                for (int k = 0; k < intensity.length; k++) {
                    System.out.print(intensity[k] + " ");
                }
                System.out.println();*/

                //find the middle
                int middle = intensity[4];
                int rgb = (middle << 16) | (middle << 8) | middle;
                img.setRGB(i, j, rgb);
            }
        }
        try {
            File output = new File("imgOut/outputMedianFilter.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void averagingFilter(BufferedImage img) {
        //will only work with greyscale images currently
        width = img.getWidth();
        height = img.getHeight();
        int[][] pixels = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //gets blue value for pixels (grey value when in greyscale)
                pixels[i][j] = img.getRGB(i, j)&0xFF;
            }
        }

        System.out.println("Applying Averaging Filter...");
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                //find the pixels in the 3x3 neighborhood
                //System.out.println("Test");
                int[] intensity = new int[9];
                intensity[0] = pixels[i - 1][j - 1];
                intensity[1] = pixels[i][j - 1];
                intensity[2] = pixels[i + 1][j - 1];
                intensity[3] = pixels[i - 1][j];
                intensity[4] = pixels[i][j];
                intensity[5] = pixels[i + 1][j];
                intensity[6] = pixels[i - 1][j + 1];
                intensity[7] = pixels[i][j + 1];
                intensity[8] = pixels[i + 1][j + 1];

                int sum = 0;
                for (int k = 0; k < intensity.length; k++) {
                    sum += intensity[k];
                }
                sum = sum / intensity.length;

                int rgb = (sum << 16) | (sum << 8) | sum;
                img.setRGB(i, j, rgb);
            }
        }
        try {
            File output = new File("imgOut/outputAveragingFilter.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }


    }

    private int[] bubbleSort(int[] array) {
        int n = array.length;
        int temp = 0;
        for(int i=0; i < n; i++){
            for(int j=1; j < (n-i); j++){
                if(array[j-1] > array[j]){
                    //swap elements
                    temp = array[j-1];
                    array[j-1] = array[j];
                    array[j] = temp;
                }

            }
        }
        return array;
    }
}
