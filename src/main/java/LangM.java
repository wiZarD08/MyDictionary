import javax.swing.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class LangM {
    private final String[] eng = {"Space", "Arrows", "Word/Phrase", "Translation", "-> translation", "-> word",
            "Definition/Examples", "Learned", "First side of the card", "Second side of the card", "Hard words",
            "Well-learned", "in Learned", "Corr", "Total", "Type", "Add", "Search", "Test", "Flashcards",
            "Word list", "Statistics", "Skip/Next", "Check", "Correct answer:", "Go to statistics",
            "Start", "flashcards", "Add to learned", "Repeat", "Clear all data"};
    private final String[] rus = {"Пробел", "Стрелки", "Слово/Фраза", "Перевод", "-> перевод", "-> слово",
            "Определение/Примеры", "Изучено", "Первая сторона карточки", "Вторая сторона карточки", "Трудные слова",
            "Хорошо выученное", "Изученное", "Прав", "Всего", "Тип", "Добавить", "Поиск", "Тест", "Карточки",
            "Список слов", "Статистика", "Пропуск/Далее", "Проверка", "Правильный ответ:", "Перейти к статистике",
            "Начать", "Повторить по карточкам", "Добавить в изученое", "Повторить", "Отчистить всё"};
    private final JLabel[] labels = new JLabel[11];
    private final JButton[] buttons = new JButton[15];
    private final JRadioButton[] rb = new JRadioButton[6];

    public void intoEng() {
        int index;
        for (JRadioButton r : rb) {
            if (r != null && (index = IntStream.range(0, rus.length).filter(i -> r.getText().equals(rus[i])).findFirst().
                    orElse(-1)) != -1) r.setText(eng[index]);
        }
        InfoFrame.revalidate("InfoEng");
        System.out.println(Arrays.toString(rb));
    }

    public void intoRus() {
        int index;
        for (JRadioButton r : rb) {
            if (r != null && (index = IntStream.range(0, eng.length).filter(i -> r.getText().equals(eng[i])).findFirst().
                    orElse(-1)) != -1) r.setText(rus[index]);
            InfoFrame.revalidate("InfoRus");
        }
    }

    public void addLab(JLabel l) {
        for (int i = 0; i < 11; i++)
            if (labels[i] == null)
                labels[i] = l;
    }

    public void addBut(JButton b) {
        for (int i = 0; i < 15; i++)
            if (buttons[i] == null)
                buttons[i] = b;
    }

    public void addRB(JRadioButton r) {
        for (int i = 0; i < 6; i++)
            if (rb[i] == null) {
                rb[i] = r;
                break;
            }
    }
}
