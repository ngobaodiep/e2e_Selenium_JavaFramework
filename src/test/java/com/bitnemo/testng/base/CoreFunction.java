package com.bitnemo.testng.base;

import com.bitnemo.testng.pages.LoginPage;
import com.bitnemo.testng.pages.MainPage;
import com.bitnemo.testng.utils.HttpUtils;
import com.bitnemo.testng.utils.SessionManager;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.json.JSONArray;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.Cookie;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

// Singleton Class
public class CoreFunction {
    private static CoreFunction instance;
    private CoreFunction() {}
    public static CoreFunction getInstance() {
        if (instance == null) {
            synchronized(CoreFunction.class) {
                if(null == instance) {
                    instance  = new CoreFunction();
                }
            }
        }
        return instance;
    }

    GraphicsConfiguration gc = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();
    ScreenRecorder screenRecorder;

    {
        try {
            screenRecorder = new ScreenRecorder(gc,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, (int)24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, (int) (15 * 60)),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,"black",
                            FrameRateKey, Rational.valueOf(30)),
                    null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    LoginPage loginPage = LoginPage.getInstance();
    MainPage mainPage = MainPage.getInstance();
    public void login() {
        String path = System.getProperty("user.dir");
        Configuration.browserSize = "1280x800";
        SelenideLogger.addListener("allure", new AllureSelenide());
        //open(getUrl());
        open("https://int.logifleet360.ch/app/resources/img/logifleet/2021-favicon.png");
        SessionManager sessionManager = new SessionManager(WebDriverRunner.getWebDriver());

        sessionManager.usePreviousLoggedInSession("linhht@bitnemo.vn", getTrackingUrl());
        System.out.println("Use old session");
        WebDriverRunner.getWebDriver().navigate().to(getTrackingUrl());

    }

    public void globalSetup(){
        System.out.println("Before Suite Run it");
        HttpUtils util = new HttpUtils();
        JSONArray arrayCookies = util.getCookiesFromHttp(getUrl()+"/lfr3/eg-services/login",null);
        SessionManager sessionManager = new SessionManager(null);
        try {
            //TODO add all json files into the session folder
            //TODO load accounts from json file
            //TODO check if a file is new so skip login via API
            sessionManager.saveCookiesToAFile("linhht@bitnemo.vn", "linhht@bitnemo.vn",arrayCookies);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveSessionID() {
        for (Cookie s : WebDriverRunner.getWebDriver().manage().getCookies()) {
            System.out.println(s.getName());
            if (s.getName().equals("SESSION")) return true;
        }
        return false;
    }

    private String getUrl() {
        if (System.getenv().get("URL") == null) return "https://int.logifleet360.ch";
        switch (System.getenv().get("URL")) {
            case "INT": {
                return "https://int.logifleet360.ch/";
            }
            case "PTA": {
                return "https://pta.logifleet360.ch/";
            }
            case "PRD": {
                return "https://logifleet360.ch/";
            }
            default: {
                return "https://int.logifleet360.ch/";
            }
        }
    }

    private String getTrackingUrl() {
        if (System.getenv().get("URL") == null) return "https://int.logifleet360.ch/lfr3/#/tracking";
        switch (System.getenv().get("URL")) {
            case "INT": {
                return "https://int.logifleet360.ch/lfr3/#/tracking";
            }
            case "PTA": {
                return "https://pta.logifleet360.ch/lfr3/#/tracking";
            }
            case "PRD": {
                return "https://logifleet360.ch/lfr3/#/tracking";
            }
            default: {
                return "https://int.logifleet360.ch/lfr3/#/tracking";
            }
        }
    }

    public void startRecord() {
        try {
            screenRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        try {
            screenRecorder.stop();
            List<File> createdMovieFiles = screenRecorder.getCreatedMovieFiles();
            for(File movie : createdMovieFiles){
                System.out.println("New movie created: " + movie.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
