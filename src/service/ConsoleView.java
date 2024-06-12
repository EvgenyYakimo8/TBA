package service;

import fileController.FileBrowser;
import fileController.FileMoverAndFinder;

import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleView extends FileBrowser {
    public static void choosingAction() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            int choose = 0;
            boolean validationFlag = false;

            System.out.println("""
                                                 TBA
                                   ♦ "Transfer to a bank account" ♦
                    __________________________________________________________________
                    Сделайте ваш выбор :                                              |
                            1. Парсинг входных файлов с данными и выполнение переводов;   |
                            2. Вывод всех данных файла-отчета;                            |
                            3. Вывод всех данных файла-отчета за промежуток;              |
                            4. Показать текущие данные счетов (Персональные данные!);     |
                            5. Вывести список всех проверок, реализованных в программе;   |
                            6. Выйти из программы.                                        |
                    ------------------------------------------------------------------
                    """);

            while (!validationFlag) {
                if (scanner.hasNextInt()) {
                    choose = scanner.nextInt();
                    if (choose >= 1 && choose <= 6) {
                        validationFlag = true;
                    } else {
                        System.out.println("Необходимо ввести число от 1 до 6. Попробуйте снова.");
                    }
                } else {
                    scanner.nextLine();
                    System.out.println("Необходимо ввести число от 1 до 6. Попробуйте снова.");
                }
            }
            switch (choose) {
                case 1 -> FileBrowser.parsingInputFiles();
                case 2 -> FileBrowser.showReportFile(FileMoverAndFinder.getOutputPack());
                case 3 -> fileOutputInTime();
                case 4 -> FileBrowser.showAccountsFile(FileMoverAndFinder.getAccountsPack());
                case 5 -> implementedChecks();
                case 6 -> System.exit(0);
            }
        }
    }

    public static boolean createOrIgnoreAccount() {
        Scanner scanner = new Scanner(System.in);
        int choose = 0;
        boolean validationFlag = false;
        boolean ischoose;
        System.out.println("""
                ________________________________________________________________________________________
                Сделайте ваш выбор :                                                                    |
                    1. Создать новый счет-получатель и начислить перевод (тип перевода "Пополнение");   |
                    2. Игнорировать перевод;                                                            |
                ----------------------------------------------------------------------------------------
                """);
        while (!validationFlag) {
            if (scanner.hasNextInt()) {
                choose = scanner.nextInt();
                if (choose >= 1 && choose <= 2) {
                    validationFlag = true;
                } else {
                    System.out.println("Необходимо ввести число от 1 до 2. Попробуйте снова.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Необходимо ввести число от 1 до 2. Попробуйте снова.");
            }
        }
        ischoose = choose == 1;
        return ischoose;
    }

    public static void fileOutputInTime() {
        Scanner scanner = new Scanner(System.in);
        LocalDate dateNow = LocalDate.now();
        LocalDate dateNowMinusDay;
        LocalDate dateNowMinusWeek;
        LocalDate dateNowMinusMonths;
        int choose = 0;
        boolean validationFlag = false;
        System.out.println("""
                _________________________________________________________________________
                Выберите время выборки:                                                  |
                    1. за Сегодня;                                                       |
                    2. за Сегодня - Вчера;                                               |
                    3. за последнюю Неделю;                                              |
                    4. за последний Месяц;                                               |
                -------------------------------------------------------------------------
                """);
        while (!validationFlag) {
            if (scanner.hasNextInt()) {
                choose = scanner.nextInt();
                if (choose >= 1 && choose <= 4) {
                    validationFlag = true;
                } else {
                    System.out.println("Необходимо ввести число от 1 до 4. Попробуйте снова.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Необходимо ввести число от 1 до 4. Попробуйте снова.");
            }
        }
        switch (choose) {
            case 1 ->
                    FileMoverAndFinder.showReportFileByDate(FileMoverAndFinder.getOutputPack(), dateNow, FileBrowser.getFindToDateList());
            case 2 -> {
                dateNowMinusDay = dateNow.minusDays(1);
                FileMoverAndFinder.showReportFileByDate(FileMoverAndFinder.getOutputPack(), dateNowMinusDay, FileBrowser.getFindToDateList());
            }
            case 3 -> {
                dateNowMinusWeek = dateNow.minusWeeks(1);
                FileMoverAndFinder.showReportFileByDate(FileMoverAndFinder.getOutputPack(), dateNowMinusWeek, FileBrowser.getFindToDateList());
            }
            case 4 -> {
                dateNowMinusMonths = dateNow.minusMonths(1);
                FileMoverAndFinder.showReportFileByDate(FileMoverAndFinder.getOutputPack(), dateNowMinusMonths, FileBrowser.getFindToDateList());
            }
        }
    }

    public static void implementedChecks() {
        System.out.println("""
                ☻ Реализованные проверки и защиты в программе ->
                ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
                1. Защита от некорректного ввода данных в консоли, проверка принимаемого типа int, проверка диапазона ввода по количеству пунктов списка.
                ♦
                2. Первичная проверка файлов и папок при работе с ними на существование и пустоту (Например: Это папка? Она пустая? Список файлов содержит хоть один? Этот файл мы ищем?
                ♦  Перед чтением или записью проверка на существование. Обработка негативного результата).
                ♦
                3. При чтении/записи данных файлов повторные проверки из пункта (2) после отработки "валидатора" transfersList и accountsList проверяются на пустоту (в случае если Regex-выражение ничего не нашло в файлах).
                ♦
                4. "Валидатор" проверяет все поступившие данные (transfersList и accountsList) на конфликты логики, например: Есть ли у счетов двойники(защита от случайного дублирования счетов в базе(accounts.txt))?
                ♦  Существуют ли счет-отправитель и(или) счет-получатель? Хватает ли денег для перевода(а также есть ли они вообще?). Если сумма указана(или нет), а на счету 0 или задолженность - это будет проверено.
                ♦
                5. После валидации при совершении переводов делаются проверки на корректную сумму перевода (если указан 0 - программа скажет, что об этом думает),а так же хватает ли средств на соответствующих счетах
                ♦  в случаях пополнения или снятия средств (число со знаком минус "-" снятие, со знаком "+" пополнение).
                ♦
                6. Реализовано усложнение программы по заданию: перевод средств с дробной частью и вывод отчета за определённый период.
                ♦
                7. Программа была продумана и реализована без необходимости создавать объекты классов, так как цель программы - перевод средств и заполнение файла-отчета. Держателей счетов, как клиентов с их характеристиками и параметрами, нет.
                ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
                """);
    }
}