import javax.swing.*;

public class card2 {
    private JPanel panel;
    private JLabel label1;
    private JLabel label2;

    private static card2 thisObj;

    public static card2 newCard2() {
        if (thisObj == null)
            thisObj = new card2();
        return thisObj;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JLabel getLab() {
        return label1;
    }

    public JLabel getLabNum() {
        return label2;
    }
}