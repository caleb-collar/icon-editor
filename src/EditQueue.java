//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | Edit Queue

/**
 * Edit Queue is the subject implementation in the Observer pattern used
 * in the multi-pane extended bitmap editor.
 * @author Caleb
 */
public class EditQueue extends Icon implements Subject{
    private Subject subject;
    private UpdateData data;
    
    public EditQueue(int x, int y){
        super(x, y);
        subject = new SubjectImpl();
    }

    @Override
    public void attach(Observer o) {
        subject.attach(o);
    }

    @Override
    public void detach(Observer o) {
        subject.detach(o);
    }

    @Override
    public void notifyObservers() {
        subject.notifyObservers();
    }
    
    public void notifySetPixel(int x, int y, int r, int g, int b){
        data = new UpdateData(); //Instantiate update data structure.
        super.setPixel(x, y, r, g, b);
        data.setX(x);
        data.setY(y);
        data.setR(r);
        data.setR(r);
        data.setG(g);
        data.setB(b);
        notifyObservers(); //Notify observers of pixel data change.
    }

    public UpdateData getData() {
        return data;
    }
}