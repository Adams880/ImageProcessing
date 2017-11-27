package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Adam Hayes on 11/20/2017 for project ImageProcessing.
 */

// Course: CS4732/CS7367
// Student name: Adam Hayes
// Student ID: 000582743
// Assignment #: Test 2 Part 1
// Due Date: 11/27/2017
//
// Signature: ________________________
// (The signature means that the work is your own, not from somewhere else)
//
// Score: ______________
public class ObjectExtraction {

    private int numOfObj;
    private BufferedImage img;
    //private BufferedImage[] objects;
    private int height, width;
    private Pixel[][] pixels;
    private ArrayList<ArrayList<Pixel>> objects;

    private class Pixel {
        //basic pixel struct. Allows me to use basic 2-D array and still have x, y, and intensity values stored.
        public int x, y, value;

        public Pixel(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

    public ObjectExtraction(BufferedImage img) {
        //set up defaults for the object
        this.img = img;
        height = img.getHeight();
        width = img.getWidth();
        startUp();
    }

    private void startUp() {
        //originally only going to find the objects expected, functionality was later removed so this section does nothing
        Scanner sc = new Scanner(System.in);
        System.out.print("Please input the number of objects expected to be found: ");
        numOfObj = sc.nextInt();
        pixels = new Pixel[width][height];
        if (numOfObj < 1) {
            System.out.print("That is not a valid input, please input a valid number: ");
            numOfObj = sc.nextInt();
        }
        //sets all the values for the pixel array of the whole image, creating Pixel objects
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int intensity = img.getRGB(i, j)&0xFF;
                Pixel px = new Pixel(i, j, intensity);
                pixels[i][j] = px;
            }
        }
        objects = new ArrayList<ArrayList<Pixel>>(0); //will be adding to this and I want it to only expand when it needs to.

    }

    public void findObj() {
        //Cluster[] clusters = new Cluster[numOfObj];
        for (int i = 2; i < width - 2; i++) { //start from 2 because this uses a 5x5 neighborhood
            for (int j = 2; j < height - 2; j++) {
                if (pixels[i][j].value == 255) {
                    if (!checkAllObj(pixels[i][j])) { //if pixel is not in any object
                        int objPos = 0;
                        ArrayList<Pixel> obj = new ArrayList<Pixel>(0); //create an object
                        obj = pixelDet(pixels[i][j], obj, objPos); //use pixel det to fill new object
                        objects.add(obj); //add to objects
                    }
                }
            }
        }

        Random rand = new Random();
        //change the color of all the different objects in the img then save it
        for (int i = 0; i < objects.size(); i++) {
            ArrayList<Pixel> obj = objects.get(i);
            int red, green, blue;
            red = rand.nextInt(255);
            green = rand.nextInt(255);
            blue = rand.nextInt(255);
            int rgb = (red << 16) | (green << 8) | (blue);
            for (int j = 0; j < obj.size(); j++) {
                //System.out.println(obj.size());
                img.setRGB(obj.get(j).x, obj.get(j).y, rgb); //sets each obj to a unique color for identifying in the
                //final image
            }
        }

        try {
            File output = new File("imgOut/outputObject_Detection.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }

        //System.out.println(objects.size()); //error handling
    }

    private ArrayList<Pixel> pixelDet(Pixel pixel, ArrayList<Pixel> object, int objPos) {
        //takes in the 5x5 grid of pixels surrounding the sent pixel
        //checks the supplied object
        if (!checkObj(pixel, object)) {
            boolean loop;
            do {
                Pixel[] hits = fiveAround(pixel); //find all the white pixels in the 5x5 neighborhood
                for (int i = 0; i < hits.length; i++) {
                    if (hits.length != 0) {
                        if (!checkObj(hits[i], object)) { //if pixel is not already in the object
                            object.add(i, hits[i]);
                            objPos++; //not used anymore
                        }
                    }
                }
                if (hits.length != 0) { //had issues with null pointers
                    Random rand = new Random();
                    //System.out.println(hits.length);
                    int random = rand.nextInt(hits.length); //select a new host pixel at random from valid pixels
                    if (checkObj(hits[random], object)) { //check to see if its in the obj
                        pixel = hits[random]; //if it is, then set as new host pixel and re loop
                    } else {
                        for (int i = 0; i < hits.length; i++) {
                            if (random == hits.length) {
                                random = 0;
                            } else {
                                random++;
                            }
                            if (checkObj(hits[random], object)) { //if new random value is good host pixel
                                pixel = hits[random]; //set as new host, re loop
                                break; //break for loop
                            }
                        }
                    }
                }
                if (checkPxArr(hits, object)) { //if there are any hits in the object, re loop
                    loop = true;
                } else {
                    loop = false; //if there are no hits at all, end loop and return object
                }
            } while (loop);
        }
        return object;
    }

    private boolean checkObj(Pixel pixel, ArrayList<Pixel> object) {
        //checks the supplied object for specific pixel
        boolean check = false;
        for (int i = 0; i < object.size(); i++) {
            if (object.get(i) == pixel) {
                check = true;
            }
        }
        return check;
    }

    private boolean checkPxArr(Pixel[] hits, ArrayList<Pixel> obj) {
        //checks the supplied object against the whole array of pixels
        for (int i = 0; i < hits.length; i++) {
            for (int j = 0; j < obj.size(); j++) {
                if (hits[i] == obj.get(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkAllObj(Pixel pixel) {
        //checks all objects in the objects array against single pixel
        for (int i = 0; i < objects.size(); i++) {
            ArrayList<Pixel> pxArr = objects.get(i);
            for (int j = 0; j < pxArr.size(); j++) {
                if (pixel == pxArr.get(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Pixel[] fiveAround(Pixel px) {
        //checks all the pixels in 5x5 grid around center pixel.
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
            if (col == startCol + 4) { //if end of row, go down row and reset column
                col = startCol;
                row++;
            }
            if (row == px.x && col == px.y) { //if selector is on host pixel, skip over it.
                col++;
            }
        }

        //extracts the hits (white pixels) from the surrounding pixels
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
