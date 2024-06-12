package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final String FROM_TO_AMOUNT_FILE = "\\\\.+.\\w{1,3};from \\d{5}-\\d{5};to \\d{5}-\\d{5};amount -?\\d+[.,]\\d{0,2}";
    private static final String FILE_NAME = "\\w+\\.{1}\\D{1,3}";
    private static final String FROM = "from \\d{5}-\\d{5}";
    private static final String TO = "to \\d{5}-\\d{5}";
    private static final String ACCOUNT = "\\d{5}-\\d{5}";
    private static final String ACCOUNT_AND_AMOUNT = "\\d{5}-\\d{5}=-?\\d{1,10}";
    private static final String AMOUNT = "amount -?\\d+[.,]\\d{0,2}";
    private static final String SUM = "-?\\d+[.,]\\d{0,2}";

    public static List<String> validationInputData(StringBuilder bufferReadDocuments) {
        List<String> transfersList = new ArrayList<>();
        Pattern patternFromToAmount = Pattern.compile(FROM_TO_AMOUNT_FILE);
        Matcher matcherFromToAmount = patternFromToAmount.matcher(bufferReadDocuments);
        while (matcherFromToAmount.find()) {
            transfersList.add(matcherFromToAmount.group());
        }
        return transfersList;
    }

    public static void checkExistenceAccounts(List<String> transfersList, List<String> accountsList, List<String> ignoreList) {
        Pattern patternFROM = Pattern.compile(FROM);
        Pattern patternACCOUNT = Pattern.compile(ACCOUNT);
        Pattern patternTO = Pattern.compile(TO);
        Pattern patternAMOUNT = Pattern.compile(AMOUNT);
        Pattern patternSUM = Pattern.compile(SUM);

        for (String transferString : transfersList) {
            boolean choose;
            String from = "", to = "", sumAmount = "";
            int countFromMatchAccounts = 0, countToMatchAccounts = 0;
            double sumAmountToInt = 0;

            Matcher matcherFROM = patternFROM.matcher(transferString);
            while (matcherFROM.find()) {
                from = matcherFROM.group();

            }
            Matcher matcherAccountInFromPattern = patternACCOUNT.matcher(from);
            while (matcherAccountInFromPattern.find()) {
                from = matcherAccountInFromPattern.group();
            }

            Matcher matcherTO = patternTO.matcher(transferString);
            while (matcherTO.find()) {
                to = matcherTO.group();
            }
            Matcher matcherToAccount = patternACCOUNT.matcher(to);
            while (matcherToAccount.find()) {
                to = matcherToAccount.group();
            }

            Matcher matcherAmount = patternAMOUNT.matcher(transferString);
            while (matcherAmount.find()) {
                sumAmount = matcherAmount.group();
            }
            Matcher matcherAmountSum = patternSUM.matcher(sumAmount);
            while (matcherAmountSum.find()) {
                sumAmount = matcherAmountSum.group();
                sumAmountToInt = Double.parseDouble(sumAmount);
            }

            for (String accounts : accountsList) {
                String account = "";
                Matcher matcherACCOUNT = patternACCOUNT.matcher(accounts);
                while (matcherACCOUNT.find()) {
                    account = matcherACCOUNT.group();
                }
                if (from.equalsIgnoreCase(account)) {
                    countFromMatchAccounts++;
                }
                if (to.equalsIgnoreCase(account)) {
                    countToMatchAccounts++;
                }
            }

            if (countFromMatchAccounts == 0) {
                System.out.println("Счет-отправитель " + from + " не существует!");
                System.out.println("Перевод будет проигнорирован и добавлен в игнор-лист.");
                ignoreList.add(transferString);
                continue;
            } else if (countFromMatchAccounts > 1) {
                System.err.println("Счет-отправитель " + from + " - обнаружен двойник в базе! Проведите проверку файла-счетов");
                System.err.println("Программа будет завершена, после исправления попробуйте снова.");
                System.exit(0);
            }

            if (countToMatchAccounts == 0) {
                System.out.println("Счет-получатель " + to + " не существует!");
                if (sumAmountToInt <= 0) {
                    System.err.println("Создание нового счета-получателя невозможно, так как перевод со счета-отправителя " + from + " отрицательный, либо равен нулю.");
                    System.err.println("Перевод будет проигнорирован и добавлен в игнор-лист.");
                    ignoreList.add(transferString);
                } else {
                    choose = ConsoleView.createOrIgnoreAccount();
                    if (choose) {
                        accountsList.add(to + "=0");
                        System.out.println("Создан новый счет " + to + " с балансом 0 для возможности дальнейшего пополнения.");
                    } else {
                        System.out.println("Перевод будет проигнорирован и добавлен в игнор-лист.");
                        ignoreList.add(transferString);
                    }
                }
            } else if (countToMatchAccounts > 1) {
                System.err.println("Счет-получатель " + to + " - обнаружен двойник в базе! Проведите проверку файла-счетов");
                System.err.println("Программа будет завершена, после исправления попробуйте снова.");
                System.exit(0);
            }
        }
    }

    public static String getACCOUNT_AND_AMOUNT() {
        return ACCOUNT_AND_AMOUNT;
    }

    public static String getFROM() {
        return FROM;
    }

    public static String getTO() {
        return TO;
    }

    public static String getACCOUNT() {
        return ACCOUNT;
    }

    public static String getAMOUNT() {
        return AMOUNT;
    }

    public static String getSUM() {
        return SUM;
    }

    public static String getFILE_NAME() {
        return FILE_NAME;
    }
}