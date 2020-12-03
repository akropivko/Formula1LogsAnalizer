package ua.com.foxminded.formulalogs;

import java.io.IOException;

public class Application {

    public static final String LOG_FILE_PATH = "c:/workspace/FormulaLogs/logfiles/";
    public static final String ABBREVIATIONS = "abbreviations.txt";
    public static final String START_LOG = "start.log";
    public static final String END_LOG = "end.log";

    public static void main(String[] args) {

        try {
            new LogsAnalyzer().doLogAnalyzingRoutine();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());

        }
    }

}
