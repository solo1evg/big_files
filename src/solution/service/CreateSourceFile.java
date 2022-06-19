package solution.service;


import solution.helpers.RandomString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


/**
 * Создает файл с определенным количеством строк определенной длины,
 * позволяет создавать большой файл с использованием небольшого количество оперативной памяти.
 */
public class CreateSourceFile {

    /**
     * Генерирует строки определенной длины и записывает в файл
     *
     * @param sourcePath путь к генерируемому файлу
     * @param totalRows  общее количество строк
     * @param limitRows  ограничение для одновременной записи в файл
     * @param rowLength  длина создаваемой строки
     */
    public void create(String sourcePath, int totalRows, int limitRows, int rowLength) throws IOException {
        List<String> rows = new ArrayList<>(limitRows);
        RandomString randomString = new RandomString();

        Path path = Paths.get(sourcePath);
        Files.deleteIfExists(path);
        Files.createFile(path);

        for (int i = 0; i < totalRows; ) {
            for (int j = 0; j < limitRows; j++, i++) {
                rows.add(randomString.getRandomString(rowLength));
            }
            Files.write(path, rows, StandardOpenOption.APPEND);
            rows.clear();
        }
    }
}
