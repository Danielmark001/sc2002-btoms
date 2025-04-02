package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvDataService {
    /**
     * Reads data from a CSV file.
     * 
     * @param filePath Path to the CSV file
     * @return List of string arrays, where each array represents a row in the CSV
     * @throws IOException If there's an error reading the file
     */
    public List<String[]> readData(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;
                
                // Split the line by comma, handling potential CSV complexities
                String[] values = splitCsvLine(line);
                data.add(values);
            }
        }
        
        return data;
    }

    /**
     * Writes data to a CSV file.
     * 
     * @param filePath Path to the CSV file
     * @param data List of string arrays to write
     * @throws IOException If there's an error writing to the file
     */
    public void writeData(String filePath, List<String[]> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }

    /**
     * Sophisticated CSV line splitting method to handle complex CSV formatting.
     * 
     * @param line Raw CSV line to split
     * @return Array of values in the CSV line
     */
    private String[] splitCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add the last value
        values.add(currentValue.toString().trim());

        return values.toArray(new String[0]);
    }

    /**
     * Converts a map of data to CSV format for writing.
     * 
     * @param dataMap Map of data to convert
     * @return List of string arrays representing CSV rows
     */
    public List<String[]> convertMapToCSV(Map<String, List<String[]>> dataMap) {
        List<String[]> csvData = new ArrayList<>();
        
        // Add headers and data for each entity type
        for (Map.Entry<String, List<String[]>> entry : dataMap.entrySet()) {
            csvData.addAll(entry.getValue());
        }
        
        return csvData;
    }

    /**
     * Reads all CSV files specified in the file paths.
     * 
     * @param filePaths Map of file types to file paths
     * @return Map of file types to their read data
     * @throws IOException If there's an error reading files
     */
    public Map<String, List<String[]>> readAllData(Map<String, String> filePaths) throws IOException {
        Map<String, List<String[]>> allData = new HashMap<>();
        
        for (Map.Entry<String, String> entry : filePaths.entrySet()) {
            String fileType = entry.getKey();
            String filePath = entry.getValue();
            
            try {
                List<String[]> fileData = readData(filePath);
                allData.put(fileType, fileData);
            } catch (IOException e) {
                System.err.println("Error reading " + fileType + " file: " + e.getMessage());
                throw e;
            }
        }
        
        return allData;
    }

    /**
     * Writes all data to their respective CSV files.
     * 
     * @param filePaths Map of file types to file paths
     * @param allData Map of file types to their data
     * @throws IOException If there's an error writing files
     */
    public void writeAllData(Map<String, String> filePaths, Map<String, List<String[]>> allData) throws IOException {
        for (Map.Entry<String, String> entry : filePaths.entrySet()) {
            String fileType = entry.getKey();
            String filePath = entry.getValue();
            
            List<String[]> fileData = allData.get(fileType);
            if (fileData != null) {
                try {
                    writeData(filePath, fileData);
                } catch (IOException e) {
                    System.err.println("Error writing " + fileType + " file: " + e.getMessage());
                    throw e;
                }
            }
        }
    }
}