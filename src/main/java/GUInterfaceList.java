import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUInterfaceList {
    private JPanel panel;
    private JTable table;
    private final JFrame f;
    private final DefaultTableModel tableModel;

    private final DBManager dbManager;
    private final LangM langM;

    private static GUInterfaceList thisObj;

    public static void newGUInterfaceList() {
        if (thisObj == null)
            thisObj = new GUInterfaceList();
        else thisObj.start();
    }

    private GUInterfaceList() {
        dbManager = GUInterface.getDbManager();
        langM = GUInterface.getLangM();
        f = new JFrame("table");
        f.setContentPane(this.panel);

        Object[] columnsHeader = new String[]{"", "", "Слово/Фраза", "", "", "Перевод", "Определение/Примеры", "", "Изучено"};
        Class<?>[] classes = new Class[]{Integer.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, Boolean.class};
        Object[][] data = new Object[][]{};
        tableModel = new DefaultTableModel(data, columnsHeader) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return classes[columnIndex];
            }
        };

        dbManager.fillTable(tableModel);
        table.setModel(tableModel);
        Font font = new Font("TimesRoman", Font.PLAIN, 16);
        table.setFont(font);
        table.setRowHeight(25);
        columnCorrection(false);
        f.setVisible(true);
        table.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                added();
            }
        });
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (added() != 0)
                    dbManager.updateDB(table);
            }
        });
    }

    private void columnCorrection(boolean clean) {
        if (clean) {
            for (int i = table.getColumnCount() - 1; i >= 0; i--)
                table.removeColumn(table.getColumnModel().getColumn(i));
            for (int i = 0; i < 9; i++)
                table.addColumn(new TableColumn(i));
        }
        table.removeColumn(table.getColumnModel().getColumn(0));
        boolean[] sett = InfoFrame.getThisObj().getSettings();
        int cof = 0;
        int size = 0;
        if (sett[0]) {
            table.getColumnModel().getColumn(0).setPreferredWidth(90);         //add1
            table.getColumnModel().getColumn(0).setMaxWidth(110);
            size += 90;
        } else {
            table.removeColumn(table.getColumnModel().getColumn(0));
            cof++;
        }
        table.getColumnModel().getColumn(1 - cof).setPreferredWidth(140);      //word
        table.getColumnModel().getColumn(1 - cof).setMaxWidth(200);
        if (sett[1]) {
            table.getColumnModel().getColumn(2 - cof).setPreferredWidth(140);   //add2
            table.getColumnModel().getColumn(2 - cof).setMaxWidth(200);
            size += 170;
        } else {
            table.removeColumn(table.getColumnModel().getColumn(2 - cof));
            cof++;
        }
        if (sett[2]) {
            table.getColumnModel().getColumn(3 - cof).setPreferredWidth(140);   //add3
            table.getColumnModel().getColumn(3 - cof).setMaxWidth(200);
            size += 170;
        } else {
            table.removeColumn(table.getColumnModel().getColumn(3 - cof));
            cof++;
        }
        table.getColumnModel().getColumn(4 - cof).setPreferredWidth(140);    //translation
        table.getColumnModel().getColumn(4 - cof).setMaxWidth(200);
        table.getColumnModel().getColumn(5 - cof).setPreferredWidth(240);    //definition
        //table.getColumnModel().getColumn(5 - cof).setMaxWidth(200);
        if (sett[3]) {
            table.getColumnModel().getColumn(6 - cof).setPreferredWidth(240);   //add4
            //table.getColumnModel().getColumn(6 - cof).setMaxWidth(200);
            size += 240;
        } else {
            table.removeColumn(table.getColumnModel().getColumn(6 - cof));
            cof++;
        }
        table.getColumnModel().getColumn(7 - cof).setMaxWidth(80);             //learned

        f.setSize(600 + size, 550);
        f.setLocationRelativeTo(null);
    }

    private void start() {
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--)
            tableModel.removeRow(i);
        columnCorrection(true);
        dbManager.fillTable(tableModel);
        table.setModel(tableModel);
        f.setVisible(true);
    }

    private int id = 0;

    private int added() {
        return id++;
    }
}
