package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
        
        // Check if file exists, create if it doesn't
        File file = new File(filePath);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            file.createNewFile();
            return data; // Return empty data for a new file
        }
        
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
        // Create directory if it doesn't exist
        File file = new File(filePath);
        File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                // Handle null values in the row
                String[] sanitizedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    sanitizedRow[i] = row[i] != null ? row[i] : "";
                }
                
                bw.write(String.join(",", sanitizedRow));
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
                // Initialize with empty list if file can't be read
                allData.put(fileType, new ArrayList<>());
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