package functions;

/**
 * Created by Adam Hayes on 9/26/2017 for project ImageProcessing.
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Greyscale {
    BufferedImage img;
    File f;

    public Greyscale(BufferedImage img) {
        this.img = img;
    }

    public BufferedImage convertToGreyscale() {
        int width = img.getWidth();
        int height = img.getHeight();

        System.out.println("Converting to Greyscale...");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixelRGB = img.getRGB(i, j);

                int alpha = (pixelRGB>>24)&0xFF;
                int red = (pixelRGB>>16)&0xFF;
                int green = (pixelRGB>>8)&0xFF;
                int blue = pixelRGB&0xFF;

                int avg = (red + green + blue)/3;

                pixelRGB = (alpha<<24) | (avg<<16) | (avg<<8) | avg;

                img.setRGB(i, j, pixelRGB);
            }
        }
        try {
            File output = new File("imgOut/outputGreyscale.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
        return img;
    }
}
