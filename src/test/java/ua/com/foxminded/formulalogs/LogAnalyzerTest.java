package ua.com.foxminded.formulalogs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class LogAnalyzerTest {

    LogsAnalyzer logsAnalyzer = new LogsAnalyzer();
    AtomicInteger number = new AtomicInteger();

    @Test
    void getTheLongestTeamNameLength_shouldReturnTheLongestNameLength_whenInputIsStringList() {
        int expected = 16;// Sebastian Vettel
        List<String> initialList = new ArrayList<String>();
        initialList.add("DRR_Daniel Ricciardo_RED BULL RACING TAG HEUER");
        initialList.add("SVF_Sebastian Vettel_FERRARI");
        initialList.add("LHM_Lewis Hamilton_MERCEDES");
        initialList.add("KRF_Kimi Raikkonen_FERRARI");
        initialList.add("VBM_Valtteri Bottas_MERCEDES");
        int actual = logsAnalyzer.getTheLongestNameLength(initialList);
        assertEquals(expected, actual);
    }

    @Test
    void getTheLongestTeamNameLength_shouldReturnTheLongestTeamNameLength_whenInputIsStringList() {
        int expected = 25;// RED BULL RACING TAG HEUER
        List<String> initialList = new ArrayList<String>();
        initialList.add("DRR_Daniel Ricciardo_RED BULL RACING TAG HEUER");
        initialList.add("SVF_Sebastian Vettel_FERRARI");
        initialList.add("LHM_Lewis Hamilton_MERCEDES");
        initialList.add("KRF_Kimi Raikkonen_FERRARI");
        initialList.add("VBM_Valtteri Bottas_MERCEDES");
        int actual = logsAnalyzer.getTheLongestTeamNameLength(initialList);
        assertEquals(expected, actual);
    }

    @Test
    void getNamesProlongedBySpaces_shouldReturnNamesProlongedBySpaces_whenInputIsStringAndStringList() {
        String expected = "SVF_Sebastian Vettel_FERRARI                  ";
        String initialString = "SVF_Sebastian Vettel_FERRARI";
        List<String> initialList = new ArrayList<String>();
        initialList.add("DRR_Daniel Ricciardo_RED BULL RACING TAG HEUER");
        initialList.add("SVF_Sebastian Vettel_FERRARI");
        initialList.add("LHM_Lewis Hamilton_MERCEDES");
        initialList.add("KRF_Kimi Raikkonen_FERRARI");
        initialList.add("VBM_Valtteri Bottas_MERCEDES");
        String actual = logsAnalyzer.getNamesProlongedBySpaces(initialString, initialList);
        assertEquals(expected, actual);
    }

    @Test
    void getNamesProlongedBySpaces_2ndTest_shouldReturnNamesProlongedBySpaces_whenInputIsStringAndStringList() {
        String expected = "SPF_Sergio Perez    _FORCE INDIA MERCEDES     ";
        String initialString = "SPF_Sergio Perez_FORCE INDIA MERCEDES";
        List<String> initialList = new ArrayList<String>();
        initialList.add("DRR_Daniel Ricciardo_RED BULL RACING TAG HEUER");
        initialList.add("SVF_Sebastian Vettel_FERRARI");
        initialList.add("LHM_Lewis Hamilton_MERCEDES");
        initialList.add("KRF_Kimi Raikkonen_FERRARI");
        initialList.add("VBM_Valtteri Bottas_MERCEDES");
        initialList.add("SPF_Sergio Perez_FORCE INDIA MERCEDES");
        String actual = logsAnalyzer.getNamesProlongedBySpaces(initialString, initialList);
        assertEquals(expected, actual);
    }

    @Test
    void calculateTheLapTime_shouldReturnTheLapTime_whenInputAreStartLogStringAndEndLogMap() {
        String expected = "SVF | 1:04.415";
        String startLog = "SVF2018-05-24_12:02:58.917";
        Map<String, String> endLog = new HashMap<String, String>() {
            {
                put("VBM", "12:01:12.434");
                put("SVF", "12:04:03.332");
                put("CSR", "12:04:28.095");
                put("KMH", "12:04:04.396");
                put("DRR", "12:15:24.067");
            }
        };
        String actual = logsAnalyzer.calculateTheLapTime(startLog, endLog);
        assertEquals(expected, actual);
    }

    @Test
    void calculateTheLapTime_2ndTest_shouldReturnTheLapTime_whenInputAreStartLogStringAndEndLogMap() {
        String expected = "KMH | 1:13.393";
        String startLog = "KMH2018-05-24_12:02:51.003";
        Map<String, String> endLog = new HashMap<String, String>() {
            {
                put("VBM", "12:01:12.434");
                put("SVF", "12:04:03.332");
                put("CSR", "12:04:28.095");
                put("KMH", "12:04:04.396");
                put("DRR", "12:15:24.067");
            }
        };
        String actual = logsAnalyzer.calculateTheLapTime(startLog, endLog);
        assertEquals(expected, actual);
    }

    void addNumeration_1LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 1. The 1st line";
        String initialString = "The 1st line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_2LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 2. The 2nd line";
        String initialString = "The 2nd line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_3LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 3. The 3rd line";
        String initialString = "The 3rd line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_4LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 4. The 4th line";
        String initialString = "The 4th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_5LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 5. The 5th line";
        String initialString = "The 5th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_6LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 6. The 6th line";
        String initialString = "The 6th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_7LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 7. The 7th line";
        String initialString = "The 7th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_8LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 8. The 8th line";
        String initialString = "The 8th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_9LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = " 9. The 9th line";
        String initialString = "The 9th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_10LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = "10. The 10th line";
        String initialString = "The 10th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    void addNumeration_11LineTest_shouldReturnNumberedString_whenInputIsString() {
        String expected = "11. The 11th line";
        String initialString = "The 11th line";
        String actual = logsAnalyzer.addNumeration(initialString, number);
        assertEquals(expected, actual);
    }

    @Test
    void test_addNumerationWithAtomicIntegerAutoIncrementing() {
        addNumeration_1LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_2LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_3LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_4LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_5LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_6LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_7LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_8LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_9LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_10LineTest_shouldReturnNumberedString_whenInputIsString();
        addNumeration_11LineTest_shouldReturnNumberedString_whenInputIsString();
    }

}
