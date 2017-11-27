package main;

import functions.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


/**
 * Created by Adam Hayes on 9/25/2017 for project ImageProcessing.
 */
public class ClassHub {
    public static void main(String[] args) {
        BufferedImage img = null;
        File input;
        Greyscale g;
        Noise noise;
        MedianFilter mFilter;
        K_means kMeans;
        HistStretch hStretch;
        Morphological morph;
        ObjectExtraction objExt;
        String path;
        Scanner sc = new Scanner(System.in);
        boolean finished = false;
        boolean isGreyscale = false;
        boolean imgError;


        do {
            imgError = false;
            System.out.print("Please input the file path of the image you want to use: ");
            path = sc.nextLine();

            try {
                input = new File(path);
                img = ImageIO.read(input);
            } catch (IOException e) {
                System.out.println("Image Read Error: ");
                imgError = true;
            }
        } while (imgError);


        while(!finished) {
            int choice;
            System.out.println("\nPlease select an option below using (1 - 12)");

            System.out.println("\n    1. Convert to Greyscale\n    2. Add noise\n" +
                    "    3. Median Filter\n    4. K-Means Algorithm\n " +
                    "   5. Binary Image\n    6. Linear Histogram Stretch" +
                    "\n    7. Erosion\n    8. Dilation\n    9. Opening\n    10. Closing\n" +
                    "    11. Obj Extraction\n    12. Finish\n");

            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    g = new Greyscale(img);
                    img = g.convertToGreyscale();
                    isGreyscale = true;
                    break;
                case 2:
                    noise = new Noise(img, isGreyscale);
                    img = noise.addNoise();
                    break;
                case 3:
                    mFilter = new MedianFilter(isGreyscale);
                    mFilter.medianFilter(img);
                    mFilter.averagingFilter(img);
                    break;
                case 4:
                    kMeans = new K_means(img, isGreyscale);
                    kMeans.calculate();
                    System.out.print("Would you like to use this image for future calculations? y/n ");
                    String kChoice = sc.nextLine();
                    if (kChoice.equalsIgnoreCase("y"))
                        img = kMeans.getImg();
                    break;
                case 5:
                    Binary obj = new Binary(img);
                    obj.binaryImage();
                    img = obj.getImg();
                    break;
                case 6:
                    hStretch = new HistStretch(img, isGreyscale);
                    hStretch.stretch();
                    System.out.print("Would you like to use this image for future calculations? y/n ");
                    String hChoice = sc.nextLine();
                    if (hChoice.equalsIgnoreCase("y"))
                        img = hStretch.getImg();
                    break;
                case 7:
                    morph = new Morphological(img);
                    morph.Erode();
                    img = morph.getImg();
                    break;
                case 8:
                    morph = new Morphological(img);
                    morph.Dilate();
                    img = morph.getImg();
                    break;
                case 9:
                    morph = new Morphological(img);
                    morph.Open();
                    img = morph.getImg();
                    break;
                case 10:
                    morph = new Morphological(img);
                    morph.Close();
                    img = morph.getImg();
                    break;
                case 11:
                    objExt = new ObjectExtraction(img);
                    objExt.findObj();
                case 12:
                    finished = true;
            }
        }

        /*System.out.print("Would you like to convert to greyscale? y/n ");
        String grey = sc.nextLine();
        if (grey.equals("y")) {
            g = new Greyscale(img);
            img = g.convertToGreyscale();
        }

        //noise = new Noise(img, true);
        //img = noise.addNoise();

        //mFilter = new MedianFilter(true);
        //mFilter.medianFilter(img);
        //mFilter.averagingFilter(img);

        kMeans = new K_means(img);
        kMeans.calculate();*/
    }
}
