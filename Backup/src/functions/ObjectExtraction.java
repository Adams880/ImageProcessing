package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Adam Hayes on 11/24/2017 for project ImageProcessing.
 */
public class ObjectExtraction {

    private int numOfObj;
    private BufferedImage img;
    //private BufferedImage[] objects;
    private int height, width;
    private Pixel[][] pixels;
    private ArrayList<Pixel[]> objects;

    private class Pixel {
        public int x, y, value;

        public Pixel(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

    public ObjectExtraction(BufferedImage img) {
        this.img = img;
        height = img.getHeight();
        width = img.getWidth();
        startUp();
    }

    private void startUp() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please input the number of objects expected to be found: ");
        numOfObj = sc.nextInt();
        pixels = new Pixel[width][height];
        if (numOfObj < 1) {
            System.out.print("That is not a valid input, please input a valid number: ");
            numOfObj = sc.nextInt();
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int intensity = img.getRGB(i, j)&0xFF;
                Pixel px = new Pixel(i, j, intensity);
                pixels[i][j] = px;
            }
        }
        objects = new ArrayList<Pixel[]>(0);
    }

    public void findObj() {
        //Cluster[] clusters = new Cluster[numOfObj];
        for (int i = 2; i < width - 2; i++) {
            for (int j = 2; j < height - 2; j++) {
                if (pixels[i][j].value == 255) {
                    if (!checkAllObj(pixels[i][j])) {
                        int objPos = 0;
                        Pixel[] obj = new Pixel[100];
                        obj = pixelDet(pixels[i][j], obj, objPos);
                        objects.add(obj);
                    }
                }
            }
        }

        Random rand = new Random();
        //change the color of all the different objects in the img then save it
        for (int i = 0; i < objects.size(); i++) {
            Pixel[] obj = objects.get(i);
            int red, green, blue;
            red = rand.nextInt(255);
            green = rand.nextInt(255);
            blue = rand.nextInt(255);
            int rgb = (red << 16) | (green << 8) | (blue);
            for (int j = 0; j < obj.length; j++) {
                System.out.println(obj.length);
                if (obj[j].value != 0) {
                    img.setRGB(obj[j].x, obj[j].y, rgb);
                }
            }
        }

        try {
            File output = new File("imgOut/outputObject_Detection.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private Pixel[] pixelDet(Pixel pixel, Pixel[] object, int objPos) {
        //takes in the 5x5 grid of pixels surrounding the sent pixel
        //checks the supplied object
        if (!checkObj(pixel, object)) {
            Pixel[] hits = fiveAround(pixel);
            for (int i = 0; i < hits.length; i++) {
                if (!checkObj(hits[i], object)) {
                    object[objPos] = hits[i];
                    objPos++;
                }
            }
            if (hits.length != 0) {
                Random rand = new Random();
                //System.out.println(hits.length);
                int random = rand.nextInt(hits.length);
                if (checkObj(hits[random], object)) {
                    pixelDet(hits[random], object, objPos);
                } else {
                    for (int i = 0; i < hits.length; i++) {
                        if (random == hits.length) {
                            random = 0;
                        } else {
                            random++;
                        }
                        if (checkObj(hits[random], object)) {
                            pixelDet(hits[random], object, objPos);
                            break;
                        }
                    }
                }
            }
        }
        return object;
    }

    private boolean checkObj(Pixel pixel, Pixel[] object) {
        boolean check = false;
        for (int i = 0; i < object.length; i++) {
            if (object[i] == pixel) {
                check = true;
            }
        }
        return check;
    }

    private boolean checkAllObj(Pixel pixel) {
        for (int i = 0; i < objects.size(); i++) {
            Pixel[] pxArr = objects.get(i);
            for (int j = 0; j < pxArr.length; j++) {
                if (pixel == pxArr[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private Pixel[] fiveAround(Pixel px) {
        Pixel[] pxs = new Pixel[24];
        int startRow, startCol;
        int row, col;
        startRow = px.x - 2;
        startCol = px.y - 2;
        row = startRow;
        col = startCol;
        int hitCount = 0;
        for (int i = 0; i < pxs.length; i++) {
            if (pixels[row][col].value == 255) {
                hitCount++;
                pxs[i] = pixels[row][col];
            } else {
                pxs[i] = pixels[row][col];
            }
            if (col == startCol + 4) {
                col = startCol;
                row++;
            }
            if (row == px.x && col == px.y) {
                col++;
            }
        }

        Pixel[] hits = new Pixel[hitCount];
        int hitI = 0;
        for (int i = 0; i < pxs.length; i++) {
            if (pxs[i].value == 255) {
                hits[hitI] = pxs[i];
                hitI++;
            }
        }
        return hits;
    }

    /*private class Cluster {
        ArrayList x;
        ArrayList y;
        int centX;
        int centY;
        int xIterator = 0;
        int yIterator = 0;

        public Cluster(int centX, int centY){
            this.centX = centX;
            this.centY = centY;
            x = new ArrayList();
            y = new ArrayList();
        }

        public void addPixel(int xCo, int yCo) {
            x.add(xIterator, xCo);
            y.add(yIterator, yCo);
            xIterator++;
            yIterator++;
        }

        public void clear() {
            x.clear();
            y.clear();
        }

        public float distance(int xCo, int yCo) {
            float xDist, yDist;
            float distance;

            xDist = Math.abs((centX - xCo));
            yDist = Math.abs((centY - yCo));

            distance = (xDist + yDist) / 2;
            return distance;
        }
    }*/
}
