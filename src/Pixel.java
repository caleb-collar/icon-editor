//CSC 2910 | p1 | Caleb Collar | Pixel Class

public class Pixel {

    //Store pixel value in int, 4 bytes.
    private int pixelVal = 0;

    //Constructor
    public Pixel() {
        pixelVal = 0;
    }

    //Set Methods.
    public void setRed(int r) {
        pixelVal = r;
    }

    public void setGreen(int g) {
        pixelVal = (pixelVal << 8) + g;
    }

    public void setBlue(int b) {
        pixelVal = (pixelVal << 8) + b;
    }

    //Get methods.
    public int getRed() {
        int r = (pixelVal >> 16) & 0xFF;
        return r;
    }

    public int getGreen() {
        int g = (pixelVal >> 8) & 0xFF;
        return g;
    }

    public int getBlue() {
        int b = pixelVal & 0xFF;
        return b;
    }

    public int getColor() {
        return pixelVal;
    }

    //Convert pixel value to hex string.
    public String toStringHex() {
        String hexString = "#", red, green, blue;
        if (getRed() == 0) {
            red = "00";
        }
        else {
            red = Integer.toHexString(getRed());
        }
        if (getGreen() == 0) {
            green = "00";
        }
        else {
            green = Integer.toHexString(getGreen());
        }
        if (getBlue() == 0) {
            blue = "00";
        }
        else {
            blue = Integer.toHexString(getBlue());
        }
        hexString = hexString + red + green + blue;
        return hexString;
    }
}
