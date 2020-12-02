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
    final String INITIAL_SEPARATOR = "_";
    final String NEW_SEPARATOR = " | ";
    final String TOPLIST_SEPARATOR = "-";
    final String SPACE_CHAR = " ";
    final int ABBREVIATIONS_CHARS_QUANTITY = 3;
    final int TOP_LIST_QTY = 15;
    final int TIMESTAMP_LENGTH = 8;// for instance, 1:13.393
    
    public String getNameProlongedBySpaces(String initialAbbreviationString, int longestLength) {
        String oldName = initialAbbreviationString.substring(ABBREVIATIONS_CHARS_QUANTITY + 1,
                initialAbbreviationString.indexOf(INITIAL_SEPARATOR, ABBREVIATIONS_CHARS_QUANTITY + 2));
        String nameWithSpaces = oldName + Stream.generate(() -> SPACE_CHAR)
        .limit(longestLength - oldName.length())
        .collect(Collectors.joining());
        return initialAbbreviationString.replace(oldName, nameWithSpaces);
    }
    
    public String getTeamNameProlongedBySpaces(String initialAbbrString, int longestTeamNameLength, int longestNameLength) {
        String oldTeamName = initialAbbrString.substring(initialAbbrString.indexOf(INITIAL_SEPARATOR, longestNameLength) + 1, initialAbbrString.length());
        String teamNameWithSpaces = oldTeamName + Stream.generate(() -> SPACE_CHAR)
                .limit(longestTeamNameLength - oldTeamName.length())
                .collect(Collectors.joining());
        return initialAbbrString.replace(oldTeamName, teamNameWithSpaces);
    }
    
    public void doLogAnalyzingRoutine() throws IOException {
    /*    final String INITIAL_SEPARATOR = "_";
        final String NEW_SEPARATOR = " | ";
        final String TOPLIST_SEPARATOR = "-";
        final String SPACE_CHAR = " ";
        final int ABBREVIATIONS_CHARS_QUANTITY = 3;
        final int TOP_LIST_QTY = 15;
        final int TIMESTAMP_LENGTH = 8;// for instance, 1:13.393
       */ 

        Stream<String> startLogStream      = Files.lines(Paths.get(Application.LOG_FILE_PATH + Application.START_LOG));
        Stream<String> endLogStream        = Files.lines(Paths.get(Application.LOG_FILE_PATH + Application.END_LOG));
        Stream<String> abbreviationsStream = Files.lines(Paths.get(Application.LOG_FILE_PATH + Application.ABBREVIATIONS));
        
        Map<String, String> endLogMap;
        List<String> abbreviationList = abbreviationsStream.collect(Collectors.toList());
        List<String> finalList;
        AtomicInteger finalListItemNumber = new AtomicInteger();
        
        abbreviationsStream.close(); 

        // find the longest name and the longest team's name
        int longestNameLength = abbreviationList.stream()
                .map(e -> e.substring(ABBREVIATIONS_CHARS_QUANTITY + 1, e.length()))
                .map(e -> e.substring(0, e.indexOf(INITIAL_SEPARATOR)))
                .mapToInt(String::length)
                .max().orElse(0);
        int longestTeamNameLength = abbreviationList.stream()
                .map(e -> e.substring(ABBREVIATIONS_CHARS_QUANTITY + 1, e.length()))
                .map(e -> e.substring(e.indexOf(INITIAL_SEPARATOR) + 1, e.length()))
                .mapToInt(String::length)
                .max().orElse(0);
        
        
        
        //make a map of abbreviations to use later for finalList. all map items are prolonged by spaces  
        Map<String, String> abbreviationsMap = abbreviationList.stream()
                /*.map(e -> {
                    String oldName = e.substring(ABBREVIATIONS_CHARS_QUANTITY + 1,
                    e.indexOf(INITIAL_SEPARATOR, ABBREVIATIONS_CHARS_QUANTITY + 2));
                    String nameWithSpaces = oldName + Stream.generate(() -> SPACE_CHAR)
                    .limit(longestNameLength - oldName.length())
                    .collect(Collectors.joining());
                    return e.replace(oldName, nameWithSpaces);
                    })*/
                .map(e -> getNameProlongedBySpaces(e, longestNameLength))
                /*
                .map(e -> {
                        String oldTeamName = e.substring(e.indexOf(INITIAL_SEPARATOR, longestNameLength) + 1, e.length());
                        String teamNameWithSpaces = oldTeamName + Stream.generate(() -> SPACE_CHAR)
                                .limit(longestTeamNameLength - oldTeamName.length())
                                .collect(Collectors.joining());
                        return e.replace(oldTeamName, teamNameWithSpaces);
                    })
                 */   
                .map(e -> getTeamNameProlongedBySpaces(e, longestTeamNameLength, longestNameLength))
                .collect(Collectors.toMap(e -> e.substring(0, ABBREVIATIONS_CHARS_QUANTITY),
                        e -> e.substring(ABBREVIATIONS_CHARS_QUANTITY + 1, e.length()).replace("_", NEW_SEPARATOR)));
        System.out.println(abbreviationsMap.toString());

        endLogMap = endLogStream.collect(Collectors.toMap(e -> e.substring(0, ABBREVIATIONS_CHARS_QUANTITY),
                e -> e.substring(e.indexOf(INITIAL_SEPARATOR) + 1, e.length())));
        endLogStream.close();
        
        finalList = startLogStream.map(e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            LocalTime startTime = LocalTime.parse(e.substring(e.indexOf(INITIAL_SEPARATOR) + 1, e.length()), formatter);
            LocalTime endTime = LocalTime.parse(endLogMap.get(e.substring(0, ABBREVIATIONS_CHARS_QUANTITY)), formatter);
            long minutesBetween = ChronoUnit.MINUTES.between(startTime, endTime);
            long secsBetween = ChronoUnit.SECONDS.between(startTime, endTime);
            long millisBetween = ChronoUnit.MILLIS.between(startTime, endTime);
            return e.substring(0, ABBREVIATIONS_CHARS_QUANTITY) + NEW_SEPARATOR
                    + String.format("%d:%02d.%03d", minutesBetween % 60, secsBetween % 60, millisBetween % 1000);

        }).sorted(Comparator.comparing(e -> e.substring(e.length() - TIMESTAMP_LENGTH, e.length())))
                .map(e -> abbreviationsMap.get(e.substring(0, ABBREVIATIONS_CHARS_QUANTITY))
                        + e.substring(ABBREVIATIONS_CHARS_QUANTITY, e.length()))
                .map(e -> {
                    int itemNumber = finalListItemNumber.incrementAndGet();
                    String newItem = (itemNumber < 10) ? (SPACE_CHAR + itemNumber + "." + SPACE_CHAR + e)
                            : (itemNumber + "." + SPACE_CHAR + e);
                    if (itemNumber == TOP_LIST_QTY) {
                        newItem += "\n" + Stream.generate(() -> TOPLIST_SEPARATOR).limit(newItem.length())
                                .collect(Collectors.joining());
                    }
                    return newItem;
                }).collect(Collectors.toList());
        startLogStream.close();
        finalList.forEach(System.out::println);

    }
    
    
}

