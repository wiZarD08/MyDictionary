import Records.Entry;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class GUInterfaceCard {
    private final JFrame f;
    private JPanel initialPanel;
    private JButton startButton;
    private JComboBox<String> comboBox1;
    private JComboBox<String> comboBox2;
    private JLabel l1;
    private JLabel l2;
    private final JPanel panel1;
    private final JLabel lab1;
    private final JLabel labNum1;
    private final JPanel panel2;
    private final JLabel lab2;
    private final JLabel labNum2;

    private final String[] elementsRus = new String[]{"Слово", "Перевод", "Определение", "1 доп столбец", "2 доп столбец"
            , "3 доп столбец", "4 доп столбец"};
    private final String[] elementsEng = new String[]{"Word", "Translation", "Definition", "1 add. column", "2 add. column"
            , "3 add. column", "4 add. column"};

    private boolean hard;
    private final List<Entry> list = new ArrayList<>();
    private int index;
    private final DBManager dbManager;
    private final LangM langM;

    private static GUInterfaceCard thisObj;

    public static void newGUInterfaceCard(boolean hard) {
        if (thisObj == null)
            thisObj = new GUInterfaceCard();
        thisObj.hard = hard;
        thisObj.start();
    }

    private GUInterfaceCard() {
        dbManager = GUInterface.getDbManager();
        langM = GUInterface.getLangM();
        f = new JFrame("cards");
        f.setSize(400, 250);
        f.setLocationRelativeTo(null);
        f.setFocusable(true);

        panel1 = card1.newCard1().getPanel();
        panel2 = card2.newCard2().getPanel();
        lab1 = card1.newCard1().getLab();
        lab2 = card2.newCard2().getLab();
        labNum1 = card1.newCard1().getLabNum();
        labNum2 = card2.newCard2().getLabNum();

        langM.addLab(l1);
        langM.addLab(l2);
        langM.addBut(startButton);
        DefaultComboBoxModel<String> cbModel1 = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> cbModel2 = new DefaultComboBoxModel<>();
        String[] columns = new String[]{"word", "translation", "definition", "addition1", "addition2"
                , "addition3", "addition4"};
        String[] elements2 = new String[elementsRus.length];
        System.arraycopy(elementsRus, 0, elements2, 0, elementsRus.length);
        for (String el : elementsRus)
            cbModel1.addElement(el);
        for (String el : elements2)
            cbModel2.addElement(el);
        comboBox1.setModel(cbModel1);
        comboBox1.setSelectedIndex(1);
        comboBox2.setModel(cbModel2);
        comboBox2.setSelectedIndex(0);
        startButton.addActionListener(e -> {
            int index1 = IntStream.range(0, elementsRus.length).
                    filter(i -> comboBox1.getSelectedItem().equals(elementsRus[i])).findFirst().orElse(-1);
            if (index1 == -1) index1 = IntStream.range(0, elementsEng.length).
                    filter(i -> comboBox1.getSelectedItem().equals(elementsEng[i])).findFirst().orElse(-1);
            int index2 = IntStream.range(0, elementsRus.length).
                    filter(i -> comboBox2.getSelectedItem().equals(elementsRus[i])).findFirst().orElse(-1);
            if (index2 == -1) index2 = IntStream.range(0, elementsEng.length).
                    filter(i -> comboBox1.getSelectedItem().equals(elementsEng[i])).findFirst().orElse(-1);
            list.clear();
            dbManager.fillMap(list, columns[index1], columns[index2], hard);
            index = -1;
            next();
            f.setContentPane(panel1);
            f.revalidate();
        });
        panel1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                f.setContentPane(panel2);
                f.revalidate();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_RIGHT) {
                    next();
                    f.setContentPane(panel1);
                    f.revalidate();
                }
                if (code == KeyEvent.VK_LEFT) {
                    previous();
                    f.setContentPane(panel1);
                    f.revalidate();
                }
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP) {
                    if (f.getContentPane().equals(panel2))
                        f.setContentPane(panel1);
                    else f.setContentPane(panel2);
                    f.revalidate();
                }
            }
        });
        panel2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                next();
                f.setContentPane(panel1);
                f.revalidate();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void start() {
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
        if (langM.isRus()) {
            if (!Objects.equals(comboBox1.getModel().getElementAt(0), "Слово"))
                for (String el : elementsRus)
                    cbModel.addElement(el);
        } else if (!Objects.equals(comboBox1.getModel().getElementAt(0), "Word"))
            for (String el : elementsEng)
                cbModel.addElement(el);

        if (cbModel.getSize() != 0) {
            DefaultComboBoxModel<String> cbModel1 = new DefaultComboBoxModel<>();
            for (int i = 0; i < cbModel.getSize(); i++) {
                cbModel1.addElement(cbModel.getElementAt(i));
            }
            comboBox1.setModel(cbModel);
            comboBox1.setSelectedIndex(1);
            comboBox2.setModel(cbModel1);
            comboBox2.setSelectedIndex(0);
        }
        f.setContentPane(this.initialPanel);
        f.revalidate();
        f.setVisible(true);
    }

    private void next() {
        if (index + 1 < list.size()) {
            Entry entry = list.get(++index);
            lab1.setText(entry.key());
            lab2.setText(entry.value());
            labNum1.setText(String.valueOf(index + 1));
            labNum2.setText(String.valueOf(index + 1));
            int key = entry.key().length();
            int value = entry.value().length();
            if (key > 27 || value > 27) {
                if (key > 34 || value > 34)
                    f.setSize(800, 250);
                else f.setSize(600, 250);
                f.setLocationRelativeTo(null);
            }
        } else f.setVisible(false);
    }

    private void previous() {
        if (index > 0) {
            Entry entry = list.get(--index);
            lab1.setText(entry.key());
            lab2.setText(entry.value());
            labNum1.setText(String.valueOf(index + 1));
            labNum2.setText(String.valueOf(index + 1));
        }
    }
}
