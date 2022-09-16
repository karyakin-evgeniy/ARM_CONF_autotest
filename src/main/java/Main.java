import io.appium.java_client.MobileElement;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
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
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String urlWinApp = "http://127.0.0.1:4723/";
    private static WindowsDriver<WindowsElement> driver = null;
    private static WindowsDriver<WindowsElement> driverRoot = null;
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
        JSONObject tests = properties.getJSONObject("test");
        boolean startAllTests = tests.getBoolean("allTestStart");
        if (startAllTests || tests.getBoolean("TC_ARM_CFG_Compile")) {
            testRunner("TC_ARM_CFG_Compile");
        } if (startAllTests || tests.getBoolean("TC_redundancy_protocol")) {
            testRunner("TC_redundancy_protocol");
        } if (startAllTests || tests.getBoolean("TC_change_project_param_and_get_osc")) {
            testRunner("TC_change_project_param_and_get_osc");
        } if (startAllTests || tests.getBoolean("TC_rename_input_output")) {
            testRunner("TC_rename_input_output");
        } if (startAllTests || tests.getBoolean("TC_add_image")) {
            testRunner("TC_add_image");
        } if (startAllTests || tests.getBoolean("TC_download_document")) {
            testRunner("TC_download_document");
        } if (startAllTests || tests.getBoolean("TC_Ethernet_connect")) {
            testRunner("TC_Ethernet_connect");
        } if (startAllTests || tests.getBoolean("TC_USB_Connection")) {
            testRunner("TC_USB_Connection");
        }
        if (startAllTests || tests.getBoolean("TC_Link_MT")) {
            testRunner("TC_Link_MT");
        }
        Thread.sleep(5000);
        Runtime.getRuntime().exit(testStatus);

        try {
            driver.close();
        } catch (Exception ignored) {}
    }

    public static void TCDownloadDocument() throws Exception {
            openApp(capForOpenConf);
            sleep(4);
            WindowsDriver<WindowsElement> configDriver = driver;

            if (!openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
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
                WindowsElement directoryField =  driver.findElementByName("Адрес");
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
                    driver.close();
                    LOGGER.info("Тест TC_download_document завершился удачно!");
                    System.out.println("Тест TC_download_document завершился с положительным результатом");
                } else {
                    LOGGER.error("Тест TC_download_document завершился неудачно");
                    LOGGER.error("Документ не был сохранён");
                    driver.close();
                    throw new Exception("Тест TC_download_document завершился неудачно. Документ не был сохранён");
                }
            }


    }

    public static void TCRenameInputOutput() throws Exception {
            openApp(capForOpenConf);
            sleep(4);
            WindowsDriver<WindowsElement> configDriver = driver;

            if (!openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
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
                    choiceStringAndRenameIt("Я22 Вход", "22", -50, "Д Вход");

                } else if (!checkHaveElementWithName("Д Вход")) {
                    throw new Exception("Дискретный вход имеет неккоректное имя");
                }


                applyingAndPushing(configDriver);


//                hideInformation();
//                choiceButton("Настройки РЗА");
                findElementByNameAndClick("Дискретные входы");

                choiceStringAndRenameIt("Д Вход", "22", -50, "Я22 Вход");

                applyingAndPushing(configDriver);

//=================================================================================================
//                Дискретные выходы

                findElementByNameAndClick("Дискретные выходы");
                if (checkHaveElementWithName("K7 Выход")) {
                    choiceStringAndRenameIt("K7 Выход", "Перекидной", -150, "Выход");

                } else if (!checkHaveElementWithName("Выход")) {
                    throw new Exception("Дискретный выход имеет неккоректное имя");
                }


                applyingAndPushing(configDriver);


//                hideInformation();
//                choiceButton("Настройки РЗА");
                findElementByNameAndClick("Дискретные выходы");

                choiceStringAndRenameIt("Выход", "Перекидной", -150, "K7 Выход");

                applyingAndPushing(configDriver);



//=================================================================================================
//                Виртуальные входы

                findElementByNameAndClick("Виртуальные входы");
                if (checkHaveElementWithName("Вирт вход 1")) {
                    choiceStringAndRenameIt("Вирт вход 1", "Вирт вход 1", -200, "Вход");

                } else if (!checkHaveElementWithName("Вход")) {
                    throw new Exception("Вирутальный вход имеет неккоректное имя");
                }


                applyingAndPushing(configDriver);


//                hideInformation();
//                choiceButton("Настройки РЗА");
                findElementByNameAndClick("Виртуальные входы");

                choiceStringAndRenameIt("Вход", "Вход", -200, "Вирт вход 1");

                applyingAndPushing(configDriver);



//=================================================================================================
//                Виртуальные выходы

                findElementByNameAndClick("Виртуальные выходы");
                if (checkHaveElementWithName("Вирт выход 1")) {
                    choiceStringAndRenameIt("Вирт выход 1", "Вирт выход 1", -200, "В Выход");

                } else if (!checkHaveElementWithName("В Выход")) {
                    throw new Exception("Виртуальный выход имеет неккоректное имя");
                }


                applyingAndPushing(configDriver);


//                hideInformation();
//                choiceButton("Настройки РЗА");
                findElementByNameAndClick("Виртуальные выходы");

                choiceStringAndRenameIt("В Выход", "В Выход", -200, "Вирт выход 1");

                applyingAndPushing(configDriver);



                driver = configDriver;
                driver.close();
                LOGGER.info("Тест TCRenameInputOutput завершился удачно!");
                System.out.println("Тест TCRenameInputOutput завершился с положительным результатом");
            }


    }

    public static void TCAddImage() throws Exception {
            openApp(capForOpenConf);
            Thread.sleep(4000);
            WindowsDriver<WindowsElement> configDriver = driver;

            if (!openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
                throw new Exception("Проект не был открыт");
            } else {

                actions = new Actions(driver);
                findElementByNameAndClick("Рисунок");
                JSONObject image = properties.getJSONObject("image");
                String directoryToImage = image.getString("directory");
                String imageName = image.getString("name");
                openFileWithDirectory(directoryToImage, imageName);
                findElementByNameAndClick("Да");
                findElementByNameAndClick("Рисунок");
                actions.moveByOffset(0, 30);
                actions.click().build().perform();
                actions.moveByOffset(5, 5);
                actions.contextClick().build().perform();
                configDriver = driver;
                choiceWindowsRoot();
                choiceWindowWithName("Контекст");
                findElementByNameAndClick("Удалить элемент");
                driver = configDriver;
                findElementByNameAndClick("Да");
                sleep(60);

                checkSaveProject();
                driver = configDriver;
                driver.close();
                LOGGER.info("Тест TCAddImage завершился удачно!");
                System.out.println("Тест TCAddImage завершился с положительным результатом");

            }



    }

    public static void TCChangeProjectParamAndGetOsc() throws Exception {
            openApp(capForOpenConf);
            Thread.sleep(4000);
            WindowsDriver<WindowsElement> configDriver = driver;

            if (!openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
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

                applyingAndPushing(configDriver);

                sleep(10);
                hideInformation();
                choiceButton("Мониторинг");

                findElementByNameAndClick("Осциллограммы");
                reboot();
                sleep(10);
                findElementByNameAndClick("Загрузить");driver.findElementByName("Все папки").click();
                String directory = properties.getJSONObject("paths").getString("pathForOsc");

                WindowsElement directoryField =  driver.findElementByName("Адрес");
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

                applyingAndPushing(configDriver);

                if (oscNameCheck && checkHaveOsc) {
                    driver = configDriver;
                    driver.close();
                    LOGGER.info("Тест TC_change_project_param_and_get_osc завершился удачно!");
                    System.out.println("Тест TC_change_project_param_and_get_osc завершился с положительным результатом");
                } else {
                    LOGGER.error("Тест TC_change_project_param_and_get_osc завершился неудачно");
                    LOGGER.error("Имя осциллогаммы не корректное");
                    driver.close();
                    throw new Exception();
                }
            }


    }

    public static void TCRedundancyProtocols() throws Exception {
            openApp(capForOpenConf);
            Thread.sleep(4000);
            WindowsDriver<WindowsElement> configDriver = driver;

            if (!openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
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
                checkSaveProject();
                if (LinkBackUp && RayMode && RSTP && PRP && HSR) {
                    LOGGER.info("Тест TCRedundancyProtocols завершился удачно!");
                    System.out.println("Тест TCRedundancyProtocols завершился с положительным результатом");
                } else {
                    LOGGER.error("Тест TC_redundancy_protocols завершился неудачно");
                    LOGGER.error("LinkBackUp - " + LinkBackUp + "; RayMode - " + RayMode + "; RSTP - " + RSTP + "; PRP - " + PRP + "; HSR - " + HSR);
                    driver.close();
                }
            }


    }

    public static void TCEthernetConnect() throws Exception {
            openApp(capForOpenConf);
            Thread.sleep(4000);

            WindowsDriver<WindowsElement> configDriver = driver;
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
            if (openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
                findElementByNameAndClick("Подключиться (Ctrl+Q)");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
                sleep(10);
                checkSaveProject();
                try {
                    driver.close();
                } catch (NoSuchWindowException ignored) {}
                LOGGER.info("Тест TCEthernetConnection завершился удачно!");
                System.out.println("Тест TCEthernetConnection завершился с положительным результатом");
            } else {
                LOGGER.error("Проект " + pmkName + " не был открыт");
                LOGGER.error("Тест TCEthernetConnection завершился неудачно");
                driver.close();
                throw new Exception();
            }


    }

    public static void TCUSBConnection() throws Exception {
            openApp(capForOpenConf);
            sleep(4);

            WindowsDriver<WindowsElement> configDriver = driver;
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
            if (openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
                findElementByNameAndClick("Подключиться (Ctrl+Q)");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
                sleep(10);
                checkSaveProject();
                try {
                    driver.close();
                } catch (NoSuchWindowException ignored) {}
                LOGGER.info("Тест TCUSBConnection завершился удачно!");
                System.out.println("Тест TCUSBConnection завершился с положительным результатом");
            } else {
                LOGGER.error("Проект " + pmkName + " не был открыт");
                LOGGER.error("Тест TCUSBConnection завершился неудачно");
                driver.close();
                throw new Exception();
            }


    }

    public static void TCLinkMT() throws MalformedURLException, InterruptedException {
            try {
                driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), capForOpenLinkMT);
            } catch (SessionNotCreatedException ignored) {}
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

            if(!openFileARM(bfpoDirectory, bfpoName)) {
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

            WindowsDriver<WindowsElement> configDriver = driver;

            if (!openProject(bfpoDirectory, pmkName, bfpoName, configDriver)) {
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
                    driver.close();
                    throw new NoSuchElementException("Запись проекта в блок не завершилась или завершилась неудачно");

                }
//            }
                driver = configDriver;
                driver.close();
                LOGGER.info("Тест ARM_CFG_Compile завершился удачно!");
                System.out.println("Тест ARM_CFG_Compile завершился с положительным результатом");
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
        driver.close();
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

    public static void findElementByNameAndClick(String name) {
        LOGGER.info("Поиск элемента с именем - " + name);
        driver.findElementByName(name).click();
    }

    public static boolean openFileARM(String directory, String fileName) throws InterruptedException {
        driver.findElementByName("Стандартный").findElementByName("Открыть").click();

        return openFileWithDirectory(directory, fileName);
    }

    public static boolean openPMKFile(String directory, String fileName) throws InterruptedException {
        findElementByNameAndClick("Открыть");
        driver.getKeyboard().sendKeys(Keys.CONTROL + "o" + Keys.CONTROL);
        return openFileWithDirectory(directory, fileName);
    }

    public static boolean openFileWithDirectory(String directory, String fileName) throws InterruptedException {


        driver.findElementByName("Все папки").click();
        Thread.sleep(timer);
        WindowsElement directoryField =  driver.findElementByName("Адрес");
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
            } catch (WebDriverException ignored) {}
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
        } catch (NoSuchElementException ignored) {}
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
        driverRoot = new WindowsDriver<WindowsElement>(new URL(urlWinApp), windowsCap);;


    }

    public static void openApp(DesiredCapabilities appCap) {
        try {
            driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), appCap);
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            expandApp();
        } catch (MalformedURLException e) {
            e.printStackTrace();
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

    public static boolean openProject(String bfpoDirectory, String pmkName, String bfpoName, WindowsDriver<WindowsElement> configDriver) {
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
                if (checkHaveElementWithName("Обновление программного комплекса \"Конфигуратор-МТ\"")){
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

            for ( int i = 0; i < 5; i++) {
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
        } catch (Exception ignored) {}
    }

    public static void hideInformation() throws InterruptedException {
        while (checkHaveElementWithName("Информация")) {
//            driver.findElementByName("Круговая диаграмма").findElementByName("Круговая диаграмма").click();
            driver.findElementByName("Круговая диаграмма").findElementByName("PropertyList").click();
//            driver.findElementByName("Панель задач").click();
//            driver.getKeyboard().sendKeys(Keys.LEFT_ALT);
            sleep(5);
        }
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

    public static void checkSaveProject() {
        driver.findElementByName("Закрыть").click();
        if (checkHaveElementWithName("Configurator-MT")) {
            findElementByNameAndClick("Да");
        }
    }

    public static void choiceButton(String buttonName) {
        if (driver.findElementsByName(buttonName).size() == 1) {
            findElementByNameAndClick(buttonName);
        }
    }

    public static void setParameterValue(String parameter, String value) {

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

    public static void applyingAndPushing(WindowsDriver configDriver) throws MalformedURLException, InterruptedException {
        checkAndApplying();

        differentProperties();
        driver = configDriver;
        reboot();
        setProject();
    }

    public static void choiceStringAndRenameIt(String string, String stringIndicator, int moveDistance, String resultString) {
        driver.findElementByName(string).findElementByName(stringIndicator).click();
        actions.moveByOffset(moveDistance, 0).build().perform();
        actions.doubleClick().build().perform();
        driver.getKeyboard().sendKeys(resultString + Keys.ENTER);
    }

    public static void closeConfigurator() {
        try {
            Runtime.getRuntime().exec("taskkill /IM Configurator-MT.exe /f");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void testRunner(String testName) {
        try {
            choiceTestByName(testName);
        } catch (Exception e1) {
            closeConfigurator();
            try {
                catchException("1 попытка теста: {} прошла неудачно", testName , e1);
                choiceTestByName(testName);
            } catch (Exception e2) {
                closeConfigurator();
                try {
                    catchException("2 попытка теста: {} прошла неудачно", testName , e2);
                    choiceTestByName(testName);
                } catch (Exception e3) {
                    catchException("3 попытка теста: {} прошла неудачно", testName , e3);
                    closeConfigurator();
                    testStatus = 1;
                }
            }
        }
    }

    public static void choiceTestByName(String testName) throws Exception {
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
            case "TC_add_image":
                TCAddImage();
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
        }
    }
}
