import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Desnyder Rémi
 * Date: 09/12/2023
 */

public class WeatherAppGUI extends JFrame {

    private JSONObject weatherData;

    WeatherAppGUI() {
        super("Weather App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 650);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        //this.setVisible(true);

        addGuiComponents();

    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 350, 45);
        searchTextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));

        add(searchTextField);

        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 215);

        add(weatherConditionImage);

        JLabel temperatureLabel = new JLabel("0°C");
        temperatureLabel.setBounds(0, 350, 450, 50);
        temperatureLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 48));

        temperatureLabel.setHorizontalAlignment(JLabel.CENTER);

        add(temperatureLabel);

        JLabel weatherConditionLabel = new JLabel("Cloudy");
        weatherConditionLabel.setBounds(0, 405, 450, 50);
        weatherConditionLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 32));
        weatherConditionLabel.setHorizontalAlignment(JLabel.CENTER);

        add(weatherConditionLabel);

        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 75, 70);

        add(humidityImage);

        JLabel humidityLabel = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityLabel.setBounds(100, 500, 85, 70);
        humidityLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        add(humidityLabel);

        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 75, 70);

        add(windspeedImage);

        JLabel windspeedLabel = new JLabel("<html><b>Windspeed</b> 100km/h</html>");
        windspeedLabel.setBounds(305, 500, 85, 70);
        windspeedLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        add(windspeedLabel);

        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 15, 45, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                weatherData = WeatherApp.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weatherCondition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureLabel.setText(temperature + "°C");

                weatherConditionLabel.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityLabel.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                double windSpeed = (double) weatherData.get("windSpeed");
                windspeedLabel.setText("<html><b>Windspeed</b> " + windSpeed + "km/h</html>");

            }
        });

        add(searchButton);

    }

    private ImageIcon loadImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Error loading image: " + path);

        return null;
    }

}
