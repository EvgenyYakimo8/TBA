package operationAccount;

import service.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
    public static void transferToAccount(List<String> transfersList, List<String> accountsList, List<String> ignoreList, Map<String, Double> accountsMap, List<String> outputList) {
        transfersList.removeIf(ignoreList::contains);
        for (String account : accountsList) {
            String[] tempStrArray = account.split("=");
            accountsMap.put(tempStrArray[0], Double.valueOf(tempStrArray[1]));
        }

        String from = "", to = "", sumAmount = "", dateNow, fileName = "", resultTransfer;
        double sumAmountToInt = 0, sumAccountFrom, sumAccountTo;
        int checkResult;

        Pattern patternFROM = Pattern.compile(Validator.getFROM());
        Pattern patternTO = Pattern.compile(Validator.getTO());
        Pattern patternACCOUNT = Pattern.compile(Validator.getACCOUNT());
        Pattern patternAMOUNT = Pattern.compile(Validator.getAMOUNT());
        Pattern patternSUM = Pattern.compile(Validator.getSUM());
        Pattern patternFILE_NAME = Pattern.compile(Validator.getFILE_NAME());

        for (String transfer : transfersList) {
            Matcher matcherFILE_NAME = patternFILE_NAME.matcher(transfer);
            while (matcherFILE_NAME.find()) {
                fileName = matcherFILE_NAME.group();
            }

            Matcher matcherFROM = patternFROM.matcher(transfer);
            while (matcherFROM.find()) {
                from = matcherFROM.group();
            }
            Matcher matcherAccountFrom = patternACCOUNT.matcher(from);
            while (matcherAccountFrom.find()) {
                from = matcherAccountFrom.group();
            }

            Matcher matcherTo = patternTO.matcher(transfer);
            while (matcherTo.find()) {
                to = matcherTo.group();
            }
            Matcher matcherAccountTo = patternACCOUNT.matcher(to);
            while (matcherAccountTo.find()) {
                to = matcherAccountTo.group();
            }

            Matcher matcherAmount = patternAMOUNT.matcher(transfer);
            while (matcherAmount.find()) {
                sumAmount = matcherAmount.group();
            }
            Matcher matcherSum = patternSUM.matcher(sumAmount);
            while (matcherSum.find()) {
                sumAmount = matcherSum.group();
                sumAmountToInt = Double.parseDouble(sumAmount);
            }
            sumAccountFrom = accountsMap.get(from);
            sumAccountTo = accountsMap.get(to);
            if (sumAmountToInt == 0) {
                System.err.println("Перевод от " + from + " к " + to + " не выполнен, так-как не корректно указана сумма, он содержит " + sumAmountToInt);
                checkResult = 1;
            } else if (sumAmountToInt > 0) {
                if ((sumAccountFrom <= 0) || ((sumAccountFrom - Math.abs(sumAmountToInt)) < 0)) {
                    System.err.println("Перевод(пополнение) невозможен, у отправителя " + from + " нехватает средств.");
                    checkResult = 2;
                } else {
                    sumAccountFrom -= sumAmountToInt;
                    sumAccountTo += sumAmountToInt;
                    accountsMap.put(from, (Math.round(sumAccountFrom * 100D) / 100D));
                    accountsMap.put(to, (Math.round(sumAccountTo * 100D) / 100D));
                    checkResult = 4;
                }
            } else {
                if ((sumAccountTo <= 0) || ((sumAccountTo - Math.abs(sumAmountToInt)) < 0)) {
                    System.err.println("Перевод(снятие) невозможен, у отправителя " + to + " нехватает средств.");
                    checkResult = 3;
                } else {
                    sumAccountTo -= Math.abs(sumAmountToInt);
                    sumAccountFrom += Math.abs(sumAmountToInt);
                    accountsMap.put(to, (Math.round(sumAccountTo * 100D) / 100D));
                    accountsMap.put(from, (Math.round(sumAccountFrom * 100D) / 100D));
                    checkResult = 5;
                }
            }

            resultTransfer = switch (checkResult) {
                case 1 ->
                        "Перевод от " + from + " к " + to + " не выполнен, так-как не корректно указана сумма, он содержит " + sumAmountToInt;
                case 2 -> "Перевод(пополнение) невозможен, у отправителя " + from + " нехватает средств.";
                case 3 -> "Перевод(снятие) невозможен, у отправителя " + to + " нехватает средств.";
                case 4 -> "Перевод(пополнение) успешно выполнен";
                case 5 -> "Перевод(снятие) успешно выполнен";
                default -> "Перевод успешно выполнен;";
            };
            dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSSS"));
            outputList.add(dateNow + " | " + fileName + " | " + "from: " + from + " | " + "to: " + to + " | " + sumAmount + " | " + resultTransfer);
        }
    }
}