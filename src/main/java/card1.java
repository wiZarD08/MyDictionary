import javax.swing.*;

public class card1 {
    private JPanel panel;
    private JLabel label1;
    private JLabel label2;

    private static card1 thisObj;

    public static card1 newCard1() {
        if (thisObj == null)
            thisObj = new card1();
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