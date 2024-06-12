package fileController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMoverAndFinder {
    private static final Pattern PATTERN_DATE_AND_TIME_IN_Transfer = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
    private static final Path INPUT_PACK = Paths.get("src/files/inputPack");
    private static final Path OUTPUT_PACK = Paths.get("src/files/outputPack");
    private static final Path ARCHIVE_PACK = Paths.get("src/files/archivePack");
    private static final Path SPAM_PACK = Paths.get("src/files/inputPack/spamPack");
    private static final Path ACCOUNTS_PACK = Paths.get("src/files/accountsPack");

    private static List<File> listInputFiles = new ArrayList<>();

    public static void checkInputFolder(Path inputPack) {
        if (Files.isDirectory(inputPack)) {
            File[] listDirectoryFiles = inputPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    //boolean flagToMoveFiles = false;
                    if (file.isDirectory()) {
                        checkInputFolder(file.toPath());
                    } else if (file.getName().toLowerCase().endsWith(".txt")) {
                        listInputFiles.add(file);
                    } else if (!(inputPack.equals(SPAM_PACK))) {
                        System.out.print("Перемещаю " + file.getName() + " в папку для спама: " + SPAM_PACK.getFileName() + "... -> ");
                        try {
                            Files.move(file.toPath(), SPAM_PACK.resolve(file.toPath().getFileName()),
                                    StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Успешно.");
                            //flagToMoveFiles = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                            /*System.err.println("Перемещение файла " + file.getName() + " не удалось! Либо файл с таким именем уже существует, либо неверный путь.");
                            System.out.println("Пробую обойти проблему... переименовываю файл...");
                            renameAndMoveFile(file, flagToMoveFiles);*/
                        }
                    }
                }
            }
        }
    }

    public static void transferReadFilesToArchive(Path inputPack, Path archivePack) {
        if (Files.isDirectory(inputPack)) {
            File[] listDirectoryFiles = inputPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (!(file.isDirectory())) {
                        System.out.print("Перемещаю " + file.getName() + " в архив, папка: " + archivePack.getFileName() + "... -> ");
                        try {
                            Files.move(file.toPath(), archivePack.resolve(file.toPath().getFileName()),
                                    StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Успешно.");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public static void showReportFileByDate(Path outputPack, LocalDate dateNowMinus, List<String> findToDateList) {
        LocalDate localDateNow = LocalDate.now();
        String readDateTime = "";
        boolean isExist = false;

        if (Files.isDirectory(outputPack)) {
            File[] listDirectoryFiles = outputPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (file.isDirectory()) {
                        showReportFileByDate(file.toPath(), dateNowMinus, findToDateList);
                    } else if (file.getName().equalsIgnoreCase("outputLogger.txt")) {
                        isExist = true;
                        String tempStr;
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                            while ((tempStr = bufferedReader.readLine()) != null) {
                                Matcher matcherFindDateTime = PATTERN_DATE_AND_TIME_IN_Transfer.matcher(tempStr);
                                while (matcherFindDateTime.find()) {
                                    readDateTime = matcherFindDateTime.group();
                                }
                                if (isInTheRange(localDateNow, dateNowMinus, readDateTime)) {
                                    findToDateList.add(tempStr);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (!isExist) {
                    System.err.println("Файл выходных данных не найден! Проверьте существование файла выходных данных по адресу " + outputPack);
                    System.err.println("Программа будет завершена.");
                    System.exit(0);
                }
            }
        }
        for (String dateList : findToDateList) {
            System.out.println(dateList);
        }
        findToDateList.clear();
    }

    public static boolean isInTheRange(LocalDate localDateNow, LocalDate dateNowMinus, String readDateTime) {
        boolean isInTheRange = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate parseReadDateTime = LocalDate.parse(readDateTime, formatter);
        if (((parseReadDateTime.isAfter(dateNowMinus)) || (parseReadDateTime.isEqual(dateNowMinus))) && ((parseReadDateTime.isBefore(localDateNow)) || (parseReadDateTime.isEqual(localDateNow)))) {
            isInTheRange = true;
        }
        return isInTheRange;
    }



    /*private static void renameAndMoveFile(File file, boolean flagToMoveFiles) {
        String baseName = file.getName();
        int indexDot = baseName.lastIndexOf(".");
        String filePrefix = indexDot == -1 ? baseName : baseName.substring(0, indexDot);
        String fileSuffix = indexDot == -1 ? "" : baseName.substring(indexDot);
        int count = 1;
        while (!flagToMoveFiles) {
            String nextPath = filePrefix + count + fileSuffix;
            Path tempPath = Paths.get(nextPath);
            try {
                Files.move(file.toPath(), file.toPath().resolveSibling(tempPath));
                Files.move(file.toPath(), spamPack.resolve(file.toPath().getFileName())StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Файл" + file.getName() + "успешно перемещён.");
                flagToMoveFiles = true;
            } catch (IOException ex) {
                System.err.println("Снова не удалось переместить файл... Ещё попытка...");
                count++;
                if (count == 10) {
                    System.out.println("Слишком много однотипных файлов, проверьте и почистите папку " + spamPack);
                    System.err.println("Программа будет завершена.");
                    System.exit(0);
                }
            }
        }
    }*/

    public static List<File> getListInputFiles() {
        return listInputFiles;
    }

    public static Path getInputPack() {
        return INPUT_PACK;
    }

    public static Path getAccountsPack() {
        return ACCOUNTS_PACK;
    }

    public static Path getOutputPack() {
        return OUTPUT_PACK;
    }

    public static Path getArchivePack() {
        return ARCHIVE_PACK;
    }

}
