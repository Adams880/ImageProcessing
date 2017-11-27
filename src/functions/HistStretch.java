package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


/**
 * Created by Adam Hayes on 11/13/2017 for project ImageProcessing.
 */
public class HistStretch {

    private BufferedImage img;
    private int min, max;
    private int width, height;
    private int[] intensity;
    private boolean isGreyscale;

    public HistStretch (BufferedImage img, boolean isGreyscale) {
        this.img = img;
        this.isGreyscale = isGreyscale;
        width = img.getWidth();
        height = img.getHeight();
        startUp();
    }

    private void startUp() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nPlease input a minimum pixel intensity value: ");
        min = sc.nextInt();
        System.out.print("\nPlease input a maximum pixel intensity value: ");
        max = sc.nextInt();
    }

    public BufferedImage getImg() {
        return img;
    }

    public void stretch() {
        //create an array to hold all the intesity values for the image
        //because image is greyscale &0xFF after the img.getRGB() function
        //will return only the grey value of that pixel. Intesity[] will hold
        //every pixel value in an array.
        intensity = new int[width * height];
        int iterator = 0;
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                intensity[iterator] = (img.getRGB(i, j)&0xFF);
                iterator++;
            }
        }

        for(int i = 0; i < intensity.length; i++) {
            //System.out.println(intensity[i]);
            int pixelInten = intensity[i];
            //System.out.println("Initial Intensity: " + pixelInten);
            if (pixelInten <= min) {
                //System.out.println("Min Trigger");
                pixelInten = 0;
            } else if (pixelInten >= max) {
                //System.out.println("Max Trigger");
                pixelInten = 255;
            }
            if (pixelInten > min && pixelInten < max) {
                pixelInten = (int) (1.0 * (pixelInten - min)/(max - min) * 255);
            }
            //System.out.println("Final Intensity: " + pixelInten);
            intensity[i] = pixelInten;
        }

        int iteratorOut = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = (intensity[iteratorOut] << 16) | (intensity[iteratorOut] << 8) | intensity[iteratorOut];
                img.setRGB(i, j, rgb);
                iteratorOut++;
            }
        }

        try {
            File output = new File("imgOut/outputLinearStretch.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
