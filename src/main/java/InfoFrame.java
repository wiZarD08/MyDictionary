import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Style;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class InfoFrame {
    private final JFrame f;
    private JPanel panel;
    private JTextPane textPane;
    private final JRadioButton space = new JRadioButton("Пробел");
    private final JRadioButton arrow = new JRadioButton("Стрелки");
    private final Checkbox box1 = new Checkbox("1");
    private final Checkbox box2 = new Checkbox("2");
    private final Checkbox box3 = new Checkbox("3");
    private final Checkbox box4 = new Checkbox("4");
    private final Style style = textPane.getStyle("Times New Roman");

    private final DBManager dbManager;
    private boolean[] settings;
    private static InfoFrame thisObj;

    public static InfoFrame getThisObj() {
        return thisObj;
    }

    public static void newInfoFrame(DBManager dbManager, boolean start) {
        if (thisObj == null)
            thisObj = new InfoFrame(dbManager);
        if (start)
            thisObj.f.setVisible(true);
    }

    private InfoFrame(DBManager dbManager) {
        this.dbManager = dbManager;
        f = new JFrame("info");
        f.setSize(500, 600);
        f.setContentPane(this.panel);
        f.setLocationRelativeTo(null);
        String infoSt = "";
        try {
            infoSt = Files.readString(Path.of("Info"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] info = infoSt.split("//");
        textPane.setEditable(false);

        space.setOpaque(false);
        space.setSelected(true);
        arrow.setOpaque(false);
        ButtonGroup keysGroup = new ButtonGroup();
        keysGroup.add(space);
        keysGroup.add(arrow);

        insert(info[0]);
        textPane.insertComponent(arrow);
        textPane.insertComponent(space);
        insert(info[1]);
        insert(info[2]);
        insert(info[3]);
        Checkbox[] box = {box1, box2, box3, box4};
        for (int i = 3; i >= 0; i--)
            textPane.insertComponent(box[i]);
        insert(info[4]);
        settings = dbManager.getAllSettings();
        if (settings[0]) arrow.setSelected(true);
        for (int i = 1; i < 5; i++)
            if (settings[i]) box[i - 1].setState(true);
    }

    private void insert(String st) {
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), st, style);
            textPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean arrowIsSelected() {
        return arrow.isSelected();
    }

    public boolean[] getSettings() {
        return new boolean[]{box1.getState(), box2.getState(), box3.getState(), box4.getState()};
    }

    public void updateSettings() {
        settings[0] = arrowIsSelected();
        boolean[] boxes = getSettings();
        System.arraycopy(boxes, 0, settings, 1, boxes.length);
        if (!Arrays.equals(settings, dbManager.getAllSettings()))
            dbManager.setAllSettings(settings);
    }
}
