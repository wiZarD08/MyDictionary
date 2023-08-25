import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUInterface {
    private JButton addButton;
    protected JPanel panel;
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

    private final DBManager dbManager;

    private static GUInterface thisObj;

    public static void newGUInterface() {
        if (thisObj == null)
            thisObj = new GUInterface();
    }

    public static GUInterface getThisObj() {
        return thisObj;
    }

    public GUInterface() {
        dbManager = new DBManager();

        JFrame f = new JFrame();
        f.setSize(750, 550);
        f.setContentPane(this.panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        infoButton.setMargin(new java.awt.Insets(1, 2, 1, 2));
        ButtonGroup SrGroup = new ButtonGroup();
        SrGroup.add(TrSearchRB);
        SrGroup.add(WrSearchRB);
        TrSearchRB.setSelected(true);

        ButtonGroup testGroup = new ButtonGroup();
        testGroup.add(TrTestRB);
        testGroup.add(WrTestRB);
        TrTestRB.setSelected(true);

        f.setVisible(true);
        InfoFrame.newInfoFrame(dbManager, false);
        addButton.addActionListener(e -> {
            dbManager.insert(textField.getText(), TTextField.getText());
            textField.setText("");
            TTextField.setText("");
        });
        searchButton.addActionListener(e -> search());
        testButton.addActionListener(e -> GUInterfaceTest.newGUInterfaceTest(dbManager, WrTestRB.isSelected(), false));
        listButton.addActionListener(e -> GUInterfaceList.newGUInterfaceList(dbManager));
        statistButton.addActionListener(e -> GUInterfaceStatistic.newGUInterfaceStatistic(dbManager));
        infoButton.addActionListener(e -> InfoFrame.newInfoFrame(dbManager, true));
        cardButton.addActionListener(e -> GUInterfaceCard.newGUInterfaceCard(dbManager, false));
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
    }

    private void search() {
        if (TrSearchRB.isSelected())
            textPane.setText(dbManager.findTr(FTextField.getText().trim()));
        else textPane.setText(dbManager.findWr(FTextField.getText().trim()));
    }
}
