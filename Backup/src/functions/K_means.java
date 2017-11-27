package functions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

// Course: CS4732/CS7367
// Student name: Adam Hayes
// Student ID: 000582743
// Assignment #: #3
// Due Date: 10/30/2017
//
// Signature: ________________________
// (The signature means that the work is your own, not from somewhere else)
// Score: ______________

//Jussi. “Image Segmentation with K-Means Algorithm - Java Implementation.” Popscan, 16 June. 2013, popscan.blogspot.com/2013/06/image-segmentation-with-k-means.html.

public class K_means {

    private int k;
    private BufferedImage img;
    private BufferedImage returnImg;
    private int width, height;
    private boolean isGreyscale;

    public K_means(BufferedImage img, boolean isGreyscale) {
        this.img = img;
        this.isGreyscale = isGreyscale;
        startUp();
    }

    private void startUp() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nPlease input the number of clusters you would like to use (Integer): ");
        k = sc.nextInt();
    }

    public BufferedImage getImg() {
        return img;
    }

    public void calculate() {
        //currently must use GREYSCALE image
        width = img.getWidth();
        height = img.getHeight();
        //create lookup for pixel clusters
        int[][] lut = new int[width][height]; //store the cluster values for each pixel
        lut = fillArray(lut); //fills with -1 so all pixels are unassigned to clusters
        Cluster[] clusters = new Cluster[k]; //create array of k clusters
        clusters = createClusters(clusters); //initalize clusters
        boolean pixelChanged = true; //start looping
        int numOfLoops = 0; //tracks num of loops

        System.out.println("\nApplying K Means Clustering...");

        //printClusters(clusters);

        while (pixelChanged) {
            pixelChanged = false; //if no changes stop looping
            numOfLoops++;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixelInten;
                    if (isGreyscale) {
                        pixelInten = img.getRGB(i, j) & 0xFF; //grabs blue value, for greyscale is same as red and green values
                    } else {
                        pixelInten = img.getRGB(i, j);
                    }
                    Cluster cluster = findMinCluster(clusters, pixelInten);
                    if (lut[i][j] != cluster.getClusterID()) {
                        //pixel has changed clusters
                        pixelChanged = true; //keep looping
                        lut[i][j] = cluster.getClusterID(); //assign pixel to that cluster
                    }
                }
            }

            for (int i = 0; i < clusters.length; i++) {
                //clear out old clusters of prev values
                clusters[i].clear();
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    //update clusters with all pixel values to find new center
                    int pixelInten;
                    if (isGreyscale) {
                        pixelInten = img.getRGB(i, j)&0xFF;
                    } else {
                        pixelInten = img.getRGB(i, j);
                    }
                    clusters[lut[i][j]].addPixel(pixelInten); //add pixel automatically calculates new pixel centers
                }
            }
            //printClusters(clusters);
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb;
                int pixelInten = clusters[lut[i][j]].getIntensity();
                if (isGreyscale) {
                    rgb = (pixelInten << 16) | (pixelInten << 8) | pixelInten;
                } else {
                    rgb = pixelInten;
                }

                img.setRGB(i, j, rgb);
            }
        }

        try {
            File output = new File("imgOut/outputK_Means.png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Cluster[] createClusters(Cluster[] clusters) {
        Random rand = new Random();
        int random;

        if (isGreyscale) {
            for (int i = 0; i < clusters.length; i++) {
                //create a random intensity from 0 to 255
                random = rand.nextInt(255);
                //assign clusters their starting intensity and their ID
                clusters[i] = new Cluster(i, random);
            }
        } else {
            int r, g, b;
            for (int i = 0; i < clusters.length; i++) {
                int randX, randY;
                randX = rand.nextInt(width);
                randY = rand.nextInt(height);
                System.out.println("Initial Cluster " + i + ": " + randX + ", " + randY);
                int rgb = img.getRGB(randX, randY);
                clusters[i] = new Cluster(i, rgb);
            }
        }

        return clusters;
    }

    private void printClusters(Cluster[] clusters) {
        for (int i = 0; i < clusters.length; i++) {
            System.out.println("Cluster " + i + ": " + clusters[i].getIntensity());
        }
    }

    public Cluster findMinCluster(Cluster[] clusters, int pixelInten) {
        int minDist = 10000;
        Cluster closestCluster = null;

        for (int i = 0; i < clusters.length; i++) {
            int dist = clusters[i].distance(pixelInten);

            if (dist < minDist) {
                minDist = dist;
                closestCluster = clusters[i];
            }
        }

        return closestCluster;
    }

    public int[][] fillArray(int[][] array) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                array[i][j] = -1;
            }
        }

        return array;
    }

    class Cluster {
        int clusterID;
        int intensity;
        int pixelCount;
        int avgIntensity;
        int red, green, blue;
        int rAvg, gAvg, bAvg;

        public Cluster (int clusterID, int intensity) {
            if (isGreyscale) {
                this.intensity = intensity;
                this.clusterID = clusterID;
                pixelCount = 0;
                avgIntensity = 0;
                addPixel(intensity);
            } else {
                int r, g, b;
                this.clusterID = clusterID;

                r = intensity >> 16&0x000000FF;
                g = intensity >> 8&0x000000FF;
                b = intensity >> 0&0x000000FF;
                red = r;
                green = g;
                blue = b;
                addPixel(intensity);
            }
        }

        public int getClusterID() {
            return clusterID;
        }

        public int getIntensity() {
            if (isGreyscale) {
                return intensity;
            } else {
                int r = rAvg / pixelCount;
                int g = gAvg / pixelCount;
                int b = bAvg / pixelCount;
                int rgb = 0xff000000 | (r << 16) | (g << 8) | b;
                return rgb;
            }
        }

        public void addPixel(int pixelInten) {
            if (isGreyscale) {
                pixelCount++;
                avgIntensity += pixelInten;
                intensity = avgIntensity / pixelCount;
            } else {
                int r, g, b;
                r = pixelInten >> 16&0x000000FF;
                g = pixelInten >> 8&0x000000FF;
                b = pixelInten >> 0&0x000000FF;
                rAvg += r;
                gAvg += g;
                bAvg += b;
                pixelCount++;
                red = rAvg / pixelCount;
                green = gAvg / pixelCount;
                blue = bAvg / pixelCount;
            }
        }

        /*public void removePixel(int pixelInten) {
            //removes a pixel from the cluster.
            pixelCount--;
            avgIntensity -= pixelInten;
            intensity = avgIntensity / pixelCount;
        }*/

        public void clear() {
            intensity = 0;
            red = 0;
            green = 0;
            blue = 0;
            rAvg = 0;
            gAvg = 0;
            bAvg = 0;
            //rgb = 0;
            pixelCount = 0;
            avgIntensity = 0;
        }

        public int distance(int pixelInten) {
            //calculate the color distance from what intesity this cluster represents
            //to where that particular pixel is and return that value
            int dist;

            if (isGreyscale) {
                dist = Math.abs((pixelInten - intensity));
            } else {
                int r, g, b;
                r = pixelInten >> 16&0x000000FF;
                g = pixelInten >> 8&0x000000FF;
                b = pixelInten >> 0&0x000000FF;
                int rDist = Math.abs((r - red));
                int gDist = Math.abs((g - green));
                int bDist = Math.abs((b - blue));
                dist = ((rDist + gDist + bDist) / 3);
            }
            return dist;
        }
    }
}
