import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GUInterfaceStatistic {
    private final JFrame f;
    private JPanel panel;
    private JTextPane textPane1;
    private JTextPane textPane2;
    private JTextPane textPane3;
    private JTable table;
    private final DefaultTableModel tableModel;
    private JButton cardButton;
    private JButton addLearnedButton;
    private JButton repeatButton;
    private JButton clearButton;
    private JLabel statLab1;
    private JLabel statLab2;
    private JLabel statLab3;
    private JLabel l1;
    private JLabel l2;
    private JLabel l3;

    private final DBManager dbManager;
    private final LangM langM;

    private static GUInterfaceStatistic thisObj;

    public static void newGUInterfaceStatistic(boolean visible) {
        if (thisObj == null)
            thisObj = new GUInterfaceStatistic();
        thisObj.start();
        thisObj.f.setVisible(visible);
    }

    private GUInterfaceStatistic() {
        dbManager = GUInterface.getDbManager();
        langM = GUInterface.getLangM();
        f = new JFrame("statistics");
        f.setSize(500, 550);
        f.setContentPane(this.panel);
        f.setLocationRelativeTo(null);

        langM.addLab(l1);
        langM.addLab(l2);
        langM.addLab(l3);
        langM.addBut(cardButton);
        langM.addBut(addLearnedButton);
        langM.addBut(repeatButton);
        langM.addBut(clearButton);
        Object[] columnsHeader = new String[]{"", "Прав", "Всего", " % ", "Тип"};
        Class<?>[] classes = new Class[]{Integer.class, Integer.class, Integer.class, String.class, String.class};
        Object[][] data = new Object[][]{};
        tableModel = new DefaultTableModel(data, columnsHeader) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return classes[columnIndex];
            }
        };
        table.setModel(tableModel);
        Font font = new Font("TimesRoman", Font.PLAIN, 16);
        table.setFont(font);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(40);
        table.getColumnModel().getColumn(3).setMaxWidth(70);
        table.getColumnModel().getColumn(4).setMaxWidth(100);


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        CustomRenderer customRenderer = new CustomRenderer();
        customRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(customRenderer);

        cardButton.addActionListener(e -> GUInterfaceCard.newGUInterfaceCard(true, true));
        addLearnedButton.addActionListener(e -> {
            dbManager.addToLearned(textPane2.getText());
            textPane2.setText(dbManager.getGoodWords());
            textPane3.setText(dbManager.getLearnedWords());
        });
        repeatButton.addActionListener(e -> GUInterfaceTest.newGUInterfaceTest(true, true, true));
        clearButton.addActionListener((e -> {
            int result = JOptionPane.showConfirmDialog(f, langM.getMessage(), langM.getTitle(),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION)
                dbManager.deleteAll();
        }));
    }

    private static final Font smallFont = new Font("TimesRoman", Font.PLAIN, 12);

    static class CustomRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0)
                l.setFont(smallFont);
            if (column == 3) {
                Color color;
                int per = Integer.parseInt(l.getText().substring(0, l.getText().length() - 1));
                if (per >= 70) color = Color.GREEN;
                else if (per >= 30) color = Color.ORANGE;
                else color = Color.RED;
                l.setForeground(color);
            }
            return l;
        }
    }

    private void start() {
        textPane1.setText(dbManager.getHardWords());
        textPane2.setText(dbManager.getGoodWords());
        textPane3.setText(dbManager.getLearnedWords());
        String[] arr = dbManager.getStatString(langM);
        statLab1.setText(arr[0]);
        statLab2.setText(arr[1]);
        statLab3.setText(arr[2]);

        for (int i = tableModel.getRowCount() - 1; i >= 0; i--)
            tableModel.removeRow(i);
        dbManager.fillStatTable(tableModel);
        table.setModel(tableModel);
        langM.changeTable(table);
    }
}
