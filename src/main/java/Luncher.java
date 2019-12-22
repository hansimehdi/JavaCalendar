import gui.MainFrame;

import javax.swing.*;
import java.util.Locale;

public class Luncher{
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        final MainFrame mainFrame = new MainFrame();
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
    }
}
