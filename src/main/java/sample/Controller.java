package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.openqa.selenium.WebDriver;


import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class Controller {

    @FXML
    BorderPane rootPane;

    @FXML
    TextArea favoriteURlsTA;

    @FXML
    ListView favoriteURLLV;
    @FXML
    TextField chromePathTF, firefoxPathTF, startURLTF;

    @FXML
    CheckBox startNewBrowserCB;

    @FXML
    RadioButton chromeRadio, firefoxRadio;


    private ArrayList<WebDriver> allFireFoxDrivers = new ArrayList<WebDriver>();
    private ArrayList<WebDriver> allChromeDrivers = new ArrayList<WebDriver>();
    @FXML
    public void initialize()
    {
        ToggleGroup radioToggleGroup = new ToggleGroup();
        firefoxRadio.setToggleGroup(radioToggleGroup);
        chromeRadio.setToggleGroup(radioToggleGroup);
        firefoxRadio.setSelected(true);
        startNewBrowserCB.setSelected(true);

        setChromePathTF();
        setFirefoxPathTF();
        populateFavoriteURLs();

        populateFavoriteURLsLV();
        handleClickOnFavoriteURLsClick();
    }

    public void startBrowsing()
    {
        String startURL = startURLTF.getText();

        if (startURL.trim().equals(""))
            startURL = "https://www.binarycarpenter.com";




        if (chromeRadio.isSelected())
        {
            NFCBrowser nfcBrowser = new NFCBrowser(startURL, "chrome");
            allChromeDrivers.add(nfcBrowser.getWebDriver());
            nfcBrowser.visit();


        } else
        {
            NFCBrowser nfcBrowser = new NFCBrowser(startURL, "firefox");
            allFireFoxDrivers.add(nfcBrowser.getWebDriver());
            nfcBrowser.visit();
        }
    }

    public void visitBinaryCarpenter()
    {
        try {
            Desktop.getDesktop().browse(new URI("https://www.binarycarpenter.com?src=nfcb_app"));
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void selectChromePath()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Chrome driver file");
        File chromeDriver = chooser.showOpenDialog(rootPane.getScene().getWindow());

        if (chromeDriver!=null)
        {
            new UserSettings().setChromeDriverPath(chromeDriver.getPath());
            setChromePathTF();
        }
    }

    private void setChromePathTF()
    {
        String chromePath = new UserSettings().getChromeDriverPath();
        if (chromePathTF!=null )
            chromePathTF.setText(chromePath);
    }

    public void killAllBrowsers()
    {
        for (WebDriver webDriver : allFireFoxDrivers)
            if (webDriver!=null)
            {
                try
                {
                    webDriver.close();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }

        for (WebDriver webDriver : allChromeDrivers)
            if (webDriver!=null)
            {
                try
                {
                    webDriver.close();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }

        NFCAlert.info("All instances killed!");
    }

    private void setFirefoxPathTF()
    {
        String firefoxPath = new UserSettings().getFirefoxDriverPath();
        if (firefoxPath!=null )
            firefoxPathTF.setText(firefoxPath);
    }


    public void selectFirefoxPath()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Firefox driver file");
        File firefoxDriver = chooser.showOpenDialog(rootPane.getScene().getWindow());

        if (firefoxDriver!=null)
        {
            new UserSettings().setFirefoxDriverPath(firefoxDriver.getPath());
            setFirefoxPathTF();
        }
    }

    public void saveFavoriteURLs()
    {
        new UserSettings().setFavoriteUrls(favoriteURlsTA.getText());
        populateFavoriteURLsLV();
    }

    private void populateFavoriteURLsLV()
    {
        favoriteURLLV.getItems().clear();
        ObservableList<String> urls = FXCollections.observableArrayList();
        urls.addAll(new UserSettings().getFavoriteUrls().trim().split("\n"));
        favoriteURLLV.setItems(urls);
    }

    private void populateFavoriteURLs()
    {
        favoriteURlsTA.setText(new UserSettings().getFavoriteUrls());
    }

    private void handleClickOnFavoriteURLsClick()
    {
        favoriteURLLV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String url = favoriteURLLV.getSelectionModel().getSelectedItem().toString();
                System.out.println("on clicked on item");
                startURLTF.setText(url);
                if (event.getClickCount() > 1)
                {
                    startBrowsing();
                }

            }
        });
    }

}
