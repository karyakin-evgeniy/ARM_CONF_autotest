import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class ConfiguratorTests {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String urlWinApp = "http://127.0.0.1:4723/";
    private static WindowsDriver<WindowsElement> driver = null;
    private static final DesiredCapabilities windowsCap = new DesiredCapabilities();
    private static final DesiredCapabilities capConfChoice = new DesiredCapabilities();
    private static final DesiredCapabilities capForOpenARM = new DesiredCapabilities();
    private static final DesiredCapabilities capForOpenConf = new DesiredCapabilities();
    private static final int timer = 3000;
    private static Actions actions;
    private static int testStatus = 0;
    private static JSONObject properties;

    @Before
    public void setUp() throws Exception {
        windowsCap.setCapability("platformName", "Windows");
        windowsCap.setCapability("app", "Root");
        windowsCap.setCapability("deviceName", "WindowsPC");
        String textProperties = new String(Files.readAllBytes(Paths.get(".\\jsonProperties\\properties.json")), StandardCharsets.UTF_8);
        properties = new JSONObject(textProperties);
        String armPath = properties.getString("pathToArm");
        String confPath = properties.getString("pathToConf");
        capForOpenARM.setCapability("app", armPath);
        capForOpenConf.setCapability("app", confPath);


    }

    @After
    public void tearDown() throws Exception {


    }
    @Test
    public void compilationArmCfg() throws Exception {

        JSONObject bfpoProperties = properties.getJSONObject("bfpo");
        String bfpoDirectory = bfpoProperties.getString("bfpoDirectory");
        String bfpoName = bfpoProperties.getString("bfpoName");
        String pmkName = bfpoProperties.getString("pmkName");
//        if (args.length > 0) {
//            armPath = args[0];
////                confPath = args[1];
//            bfpoDirectory = args[2];
//            bfpoName = args[3];
//            pmkName = args[4];
//        }
//
//
//        setUp(armPath, confPath);
        JSONObject properties = new JSONObject();
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
                Thread.sleep(2000);
            }
            if (LocalDateTime.now().isAfter(finishArm)) {
                driver.close();
                LOGGER.error("Компиляция не завершилась или завершилась неудачно");
                throw new NoSuchElementException("Компиляция не завершилась или завершилась неудачно");
            }
        }
        driver.closeApp();

        openApp(capForOpenConf);
        Thread.sleep(4000);

//                if (!checkHaveElementWithName("Информация")) {
//                    System.out.println("Кнопка информация не найдена!");
//                } else {
//                    System.out.println("Кнопка информация найдена");
//                    driver.findElementByName("Информация").click();
//                }
        WindowsDriver<WindowsElement> configDriver = driver;
        if(!openPMKFile(bfpoDirectory, pmkName)) {
            LOGGER.error("Файл " + pmkName + " не открылся");
        } else {
            choiceWindowsRoot();
            choiceWindowWithName("Выбор совместимого файла БФПО");
            findElementByNameAndClick("Выбор файла БФПО вручную");
            choiceWindowsRoot();
            choiceWindowWithName("Выберите файл БФПО (ПМК создан файлом БФПО: БФПО-152-КСЗ-01_25)");

            openFileWithDirectory(bfpoDirectory, bfpoName);
            driver = configDriver;
//                actions = new Actions(driver);
            Thread.sleep(5000);
            if (checkHaveElementWithName("Обновление программного комплекса \"Конфигуратор-МТ\"")){
//                    choiceWindowWithName("Обновление программного комплекса \"Конфигуратор-МТ\"");
                driver.findElementByName("OK").click();
            }



            Thread.sleep(50000);

            findElementByNameAndClick("Проверка и применение (Ctrl+E)");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "e" + Keys.CONTROL);
            Thread.sleep(40000);
            findElementByNameAndClick("Подключиться (Ctrl+Q)");
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "q" + Keys.CONTROL);
            Thread.sleep(10000);
            findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);
            Thread.sleep(10000);
            driver.findElementByName("Область компонентов и органов управления").findElementByClassName("AfxFrameOrView120u").click();
//                driver.getKeyboard().sendKeys(Keys.CONTROL + "w" + Keys.CONTROL);
            findElementByNameAndClick("Записать (Ctrl+W)");
            Thread.sleep(6000);
            findElementByNameAndClick("ОК");
//                driver.getKeyboard().sendKeys(Keys.ENTER);
            LocalDateTime finish = LocalDateTime.now().plusMinutes(1);
            Thread.sleep(10000);
//                Thread.sleep(2000);
//                driver.getKeyboard().sendKeys(Keys.ENTER);
            while (!checkHaveElementWithName("====== Данные успешно записаны в блок! ======") && LocalDateTime.now().isBefore(finish)) {
                Thread.sleep(500);
            }
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
            LOGGER.info("Тест завершился удачно!");
            System.out.println("Тест завершился с положительным результатом");
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
//        driver.findElementByName("Открыть (Ctrl+O)").click();
        driver.getKeyboard().sendKeys(Keys.CONTROL + "o" + Keys.CONTROL);
        return openFileWithDirectory(directory, fileName);
    }

    public static boolean openFileWithDirectory(String directory, String fileName) throws InterruptedException {


        driver.findElementByName("Все папки").click();
        Thread.sleep(timer);
        WindowsElement directoryField =  driver.findElementByName("Адрес");
        String startDirectory = directoryField.getAttribute("Value.Value");


        if (!directory.equals(startDirectory)) {
            directoryField.sendKeys(directory + Keys.ENTER);

        }
        return openFile(fileName);


    }

    public static boolean openFile(String name) throws InterruptedException {
        try {
            driver.findElementByClassName("ComboBox").findElementByName("Имя файла:").sendKeys(name + Keys.ENTER);
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
            driver.findElementByName(name);
            LOGGER.debug("Поиск элемента с именем - " + name);
            System.out.println("Поиск элемента с именем - " + name);
            return true;
        } catch (NoSuchElementException | NoSuchWindowException e) {
            LOGGER.warn("Элемент с именем - " + name + " не найден");
            System.out.println("Элемент с именем - " + name + " не найден");
            return false;
        }
    }
    public static void choiceWindowWithName(String windowName) throws MalformedURLException {
        WindowsElement appWindow = driver.findElementByName(windowName);

        if (appWindow != null) {
            int ICTIntWinHandle = Integer.parseInt(appWindow.getAttribute("NativeWindowHandle"));
            String ICTWinHandle = Integer.toHexString(ICTIntWinHandle);

            capConfChoice.setCapability("appTopLevelWindow", ICTWinHandle);
            driver.close();
            driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), capConfChoice);
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            expandApp();
        } else {
            LOGGER.error("Program " + windowName + " not found");
        }

    }

    public static void choiceWindowsRoot() throws MalformedURLException {

        driver = new WindowsDriver<WindowsElement>(new URL(urlWinApp), windowsCap);

    }

    private static void expandApp() {
        try {
            driver.findElementByName("Развернуть").click();
        } catch (NoSuchElementException ignored) {}
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
}
