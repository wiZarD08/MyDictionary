import javax.swing.*;
import java.util.stream.IntStream;

public class LangM {
    private boolean isRus = true;
    private final String[] eng = {"Result:", "Space", "Arrows", "Word/Phrase", "Translation", "-> translation", "-> word",
            "Definition/Examples", "Learned", "First side of the card", "Second side of the card", "Hard words",
            "Well-learned", "in Learned      ", "Corr", "Total", "Type", "Add", "Search", "Test", "Flashcards",
            "Word list", "Statistics", "Skip/Next", "Check", "Correct answer:", "Go to statistics",
            "Start", "flashcards", "Add to learned", "Repeat", "Clear all data"};
    private final String[] rus = {"Результат:", "Пробел", "Стрелки", "Слово/Фраза", "Перевод", "-> перевод", "-> слово",
            "Определение/Примеры", "Изучено", "Первая сторона карточки", "Вторая сторона карточки", "Трудные слова",
            "Хорошо выученное", "Изученное       ", "Прав", "Всего", "Тип", "Добавить", "Поиск", "Тест", "Карточки",
            "Список слов", "Статистика", "Пропуск/Далее", "Проверка", "Правильный ответ:", "Перейти к статистике",
            "Начать", "Повторить по карточкам", "Добавить в изученное", "Повторить", "Отчистить всё"};
    private final String[] rusStat = {"Добавлено ", " слов", "Выучено ", "Пройдено ", " тестов"};
    private final String[] engStat = {"Added ", " words", "Learned ", "Passed ", " tests"};
    private final JLabel[] labels = new JLabel[11];
    private final JButton[] buttons = new JButton[15];
    private final JRadioButton[] rb = new JRadioButton[6];

    public void intoEng() {
        isRus = false;
        int index;
        for (JLabel i : labels) {
            if (i != null && (index = IntStream.range(0, rus.length).filter(e -> i.getText().trim().equals(rus[e])).findFirst().
                    orElse(-1)) != -1) i.setText(eng[index]);
        }
        for (JButton i : buttons) {
            if (i != null && (index = IntStream.range(0, rus.length).filter(e -> i.getText().trim().equals(rus[e])).findFirst().
                    orElse(-1)) != -1) i.setText(eng[index]);
        }
        for (JRadioButton i : rb) {
            if (i != null && (index = IntStream.range(0, rus.length).filter(e -> i.getText().trim().equals(rus[e])).findFirst().
                    orElse(-1)) != -1) i.setText(eng[index]);
        }
        InfoFrame.revalidate("InfoEng");
    }

    public void intoRus() {
        isRus = true;
        int index;
        for (JLabel i : labels) {
            if (i != null && (index = IntStream.range(0, eng.length).filter(e -> i.getText().trim().equals(eng[e])).findFirst().
                    orElse(-1)) != -1) i.setText(rus[index]);
        }
        for (JButton i : buttons) {
            if (i != null && (index = IntStream.range(0, eng.length).filter(e -> i.getText().trim().equals(eng[e])).findFirst().
                    orElse(-1)) != -1) i.setText(rus[index]);
        }
        for (JRadioButton i : rb) {
            if (i != null && (index = IntStream.range(0, eng.length).filter(e -> i.getText().trim().equals(eng[e])).findFirst().
                    orElse(-1)) != -1) i.setText(rus[index]);
        }
            InfoFrame.revalidate("InfoRus");
    }

    public void changeTable(JTable t) {
        int index;
        if (t != null)
            if (isRus)
                for (int i = 0; i < t.getColumnCount(); i++) {
                    int ii = i;
                    if ((index = IntStream.range(0, eng.length).filter(e -> t.getColumnModel().getColumn(ii).getHeaderValue().
                            equals(eng[e])).findFirst().orElse(-1)) != -1)
                        t.getColumnModel().getColumn(ii).setHeaderValue(rus[index]);
                }
            else for (int i = 0; i < t.getColumnCount(); i++) {
                int ii = i;
                if ((index = IntStream.range(0, rus.length).filter(e -> t.getColumnModel().getColumn(ii).getHeaderValue().
                        equals(rus[e])).findFirst().orElse(-1)) != -1)
                    t.getColumnModel().getColumn(ii).setHeaderValue(eng[index]);
            }
    }

    public boolean isRus() {
        return isRus;
    }

    public String[] getStatSt() {
        if (isRus) return rusStat;
        return engStat;
    }

    public String getRes() {
        if (isRus) return rus[0];
        return eng[0];
    }

    public void addLab(JLabel l) {
        for (int i = 0; i < 11; i++)
            if (labels[i] == null) {
                labels[i] = l;
                break;
            }
    }

    public void addBut(JButton b) {
        for (int i = 0; i < 15; i++)
            if (buttons[i] == null) {
                buttons[i] = b;
                break;
            }
    }

    public void addRB(JRadioButton r) {
        for (int i = 0; i < 6; i++)
            if (rb[i] == null) {
                rb[i] = r;
                break;
            }
    }
}
