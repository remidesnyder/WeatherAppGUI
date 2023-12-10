import javax.swing.*;

/**
 * Created by Desnyder Rémi
 * Date: 09/12/2023
 */

public class AppLauncher {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherAppGUI().setVisible(true);

                //System.out.println(WeatherApp.getWeatherData("Paris"));
            }
        });
    }

}
