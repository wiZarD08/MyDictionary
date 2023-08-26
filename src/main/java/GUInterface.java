import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUInterface {
    protected JPanel panel;
    private JButton addButton;
    private JTextField textField;
    private JTextField TTextField;
    private JButton searchButton;
    private JTextField FTextField;
    private JTextPane textPane;
    private JButton testButton;
    private JButton listButton;
    private JRadioButton TrSearchRB;
    private JRadioButton WrSearchRB;
    private JRadioButton TrTestRB;
    private JRadioButton WrTestRB;
    private JButton statistButton;
    private JButton infoButton;
    private JButton cardButton;
    private JButton engButton;
    private JButton rusButton;
    private JLabel l1;
    private JLabel l2;

    private static final DBManager dbManager = new DBManager();
    private static final LangM langM = new LangM();

    private static GUInterface thisObj;

    public static void newGUInterface() {
        if (thisObj == null)
            thisObj = new GUInterface();
    }

    public GUInterface() {
        JFrame f = new JFrame();
        f.setSize(750, 580);
        f.setContentPane(this.panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        langM.addLab(l1);
        langM.addLab(l2);
        langM.addRB(TrSearchRB);
        langM.addRB(WrSearchRB);
        langM.addRB(TrTestRB);
        langM.addRB(WrTestRB);
        langM.addBut(addButton);
        langM.addBut(searchButton);
        langM.addBut(testButton);
        langM.addBut(cardButton);
        langM.addBut(listButton);
        langM.addBut(statistButton);
        rusButton.setEnabled(false);
        infoButton.setMargin(new java.awt.Insets(1, 2, 1, 2));
        engButton.setMargin(new java.awt.Insets(1, 2, 1, 2));
        rusButton.setMargin(new java.awt.Insets(1, 2, 1, 2));
        ButtonGroup SrGroup = new ButtonGroup();
        SrGroup.add(TrSearchRB);
        SrGroup.add(WrSearchRB);
        TrSearchRB.setSelected(true);

        ButtonGroup testGroup = new ButtonGroup();
        testGroup.add(TrTestRB);
        testGroup.add(WrTestRB);
        TrTestRB.setSelected(true);

        f.setVisible(true);
        InfoFrame.newInfoFrame(false);
        addButton.addActionListener(e -> {
            dbManager.insert(textField.getText(), TTextField.getText());
            textField.setText("");
            TTextField.setText("");
        });
        searchButton.addActionListener(e -> search());
        testButton.addActionListener(e -> GUInterfaceTest.newGUInterfaceTest(true, WrTestRB.isSelected(), false));
        listButton.addActionListener(e -> GUInterfaceList.newGUInterfaceList(true));
        statistButton.addActionListener(e -> GUInterfaceStatistic.newGUInterfaceStatistic(true));
        infoButton.addActionListener(e -> InfoFrame.newInfoFrame(true));
        cardButton.addActionListener(e -> GUInterfaceCard.newGUInterfaceCard(true, false));
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (InfoFrame.getThisObj() != null)
                    InfoFrame.getThisObj().updateSettings();
                dbManager.closeConn();
            }
        });
        FTextField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) search();
            }

            public void keyReleased(KeyEvent e) {
            }
        });
        engButton.addActionListener(e -> {
            engButton.setEnabled(false);
            rusButton.setEnabled(true);
            langM.intoEng();
        });
        rusButton.addActionListener(e -> {
            rusButton.setEnabled(false);
            engButton.setEnabled(true);
            langM.intoRus();
        });
    }

    private void search() {
        if (TrSearchRB.isSelected())
            textPane.setText(dbManager.findTr(FTextField.getText().trim()));
        else textPane.setText(dbManager.findWr(FTextField.getText().trim()));
    }

    public static DBManager getDbManager() {
        return dbManager;
    }

    public static LangM getLangM() {
        return langM;
    }
}
