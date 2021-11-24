//CSC 2910 OOP | Caleb Collar | P3: Icon Creator GUI | Driver
public class Driver {
    //Main method to run the GUI for the icon creator.
    public static void main(String args[]) {
        //Set look and feel to nimbus.
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BitmapGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        //Create and display GUI form for the user.
        java.awt.EventQueue.invokeLater(() -> {
            new BitmapGUI().setVisible(true);
        });
    }
}