package fileController;

import operationAccount.Account;
import service.Validator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileBrowser extends FileMoverAndFinder {
    private static boolean isTransfersEmpty = false;
    private static boolean isAccountsEmpty = false;
    private static List<String> transfersList = new ArrayList<>();
    private static List<String> accountsList = new ArrayList<>();
    private static List<String> ignoreList = new ArrayList<>();
    private static Map<String, Double> accountsMap = new HashMap<>();
    private static List<String> outputList = new ArrayList<>();
    private static List<String> findToDateList = new ArrayList<>();

    public static void parsingInputFiles() {
        FileMoverAndFinder.checkInputFolder(FileMoverAndFinder.getInputPack());
        transfersList = readInputFiles(FileMoverAndFinder.getListInputFiles());
        accountsList = readAccounts(FileMoverAndFinder.getAccountsPack());
        isInputFilesEmpty(isTransfersEmpty, isAccountsEmpty);
        Validator.checkExistenceAccounts(transfersList, accountsList, ignoreList);
        Account.transferToAccount(transfersList, accountsList, ignoreList, accountsMap, outputList);
        writeAccounts(FileMoverAndFinder.getAccountsPack(), accountsMap);
        writeReportFile(FileMoverAndFinder.getOutputPack(), outputList);
        FileMoverAndFinder.transferReadFilesToArchive(FileMoverAndFinder.getInputPack(), FileMoverAndFinder.getArchivePack());
    }

    public static List<String> readInputFiles(List<File> listInputFiles) {
        List<String> listInputPath = listInputFiles.stream().map(File::toString).toList();
        List<String> transfersList;
        StringBuilder bufferReadDocuments = new StringBuilder();

        for (String strFile : listInputPath) {
            String tempStr;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(strFile))) {
                while ((tempStr = bufferedReader.readLine()) != null) {
                    bufferReadDocuments.append(strFile).append(";").append(tempStr).append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        transfersList = Validator.validationInputData(bufferReadDocuments);
        isTransfersEmpty = transfersList.isEmpty();
        return transfersList;
    }

    public static List<String> readAccounts(Path accountsPack) {
        boolean isExist = false;
        List<String> accountsList = new ArrayList<>();
        Pattern patternAccountAndAmount = Pattern.compile(Validator.getACCOUNT_AND_AMOUNT());

        if (Files.isDirectory(accountsPack)) {
            File[] listDirectoryFiles = accountsPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (file.isDirectory()) {
                        readAccounts(file.toPath());
                    } else if (file.getName().equalsIgnoreCase("accounts.txt")) {
                        isExist = true;
                        String tempStr;
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                            while ((tempStr = bufferedReader.readLine()) != null) {
                                Matcher matcherAccountAndAmount = patternAccountAndAmount.matcher(tempStr);
                                while (matcherAccountAndAmount.find()) {
                                    accountsList.add(tempStr);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (!isExist) {
                    System.err.println("Файл-счетов не найден! Проверьте существование файла-счетов по адресу " + accountsPack);
                    System.err.println("Программа будет завершена.");
                    System.exit(0);
                }
            } else {
                System.err.println("Папка " + accountsPack.getFileName() + " не содержит файлов.");
                System.err.println("Проверьте это и повторите. Программа будет завершена.");
                System.exit(0);
            }
            isAccountsEmpty = accountsList.isEmpty();
        }
        return accountsList;
    }

    public static void writeAccounts(Path accountsPack, Map<String, Double> accountsMap) {
        boolean isExist = false;
        if (Files.isDirectory(accountsPack)) {
            File[] listDirectoryFiles = accountsPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (file.isDirectory()) {
                        writeAccounts(file.toPath(), accountsMap);
                    } else if (file.getName().equalsIgnoreCase("accounts.txt")) {
                        isExist = true;
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                            for (Map.Entry<String, Double> entry : accountsMap.entrySet()) {
                                bufferedWriter.write(entry.getKey() + "=" + entry.getValue());
                                bufferedWriter.newLine();
                            }
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (!isExist) {
                    System.err.println("Файл-счетов не найден! Проверьте существование файла-счетов по адресу " + accountsPack);
                    System.err.println("Программа будет завершена.");
                    System.exit(0);
                }
            } else {
                System.err.println("Папка " + accountsPack.getFileName() + " не содержит файлов.");
                System.err.println("Проверьте это и повторите. Программа будет завершена.");
                System.exit(0);
            }
        }
    }

    public static void writeReportFile(Path outputPack, List<String> outputList) {
        boolean isExist = false;
        File outputLogger = new File(String.valueOf(outputPack.resolve("outputLogger.txt")));
        if (Files.isDirectory(outputPack)) {
            File[] listDirectoryFiles = outputPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (file.isDirectory()) {
                        writeReportFile(file.toPath(), outputList);
                    } else if (file.getName().equalsIgnoreCase("outputLogger.txt")) {
                        isExist = true;
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
                            for (String outputString : outputList) {
                                bufferedWriter.write(outputString);
                                bufferedWriter.newLine();
                            }
                            bufferedWriter.flush();
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
            } else {
                System.err.println("Папка " + outputPack.getFileName() + " не содержит файлов.");
                System.err.println("Проверьте это и повторите. Программа будет завершена.");
                System.exit(0);
            }
        }
    }

    public static void showReportFile(Path outputPack) {
        boolean isExist = false;
        if (Files.isDirectory(outputPack)) {
            File[] listDirectoryFiles = outputPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (file.isDirectory()) {
                        showReportFile(file.toPath());
                    } else if (file.getName().equalsIgnoreCase("outputLogger.txt")) {
                        isExist = true;
                        String tempStr;
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                            while ((tempStr = bufferedReader.readLine()) != null) {
                                System.out.println(tempStr);
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
            } else {
                System.err.println("Папка " + outputPack.getFileName() + " не содержит файлов.");
                System.err.println("Проверьте это и повторите. Программа будет завершена.");
                System.exit(0);
            }
        }
    }

    public static void showAccountsFile(Path accountsPack) {
        boolean isExist = false;
        if (Files.isDirectory(accountsPack)) {
            File[] listDirectoryFiles = accountsPack.toFile().listFiles();
            if (listDirectoryFiles != null) {
                for (File file : listDirectoryFiles) {
                    if (file.isDirectory()) {
                        showAccountsFile(file.toPath());
                    } else if (file.getName().equalsIgnoreCase("accounts.txt")) {
                        isExist = true;
                        String tempStr;
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                            while ((tempStr = bufferedReader.readLine()) != null) {
                                System.out.println(tempStr);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (!isExist) {
                    System.err.println("Файл-счетов не найден! Проверьте существование Файл-счетов по адресу " + accountsPack);
                    System.err.println("Программа будет завершена.");
                    System.exit(0);
                }
            } else {
                System.err.println("Папка " + accountsPack.getFileName() + " не содержит файлов.");
                System.err.println("Проверьте это и повторите. Программа будет завершена.");
                System.exit(0);
            }
        }
    }

    public static void isInputFilesEmpty(boolean isTransfersEmpty, boolean isAccountsEmpty) {
        if (isTransfersEmpty) {
            System.err.println("Файл переводов пуст или отсутствует, проверьте источник по адресу " + FileMoverAndFinder.getInputPack() + " и попробуйте снова.");
            System.err.println("Программа будет завершена.");
            System.exit(0);
        } else if (isAccountsEmpty) {
            System.err.println("Файл-счетов пуст или отсутствует, проверьте источник по адресу " + FileMoverAndFinder.getAccountsPack() + " и попробуйте снова.");
            System.err.println("Программа будет завершена.");
            System.exit(0);
        }
    }

    public static List<String> getFindToDateList() {
        return findToDateList;
    }
}
