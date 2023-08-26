import Records.Entry;
import Records.Pair;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DBManager {
    private Connection conn;
    private Statement state;

    public DBManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:DB");
            state = conn.createStatement();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getClass());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("No connection");
            e.printStackTrace();
        }
    }

    //..................................................................................................................DICTIONARY

    public void closeConn() {
        try {
            if (conn != null)
                conn.close();
            if (state != null)
                state.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String word, String trans) {
        if (!Objects.equals(word, "")) {
            try (PreparedStatement preState = conn.prepareStatement
                    ("INSERT INTO dictionary(word, translation, definition) VALUES (?, ?, '');")) {
                preState.setString(1, word.trim().toLowerCase());
                preState.setString(2, trans.trim().toLowerCase());
                preState.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String findTr(String word) {
        try (PreparedStatement preState = conn.prepareStatement
                ("SELECT translation FROM dictionary WHERE UPPER(word) = ?;")) {
            preState.setString(1, word.toUpperCase());
            try (ResultSet res = preState.executeQuery()) {
                if (res.next())
                    return res.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Нет результата";
    }

    public String findWr(String trans) {
        try (PreparedStatement preState = conn.prepareStatement
                ("SELECT word FROM dictionary WHERE translation = ?;")) {
            preState.setString(1, trans.toLowerCase());
            try (ResultSet res = preState.executeQuery()) {
                if (res.next())
                    return res.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Нет результата";
    }

    public void fillPairMap(Map<Pair, String> map, boolean learned) {
        try (ResultSet res = state.executeQuery("SELECT id, word, translation FROM dictionary WHERE learned = "
                + learned + " ORDER BY id;")) {
            while (res.next())
                map.put(new Pair(res.getInt(1), res.getString(2)), res.getString(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fillMap(List<Entry> list, String keyCol, String valCol, boolean hard) {
        String hard1 = "";
        String hard2 = "";
        if (hard) {
            try {
                state.executeUpdate("UPDATE wordStatistics SET hard = 1 WHERE lastIncorrect >= 2;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            hard1 = " JOIN wordStatistics ON id = id_word";
            hard2 = " AND hard = 1";
        }
        String st1;
        String st2;
        try (ResultSet res = state.executeQuery("SELECT " + keyCol + ", " + valCol + " FROM dictionary" + hard1 +
                " WHERE learned = 0" + hard2 + ";")) {
            while (res.next())
                if (!(st1 = res.getString(1)).equals("") && !(st2 = res.getString(2)).equals(""))
                    list.add(new Entry(st1, st2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fillTable(DefaultTableModel tb) {
        try (ResultSet res = state.executeQuery("SELECT * FROM dictionary ORDER BY id;")) {
            while (res.next())
                tb.addRow(new Object[]{res.getInt(1), res.getString(2),
                        res.getString(3), res.getString(4), res.getString(5),
                        res.getString(6), res.getString(7), res.getString(8),
                        res.getBoolean(9)});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDB(JTable table) {
        String[] columns = {"addition1 = ?, ", "word = ?, ", "addition2 = ?, ", "addition3 = ?, ", "translation = ?, ",
                "definition = ?, ", "addition4 = ?, ", "learned = ? "};
        boolean[] addCol = InfoFrame.getThisObj().getSettings();
        int colsAm = 8;
        if (!addCol[0]) {
            columns[0] = "";
            colsAm--;
        }
        if (!addCol[1]) {
            columns[2] = "";
            colsAm--;
        }
        if (!addCol[2]) {
            columns[3] = "";
            colsAm--;
        }
        if (!addCol[3]) {
            columns[6] = "";
            colsAm--;
        }
        StringBuilder sb = new StringBuilder();
        for (String st : columns)
            sb.append(st);
        String colString = sb.toString();
        try (ResultSet res = state.executeQuery("SELECT * FROM dictionary ORDER BY id;")) {
            int id;
            for (int i = 0; i < table.getRowCount(); i++) {
                if (!res.next()) break;
                if ((id = (int) (table.getModel().getValueAt(i, 0))) == res.getInt(1)) {
                    PreparedStatement preState = conn.prepareStatement
                            ("UPDATE dictionary SET " + colString + " WHERE id = ?;");
                    for (int a = 0; a < colsAm - 1; a++)
                        preState.setString(a + 1, table.getValueAt(i, a).toString().trim().toLowerCase());
                    preState.setBoolean(colsAm, (Boolean) table.getValueAt(i, colsAm - 1));
                    preState.setInt(colsAm + 1, id);
                    preState.executeUpdate();
                    preState.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLearnedWords() {
        StringBuilder sb = new StringBuilder();
        try (ResultSet res = state.executeQuery("SELECT word FROM dictionary WHERE learned = 1")) {
            while (res.next())
                sb.append(res.getString(1)).append("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void addToLearned(String st) {
        String[] words = st.split("\n");
        try {
            for (String word : words)
                state.executeUpdate("UPDATE dictionary SET learned = 1 WHERE word = '" + word + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            state.executeUpdate("DELETE FROM dictionary;");
            state.executeUpdate("DELETE FROM statistics;");
            state.executeUpdate("DELETE FROM wordStatistics;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //..................................................................................................................STATISTICS

    public void addTestSt(int corWords, int allWords, String type) {
        if (corWords != 0 && allWords != 0) {
            try (PreparedStatement preState = conn.prepareStatement
                    ("INSERT INTO statistics(cWords, allWords, type) VALUES (?, ?, ?);")) {
                preState.setInt(1, corWords);
                preState.setInt(2, allWords);
                preState.setString(3, type);
                preState.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void fillStatTable(DefaultTableModel tb) {
        try (ResultSet res = state.executeQuery("SELECT * FROM statistics")) {
            while (res.next())
                tb.addRow(new Object[]{res.getInt(1), res.getInt(2),
                        res.getInt(3), res.getInt(4) + "%", res.getString(5)});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] getStatString(LangM langM) {
        String[] arr = new String[3];
        String[] words = langM.getStatSt();
        try (ResultSet res = state.executeQuery("SELECT COUNT(*) FROM dictionary;")) {
            if (res.next())
                arr[0] = words[0] + res.getInt(1) + words[1];
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (ResultSet res = state.executeQuery("SELECT COUNT(*) FROM dictionary WHERE learned = 1;")) {
            if (res.next())
                arr[1] = words[2] + res.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (ResultSet res = state.executeQuery("SELECT COUNT(*) FROM statistics")) {
            if (res.next())
                arr[2] = words[3] + res.getInt(1) + words[4];
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    //..................................................................................................................WORD STATISTICS

    public void updateCorrWord(int id) {
        try (ResultSet res = state.executeQuery("SELECT count(*) FROM wordStatistics where id_word == " + id + ";")) {
            if (res.next())
                if (res.getInt(1) == 0)
                    state.executeUpdate("INSERT INTO wordStatistics(id_word) VALUES (" + id + ");");
            state.executeUpdate("UPDATE wordStatistics SET correct = correct + 1, lastCorrect = lastCorrect + 1, " +
                    "lastIncorrect = 0 WHERE id_word = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateIncorrWord(int id) {
        try (ResultSet res = state.executeQuery("SELECT count(*) FROM wordStatistics where id_word == " + id + ";")) {
            if (res.next())
                if (res.getInt(1) == 0)
                    state.executeUpdate("INSERT INTO wordStatistics(id_word) VALUES (" + id + ");");
            state.executeUpdate("UPDATE wordStatistics SET lastCorrect = 0, lastIncorrect = lastIncorrect + 1" +
                    " WHERE id_word = " + id + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getHardWords() {
        StringBuilder sb = new StringBuilder();
        try {
            state.executeUpdate("UPDATE wordStatistics SET hard = 1 WHERE lastIncorrect >= 2;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (ResultSet res = state.executeQuery("SELECT word, hard\n" +
                " FROM wordStatistics JOIN dictionary ON id = id_word WHERE hard = 1 AND learned = 0")) {
            while (res.next())
                sb.append(res.getString(1)).append("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getGoodWords() {
        StringBuilder sb = new StringBuilder();
        try (ResultSet res = state.executeQuery("SELECT word, correct, lastCorrect FROM wordStatistics " +
                "JOIN dictionary ON id = id_word " +
                "WHERE ((correct >= 5 AND lastCorrect >= 2) OR lastCorrect >= 3) AND learned = 0;")) {
            while (res.next())
                sb.append(res.getString(1)).append("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            state.executeUpdate("UPDATE wordStatistics SET hard = 0 WHERE (correct >= 5 AND lastCorrect >= 2) " +
                    "OR lastCorrect >= 3;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //..................................................................................................................SETTINGS

    public boolean[] getAllSettings() {
        boolean[] settings = new boolean[5];
        try (ResultSet res = state.executeQuery("SELECT * FROM settings;")) {
            if (res.next())
                for (int i = 0; i < 5; i++)
                    settings[i] = res.getBoolean(i + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void setAllSettings(boolean[] s) {
        try {
            state.executeUpdate("UPDATE settings SET arrow = " + s[0] + ", b1 = " + s[1] + ", b2 = " + s[2] +
                    ", b3 = " + s[3] + ", b4 = " + s[4] + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

