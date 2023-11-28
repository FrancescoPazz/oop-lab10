package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.xml.stream.util.StreamReaderDelegate;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static int MIN;
    private static int MAX;
    private static int ATTEMPTS;
    private static final int CONFIG_PARAM = 3;
    private static final int INDEX_VALUE_CONFIG = 1;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        final String PATH = System.getProperty("user.dir") +  "\\src\\main\\resources\\config.yml";
        List<String> parameters = new LinkedList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(PATH)))) {
            String line = "";
            while ( (line = br.readLine()) != null){
                parameters.add(line.split(" ")[INDEX_VALUE_CONFIG]);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        this.model = new DrawNumberImpl(Integer.parseInt(parameters.get(0)), Integer.parseInt(parameters.get(1)), Integer.parseInt(parameters.get(2)));
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl());
    }

}
