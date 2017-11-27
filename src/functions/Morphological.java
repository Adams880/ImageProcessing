package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Adam Hayes on 11/21/2017 for project ImageProcessing.
 */
public class Morphological {

    private BufferedImage img;
    private int width, height;
    private int[] erosion, dilation;
    private int[][] pixels;

    public Morphological(BufferedImage img) {
        this.img = img;
        width = img.getWidth();
        height = img.getHeight();
        pixels = new int[width][height];
        fillPixels();

        erosion = new int[]
                {1, 1, 1,
                 1, 1, 1,
                 1, 1, 1};
        dilation = new int[]
                {0, 0, 0,
                 0, 0, 0,
                 0, 0, 0};

    }

    public BufferedImage getImg() {
        return img;
    }

    public void Erode() {

        System.out.println("\nEroding Image...");
        for (int i = 1; i < width - 1; i++) { //cannot use outside edge with 3x3 mask
            for (int j = 1; j < height - 1; j++) {
                int[] neigh = new int[9];
                boolean hit;

                neigh[0] = pixels[i-1][j-1];
                neigh[1] = pixels[i][j-1];
                neigh[2] = pixels[i+1][j-1];
                neigh[3] = pixels[i-1][j];
                neigh[4] = pixels[i][j];
                neigh[5] = pixels[i+1][j];
                neigh[6] = pixels[i-1][j+1];
                neigh[7] = pixels[i][j+1];
                neigh[8] = pixels[i+1][j+1];

                hit = hit(erosion, neigh);

                if (!hit) {
                    int rgb = (0 << 16) | (0 << 8) | 0;
                    img.setRGB(i, j, rgb);
                } else {
                    int rgb = (255 << 16) | (255 << 8) | 255;
                    img.setRGB(i, j, rgb);
                }
            }
        }
        try {
            File output = new File("imgOut/outputErosion.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void Dilate() {
        System.out.println("\nDilating Image...");
        for (int i = 1; i < width - 1; i++) { //cannot use outside edge with 3x3 mask
            for (int j = 1; j < height - 1; j++) {
                int[] neigh = new int[9];
                boolean hit;

                neigh[0] = pixels[i-1][j-1];
                neigh[1] = pixels[i][j-1];
                neigh[2] = pixels[i+1][j-1];
                neigh[3] = pixels[i-1][j];
                neigh[4] = pixels[i][j];
                neigh[5] = pixels[i+1][j];
                neigh[6] = pixels[i-1][j+1];
                neigh[7] = pixels[i][j+1];
                neigh[8] = pixels[i+1][j+1];

                hit = hit(dilation, neigh);

                if (!hit) {
                    int rgb = (255 << 16) | (255 << 8) | 255;
                    img.setRGB(i, j, rgb);
                } else {
                    int rgb = (0 << 16) | (0 << 8) | 0;
                    img.setRGB(i, j, rgb);
                }
            }
        }
        try {
            File output = new File("imgOut/outputDilation.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void Open() {
        //erosion followed by dilation
        Erode();
        Dilate();

        try {
            File output = new File("imgOut/outputOpening.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void Close() {
        //dilation followed by erosion
        Dilate();
        Erode();

        try {
            File output = new File("imgOut/outputClosing.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private boolean hit(int[] mask, int[] neigh) {
        boolean hit = true;

        for (int i = 0; i < mask.length; i++) {
            if (mask[i] == 1) {
                if (neigh[i] == 255) {
                    hit = true;
                } else {
                    hit = false;
                    break;
                }
            } else if (mask[i] == 0) {
                if (neigh[i] == 0) {
                    hit = true;
                } else {
                    hit = false;
                    break;
                }
            }
        }

        return hit;
    }

    private void fillPixels() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //gets blue value for pixels (grey value when in greyscale)
                pixels[i][j] = img.getRGB(i, j)&0xFF;
            }
        }
    }
}
