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
import java.util.function.UnaryOperator;
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
        Stream<String> startLogStream;
        Stream<String> endLogStream;
        AtomicInteger itemNumber = new AtomicInteger();
        Stream<String> abbrStream = getStreamFromFile(Application.LOG_FILE_PATH + Application.ABBREVIATIONS);
        List<String> abbrList = abbrStream.collect(Collectors.toList());
        abbrStream.close();
        
        UnaryOperator<String> logAbbreviation =  e -> e.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY);
        UnaryOperator<String> logData =  e -> e.substring(e.indexOf(INITIAL_SEPARATOR) + 1, e.length());
        UnaryOperator<String> getAlignedNames = e -> getNamesProlongedBySpaces(e, abbrList);

        //make a map of abbreviations to use later for finalList. all map items are prolonged by spaces  
        Map<String, String> abbreviationsMap = abbrList.stream()
            .map(getAlignedNames)
            .collect(Collectors.toMap(logAbbreviation, logData));
        //System.out.println(abbreviationsMap);
        
        endLogStream = getStreamFromFile(Application.LOG_FILE_PATH + Application.END_LOG);
        Map<String, String> endLogMap = endLogStream
                .collect(Collectors.toMap(logAbbreviation, logData));
        endLogStream.close();
        System.out.println(endLogMap);
        
        UnaryOperator<String> calculateLapTime = e -> calculateTheLapTime(e, endLogMap);
        UnaryOperator<String> cutTheLapTime = e -> e.substring(e.lastIndexOf(SPACE_CHAR), e.length());
        UnaryOperator<String> replaceAbbreviationsWithFullNames = e -> e.replace(e.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY)
                , abbreviationsMap.get(e.substring(STRING_BEGINNING, ABBREVIATIONS_CHARS_QUANTITY)));
        UnaryOperator<String> addNumeration = e -> addNumeration(e, itemNumber);
        UnaryOperator<String> replaceSeparator = e -> e.replace(INITIAL_SEPARATOR, NEW_SEPARATOR);
        
        startLogStream = getStreamFromFile(Application.LOG_FILE_PATH + Application.START_LOG);
        startLogStream
                .map(calculateLapTime)
                .sorted(Comparator.comparing(cutTheLapTime))
                .map(replaceAbbreviationsWithFullNames)
                .map(addNumeration)
                .map(replaceSeparator)
                .reduce((e1, e2) -> e1 + "\n" + e2)
                .ifPresent(System.out::println);
        startLogStream.close();
    }
    
    public Stream<String> getStreamFromFile(String path) throws IOException {
        return Files.lines(Paths.get(path));
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
        UnaryOperator<String> cutTheName = e -> e.substring(e.indexOf(INITIAL_SEPARATOR)+1, e.lastIndexOf(INITIAL_SEPARATOR));
        return initialList.stream()
            .map(cutTheName) 
            .mapToInt(String::length)
            .max().orElse(0);
    }
    
    public int getTheLongestTeamNameLength(List<String> initialList) {
        UnaryOperator<String> cutTheTeamName = e -> e.substring(e.lastIndexOf(INITIAL_SEPARATOR)+1 , e.length());
        return initialList.stream()
            .map(cutTheTeamName)
            .mapToInt(String::length)
            .max()
            .orElse(0);
    }
}
