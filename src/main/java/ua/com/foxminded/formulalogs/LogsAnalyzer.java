package ua.com.foxminded.formulalogs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogsAnalyzer {
    public static final String INITIAL_SEPARATOR = "_";
    public static final String NEW_SEPARATOR = " | ";
    public static final String TOPLIST_SEPARATOR = "-";
    public static final String TIME_PATTERN = "HH:mm:ss.SSS";
    public static final String SPACE_CHAR = " ";
    public static final int STRING_BEGINNING = 0;
    public static final int ABBREVIATIONS_CHARS_QUANTITY = 3;
    public static final int TOP_LIST_QTY = 15;
    public static final int TIMESTAMP_LENGTH = 8;// for instance, 1:13.393
    
    
    public void doLogAnalyzingRoutine() throws IOException {
        Stream<String> startLogStream = Files.lines(Paths.get(Application.LOG_FILE_PATH + Application.START_LOG));
        Stream<String> endLogStream   = Files.lines(Paths.get(Application.LOG_FILE_PATH + Application.END_LOG));
        List<String> abbrListFromFile = Files.lines(Paths.get(Application.LOG_FILE_PATH, Application.ABBREVIATIONS))
                .collect(Collectors.toList());
        
        //make a map of abbreviations to use later for finalList. all map items are prolonged by spaces  
        Map<String, String> abbreviationsMap = abbrListFromFile.stream()
            .map(e -> getNamesProlongedBySpaces(e, abbrListFromFile))
            .collect(Collectors.toMap(e -> e.substring(STRING_BEGINNING, e.indexOf(INITIAL_SEPARATOR)),
                        e -> e.substring(e.indexOf(INITIAL_SEPARATOR) + 1, e.length())
                            .replace("_", NEW_SEPARATOR)));
        //System.out.println(abbreviationsMap.toString());

        Map<String, String> endLogMap = endLogStream
                .collect(Collectors.toMap(e -> e.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY),
                e -> e.substring(e.indexOf(INITIAL_SEPARATOR) + 1, e.length())));
        endLogStream.close();
        
        AtomicInteger itemNumber = new AtomicInteger();
        
        List<String> finalList = startLogStream
                .map(e -> calculateTheLapTime(e, endLogMap))
                .sorted(Comparator.comparing(e -> e.substring(e.lastIndexOf(SPACE_CHAR), e.length())))
                .map(e -> abbreviationsMap.get(e.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY))
                        + e.substring(ABBREVIATIONS_CHARS_QUANTITY, e.length()))
                .map(e -> addNumeration(e, itemNumber))
                .collect(Collectors.toList());
        startLogStream.close();
        finalList.forEach(System.out::println);

    }
    
    public String addNumeration(String stringToNumerate, AtomicInteger number) {
        int itemNumber = number.incrementAndGet();
        String newItem = (itemNumber < 10) 
                ? (SPACE_CHAR + itemNumber + "." + SPACE_CHAR + stringToNumerate)
                : (itemNumber + "." + SPACE_CHAR + stringToNumerate);
        if (itemNumber == TOP_LIST_QTY) {
            newItem += "\n" + Stream.generate(() -> TOPLIST_SEPARATOR).limit(newItem.length())
                .collect(Collectors.joining());
        }
        return newItem;
    }
    
    public String calculateTheLapTime(String startLog, Map<String, String> endLog) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        LocalTime startTime = LocalTime.parse(startLog.substring(startLog.indexOf(INITIAL_SEPARATOR) + 1, startLog.length()), formatter);
        LocalTime endTime = LocalTime.parse(endLog.get(startLog.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY)), formatter);
        long minutesBetween = ChronoUnit.MINUTES.between(startTime, endTime);
        long secsBetween    = ChronoUnit.SECONDS.between(startTime, endTime);
        long millisBetween  = ChronoUnit.MILLIS.between(startTime, endTime);
        return startLog.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY) 
                + NEW_SEPARATOR
                + String.format("%d:%02d.%03d", minutesBetween % 60, secsBetween % 60, millisBetween % 1000);
    }
    
    public String getNamesProlongedBySpaces(String initialString, List<String> abbrList){
        String name = initialString.substring(initialString.indexOf(INITIAL_SEPARATOR)+1
                , initialString.lastIndexOf(INITIAL_SEPARATOR));
        String teamName = initialString.substring(initialString.lastIndexOf(INITIAL_SEPARATOR)+1 
                , initialString.length());
        String nameWithSpaces = name + Stream.generate(() -> SPACE_CHAR)
            .limit(getTheLongestNameLength(abbrList) - name.length())
            .collect(Collectors.joining());
        String teamNameWithSpaces = teamName + Stream.generate(() -> SPACE_CHAR)
                .limit(getTheLongestTeamNameLength(abbrList) - teamName.length())
                .collect(Collectors.joining());
        return initialString.replace(name, nameWithSpaces).replace(teamName, teamNameWithSpaces);
    }

    public int getTheLongestNameLength(List<String> initialList){
        return initialList.stream()
                .map(e -> e.substring(e.indexOf(INITIAL_SEPARATOR)+1, e.lastIndexOf(INITIAL_SEPARATOR))) 
                .mapToInt(String::length)
                .max().orElse(0);
    }
    
    public int getTheLongestTeamNameLength(List<String> initialList) {
        return initialList.stream()
                .map(e -> e.substring(e.lastIndexOf(INITIAL_SEPARATOR)+1 , e.length()))
                .mapToInt(String::length)
                .max().orElse(0);
    }
}

