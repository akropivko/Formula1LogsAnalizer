package ua.com.foxminded.formulalogs;

import java.io.IOException;

public class Application {

    static final String LOG_FILE_PATH = "c:/workspace/FormulaLogs/logfiles/";
    static final String ABBREVIATIONS = "abbreviations.txt";
    static final String START_LOG = "start.log";
    static final String END_LOG = "end.log";

    public static void main(String[] args) {

        try {
            new LogsAnalyzer().doLogAnalyzingRoutine();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());

        }
    }

}
