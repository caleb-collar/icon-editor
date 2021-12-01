//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Subject Implementation

//Imports
import java.util.*;

/**
 * Subject implementation contains list of observers.
 * @author Caleb
 */
public class SubjectImpl implements Subject {

    private List<Observer> observers = new ArrayList<>();

    public void SubjectImpl() {
        observers = new ArrayList<>();
    }

    @Override
    public void attach(Observer o) {
        if(!observers.contains(o)){
            observers.add(o);
        } 
    }

    @Override
    public void detach(Observer o) {
        if(observers.contains(o)){
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers() {
        observers.forEach(observer -> {
            observer.update();
        });
    }
}