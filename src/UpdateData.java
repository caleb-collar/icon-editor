///CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Observer Data
/**
 * Data structure for use in observer pattern.
 * Used for retrieving last edit rather than entire dataset.
 * @author Caleb
 */
public class UpdateData {
    private int x,y,r,g,b;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}