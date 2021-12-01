//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Subject Interface

//Imports

/**
 * This subject interface is for the implementation of the observer pattern.
 * @author Caleb
 */
public interface Subject {
    public void attach(Observer o); 
    public void detach(Observer o);
    public void notifyObservers(); 
}