По условию задачи необходимо написать построчную сортировку большого текстового файла, не влезающего в оперативную память. Размер требуемой памяти не должен зависеть от размера файла. Длина строки разумная, одна строка сильно меньше, чем объем памяти. Для проверки работоспособности нужен генератор таких файлов, принимающий в качестве параметров количество строк и их максимальную длину.

Для решения задачи использован метод сортировки слиянием.

В классе Main инициализируем константы. 
- TOTAL_ROWS - необходимое колличество строк в исходном файле.
- ROW_LENGTH - необходимая длина строки.
- LIMIT_ROWS - количество обрабатываемых строк в памяти, можно менять в зависимости от желаемого размера выделяемой памяти jvm
