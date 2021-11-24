//CSC 2910 | p1 | Caleb Collar | Icon Class

import java.util.ArrayList;
import java.io.*;

public class Icon {

    //Bitmap file header data.
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private byte bfType [] = {'B', 'M'};
    private int bfSize = 0;
    private int bfReserved1 = 0;
    private int bfReserved2 = 0;
    private int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;

    //Bitmap info header data.
    private final static int BITMAPINFOHEADER_SIZE = 40;
    private int biSize = BITMAPINFOHEADER_SIZE;
    static private int col = 0;
    static private int row = 0;
    private int biPlanes = 1;
    private int biBitCount = 24;
    private int biCompression = 0;
    private int biSizeImage = 0;
    private int biXPelsPerMeter = 0;
    private int biYPelsPerMeter = 0;
    private int biClrUsed = 0;
    private int biClrImportant = 0;

    //File IO.
    private FileOutputStream fo;
    private ArrayList<ArrayList<Pixel>> pixels = new ArrayList<>(col);

    //Constructor.
    private Icon(int x, int y) {
        col = x;
        row = y;
        for(int i=0; i < col; i++) {
            pixels.add(new ArrayList<Pixel>());
            for(int p=0; p < row; p++) {
                pixels.get(i).add(p, new Pixel());
            }
        }
    }

    //Public call to constructor. Default 40x40 icon.
    public static Icon init() {
        return new Icon(40,40);
    }
    
    //Public call to constructor, checks input size.
    public static Icon init(int x, int y) {
        if(x > 0 && y > 0) {
            return new Icon(x,y);
        }
        else {
            return null;
        }
    }

    //Sets an individual pixel value for the icon. Checks bounds.
    public void setPixel(int x, int y, int r, int g, int b) {
        if(x <= col && y <= row){
            pixels.get(x).get(y).setRed(r);
            pixels.get(x).get(y).setGreen(g);
            pixels.get(x).get(y).setBlue(b);
        }
        else {
            System.out.print("Input pixel for "+x+","+y+" is out of image bounds. ");
            System.out.print("Rows: " + pixels.get(0).size() + " ");
            System.out.print("Columns: " + pixels.size() + "\n");
        }
    }

    //Prints ArrayList Pixels to console as hex values.
    public void printHexArray() {
        System.out.print("Rows: " + pixels.get(0).size() + " ");
        System.out.print("Columns: " + pixels.size() + "\n");
        for(int r=0; r < row; r++){
            for(int c=0; c < col; c++){
                System.out.print(pixels.get(c).get(r).toStringHex() + " ");
                if(c == (col - 1)){
                    System.out.print("\n");
                }
            }
        }
    }

    //Public call to create a bitmap image of set pixels.
    public void createBitmapFile(String path) {
        try {
            fo = new FileOutputStream(path);
            create();
            fo.close();        
        }
        catch(Exception createFileEx) {
            createFileEx.printStackTrace();
        }
    }

    //Creation steps for BMP full file structure.
    private void create() {
        try {
           byte pixelArr [] = createBitmap();
           writeBitmapFileHeader();
           writeBitmapInfoHeader();
           writeBitmap(pixelArr);
        }
        catch(Exception createEx) {
           createEx.printStackTrace();
        }
    }

    //Writes bytes to file after file and info header.
    //Includes padding & BMP formatting. eg. bottom to top & little endian.
    private byte[] createBitmap() {
        int padding = 4-((col * 3)%4);
        int fillSize = (col * row) * 3;
        if (padding == 4) padding = 0;
        int totalBytes = fillSize + (padding * row);
        byte pixelArr [] = new byte[totalBytes];
        //Bitmap format is ->|B|G|R|Padding
        for (int r = row -1, a = 0; r >= 0; r--){
            for (int c = 0; c < col; c++){
                pixelArr[a] = (byte)pixels.get(c).get(r).getBlue(); a++;
                pixelArr[a] = (byte)pixels.get(c).get(r).getGreen(); a++;
                pixelArr[a] = (byte)pixels.get(c).get(r).getRed(); a++;
                if (c == (col-1)) a+= padding;
            }
        }
        bfSize = bfOffBits + totalBytes;
        biSizeImage = totalBytes;
        return pixelArr;
    }
    
    //Writes the bitmap to a byte array for appending to the file.
    private void writeBitmap(byte [] pixelArr) {
        try {
            fo.write(pixelArr);
        }
        catch (Exception writeEx) {
            writeEx.printStackTrace ();
        }
    }

    //Takes an int and converts it to a word.
    private byte [] intToWord(int valIn) {
        byte retValue [] = new byte [2];
        retValue [0] =(byte)(valIn & 0x00FF);
        retValue [1] =(byte)((valIn >>  8) & 0x00FF);
        return(retValue);
    }
     
    //Takes an int and converts it to a double word.
    private byte [] intToDWord(int valIn) {
        byte retValue [] = new byte [4];
        retValue [0] =(byte)(valIn & 0x00FF);
        retValue [1] =(byte)((valIn >> 8) & 0x000000FF);
        retValue [2] =(byte)((valIn >> 16) & 0x000000FF);
        retValue [3] =(byte)((valIn >> 24) & 0x000000FF);
        return(retValue);
    }

    //Puts together the file header from options specified.
    private void writeBitmapFileHeader() {
        try {
           fo.write(bfType);
           fo.write(intToDWord(bfSize));
           fo.write(intToWord(bfReserved1));
           fo.write(intToWord(bfReserved2));
           fo.write(intToDWord(bfOffBits));
        }
        catch(Exception fileHeaderEx) {
           fileHeaderEx.printStackTrace();
        }
    }

    //Puts together the info header from options specified.
    private void writeBitmapInfoHeader() {
        try {
           fo.write(intToDWord(biSize));
           fo.write(intToDWord(col));
           fo.write(intToDWord(row));
           fo.write(intToWord(biPlanes));
           fo.write(intToWord(biBitCount));
           fo.write(intToDWord(biCompression));
           fo.write(intToDWord(biSizeImage));
           fo.write(intToDWord(biXPelsPerMeter));
           fo.write(intToDWord(biYPelsPerMeter));
           fo.write(intToDWord(biClrUsed));
           fo.write(intToDWord(biClrImportant));
        }
        catch(Exception infoHeaderEx) {
           infoHeaderEx.printStackTrace();
        }
    }
}