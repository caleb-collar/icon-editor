//CSC 2910 OOP | Caleb Collar | P6: Bitmap Editor GUI Extended | GUI Pane Spawner

//Imports
import java.util.*;
import java.io.*;

/**
 *This class handles the input from the first gui pane and spawns the desired
 * number of editor panes at the desired dimension.
 * @author Caleb
 */
public class GuiExtendedPaneSpawner {
    private HashMap<String,Integer> values;
    private EditQueue editQueue;
    private static GuiExtendedInterface extendedInterface;
    private static Integer panes;
    
    public GuiExtendedPaneSpawner(){
        userDataHandler();
        paneHandler();
    }
    
    public static int getNumPanes(){
        return panes;
    }
    
    @SuppressWarnings("unchecked")
    //Reads persistant data structure containing user specified dimension and panes for editors.
    private void userDataHandler(){
        try {
            File f = new File("temp");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            values =(HashMap<String,Integer>)ois.readObject();
            ois.close();
            fis.close();
            f.delete();
        } catch(Exception e){
            System.out.println("Problem reading temp file containing user input data.");
        }
    }
    
    //Spawns number of desired panes.
    private void paneHandler(){
        panes = values.get("PANES");
        Integer width = values.get("WIDTH");
        Integer height = values.get("HEIGHT");
        editQueue = new EditQueue(width, height);
        if (panes == 1) {
            GuiExtendedInterface p = new GuiExtended(width, height);
            extendedInterface = p;
            p.getInstance().setVisible(true);
        } else {
            for (int i=0; i<panes; i++){
                GuiExtendedInterface p = new GuiExtended(width, height, editQueue);
                extendedInterface = p;
                p.getInstance().setTitle("[Editor "+(i+1)+"] Bitmap Image & Icon Creator");
                p.getInstance().setLocation(i*50, i*25);        
                p.getInstance().setVisible(true);
            }
        }
    }
    
    public static GuiExtendedInterface getinterfaceInstance(){
        return extendedInterface;
    }
}