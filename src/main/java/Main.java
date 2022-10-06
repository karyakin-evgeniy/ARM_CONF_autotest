import io.appium.java_client.MobileElement;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final StringBuilder resultString = new StringBuilder();
    private static final String urlWinApp = "http://127.0.0.1:4723/";
    private static WindowsDriver<WindowsElement> driver = null;
    private static WindowsDriver<WindowsElement> driverRoot = null;
    private static WindowsDriver<WindowsElement> configDriver = null;
    private static final DesiredCapabilities windowsCap = new DesiredCapabilities();
    private static final DesiredCapabilities capConfChoice = new DesiredCapabilities();
    private static final DesiredCapabilities capForOpenARM = new DesiredCapabilities();
    private static final DesiredCapabilities capForOpenConf = new DesiredCapabilities();
    private static final DesiredCapabilities capForOpenLinkMT = new DesiredCapabilities();
    private static final int timer = 3000;
    private static Actions actions;
    private static int testStatus = 0;
    private static JSONObject properties;

    private static String bfpoDirectory;
    private static String bfpoName;
    private static String pmkName;

    public static void main(String[] args) throws IOException, InterruptedException {
        setUp();
        closeConfigurator();
        closeConfigurator();
        resultString.append("Результаты тестирования: ");
        JSONObject tests = properties.getJSONObject("test");
        boolean startAllTests = tests.getBoolean("allTestStart");
        if (startAllTests || tests.getBoolean("TC_ARM_CFG_Compile")) {
            testRunner("TC_ARM_CFG_Compile");
        }
        if (startAllTests || tests.getBoolean("TC_redundancy_protocol")) {
            testRunner("TC_redundancy_protocol");
        }
        if (startAllTests || tests.getBoolean("TC_change_project_param_and_get_osc")) {
            testRunner("TC_change_project_param_and_get_osc");
        }
        if (startAllTests || tests.getBoolean("TC_rename_input_output")) {
            testRunner("TC_rename_input_output");
        }
        if (startAllTests || tests.getBoolean("TC_add_components_area")) {
            testRunner("TC_add_components_area");
        }
        if (startAllTests || tests.getBoolean("TC_download_document")) {
            testRunner("TC_download_document");
        }
        if (startAllTests || tests.getBoolean("TC_Ethernet_connect")) {
            testRunner("TC_Ethernet_connect");
        }
        if (startAllTests || tests.getBoolean("TC_USB_Connection")) {
            testRunner("TC_USB_Connection");
        }
        if (startAllTests || tests.getBoolean("TC_download_blanks")) {
            testRunner("TC_download_blanks");
        }
        if (startAllTests || tests.getBoolean("TC_save_block_image")) {
            testRunner("TC_save_block_image");
        }
        if (startAllTests || tests.getBoolean("TC_interrupt_transmission")) {
            testRunner("TC_interrupt_transmission");
        }
        if (startAllTests || tests.getBoolean("TC_setting_ASU")) {
            testRunner("TC_setting_ASU");
        }
        if (startAllTests || tests.getBoolean("TC_Link_MT")) {
            testRunner("TC_Link_MT");
        }
        Thread.sleep(5000);
        System.out.println(resultString);
        Runtime.getRuntime().exit(testStatus);

        closeDriver();
    }

    public static void TCDownloadDocument() throws Exception {
        openApp(capForOpenConf);
        sleep(4);
        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {

            actions = new Actions(driver);

            sleep(10);
            hideInformation();
            choiceButton("Сервисы");
            String doc = "ICD-152-KSZ-01_25.icd";

            findElementByNameAndClick("Документы");
            driver.findElementByName(doc).findElementByName(doc).click();
            actions.moveByOffset(-150, 0).build().perform();
            actions.click().build().perform();
            actions.contextClick().build().perform();
            configDriver = driver;
            choiceWindowsRoot();
            choiceWindowWithName("Контекст");
            findElementByNameAndClick("Сохранить как ...");
            driver = configDriver;
            sleep(10);


            driver.findElementByName("Все папки").click();
            String directory = properties.getJSONObject("paths").getString("pathForDocument");
            Thread.sleep(timer);
            WindowsElement directoryField = driver.findElementByName("Адрес");
            String startDirectory = directoryField.getAttribute("Value.Value");


            if (!directory.equals(startDirectory)) {
                sendKeys(directory);
            }
            sleep(5);


            addToCopyBuffer(doc);
            driver.findElementByClassName("AppControlHost").findElementByName("Имя файла:").sendKeys(Keys.CONTROL + "v" + Keys.ENTER);


//                findElementByNameAndClick("Сохранить");
            if (checkHaveElementWithName("Подтвердить сохранение в виде")) {
                findElementByNameAndClick("Да");
            }

            File document = new File(directory + "\\" + doc);
            if (document.exists()) {
                driver = configDriver;
                checkSaveProject(true);
                closeDriver();
                LOGGER.info("Тест TC_download_document завершился удачно!");
                System.out.println("Тест TC_download_document завершился с положительным результатом");
            } else {
                LOGGER.error("Тест TC_download_document завершился неудачно");
                LOGGER.error("Документ не был сохранён");
                checkSaveProject(false);
                closeDriver();
                throw new Exception("Тест TC_download_document завершился неудачно. Документ не был сохранён");
            }
        }


    }

    public static void TCRenameInputOutput() throws Exception {
        openApp(capForOpenConf);
        sleep(4);
        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {

            actions = new Actions(driver);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            sleep(20);

            differentProperties();
            driver = configDriver;
            sleep(10);

            hideInformation();
            choiceButton("Настройки РЗА");


//=================================================================================================
//                Дискретные входы


            findElementByNameAndClick("Дискретные входы");
            if (checkHaveElementWithName("Я22 Вход")) {
                choiceStringAndRenameIt("Я22 Вход", "22", -50, "Д Вход", true);

            } else if (!checkHaveElementWithName("Д Вход")) {
                throw new Exception("Дискретный вход имеет неккоректное имя");
            }


            applyingAndPushing();


//                hideInformation();
//                choiceButton("Настройки РЗА");
            findElementByNameAndClick("Дискретные входы");

            choiceStringAndRenameIt("Д Вход", "22", -50, "Я22 Вход", false);

            applyingAndPushing();

//=================================================================================================
//                Дискретные выходы

            findElementByNameAndClick("Дискретные выходы");
            if (checkHaveElementWithName("K7 Выход")) {
                choiceStringAndRenameIt("K7 Выход", "Перекидной", -150, "Выход", true);

            } else if (!checkHaveElementWithName("Выход")) {
                throw new Exception("Дискретный выход имеет неккоректное имя");
            }


            applyingAndPushing();


//                hideInformation();
//                choiceButton("Настройки РЗА");
            findElementByNameAndClick("Дискретные выходы");

            choiceStringAndRenameIt("Выход", "Перекидной", -150, "K7 Выход", false);

            applyingAndPushing();


//=================================================================================================
//                Виртуальные входы

            findElementByNameAndClick("Виртуальные входы");
            if (checkHaveElementWithName("Вирт вход 1")) {
                choiceStringAndRenameIt("Вирт вход 1", "Вирт вход 1", -200, "Вход", true);

            } else if (!checkHaveElementWithName("Вход")) {
                throw new Exception("Вирутальный вход имеет неккоректное имя");
            }


            applyingAndPushing();


//                hideInformation();
//                choiceButton("Настройки РЗА");
            findElementByNameAndClick("Виртуальные входы");

            choiceStringAndRenameIt("Вход", "Вход", -200, "Вирт вход 1", false);

            applyingAndPushing();


//=================================================================================================
//                Виртуальные выходы

            findElementByNameAndClick("Виртуальные выходы");
            if (checkHaveElementWithName("Вирт выход 1")) {
                choiceStringAndRenameIt("Вирт выход 1", "Вирт выход 1", -200, "В Выход", true);

            } else if (!checkHaveElementWithName("В Выход")) {
                throw new Exception("Виртуальный выход имеет неккоректное имя");
            }


            applyingAndPushing();


//                hideInformation();
//                choiceButton("Настройки РЗА");
            findElementByNameAndClick("Виртуальные выходы");

            choiceStringAndRenameIt("В Выход", "В Выход", -200, "Вирт выход 1", false);

            applyingAndPushing();


            driver = configDriver;
            checkSaveProject(true);
            closeDriver();
            LOGGER.info("Тест TCRenameInputOutput завершился удачно!");
            System.out.println("Тест TCRenameInputOutput завершился с положительным результатом");
        }


    }

    public static void TCAddComponentsArea() throws Exception {
        openApp(capForOpenConf);
        Thread.sleep(4000);
        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {

            actions = new Actions(driver);

//                =========================================
//                Рисунок

            findElementByNameAndClick("Рисунок");
            JSONObject image = properties.getJSONObject("image");
            String directoryToImage = image.getString("directory");
            String imageName = image.getString("name");
            openFileWithDirectory(directoryToImage, imageName);
            findElementByNameAndClick("Да");
            addChoiceDeleteComponent(5);

//                =========================================
//                Аналоговое значение

            findElementByNameAndClick("Аналоговое значение");
            findElementByNameAndClick("IA, А");
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(5);


//                =========================================
//                Логический сигнал

            findElementByNameAndClick("Логический сигнал");
            findElementByNameAndClick("[Я1] РПО");
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(5);


//                =========================================
//                Выключатель

            findElementByNameAndClick("Выключатель");
            findElementByNameAndClick("[Я1] РПО");
            driver.findElementsByName("[Я2] РПВ").get(1).click();
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(5);


//                =========================================
//                Команда управления

            findElementByNameAndClick("Команда управления");
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(5);


//                =========================================
//                Статический текст

            findElementByNameAndClick("Статический текст");
            findElementByNameAndClick("Значение");
            actions.moveByOffset(-5, 20).build().perform();
            actions.click().build().perform();
            driver.getKeyboard().sendKeys("textForExample");
            driver.getKeyboard().sendKeys(Keys.ENTER);
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(5);


//                =========================================
//                Служебная информация

            findElementByNameAndClick("Служебная информация");
//                findElementByNameAndClick("Значение");
//                actions.moveByOffset(-5, 20).build().perform();
//                actions.click().build().perform();
//                driver.getKeyboard().sendKeys("textForExample");
//                driver.getKeyboard().sendKeys(Keys.ENTER);
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(5);


//                =========================================
//                Линия

            findElementByNameAndClick("Линия");
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(0);


//                =========================================
//                Прямоугольник

            findElementByNameAndClick("Прямоугольник");
            driver.getKeyboard().sendKeys(Keys.ENTER);
            addChoiceDeleteComponent(0);

            sleep(6);


            driver = configDriver;
            checkSaveProject(true);
            closeDriver();
            LOGGER.info("Тест TCAddImage завершился удачно!");
            System.out.println("Тест TC_add_components_area завершился с положительным результатом");

        }


    }

    public static void TCChangeProjectParamAndGetOsc() throws Exception {
        openApp(capForOpenConf);
        Thread.sleep(4000);
        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {

            actions = new Actions(driver);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            sleep(20);
            differentProperties();
            driver = configDriver;
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(10);

            hideInformation();
            choiceButton("Настройки РЗА");
            findElementByNameAndClick("Параметры проекта");

            setParameterValue("Заказчик*", "aa");
            setParameterValue("Объект*", "bb");
            setParameterValue("Присоединение/комплект*", "cc");
            setParameterValue("Проектная документация*", "dd");
            setParameterValue("Номер приложения*", "ee");
            setParameterValue("Комментарий", "ff");
            setParameterValue("Организация*", "gg");
            setParameterValue("Исполнитель*", "hh");

            applyingAndPushing();

            sleep(10);
            hideInformation();
            choiceButton("Мониторинг");

            findElementByNameAndClick("Осциллограммы");
            reboot();
            sleep(10);
            findElementByNameAndClick("Загрузить");
            driver.findElementByName("Все папки").click();
            String directory = properties.getJSONObject("paths").getString("pathForOsc");

            WindowsElement directoryField = driver.findElementByName("Адрес");
            String startDirectory = directoryField.getAttribute("Value.Value");

            String oscName = driver.findElementByName("Сохранение").findElementsByName("Имя файла:").get(1).getAttribute("Value.Value");
            System.out.println(oscName);
            Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4},\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{3},0t,(bb,cc_БМРЗ-152-КСЗ-01,aa\\.cfg|ии,сс_БМРЗ-152-КСЗ-01,фф\\.cfg)");
            Matcher matcher = pattern.matcher(oscName);
            boolean oscNameCheck = matcher.find();

            if (!directory.equals(startDirectory)) {
                sendKeys(directory);
            }


            findElementByNameAndClick("Сохранить");

            sleep(20);
            File osc = new File(directory + "\\" + oscName);
            boolean checkHaveOsc = osc.exists();

            choiceWindowsRoot();
            if (checkHaveElementWithName("Механотроника FastView - " + oscName)) {
                System.out.println("Открылся фаствью");
                choiceWindowWithName("Механотроника FastView - " + oscName);
                driver.close();
                driver = configDriver;
            }
            hideInformation();
            choiceButton("Настройки РЗА");
            findElementByNameAndClick("Параметры проекта");

            setParameterValue("Заказчик*", "a");
            setParameterValue("Объект*", "b");
            setParameterValue("Присоединение/комплект*", "c");
            setParameterValue("Проектная документация*", "d");
            setParameterValue("Номер приложения*", "e");
            setParameterValue("Комментарий", "f");
            setParameterValue("Организация*", "g");
            setParameterValue("Исполнитель*", "h");

            applyingAndPushing();

            if (oscNameCheck && checkHaveOsc) {
                driver = configDriver;
                checkSaveProject(true);
                closeDriver();
                LOGGER.info("Тест TC_change_project_param_and_get_osc завершился удачно!");
                System.out.println("Тест TC_change_project_param_and_get_osc завершился с положительным результатом");
            } else {
                LOGGER.error("Тест TC_change_project_param_and_get_osc завершился неудачно");
                LOGGER.error("Имя осциллогаммы не корректное");
                checkSaveProject(false);
                closeDriver();
                throw new Exception();
            }
        }


    }

    public static void TCRedundancyProtocols() throws Exception {
        openApp(capForOpenConf);
        Thread.sleep(4000);
        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {


            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            System.out.println("Connect");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(15);

            differentProperties();
            hideInformation();
            System.out.println("hideInfo");

            choiceButton("Коммуникации");

            System.out.println("Communication");
            findElementByNameAndClick("LinkBackUp, RayMode, RSTP, PRP, HSR");

            JSONObject protocols = properties.getJSONObject("TC_redundancy_protocol");

            boolean LinkBackUp = true;
            boolean RayMode = true;
            boolean RSTP = true;
            boolean PRP = true;
            boolean HSR = true;
//                ===================================================
//                RayMode
            if (protocols.getBoolean("RayMode")) {
                findElementByNameAndClick("RayMode");

//                WindowsElement issuesPanel = driver.findElementByName("Панель задач");


//                driver.getKeyboard().sendKeys(Keys.CONTROL + "w" + Keys.CONTROL);
                reboot();
                try {
                    setProject();
                } catch (NoSuchElementException ignored) {
                }

                findElementByNameAndClick("Записать (Ctrl+W)");
                sleep(6);
                try {
                    findElementByNameAndClick("Записать");
                } catch (NoSuchElementException ignored) {
                }
                sleep(10);
                findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);


                RayMode = checkProtocols("RayMode");
            }

//                ===================================================
//                RSTP
            if (protocols.getBoolean("RSTP")) {
                findElementByNameAndClick("RSTP");

//                WindowsElement issuesPanel = driver.findElementByName("Панель задач");


//                driver.getKeyboard().sendKeys(Keys.CONTROL + "w" + Keys.CONTROL);
                findElementByNameAndClick("Записать (Ctrl+W)");
                sleep(6);
                findElementByNameAndClick("Записать");
                sleep(10);
                findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);


                RSTP = checkProtocols("RSTP");
            }

