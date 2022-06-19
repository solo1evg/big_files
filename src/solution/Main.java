package solution;

import solution.service.CreateSourceFile;

import solution.service.SortLinesInBigFile;

import java.io.IOException;

public class Main {
    private final static int TOTAL_ROWS = 10_000_000;
    private final static int LIMIT_ROWS = 100_000;
    private final static int ROW_LENGTH = 11;
    private final static String SOURCE_FILE = "src/solution/resource/source.txt";
    private final static String TARGET_FILE = "src/solution/resource/target.txt";

    public static void main(String[] args) throws IOException {
        createSourceFile();
        sortFile();
    }

    private static void createSourceFile() throws IOException {
        long l1 = System.currentTimeMillis();
        CreateSourceFile csf = new CreateSourceFile();
        csf.create(SOURCE_FILE, TOTAL_ROWS, LIMIT_ROWS, ROW_LENGTH);
        long l2 = System.currentTimeMillis();
        System.out.printf("Время создания файла: %.3f сек%n", (l2 - l1)/1e3);
    }

    private static void sortFile() {
        long l1 = System.currentTimeMillis();
        SortLinesInBigFile sortFile = new SortLinesInBigFile();
        try {
            sortFile.sort(SOURCE_FILE, TARGET_FILE, LIMIT_ROWS);
        } catch (IOException e) {
            System.err.println("Не удалось отсортировать файл с помощью метода sort класса SortFileImpl ");
            e.printStackTrace();
        }
        long l2 = System.currentTimeMillis();
        System.out.printf("Время сортировки: %.3f сек%n", (l2 - l1)/1e3);
    }
}