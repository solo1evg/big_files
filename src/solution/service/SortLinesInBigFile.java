package solution.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Класс берет файл ресурс, разбивает на множество временных файлов с ограниченным количеством строк.
 * Сортирует строки и записывает в результирующий файл, используя определенное количество оперативной памяти,
 * за счет разделения на множество временных файлов с лимитированным количеством строк
 */
public class SortLinesInBigFile {
    private int limitRows;
    private final String TEMP_FILES_DIR = "temp";
    private final String TEMP_SPLIT_FILES_DIR = TEMP_FILES_DIR + "/temp";

    /**
     * Берет файл ресурс, сортирует строки и записывает результат в другой файл.
     * Файл ресурс будет разбит на временные файлы, содержащие ограниченное количество строк.
     *
     * @param sourceFile файл ресурс
     * @param targetFile файл с отсортированными в естественном порядке строками
     * @param limitRows  ограничение количества строк при разбиении во временные файлы
     */
    public void sort(String sourceFile, String targetFile, int limitRows) throws IOException {
        this.limitRows = limitRows;
        File tempDir = createTempDir();
        splitFileAndSortLines(sourceFile);
        sorting(targetFile);
        deleteFolder(tempDir);
    }

    /**
     * Метод создает временную директорию для размещения разбитых на части сортированных временных файлов
     *
     * @return путь к директории
     */
    private File createTempDir() throws IOException {
        File tempDir = new File(TEMP_FILES_DIR);
        if (tempDir.exists()) {
            deleteFolder(tempDir);
        }
        Files.createDirectory(Paths.get(TEMP_FILES_DIR));
        return tempDir;
    }

