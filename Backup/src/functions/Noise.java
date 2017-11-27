package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Adam Hayes on 9/25/2017 for project ImageProcessing.
 */
public class Noise {
    BufferedImage img;
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();
    boolean isGreyscale;
    int noisePercent;
    int height, width;

    public Noise () {

    }
    public Noise(BufferedImage img, boolean isGreyscale) {
        this.img = img;
        this.isGreyscale = isGreyscale;
    }

    public BufferedImage addNoise() {
        width = img.getWidth();
        height = img.getHeight();
        System.out.print("Please input a percent of image to have noise (0 - 100): ");
        noisePercent = sc.nextInt();

        System.out.println("Adding Noise...");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int randomNum = rand.nextInt(100);
                if (randomNum < noisePercent && isGreyscale) {
                    int newValue = rand.nextInt(255);
                    int rgb = (newValue << 16) | (newValue << 8) | newValue;
                    img.setRGB(i, j, rgb);
                } else if (randomNum < noisePercent && !isGreyscale) {
                    int newR = rand.nextInt(255);
                    int newG = rand.nextInt(255);
                    int newB = rand.nextInt(255);
                    int rgb = (newR << 16) | (newG << 8) | newB;
                    img.setRGB(i, j, rgb);
                }
            }
        }
        try {
            File output = new File("imgOut/outputNoise.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
        return img;
    }
}
