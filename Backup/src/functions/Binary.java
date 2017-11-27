package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Adam Hayes on 11/21/2017 for project ImageProcessing.
 */
public class Binary {

    private BufferedImage img;
    private BufferedImage objImg;
    private int width, height;

    public Binary(BufferedImage img) {
        this.img = img;
        width = img.getWidth();
        height = img.getHeight();
    }

    public BufferedImage getImg() {
        return img;
    }

    public void binaryImage() {
        //this should be applied after the k-means algorithm, therefore there should only be 2 colors. I wanna make
        //these two colors black and white to make it easier for humans to see the color difference.

        int rgb1 = -1, rgb2 = -1; // set default values

        System.out.println("\nConverting to Binary Image...");

        //find the two colors in the image by searching each pixel until you find both colors then stop.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (rgb1 == -1) {
                    rgb1 = img.getRGB(i, j);
                }

                if (img.getRGB(i, j) != rgb1) {
                    rgb2 = img.getRGB(i, j);
                    break;
                }
            }
        }

        //if RGB1, set to black, if rgb2, set to white.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int finalRGB;
                if (img.getRGB(i, j) == rgb1) {
                    finalRGB = (0 << 16) | (0 << 8) | 0;
                    img.setRGB(i, j, finalRGB);
                } else if (img.getRGB(i, j) == rgb2) {
                    finalRGB = (255 << 16) | (255 << 8) | 255;
                    img.setRGB(i, j, finalRGB);
                }
            }
        }
        try {
            File output = new File("imgOut/outputBinary.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