    /**
     * Метод делит исходный файл на части и сортирует содержимое каждого файла в порядке естественной сортировки
     *
     * @param sourceFile исходный файл
     */
    private void splitFileAndSortLines(String sourceFile) throws IOException {
        List<String> result = new ArrayList<>(limitRows);
        String row;
        int rowCount = 1;
        int partCount = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
            Files.createDirectory(Paths.get(TEMP_SPLIT_FILES_DIR));
            while ((row = br.readLine()) != null) {
                result.add(row);
                if (rowCount < limitRows) {
                    rowCount++;
                } else {
                    rowCount = 1;
                    result.sort(String::compareTo);
                    Files.write(Paths.get(TEMP_SPLIT_FILES_DIR + "/" + partCount + ".txt"), result, StandardOpenOption.CREATE_NEW);
                    result.clear();
                    partCount++;
                }
            }
        }
    }

    /**
     * Метод сравнивает строки во временных файлах и записывает отсортированный результат в результирующий файл
     *
     * @param resultPath путь к результирующему файлу
     */
    private void sorting(String resultPath) throws IOException {
        File splitFilesDir = new File(TEMP_SPLIT_FILES_DIR);
        long dirCount = 1;
        long fileCount = 1;
        String dirPath = TEMP_SPLIT_FILES_DIR;
        String filePath;

        while (Objects.requireNonNull(splitFilesDir.listFiles()).length > 1) {
            if (Objects.requireNonNull(splitFilesDir.listFiles()).length % 2 != 0) { // если нечетное количество файлов, то объединяем первые 2
                File[] files = splitFilesDir.listFiles();
                filePath = dirPath + "/merge.txt";
                merge(files[0], files[1], filePath);
                files[0].delete();
                files[1].delete();
            }
            // создаём новую папку для хранения объединенных файлов
            dirPath = TEMP_SPLIT_FILES_DIR + dirCount;
            Files.createDirectory(Paths.get(dirPath));
            // попарно объединяем файлы
            File[] arrayFile = splitFilesDir.listFiles();
            for (int i = 0; i < Objects.requireNonNull(arrayFile).length; i += 2) {
                if (arrayFile.length == 2) {
                    filePath = resultPath; //если в папке 2 файла, то записываем в итоговый файл
                } else {
                    filePath = dirPath + "/" + fileCount + ".txt";
                }
                merge(arrayFile[i], arrayFile[i + 1], filePath);
                fileCount++;
            }
            dirCount++;
            fileCount = 1;

            deleteFolder(splitFilesDir); // удаляем отработанную папку
            splitFilesDir = new File(dirPath); // присваиваем путь к созданной папке для следующего цикла объединения
        }
    }

    /**
     * Метод объединяет два заранее отсортированных временных файла
     *
     * @param source1        первый файл
     * @param source2        второй файл
     * @param resultFilePath путь для записи результата объединения
     */
    private void merge(File source1, File source2, String resultFilePath) throws IOException {
        try (BufferedReader br1 = new BufferedReader(new FileReader(source1));
             BufferedReader br2 = new BufferedReader(new FileReader(source2))) {

            // создаём файл с результатом слияния
            Path resultPath = Paths.get(resultFilePath);
            Files.deleteIfExists(resultPath);
            Files.createFile(resultPath);

            // создаём 2 списка, из которых будем брать строки для сравнения,
            // и один результирующий список
            LinkedList<String> listCompare1 = new LinkedList<>();
            LinkedList<String> listCompare2 = new LinkedList<>();
            LinkedList<String> listResult = new LinkedList<>();
            // строки чтения из файла источника и строк записи в итоговый файл
            String lineRead1 = null;
            String lineRead2 = null;
            String lineWrite1 = null;
            String lineWrite2 = null;
            // флаги указывающие нужно ли считывать следующую строку
            boolean updateLine1;
            boolean updateLine2;
            // флаг указывающий последнюю итерацию
            boolean isLastIteration = false;

            while (!isLastIteration) {
                // наполняем 2 списка до ограниченного размера
                while (listCompare1.size() < limitRows / 4 && (lineRead1 = br1.readLine()) != null) {
                    listCompare1.add(lineRead1);
                }
                while (listCompare2.size() < limitRows / 4 && (lineRead2 = br2.readLine()) != null) {
                    listCompare2.add(lineRead2);
                }

                isLastIteration = (lineRead1 == null && listCompare1.isEmpty())
                        || (lineRead2 == null && listCompare2.isEmpty());

                updateLine1 = true;
                updateLine2 = true;

                while (!isLastIteration) { // цикл сравнения

                    if (updateLine1) {
                        if (listCompare1.isEmpty()) { // выходим из цикла, если список сравнения пустой
                            listCompare2.addFirst(lineWrite2); //возвращаем строку в список, так как выходим из цикла сравнения
                            break;
                        }
                        lineWrite1 = listCompare1.removeFirst(); // берём строку для сравнения из первого списка
                        updateLine1 = false;
                    }
                    if (updateLine2) {
                        if (listCompare2.isEmpty()) {
                            listCompare1.addFirst(lineWrite1);
                            break;
                        } else
                            lineWrite2 = listCompare2.removeFirst(); // берём строку для сравнения из второго списка
                        updateLine2 = false;
                    }

                    //  сравниваем и строку с меньшим значением, записываем в результирующий список
                    //  указываем, что её нужно будет обновить
                    if (lineWrite1.compareTo(lineWrite2) <= 0) {
                        listResult.add(lineWrite1);
                        updateLine1 = true;

                    } else {
                        listResult.add(lineWrite2);
                        updateLine2 = true;
                    }
                }
                // вышли из цикла сравнения, записываем результирующий список в итоговый файл
                Files.write(resultPath, listResult, StandardOpenOption.APPEND);
                listResult.clear();
                // если это последняя итерация, записываем всё, что не пустое в итоговый файл
                if (isLastIteration) {
                    if (!listCompare1.isEmpty()) {
                        Files.write(resultPath, listCompare1, StandardOpenOption.APPEND);
                        listCompare1.clear();
                    }
                    if (!listCompare2.isEmpty()) {
                        Files.write(resultPath, listCompare2, StandardOpenOption.APPEND);
                        listCompare2.clear();
                    }
                    if (lineRead1 != null) {
                        while ((lineRead1 = br1.readLine()) != null) {
                            listCompare1.add(lineRead1);
                        }
                        Files.write(resultPath, listCompare1, StandardOpenOption.APPEND);
                        listCompare1.clear();
                    } else {
                        while ((lineRead2 = br2.readLine()) != null) {
                            listCompare2.add(lineRead2);
                        }
                        Files.write(resultPath, listCompare2, StandardOpenOption.APPEND);
                        listCompare2.clear();
                    }
                }
            }
        }
    }

    /**
     * Метод удаляет временные файлы и директории.
     *
     * @param folder
     * @return
     */
    private void deleteFolder(File folder) {
        if (folder.listFiles() == null) {
            System.err.println("Папка не доступна");
            return;
        }
        //в цикле листаем временную папку и удаляем все файлы-фрагменты
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            //если это директория, очищаем и удаляем папку
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        //удаляем пустую папку
        if (Objects.requireNonNull(folder.listFiles()).length == 0) {
            folder.delete();
        }
    }
}