//                ===================================================
//                PRP

            if (protocols.getBoolean("PRP")) {
                findElementByNameAndClick("PRP");

//                WindowsElement issuesPanel = driver.findElementByName("Панель задач");


//                driver.getKeyboard().sendKeys(Keys.CONTROL + "w" + Keys.CONTROL);
                findElementByNameAndClick("Записать (Ctrl+W)");
                sleep(6);
                findElementByNameAndClick("Записать");
                sleep(10);
                findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);

                PRP = checkProtocols("PRP");
            }

//                ===================================================
//                LinkBackUp

            if (protocols.getBoolean("LinkBackUp")) {
                findElementByNameAndClick("LinkBackUp");

//                WindowsElement issuesPanel = driver.findElementByName("Панель задач");


//                driver.getKeyboard().sendKeys(Keys.CONTROL + "w" + Keys.CONTROL);
                findElementByNameAndClick("Записать (Ctrl+W)");
                sleep(6);
                findElementByNameAndClick("Записать");
                sleep(10);
                findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);

                LinkBackUp = checkProtocols("LinkBackUp");
            }

//                ===================================================
//                HSR

            if (protocols.getBoolean("HSR")) {
                findElementByNameAndClick("HSR");

//                WindowsElement issuesPanel = driver.findElementByName("Панель задач");


//                driver.getKeyboard().sendKeys(Keys.CONTROL + "w" + Keys.CONTROL);
                findElementByNameAndClick("Записать (Ctrl+W)");
                sleep(6);
                findElementByNameAndClick("Записать");
                sleep(10);
                findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);

                HSR = checkProtocols("HSR");
            }

            driver = configDriver;
            if (LinkBackUp && RayMode && RSTP && PRP && HSR) {
                LOGGER.info("Тест TCRedundancyProtocols завершился удачно!");
                System.out.println("Тест TCRedundancyProtocols завершился с положительным результатом");
                checkSaveProject(true);
                closeDriver();
            } else {
                LOGGER.error("Тест TC_redundancy_protocols завершился неудачно");
                LOGGER.error("LinkBackUp - " + LinkBackUp + "; RayMode - " + RayMode + "; RSTP - " + RSTP + "; PRP - " + PRP + "; HSR - " + HSR);
                checkSaveProject(false);
                closeDriver();
            }
        }


    }

    public static void TCEthernetConnect() throws Exception {
        openApp(capForOpenConf);
        Thread.sleep(4000);

        configDriver = driver;
        actions = new Actions(driver);
        Thread.sleep(5000);
        driver.getKeyboard().sendKeys(Keys.CONTROL + "p" + Keys.CONTROL);
        Thread.sleep(4000);
        driver.findElementByName("Настройки программного комплекса \"Конфигуратор-МТ\"").findElementByName("Общие").findElementByName("Связь с блоком").click();
        System.out.println("Связь с блоком нажато");
        findElementByNameAndClick("Связь с блоком");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
        Thread.sleep(2000);
        findElementByNameAndClick("Настройки подключения к блоку");
//                driver.getKeyboard().sendKeys(Keys.ENTER);
        Thread.sleep(10000);
        List<MobileElement> connections = driver.findElementByName("Настройки подключения к блоку").findElementsByClassName("Button");
//            WindowsElement ethernet = driver.findElement("AutomationId", "20035");
//            connections.get(7).click();
//            System.out.println(7 + " COM");
//            Thread.sleep(10000);
//            connections.get(8).click();
//            System.out.println(8 + " USB");
//            Thread.sleep(10000);
//            ethernet.click();
        connections.get(10).click();
        System.out.println(10 + " Ethernet");
        Thread.sleep(5000);
        driver.findElementByClassName("SysIPAddress32").click();
        System.out.println("click");
        sleep(5);
        sendKeys(properties.getString("ip"));
        System.out.println("123..");
        sleep(5);


        findElementByNameAndClick("ОК");
        if (openProject(bfpoDirectory, pmkName, bfpoName)) {
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(10);
            checkSaveProject(true);
            closeDriver();
            LOGGER.info("Тест TCEthernetConnection завершился удачно!");
            System.out.println("Тест TCEthernetConnection завершился с положительным результатом");
        } else {
            LOGGER.error("Проект " + pmkName + " не был открыт");
            LOGGER.error("Тест TCEthernetConnection завершился неудачно");
            checkSaveProject(false);
            closeDriver();
            throw new Exception();
        }


    }

    public static void TCUSBConnection() throws Exception {
        openApp(capForOpenConf);
        sleep(4);

        configDriver = driver;
        actions = new Actions(driver);
        sleep(5);
        driver.getKeyboard().sendKeys(Keys.CONTROL + "p" + Keys.CONTROL);
        sleep(4);
        driver.findElementByName("Настройки программного комплекса \"Конфигуратор-МТ\"").findElementByName("Общие").findElementByName("Связь с блоком").click();
        System.out.println("Связь с блоком нажато");
        findElementByNameAndClick("Связь с блоком");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
        sleep(2);
        findElementByNameAndClick("Настройки подключения к блоку");
//                driver.getKeyboard().sendKeys(Keys.ENTER);
        sleep(10);
        List<MobileElement> connections = driver.findElementByName("Настройки подключения к блоку").findElementsByClassName("Button");
//            connections.get(7).click();
//            System.out.println(7 + " COM");
        connections.get(8).click();
        System.out.println(8 + " USB");
//            connections.get(10).click();
//            System.out.println(10 + " Ethernet");
        sleep(5);
//            driver.findElementByClassName("SysIPAddress32").click();
//            System.out.println("click");
//            sleep(5);
//            sendKeys(properties.getString("ip"));
//            System.out.println("123..");
//            sleep(5);

        findElementByNameAndClick("Да");

        findElementByNameAndClick("ОК");
        if (openProject(bfpoDirectory, pmkName, bfpoName)) {
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(10);
            checkSaveProject(true);
            closeDriver();
            LOGGER.info("Тест TCUSBConnection завершился удачно!");
            System.out.println("Тест TCUSBConnection завершился с положительным результатом");
        } else {
            LOGGER.error("Проект " + pmkName + " не был открыт");
            LOGGER.error("Тест TCUSBConnection завершился неудачно");
            checkSaveProject(false);
            closeDriver();
            throw new Exception();
        }


    }

    public static void TCLinkMT() throws MalformedURLException, InterruptedException {
        try {
            driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), capForOpenLinkMT);
        } catch (SessionNotCreatedException ignored) {
        }
        choiceWindowsRoot();
        sleep(15);
        if (checkHaveElementWithName("Домашняя страница")) {
            LOGGER.info("Link-MT открылся");
        } else {
            LOGGER.error("Link-MT не открылся");
            throw new NoSuchElementException("Link-MT не открылся");
        }
        choiceWindowWithName("Домашняя страница - Google Chrome");

        actions = new Actions(driver);
        System.out.println("Тест Link-Mt завершился удачно!");
        LOGGER.info("Тест Link-Mt завершился удачно!");
        driver.close();


    }

    public static void TC_ARM_CFG_Compile() throws Exception {
        openApp(capForOpenARM);
        actions = new Actions(driver);

        if (!openFileARM(bfpoDirectory, bfpoName)) {
            LOGGER.error("Файл " + bfpoName + " не открылся");
        } else {
            LocalDateTime finishArm = LocalDateTime.now().plusMinutes(7);

            findElementByNameAndClick("Компиляция");
            Thread.sleep(timer);
            findElementByNameAndClick("Да");
            Thread.sleep(180000);
            while (!checkHaveElementWithName("Готово.") && LocalDateTime.now().isBefore(finishArm)) {
                sleep(2);
            }
            if (LocalDateTime.now().isAfter(finishArm)) {
                throw new NoSuchElementException("Компиляция не завершилась, или завершилась неудачно");
            }
        }
        driver.closeApp();

        openApp(capForOpenConf);
        Thread.sleep(4000);
        collapseMenu();

        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {


            findElementByNameAndClick("Проверка и применение (Ctrl+E)");
            sleep(40);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            sleep(10);
            findElementByNameAndClick("ОК");
            sleep(10);
            reboot();
            setProject();
            choiceWindowsRoot();


            if (checkHaveElementWithName("\"Floader\"")) {
                choiceWindowWithName("Конфигуратор-МТ");
                driver.findElementByName("ОК").click();
                actions.sendKeys(Keys.ENTER);
                driver = configDriver;
                checkSaveProject(false);
                closeDriver();
                throw new NoSuchElementException("Запись проекта в блок не завершилась или завершилась неудачно");

            }
//            }
            driver = configDriver;
            checkSaveProject(true);
            closeDriver();
            LOGGER.info("Тест ARM_CFG_Compile завершился удачно!");
            System.out.println("Тест ARM_CFG_Compile завершился с положительным результатом");
        }
    }

    public static void TCDownloadBlanks() throws Exception {
        openApp(capForOpenConf);
        sleep(4);

        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {


            actions = new Actions(driver);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            sleep(20);
            differentProperties();
            driver = configDriver;
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(10);

            hideInformation();
            choiceButton("Настройки РЗА");
            findElementByNameAndClick("Параметры проекта");

            setParameterValue("Заказчик*", Keys.DELETE);
            setParameterValue("Объект*", Keys.DELETE);
            setParameterValue("Присоединение/комплект*", Keys.DELETE);
            setParameterValue("Проектная документация*", Keys.DELETE);
            setParameterValue("Номер приложения*", Keys.DELETE);
            setParameterValue("Комментарий", Keys.DELETE);
            setParameterValue("Организация*", Keys.DELETE);
            setParameterValue("Исполнитель*", Keys.DELETE);

            findElementByNameAndClick("Бланки (Ctrl+B)");

            if (!checkHaveElementWithName("Для создания файла описания настроек блока необходимо выполнить привязку к проектной документации, заполнив обязательные поля во вкладке \"Параметры проекта\".")) {
                throw new NoSuchWindowException("Нет предупреждения о незаполненых параметров проекта");
            }
            findElementByNameAndClick("ОК");


            hideInformation();
            choiceButton("Настройки РЗА");
            findElementByNameAndClick("Параметры проекта");


            setParameterValue("Заказчик*", "a");
            setParameterValue("Объект*", "b");
            setParameterValue("Присоединение/комплект*", "c");
            setParameterValue("Проектная документация*", "d");
            setParameterValue("Номер приложения*", "e");
            setParameterValue("Комментарий", "f");
            setParameterValue("Организация*", "g");
            setParameterValue("Исполнитель*", "h");
            hideInformation();


            findElementByNameAndClick("Бланки (Ctrl+B)");
            LocalDateTime start = LocalDateTime.now();
            sleep(20);
            while (checkHaveElementWithName("Экспорт настроек проекта в файл MS Word")) {
                sleep(10);
            }


            boolean passTest = true;
            choiceWindowsRoot();
            if (checkHaveElementWithName("Документ1 - Word") && LocalDateTime.now().isBefore(start.plus(180, ChronoUnit.SECONDS))) {
                choiceWindowWithName("Документ1 - Word");

                findElementByNameAndClick("Закрыть");
                findElementByNameAndClick("Сохранить");
                String directory = properties.getJSONObject("paths").getString("pathForDocument");
                String docName = "Конфигурация блока БМРЗ.docx";
                File doc = new File(directory + "\\" + docName);
                if (doc.exists()) {
                    doc.delete();
                }
                saveFileWithDirectory(directory, docName);
                File docNew = new File(directory + "\\" + docName);


                if (docNew.exists() && docNew.getTotalSpace() > 1000) {
                    driver = configDriver;
                    checkSaveProject(true);
                    closeDriver();
                    LOGGER.info("Тест TC_download_blanks завершился удачно!");
                    System.out.println("Тест TC_download_blanks завершился с положительным результатом");

                } else {
                    passTest = false;
                }

            } else {
                passTest = false;
            }
            if (!passTest) {
                driver = configDriver;
                checkSaveProject(false);
                closeDriver();
                LOGGER.error("Тест TC_download_blanks завершился неудачно");
//                LOGGER.error("Имя осциллогаммы не корректное");
                throw new Exception();

            }

        }

    }

    public static void TCSaveBlockImage() throws Exception {
        openApp(capForOpenConf);
        sleep(4);

        configDriver = driver;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {


            actions = new Actions(driver);
            findElementByNameAndClick("Проверка и применение (Ctrl+E)");
            sleep(40);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            sleep(10);
            findElementByNameAndClick("ОК");
            sleep(10);
            reboot();
            setProject();
            driver = configDriver;
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(10);

            hideInformation();
            findElementByNameAndClick("Загрузка образа блока");
            findElementByNameAndClick("Принять");

            boolean passTest = true;
            String directory = properties.getJSONObject("paths").getString("pathForDocument");
            String fileName = "БФПО-152-КСЗ-01_25.sth_f";
            File blockImage = new File(directory, fileName);
            if (blockImage.exists()) {
                blockImage.delete();
            }
            saveFileWithDirectory(directory, fileName);
            if (checkHaveElementWithName("Подтвердить сохранение в виде")) {
                findElementByNameAndClick("Да");
            }
//            while (checkHaveElementWithName("Загрузка образа блока") && LocalDateTime.now().isBefore(start.plusMinutes(2))) {
//                sleep(5);
//            }
            sleep(60);
            if (checkHaveElementWithName("Образ блока УСПЕШНО загружен из блока и сохранен на диске!") && new File(directory, fileName).exists()) {
                findElementByNameAndClick("OK");
                driver = configDriver;

                checkSaveProject(true);
                closeDriver();
                openApp(capForOpenConf);
                configDriver = driver;
                sleep(4);

                findElementByNameAndClick("Открыть");
                findElementByNameAndClick("Тип файлов:");
                findElementByNameAndClick("Файл образа блока (*.sth_f)");
                saveFileWithDirectory(directory, fileName);
                sleep(60);

                driver = configDriver;
                hideInformation();
                checkSaveProject(true);
                closeDriver();
                LOGGER.info("Тест TC_save_block_image завершился удачно!");
                System.out.println("Тест TC_save_block_image завершился с положительным результатом");


            } else {
                passTest = false;
            }


            if (!passTest) {
                driver = configDriver;
                checkSaveProject(false);
                closeDriver();
                LOGGER.error("Тест TC_save_block_image завершился неудачно");
//                LOGGER.error("Имя осциллогаммы не корректное");
                throw new Exception();

            }
        }
    }

    public static void TCInterruptTransmission() throws Exception {
        openApp(capForOpenConf);
        Thread.sleep(4000);
        configDriver = driver;
        boolean passTest = true;
        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {

            actions = new Actions(driver);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            sleep(20);
            differentProperties();
            driver = configDriver;
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(10);

            hideInformation();
            choiceButton("Настройки РЗА");
            findElementByNameAndClick("КЦН");
            WindowsElement value = driver.findElementsByName("0.02").get(1);
            value.click();
            actions.doubleClick().build().perform();
            sleep(1);
            actions.sendKeys("0.03" + Keys.ENTER).build().perform();

            if (!value.getAttribute("Name").equals("0.03")) {
                passTest = false;
                LOGGER.error("Некорректно записалось значение уставки!");
            } else {
                findElementByNameAndClick("Записать (Ctrl+W)");
                findElementByNameAndClick("Записать");
                findElementByNameAndClick("Прервать");
//            if (checkHaveElementWithName("Прерывание связи с блоком...")) {
//                System.out.println("Прерывание удалось");
//            }
                WindowsElement connectButton = driver.findElementByName("Подключиться (Ctrl+Q)");
                passTest = Boolean.parseBoolean(connectButton.getAttribute("IsEnabled"));
                System.out.println("passTest - " + passTest);
                findElementByNameAndClick("Подключиться (Ctrl+Q)");
                sleep(20);
                differentProperties();
                driver = configDriver;
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
                sleep(10);

                value.click();
                actions.doubleClick().build().perform();
                actions.sendKeys("0.02" + Keys.ENTER).build().perform();
                findElementByNameAndClick("Записать (Ctrl+W)");
                findElementByNameAndClick("Записать");
                findElementByNameAndClick("ОК");
                sleep(10);
            }
            if (passTest) {
                driver = configDriver;
                checkSaveProject(true);
                closeDriver();
                LOGGER.info("Тест TC_interrupt_transmission завершился удачно!");
                System.out.println("Тест TC_interrupt_transmission завершился с положительным результатом");
            } else {
                LOGGER.error("Тест TC_interrupt_transmission завершился неудачно");
//                LOGGER.error("Имя осциллогаммы не корректное");
                checkSaveProject(false);
                closeDriver();
                throw new Exception();
            }
        }
    }

    public static void TCSettingASU() throws Exception {
        openApp(capForOpenConf);
        Thread.sleep(4000);
        configDriver = driver;
        String directoryForDocument = properties.getJSONObject("paths").getString("pathForDocument");
        String exceptionString = null;
        boolean passTest = true;

        if (!openProject(bfpoDirectory, pmkName, bfpoName)) {
            throw new Exception("Проект не был открыт");
        } else {

            actions = new Actions(driver);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            System.out.println("Connect");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(15);

            differentProperties();
            hideInformation();
            driver = configDriver;

            choiceButton("Коммуникации");
            findElementByNameAndClick("Интерфейсы");
            findElementByNameAndClick("1");
            actions.moveByOffset(-55, 0).click().build().perform();
            findElementByNameAndClick("Отключить интерфейс Ethernet с протоколом Modbus-MT/TCP");
            WindowsElement speed = driver.findElementsByName("115200").get(2);
            speed.click();
            actions.doubleClick().build().perform();
            findElementByNameAndClick("9600");
            WindowsElement parity = driver.findElementByName("Нет");
            parity.click();
            actions.doubleClick().build().perform();
            findElementByNameAndClick("Чет");
            WindowsElement stopBit = driver.findElementsByName("1").get(1);
            stopBit.click();
            actions.doubleClick().build().perform();
            driver.findElementsByName("2").get(1).click();

            findElementByNameAndClick("55");
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);


            recordProject();

            findElementByNameAndClick("1");
            actions.moveByOffset(10, 0).click().build().perform();
            speed.click();
            actions.doubleClick().build().perform();
            driver.findElementsByName("115200").get(3).click();
            parity.click();
            actions.doubleClick().build().perform();
            driver.findElementsByName("Нет").get(1).click();
            stopBit.click();
            actions.doubleClick().build().perform();
            driver.findElementsByName("1").get(1).click();

            findElementByNameAndClick("50");
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "5" + Keys.ENTER);


            recordProject();


            List<MobileElement> flags = driver.findElementByName("Панель задач").findElementsByClassName("Button");
            MobileElement protocolModbus = flags.get(flags.size() - 1);
            protocolModbus.click();


            findElementByNameAndClick("Отмена");

            findElementByNameAndClick("Записать (Ctrl+W)");
            if (checkHaveElementWithName("В проекте обнаружены критические ошибки. \nЗапись динамических данных в блок недопустима!\nСмотрите в области \"Информация\" вкладку \"Компиляция\"!")) {
                exceptionString = "Некорректно работает отмена протокола Modbus-MT/TCP!";
                findElementByNameAndClick("ОК");
                passTest = false;
                resultString.append("В тесте TC_setting_ASU повторился баг с неккоректной отмене протокола Modbus-MT/TCP!");
            }
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
            System.out.println("Connect");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            sleep(15);

            protocolModbus.click();
            findElementByNameAndClick("Отключить интерфейс RS-485 с протоколом Modbus-MT");

            recordProject();


            System.out.println(flags.size());
            flags.get(flags.size() - 2).click();
            sleep(5);
            findElementByNameAndClick("01-1B-19-00-00-00");
            sleep(1);
            actions.moveByOffset(-90, 0).click().build().perform();

            sleep(5);
            findElementByNameAndClick("IEEE 802.3 E2E");
            doubleClick();
            findElementByNameAndClick("IPv4 E2E");
            findElementByNameAndClick("IPv4 E2E");
            sleep(5);
            actions.moveByOffset(90, 0).click().build().perform();
            sleep(7);
            findElementByNameAndClick("Записать (Ctrl+W)");

            if (!checkHaveElementWithName("В проекте обнаружены критические ошибки. \nЗапись динамических данных в блок недопустима!\nСмотрите в области \"Информация\" вкладку \"Компиляция\"!")) {
                findElementByNameAndClick("Отмена");
                passTest = false;
                resultString.append("В тесте TC_setting_ASU в данный блок PTPv2 записываться не должен");
            }
            findElementByNameAndClick("ОК");


            sleep(5);
            findElementByNameAndClick("IPv4 E2E");
            doubleClick();
            findElementByNameAndClick("IEEE 802.3 E2E");
            findElementByNameAndClick("IEEE 802.3 E2E");
            sleep(5);
            actions.moveByOffset(90, 0).click().build().perform();
            sleep(7);
            sleep(5);
            driver.findElementsByName("0.0.0.0").get(1).click();
            sleep(1);
            actions.moveByOffset(-100, 0).click().build().perform();
            flags.get(flags.size() - 2).click();


            findElementByNameAndClick("NMEA, TSIP, SNTP");

            List<WindowsElement> times = driver.findElementsByName("1 мин ");
            times.get(0).click();
            doubleClick();
            editTime(1, 2, 1);

            times.get(1).click();
            doubleClick();
            editTime(1, 2, 1);


            times.get(2).click();
            doubleClick();
            editTime(1, 2, 1);


            recordProject();

            times.get(0).click();
            doubleClick();
            editTime(0, 1, 0);


            times.get(1).click();
            doubleClick();
            editTime(0, 1, 0);


            times.get(2).click();
            doubleClick();
            editTime(0, 1, 0);


            recordProject();

            findElementByNameAndClick("IEC 60870-5");

            WindowsElement lengthAddress = driver.findElementByName("1");
            WindowsElement generalAddress = driver.findElementByName("2");
            WindowsElement addressObject = driver.findElementsByName("2").get(2);
            WindowsElement reasonTransfer = driver.findElementsByName("1").get(3);
            List<WindowsElement> allAddressASDU = driver.findElementsByName("3");
            WindowsElement addressASDU101 = allAddressASDU.get(3);
            WindowsElement addressASDU103 = allAddressASDU.get(4);
            WindowsElement addressASDU104 = allAddressASDU.get(5);
            List<WindowsElement> allIntervalScan = driver.findElementsByName("180");
            WindowsElement intervalScan101 = allIntervalScan.get(0);
            WindowsElement intervalScan103 = allIntervalScan.get(1);
            WindowsElement intervalScan104 = allIntervalScan.get(2);
            List<WindowsElement> allLengthFrame = driver.findElementsByName("253");
            WindowsElement lengthFrame101 = allLengthFrame.get(0);
            WindowsElement lengthFrame103 = allLengthFrame.get(1);
            List<WindowsElement> allAnswers = driver.findElementsByName("Нет");
            WindowsElement shortAnswer101 = allAnswers.get(0);
            WindowsElement shortAnswer103 = allAnswers.get(1);
            WindowsElement answerFirst101 = allAnswers.get(2);
            WindowsElement answerFirst103 = allAnswers.get(3);
            List<WindowsElement> allTimes = driver.findElementsByName("5 мин ");
            WindowsElement timeSinch101 = allTimes.get(0);
            WindowsElement timeSinch103 = allTimes.get(1);
            WindowsElement timeSinch104 = allTimes.get(2);
            WindowsElement leadTime = driver.findElementByName("10000");
            WindowsElement t0 = driver.findElementByName("30");
            WindowsElement t1 = driver.findElementByName("15");
            WindowsElement t2 = driver.findElementByName("10");
            WindowsElement t3 = driver.findElementByName("20");
            WindowsElement k = driver.findElementByName("12");
            WindowsElement w = driver.findElementByName("8");


            lengthAddress.click();
            choiceSubItem("2");

            generalAddress.click();
            choiceSubItem("1");

            addressObject.click();
            choiceSubItem("3");

            reasonTransfer.click();
            choiceSubItem("2");

            addressASDU101.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys("1" + Keys.ENTER);

            addressASDU103.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys("1" + Keys.ENTER);

            addressASDU104.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys("1" + Keys.ENTER);

            intervalScan101.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            intervalScan103.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            intervalScan104.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            lengthFrame101.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            lengthFrame103.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            shortAnswer101.click();
            actions.doubleClick().build().perform();

            shortAnswer103.click();
            actions.doubleClick().build().perform();

            answerFirst101.click();
            actions.doubleClick().build().perform();

            answerFirst103.click();
            actions.doubleClick().build().perform();

            if (driver.findElementsByName("Да").size() != 4) {
                passTest = false;
                resultString.append("В тесте TC_setting_ASU не изменилось значение для ответов в настройках протоколов IEC 60870-5");
            }

            timeSinch101.click();
            actions.doubleClick().build().perform();
            editTime(1, 1, 1);

            timeSinch103.click();
            actions.doubleClick().build().perform();
            editTime(1, 1, 1);

            timeSinch104.click();
            actions.doubleClick().build().perform();
            editTime(1, 1, 1);

            leadTime.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            t0.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            t1.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            t2.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            t3.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            k.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);

            w.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "1" + Keys.ENTER);


            recordProject();


            findElementByNameAndClick("IEC 60870-5");

            lengthAddress.click();
            choiceSubItem("1");

            generalAddress.click();
            choiceSubItem("2");

            addressObject.click();
            choiceSubItem("2");

            reasonTransfer.click();
            choiceSubItem("1");

            addressASDU101.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "" + Keys.ENTER);

            addressASDU103.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "" + Keys.ENTER);

            addressASDU104.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "" + Keys.ENTER);

            intervalScan101.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            intervalScan103.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            intervalScan104.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            lengthFrame101.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "3" + Keys.ENTER);

            lengthFrame103.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "3" + Keys.ENTER);

            shortAnswer101.click();
            actions.doubleClick().build().perform();

            shortAnswer103.click();
            actions.doubleClick().build().perform();

            answerFirst101.click();
            actions.doubleClick().build().perform();

            answerFirst103.click();
            actions.doubleClick().build().perform();

            timeSinch101.click();
            actions.doubleClick().build().perform();
            editTime(0, 5, 0);

            timeSinch103.click();
            actions.doubleClick().build().perform();
            editTime(0, 5, 0);

            timeSinch104.click();
            actions.doubleClick().build().perform();
            editTime(0, 5, 0);

            leadTime.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            t0.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            t1.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "5" + Keys.ENTER);

            t2.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            t3.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "0" + Keys.ENTER);

            k.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "2" + Keys.ENTER);

            w.click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys(Keys.BACK_SPACE + "8" + Keys.ENTER);

            recordProject();

            if (!test60870("101", directoryForDocument)) {
                passTest = false;
            }
            if (!test60870("103", directoryForDocument)) {
                passTest = false;
            }
            if (!test60870("104", directoryForDocument)) {
                passTest = false;
            }
            if (!test60870("RTU", directoryForDocument)) {
                passTest = false;
            }
            if (!test60870("TCP", directoryForDocument)) {
                passTest = false;
            }


            driver.findElementByName("Протоколы АСУ").findElementByName("IEC 61850-8-1").click();
            WindowsElement TOsrab = driver.findElementByName("ТО сраб.");
            dragAndDrop(TOsrab, -350, 50);

            recordProject();

            List<WindowsElement> allTOsrab = driver.findElementsByName("ТО сраб.");
            allTOsrab.remove(TOsrab);
            allTOsrab.get(0).click();
            driver.getKeyboard().sendKeys(Keys.DELETE);
            allTOsrab = driver.findElementsByName("ТО сраб.");
            if (allTOsrab.size() > 1) {
                passTest = false;
                resultString.append("Элемент ТОсраб. не был удален");
            }
            recordProject();

            driver = configDriver;
            if (passTest) {
                LOGGER.info("Тест TC_setting_ASU завершился удачно!");
                System.out.println("Тест TC_setting_ASU завершился с положительным результатом");
                checkSaveProject(true);
                closeDriver();
            } else {
                LOGGER.error("Тест TC_setting_ASU завершился неудачно");
                LOGGER.error(exceptionString);
                System.out.println(exceptionString);
                checkSaveProject(false);
                closeDriver();
            }
        }
    }

    public static void reboot() {
        driver.findElementByName("Область компонентов и органов управления").findElementByClassName("AfxFrameOrView120u").click();
    }

    public static void catchException(String textError, String testName, Exception e) {
        System.out.println(textError + testName);
        LOGGER.error(textError, testName);
        LOGGER.error(e);
        e.printStackTrace();
        closeDriver();
        testStatus = 1;
    }

    public static void differentProperties() throws MalformedURLException, InterruptedException {
        if (!checkHaveElementWithName("Configurator-MT")) {
            choiceWindowsRoot();
            if (checkHaveElementWithName("Configurator-MT")) {
                choiceWindowWithName("Configurator-MT");
                findElementByNameAndClick("ОК");
                driver.close();

            }
        } else {
            findElementByNameAndClick("ОК");

        }

    }

    public static boolean test60870(String protocol, String directoryForDocument) throws InterruptedException, MalformedURLException {
        driver.findElementByName("Протоколы АСУ").findElementByName(protocol).click();
        boolean passTest = true;


        WindowsElement equipmentLayout = null;
        WindowsElement analogValue = null;
        WindowsElement timezone = null;
        WindowsElement clearBuffer = null;
        WindowsElement maxValue = null;
        WindowsElement generalFunction = null;
        if (!(protocol.equals("RTU") || protocol.equals("TCP")) && checkHaveElementWithName("Is not set")) {
            equipmentLayout = driver.findElementByName("Is not set");
            equipmentLayout.click();
            doubleClick();
            actions.sendKeys("1" + Keys.ENTER).build().perform();
            if (!equipmentLayout.getAttribute("Name").equals("Is not set1")) {
                passTest = false;
                resultString.append("Дополнительные настройки расположения аппаратурыне защиты не изменились\n");
            }
        }
        if (checkHaveElementWithName("Вторичное")) {
            analogValue = driver.findElementByName("Вторичное");
            analogValue.click();
            doubleClick();
            if (!analogValue.getAttribute("Name").equals("Первичное")) {
                passTest = false;
                resultString.append("Дополнительные настройки аналоговых значений не изменились\n");
            }
        }
        if (!(protocol.equals("RTU") || protocol.equals("TCP")) && checkHaveElementWithName("Нет") && checkHaveElementWithName("Да")) {
            timezone = driver.findElementByName("Нет");
            clearBuffer = driver.findElementByName("Да");
            timezone.click();
            doubleClick();
            clearBuffer.click();
            doubleClick();
            if (!timezone.getAttribute("Name").equals("Да")
                    && !clearBuffer.getAttribute("Name").equals("Нет")) {
                passTest = false;
                resultString.append("Дополнительные настройки не изменились\n");
            }
        }
        if (Objects.equals(protocol, "103") && checkHaveElementWithName("1.2")) {
            maxValue = driver.findElementByName("1.2");
            maxValue.click();
            doubleClick();
            if (!maxValue.getAttribute("Name").equals("2.4")) {
                passTest = false;
                resultString.append("Дополнительные настройки максимального значения не изменились\n");
            }
        }
        if (Objects.equals(protocol, "103") && checkHaveElementWithName("1")) {
            generalFunction = driver.findElementByName("1");
            generalFunction.click();
            doubleClick();
            actions.sendKeys("1" + Keys.ENTER).build().perform();
            if (!generalFunction.getAttribute("Name").equals("11")) {
                passTest = false;
                resultString.append("Дополнительные настройки основной функции блока не изменились\n");
            }
        }
        WindowsElement countElements = null;
        WindowsElement percents = null;
        WindowsElement free = null;
        if (protocol.equals("101") || protocol.equals("104")) {
            countElements = driver.findElementByName("20");
            percents = driver.findElementByName("1%");
            free = driver.findElementByName("1880");
        } else if (protocol.equals("103")) {
            countElements = driver.findElementByName("8");
            percents = driver.findElementByName("0%");
            free = driver.findElementByName("1892");
        } else {
            countElements = driver.findElementByName("9");
            percents = driver.findElementByName("0%");
            free = driver.findElementByName("1891");
        }

        findElementByNameAndClick("Редактировать список");
        driver = configDriver;
        if (protocol.equals("101") || protocol.equals("104")) {
            WindowsElement discreteInputs = driver.findElementByName("Дискретные входы");
            dragAndDrop(discreteInputs, -300, 300);
//            actions.dragAndDropBy(discreteInputs, -300, 300).build().perform();
            sleep(5);
            driver.findElementByName("Выбор группы для вставки").findElementByName("Дискретные входы").click();

            driver.findElementByName("Выбор группы для вставки").findElementByName("OK").click();
//            driver.findElementByName("Редактирование списка данных для мониторинга (протокол IEC 60870-5-101)").findElementsByClassName("Button").get(2).click();
            List<WindowsElement> buttons = driver.findElementsByClassName("Button");
            buttons.get(2).click();
        } else if (protocol.equals("103")) {
            sleep(5);
            List<WindowsElement> discreteInputs = driver.findElementsByName("Дискретные входы");
            System.out.println(discreteInputs.size());
            discreteInputs.get(1).click();
            discreteInputs.get(0).click();
            dragAndDrop(discreteInputs.get(0), -300, 500);
//            driver.findElementByName("Редактирование списка данных для мониторинга (протокол IEC 60870-5-101)").findElementsByClassName("Button").get(2).click();
            List<WindowsElement> buttons = driver.findElementsByClassName("Button");
            buttons.get(0).click();
        } else {
            WindowsElement discreteInputs = driver.findElementByName("Дискретные входы");
//            discreteInputs.click();
            dragAndDrop(discreteInputs, -300, 300);
//            actions.dragAndDropBy(discreteInputs, -300, 300).build().perform();
            sleep(5);
            List<WindowsElement> buttons = driver.findElementsByClassName("Button");
            buttons.get(6).click();
        }
        saveFileWithDirectory(directoryForDocument, "test" + protocol);
        driver.getKeyboard().sendKeys(Keys.ENTER);
        findElementByNameAndClick("OK");

        if ((protocol.equals("101") || protocol.equals("104")) && (countElements.getAttribute("Name").equals("20")
                || percents.getAttribute("Name").equals("1%")
                || free.getAttribute("Name").equals("1880"))) {
            passTest = false;
            resultString.append("Список не обновился у протокола ").append(protocol).append("\n");
        } else if (protocol.equals("103") && (countElements.getAttribute("Name").equals("8")
                || percents.getAttribute("Name").equals("0%")
                || free.getAttribute("Name").equals("1892"))) {

            passTest = false;
            resultString.append("Список не обновился у протокола ").append(protocol).append("\n");
        } else if ((protocol.equals("RTU") || protocol.equals("TCP")) && (countElements.getAttribute("Name").equals("9")
                || percents.getAttribute("Name").equals("0%")
                || free.getAttribute("Name").equals("1891"))) {

            passTest = false;
            resultString.append("Список не обновился у протокола ").append(protocol).append("\n");
        }

        recordProject();

        if (protocol.startsWith("10")) {
            if (equipmentLayout == null) {
                equipmentLayout = driver.findElementByName("Is not set1");

            }
            equipmentLayout.click();
            doubleClick();
            actions.sendKeys(Keys.BACK_SPACE + "" + Keys.ENTER).build().perform();
            if (analogValue == null) {
                analogValue = driver.findElementByName("Первичное");

            }
            if (maxValue == null && protocol.equals("103")) {
                maxValue = driver.findElementByName("2.4");
                maxValue.click();
                doubleClick();
            }
            if (generalFunction == null && protocol.equals("103")) {
                maxValue = driver.findElementByName("11");
                maxValue.click();
                doubleClick();
                actions.sendKeys(Keys.BACK_SPACE + "" + Keys.ENTER).build().perform();
            }
            analogValue.click();
            doubleClick();
            timezone.click();
            doubleClick();

            clearBuffer.click();
            doubleClick();
            if (equipmentLayout.getAttribute("Name").equals("Is not set1")
                    && timezone.getAttribute("Name").equals("Да")
                    && analogValue.getAttribute("Name").equals("Первичное")
                    && clearBuffer.getAttribute("Name").equals("Нет")) {
                passTest = false;
                resultString.append("Дополнительные настройки не изменились");
            }
        } else {
            if (analogValue == null) {
                analogValue = driver.findElementByName("Первичное");

            }
            analogValue.click();
            doubleClick();
            if (analogValue.getAttribute("Name").equals("Первичное")) {
                passTest = false;
                resultString.append("Дополнительные настройки не изменились");
            }
        }
        findElementByNameAndClick("Редактировать список");

        if (protocol.equals("101") || protocol.equals("104")) {
            findElementByNameAndClick("[+] Дискретные входы");
            doubleClick();
            for (int i = 0; i < 24; i++) {
                actions.sendKeys(Keys.DOWN).sendKeys(Keys.DELETE).build().perform();
            }
        } else if (protocol.equals("103")) {
            sleep(5);
            List<WindowsElement> discreteInputs = driver.findElementsByName("Дискретные входы");
            discreteInputs.get(1).click();
            findElementByNameAndClick("[Я1] РПО");
            for (int i = 0; i < 23; i++) {
                actions.sendKeys(Keys.DELETE).sendKeys(Keys.DOWN).build().perform();
            }
        } else {
            findElementByNameAndClick("[+] Дискретные входы (Discrete Inputs)");
            doubleClick();
            actions.sendKeys(Keys.DOWN).build().perform();
            for (int i = 0; i < 24; i++) {
                actions.sendKeys(Keys.DELETE).build().perform();
            }
        }

        findElementByNameAndClick("OK");

        if ((protocol.equals("101") || protocol.equals("104")) && (!countElements.getAttribute("Name").equals("20")
                || !percents.getAttribute("Name").equals("1%")
                || !free.getAttribute("Name").equals("1880"))) {
            passTest = false;
            resultString.append("Список не обновился у протокола ").append(protocol).append("\n");
        } else if (protocol.equals("103") && (!countElements.getAttribute("Name").equals("8")
                || !percents.getAttribute("Name").equals("0%")
                || !free.getAttribute("Name").equals("1892"))) {

            passTest = false;
            resultString.append("Список не обновился у протокола ").append(protocol).append("\n");
        } else if ((protocol.equals("RTU") || protocol.equals("TCP")) && (!countElements.getAttribute("Name").equals("9")
                || !percents.getAttribute("Name").equals("0%")
                || !free.getAttribute("Name").equals("1891"))) {
            passTest = false;
            resultString.append("Список не обновился у протокола ").append(protocol).append("\n");
        }

        recordProject();

        return passTest;
    }

    public static void recordProject() throws InterruptedException {
        findElementByNameAndClick("Записать (Ctrl+W)");
        findElementByNameAndClick("Записать");
        sleep(30);
        findElementByNameAndClick("ОК");
    }

    public static void choiceSubItem(String subItemName) {
        List<WindowsElement> allElementsWithoutSubItems = driver.findElementsByName(subItemName);
        doubleClick();
        List<WindowsElement> allElementsWithSubItems = driver.findElementsByName(subItemName);
        allElementsWithSubItems.removeAll(allElementsWithoutSubItems);
        allElementsWithSubItems.get(0).click();
    }

    public static void editTime(int hour, int minute, int second) {
        List<WindowsElement> timeUnits = driver.findElementsByClassName("Edit");
        timeUnits.get(0).click();
        doubleClick();
        driver.getKeyboard().sendKeys(String.valueOf(hour));
        timeUnits.get(1).click();
        doubleClick();
        driver.getKeyboard().sendKeys(String.valueOf(minute));
        timeUnits.get(2).click();
        doubleClick();
        driver.getKeyboard().sendKeys(String.valueOf(second));
        findElementByNameAndClick("OK");
    }

    public static void setProject() throws InterruptedException {
        findElementByNameAndClick("Записать (Ctrl+W)");
        sleep(6);
        findElementByNameAndClick("ОК");
        LocalDateTime finish = LocalDateTime.now().plusMinutes(1);
        sleep(10);
        while (!checkHaveElementWithName("====== Данные успешно записаны в блок! ======") && LocalDateTime.now().isBefore(finish)) {
            sleep(1);
        }
    }

    public static void addChoiceDeleteComponent(int yOffset) throws MalformedURLException, InterruptedException {
        findElementByNameAndClick("Рисунок");
        actions.moveByOffset(0, 30);
        actions.click().build().perform();
        actions.moveByOffset(5, yOffset);
        actions.contextClick().build().perform();
        configDriver = driver;
        choiceWindowsRoot();
        choiceWindowWithName("Контекст");
        findElementByNameAndClick("Удалить элемент");
        driver = configDriver;
        findElementByNameAndClick("Да");
    }

    public static void findElementByNameAndClick(String name) {
        LOGGER.info("Поиск элемента с именем - " + name);
        driver.findElementByName(name).click();
    }

    public static void dragAndDrop(WindowsElement element, int x, int y) {
        actions.moveToElement(element).build().perform();
        actions.clickAndHold(element).moveByOffset(x, y).click().build().perform();
        actions.click().build().perform();
        actions.release().perform();
    }

    public static boolean openFileARM(String directory, String fileName) throws InterruptedException {
        driver.findElementByName("Стандартный").findElementByName("Открыть").click();

        return openFileWithDirectory(directory, fileName);
    }

    public static boolean openPMKFile(String directory, String fileName) throws InterruptedException {
        findElementByNameAndClick("Открыть");
//        driver.getKeyboard().sendKeys(Keys.CONTROL + "o" + Keys.CONTROL);
        return openFileWithDirectory(directory, fileName);
    }

    public static boolean openFileWithDirectory(String directory, String fileName) throws InterruptedException {


        driver.findElementByName("Все папки").click();
        Thread.sleep(timer);
        WindowsElement directoryField = null;
        if (checkHaveElementWithName("Адрес")) {
            directoryField = driver.findElementByName("Адрес");
        } else {
            actions.click().build().perform();
            directoryField = driver.findElementByName("Адрес");

        }
        String startDirectory = directoryField.getAttribute("Value.Value");


        if (!directory.equals(startDirectory)) {
            sendKeys(directory);
        }
        return openFile(fileName);


    }

    public static boolean openFile(String name) throws InterruptedException {
        try {
            addToCopyBuffer(name);
            driver.findElementByClassName("ComboBox").findElementByName("Имя файла:").sendKeys(Keys.CONTROL + "v" + Keys.ENTER);
            LOGGER.debug("Открывается файл - " + name);
            Thread.sleep(10000);
        } catch (NoSuchElementException e) {

            LOGGER.warn("File with name - " + name + " not found");

            return false;

        }
        return true;
    }


    public static boolean saveFileWithDirectory(String directory, String fileName) throws InterruptedException {


        driver.findElementByName("Все папки").click();
        Thread.sleep(timer);
        WindowsElement directoryField = driver.findElementByName("Адрес");
        String startDirectory = directoryField.getAttribute("Value.Value");


        if (!directory.equals(startDirectory)) {
            sendKeys(directory);
        }
        return saveFile(fileName);


    }

    public static boolean saveFile(String name) throws InterruptedException {
        try {
            addToCopyBuffer(name);
            driver.findElementByClassName("Edit").findElementByName("Имя файла:").sendKeys(Keys.CONTROL + "v" + Keys.ENTER);
            LOGGER.debug("Сохраняется файл - " + name);
            Thread.sleep(10000);
        } catch (NoSuchElementException e) {

            LOGGER.warn("File with name - " + name + " not found");

            return false;

        }
        return true;
    }

    public static boolean checkHaveElementWithName(String name) {
        try {
            LOGGER.debug("Поиск элемента с именем - " + name);
            System.out.println("Поиск элемента с именем - " + name);
            driver.findElementByName(name);
            return true;
        } catch (NoSuchElementException | NoSuchWindowException e) {
            LOGGER.warn("Элемент с именем - " + name + " не найден");
            System.out.println("Элемент с именем - " + name + " не найден");
            return false;
        }
    }

    public static void choiceWindowWithName(String windowName) throws MalformedURLException, InterruptedException {
        sleep(5);
        WindowsElement appWindow = driver.findElementByName(windowName);

        if (appWindow != null) {
            int appIntWinHandle = Integer.parseInt(appWindow.getAttribute("NativeWindowHandle"));
            String appWinHandle = Integer.toHexString(appIntWinHandle);

            capConfChoice.setCapability("appTopLevelWindow", appWinHandle);
//            driver.close();
            driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), capConfChoice);
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            try {
                expandApp();
            } catch (WebDriverException ignored) {
            }
        } else {
            LOGGER.error("Program " + windowName + " not found");
        }

    }

    public static void choiceWindowsRoot() throws MalformedURLException {
        driver = driverRoot;
    }

    private static void expandApp() {
        try {
            driver.findElementByName("Развернуть").click();
        } catch (NoSuchElementException ignored) {
        }
    }

    private static void setUp() throws IOException {
        windowsCap.setCapability("platformName", "Windows");
        windowsCap.setCapability("app", "Root");
        windowsCap.setCapability("deviceName", "WindowsPC");
        String textProperties = new String(Files.readAllBytes(Paths.get(".\\jsonProperties\\properties.json")), StandardCharsets.UTF_8);
        properties = new JSONObject(textProperties);
        JSONObject paths = properties.getJSONObject("paths");
        String armPath = paths.getString("pathToArm");
        String confPath = paths.getString("pathToConf");
        String linkMtPath = paths.getString("pathToLinkMT");
        capForOpenARM.setCapability("app", armPath);
        capForOpenConf.setCapability("app", confPath);
        capForOpenLinkMT.setCapability("app", linkMtPath);
        JSONObject bfpoProperties = properties.getJSONObject("bfpo");
        bfpoDirectory = bfpoProperties.getString("bfpoDirectory");
        bfpoName = bfpoProperties.getString("bfpoName");
        pmkName = bfpoProperties.getString("pmkName");
        driverRoot = new WindowsDriver<WindowsElement>(new URL(urlWinApp), windowsCap);
        ;


    }

    public static void openApp(DesiredCapabilities appCap) throws MalformedURLException, InterruptedException {
        if (properties.getJSONObject("debug").getBoolean("useOpenConf")) {
            choiceWindowsRoot();
            choiceWindowWithName("БФПО-152-КСЗ-01_25.sth_a - Конфигуратор-МТ");
        } else {
            try {
                driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), appCap);
                driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
                expandApp();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void sleep(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
    }

    public static void addToCopyBuffer(String copy) {
        StringSelection stringSelection = new StringSelection(copy);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void sendKeys(String name) {
        addToCopyBuffer(name);
        driver.getKeyboard().sendKeys(Keys.CONTROL + "v" + Keys.CONTROL + Keys.ENTER);
    }

    public static boolean openProject(String bfpoDirectory, String pmkName, String bfpoName) {
        if (properties.getJSONObject("debug").getBoolean("openProject")) {
            return true;
        }
        try {

            if (openPMKFile(bfpoDirectory, pmkName)) {
                choiceWindowsRoot();
                choiceWindowWithName("Выбор совместимого файла БФПО");
                findElementByNameAndClick("Выбор файла БФПО вручную");
                choiceWindowsRoot();
                choiceWindowWithName("Выберите файл БФПО (ПМК создан файлом БФПО: БФПО-152-КСЗ-01_25)");

                openFileWithDirectory(bfpoDirectory, bfpoName);
                driver = configDriver;
                Thread.sleep(5000);
                if (checkHaveElementWithName("Обновление программного комплекса \"Конфигуратор-МТ\"")) {
                    driver.findElementByName("OK").click();
                }


                Thread.sleep(50000);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Открытие проекта: " + pmkName + " не было выполнено из-за ошибки: " + e);
            return false;
        }
    }

    public static void collapseMenu() {
        try {

            for (int i = 0; i < 5; i++) {
                hideInformation();
                driver.findElementByName("Круговая диаграмма").findElementByName("PropertyList").click();
                findElementByNameAndClick("Параметры панели инструментов");
                sleep(2);
//                driver.findElementByName("Параметры панели инструментов").findElementByName("Отображать меньше кнопок").click();
//                findElementByNameAndClick("Параметры панели инструментов");
//                driver.findElementByName("Параметры области переходов...").click();
                driver.getKeyboard().sendKeys(Keys.DOWN);
                driver.getKeyboard().sendKeys(Keys.DOWN);
                driver.getKeyboard().sendKeys(Keys.ENTER);
                sleep(2);
            }
        } catch (Exception ignored) {
        }
    }

    public static void hideInformation() throws InterruptedException {
//        while (checkHaveElementWithName("Информация")) {
//            driver.findElementByName("Круговая диаграмма").findElementByName("Круговая диаграмма").click();
        driver.findElementByName("Круговая диаграмма").findElementByName("PropertyList").click();
//            driver.findElementByName("Панель задач").click();
//            driver.getKeyboard().sendKeys(Keys.LEFT_ALT);
        sleep(5);
//        }
    }

    public static boolean checkProtocols(String protocol) {
        boolean checkHaveUp = checkHaveElementWithName("Up");
        boolean checkHaveDown = checkHaveElementWithName("Down");
        boolean checkHaveQuestion = checkHaveElementWithName("?");
        boolean checkProtocol;
        switch (protocol) {
            case "RSTP":
                checkProtocol = checkHaveElementWithName("Root");
                break;
            case "PRP":
                checkProtocol = checkHaveElementWithName("On") && checkHaveElementWithName("Off");
                break;
            default:
                checkProtocol = checkHaveElementWithName("Forwarding") && checkHaveElementWithName("Disconnected");
                break;
        }


        return checkHaveDown && checkHaveUp && !checkHaveQuestion && checkProtocol;
    }

    public static void checkSaveProject(boolean save) {

        if (properties.getJSONObject("debug").getBoolean("needToCloseApp")) {
            try {

                driver.findElementByName("Закрыть").click();
                if (checkHaveElementWithName("Configurator-MT")) {
                    if (save) {
                        findElementByNameAndClick("Да");
                    } else {
                        findElementByNameAndClick("Нет");
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void choiceButton(String buttonName) {
        if (driver.findElementsByName(buttonName).size() == 1) {
            findElementByNameAndClick(buttonName);
        }
    }

    public static void setParameterValue(String parameter, CharSequence value) {

        findElementByNameAndClick(parameter);
        actions.doubleClick().build().perform();
        driver.getKeyboard().sendKeys(value);
    }

    public static void checkAndApplying() throws InterruptedException {
        findElementByNameAndClick("Проверка и применение (Ctrl+E)");
        sleep(20);
        if (checkHaveElementWithName("Да")) {
            findElementByNameAndClick("Да");
        }
        sleep(80);
    }

    public static void applyingAndPushing() throws MalformedURLException, InterruptedException {
        checkAndApplying();

        differentProperties();
        driver = configDriver;
        reboot();
        setProject();
    }

    public static void choiceStringAndRenameIt(String string, String stringIndicator, int moveDistance, String resultString, boolean beforeChangeComment) {
        MobileElement center = driver.findElementByName(string).findElementByName(stringIndicator);
        center.click();
        actions.moveByOffset(moveDistance, 0).build().perform();
        actions.doubleClick().build().perform();
        driver.getKeyboard().sendKeys(resultString + Keys.ENTER);
        if (beforeChangeComment) {
            if (checkHaveElementWithName("123")) {
                driver.findElementByName("123").click();
                actions.doubleClick().build().perform();
                driver.getKeyboard().sendKeys("456" + Keys.ENTER);
            }
        } else {
            driver.findElementByName("456").click();
            actions.doubleClick().build().perform();
            driver.getKeyboard().sendKeys("123" + Keys.ENTER);
        }
    }

    public static void closeConfigurator() {
        if (properties.getJSONObject("debug").getBoolean("needToCloseApp")) {
            try {
                Runtime.getRuntime().exec("taskkill /IM Configurator-MT.exe /f");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeDriver() {
        if (properties.getJSONObject("debug").getBoolean("needToCloseApp")) {
            try {
                driver.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static void checkSystemError() {
        try {
            choiceWindowsRoot();
            if (checkHaveElementWithName("Configurator-MT.exe - System Error")) {
                System.err.println("Открыто окно ошибки для запуска нового запуска тестов");
                System.exit(1);
            }
        } catch (Exception ignored) {
        }
    }

    public static void testRunner(String testName) {
        resultString.append("\nТест: ").append(testName);
        try {
            checkSystemError();
            choiceTestByName(testName);
            resultString.append(" - завершился удачно с 1 попытки");
        } catch (Exception e1) {
            checkSaveProject(false);
            closeConfigurator();
            closeDriver();
            try {
                catchException("1 попытка теста: {} прошла неудачно", testName, e1);
                choiceTestByName(testName);
                resultString.append("завершился удачно с 2 попытки");
            } catch (Exception e2) {
                closeConfigurator();
                closeDriver();
                try {
                    catchException("2 попытка теста: {} прошла неудачно", testName, e2);
                    choiceTestByName(testName);
                    resultString.append("завершился удачно с 3 попытки");
                } catch (Exception e3) {
                    catchException("3 попытка теста: {} прошла неудачно", testName, e3);
                    closeConfigurator();
                    closeDriver();
                    testStatus = 1;
                    resultString.append("завершился неудачно все 3 раза");
                }
            }
        }
    }

    public static void choiceTestByName(String testName) throws Exception {
        closeConfigurator();
        switch (testName) {
            case "TC_ARM_CFG_Compile":
                TC_ARM_CFG_Compile();
                break;
            case "TC_redundancy_protocol":
                TCRedundancyProtocols();
                break;
            case "TC_change_project_param_and_get_osc":
                TCChangeProjectParamAndGetOsc();
                break;
            case "TC_rename_input_output":
                TCRenameInputOutput();
                break;
            case "TC_add_components_area":
                TCAddComponentsArea();
                break;
            case "TC_download_document":
                TCDownloadDocument();
                break;
            case "TC_Ethernet_connect":
                TCEthernetConnect();
                break;
            case "TC_USB_Connection":
                TCUSBConnection();
                break;
            case "TC_Link_MT":
                TCLinkMT();
                break;
            case "TC_download_blanks":
                TCDownloadBlanks();
                break;
            case "TC_save_block_image":
                TCSaveBlockImage();
                break;
            case "TC_interrupt_transmission":
                TCInterruptTransmission();
                break;
            case "TC_setting_ASU":
                TCSettingASU();
                break;
        }
    }

    public static void doubleClick() {
        actions.doubleClick().build().perform();
    }
}
