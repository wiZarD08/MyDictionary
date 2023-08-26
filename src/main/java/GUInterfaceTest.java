import Records.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GUInterfaceTest {
    private JPanel panel;
    private JTextField TextField;
    private JButton nextButton;
    private JButton checkButton;
    private JLabel wordLabel;
    private JButton CorrectButton;
    private JLabel answerLabel;
    private final JFrame f;

    private final JPanel endPanel = new JPanel();
    private final JLabel result = new JLabel();
    private final JLabel percent = new JLabel();
    private final JButton toStatistics = new JButton("Перейти к статистике");

    private final DBManager dbManager;
    private final LangM langM;

    private final Map<Pair, String> map = new HashMap<>();
    private Iterator<Map.Entry<Pair, String>> it;
    private Map.Entry<Pair, String> now;
    private boolean learned;
    private boolean wrTestRBSelected;
    private int corWord;
    private boolean isCorrect;
    private boolean isNotCorrect;

    private static GUInterfaceTest thisObj;

    public static void newGUInterfaceTest(boolean wrTestRBSelected, boolean learned) {
        if (thisObj == null)
            thisObj = new GUInterfaceTest();
        thisObj.f.setVisible(false);
        thisObj.wrTestRBSelected = wrTestRBSelected;
        thisObj.learned = learned;
        thisObj.start();
    }

    private GUInterfaceTest() {
        dbManager = GUInterface.getDbManager();
        langM = GUInterface.getLangM();
        f = new JFrame("test");
        f.setSize(440, 300);
        f.setContentPane(this.panel);
        f.setLocationRelativeTo(null);
        nextButton.addActionListener(e -> nextWord());
        CorrectButton.addActionListener(e -> answerLabel.setText(getCorr()));
        checkButton.addActionListener(e -> checkAnswer());
        TextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkAnswer();
                    return;
                }
                if (InfoFrame.getThisObj().arrowIsSelected()) {
                    if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40)
                        nextWord();
                    return;
                }
                if (e.getKeyCode() == 32)
                    nextWord();
            }
        });
    }

    private String getCorr() {
        if (wrTestRBSelected)
            return now.getKey().word();
        else return now.getValue();
    }

    private void checkAnswer() {
        if (!TextField.getText().trim().equalsIgnoreCase(getCorr())) {
            TextField.setForeground(Color.RED);
            isNotCorrect = true;
        } else {
            TextField.setForeground(Color.GREEN);
            if (answerLabel.getText().equals("") && !isCorrect) {
                isCorrect = true;
                corWord++;
                dbManager.updateCorrWord(now.getKey().id());
            }
        }
    }

    private void start() {
        f.setContentPane(this.panel);
        corWord = 0;
        map.clear();
        dbManager.fillPairMap(map, learned);
        it = map.entrySet().iterator();
        thisObj.nextWord();
        f.setVisible(true);
    }

    private void nextWord() {
        if (!isCorrect && isNotCorrect && now != null) dbManager.updateIncorrWord(now.getKey().id());
        isCorrect = false;
        isNotCorrect = false;
        if (it.hasNext())
            if (wrTestRBSelected)
                wordLabel.setText((now = it.next()).getValue());
            else wordLabel.setText((now = it.next()).getKey().word());
        else if (map.size() != 0)
            end();
        answerLabel.setText("");
        TextField.setText("");
        TextField.setForeground(Color.BLACK);
    }

    protected void end() {
        endPanel.removeAll();
        endPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        endPanel.add(Box.createRigidArea(new Dimension(2000, 70)));
        result.setText("Результат:   " + corWord + "/" + map.size());
        result.setFont(new Font("TimesRoman", Font.PLAIN, 25));
        endPanel.add(result);
        endPanel.add(Box.createRigidArea(new Dimension(2000, 8)));
        int per = corWord * 100 / map.size();
        percent.setText(per + " %");
        Color color;
        if (per >= 70) color = Color.GREEN;
        else if (per >= 30) color = Color.ORANGE;
        else color = Color.RED;
        percent.setForeground(color);
        percent.setFont(new Font("TimesRoman", Font.PLAIN, 21));
        endPanel.add(percent);
        endPanel.add(Box.createRigidArea(new Dimension(2000, 30)));
        endPanel.add(toStatistics);
        f.setContentPane(endPanel);
        f.revalidate();

        String type;
        if (!wrTestRBSelected) type = "-> перевод";
        else if (!learned) type = "-> слово";
        else type = "повторение";
        dbManager.addTestSt(corWord, map.size(), type);
        toStatistics.addActionListener(e -> {
            GUInterfaceStatistic.newGUInterfaceStatistic();
            f.setVisible(false);
        });
    }
}
