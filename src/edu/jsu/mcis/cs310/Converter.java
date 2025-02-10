package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    // Utility method for CSV parsing
    private static CSVReader createCsvReader(String csvString) {
        CSVParser cParse = new CSVParserBuilder().withSeparator(',').build();
        return new CSVReaderBuilder(new StringReader(csvString))
                .withCSVParser(cParse)
                .build();
    }
    
    // Utility method for type conversion
    private static Object convertDataType(String[] columnHeadings, int index, String value) {
        if (columnHeadings[index].equals("Season") || columnHeadings[index].equals("Episode")) {
            return Integer.parseInt(value);
        }
        return value;
    }
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        String result = "{}"; // default return value; replace later!
        
        try {
            // Reading all the CSV data that is given
            CSVReader cReader = createCsvReader(csvString);
            List<String[]> csvData = cReader.readAll();
            // Taking the column headings from the front row
            String[] columnHeadings = csvData.get(0);
            // Starting the necessary JSON structures
            JsonObject json = new JsonObject();
            JsonArray producedNums = new JsonArray();
            JsonArray data1 = new JsonArray();
            // Processing the rows except for the header row
            for (int i = 1; i < csvData.size(); i++) {
                String[] row = csvData.get(i);
                producedNums.add(row[0]);
                // Making a JSON array for current row's data
                JsonArray rowData = new JsonArray();
                for (int j = 1; j < row.length; j++) {
                    rowData.add(convertDataType(columnHeadings, j, row[j]));
                }
                // Adding the row data to the Data array made
                data1.add(rowData);
            }
            // Adding the JSON structures to the main JSON object created
            json.put("ProdNums", producedNums);
            json.put("ColHeadings", columnHeadings);
            json.put("Data", data1);
            // Converting the JSON object to a string value
            result = Jsoner.serialize(json);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
    }
    
    // Utility method for formatting episode number
    private static String formatEpisodeNumber(String episode) {
        return String.format("%02d", Integer.parseInt(episode));
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        String result = ""; // default return value; replace later!
        
        try {
            // Parsing the JSON string so it can go into a JsonObject
            JsonObject json = Jsoner.deserialize(jsonString, new JsonObject());
            // Reading the column heading and Produced Numbers, as well as the Data
            JsonArray columnHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray producedNums = (JsonArray) json.get("ProdNums");
            JsonArray data2 = (JsonArray) json.get("Data");
            // Making a CSV writer
            StringWriter cWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(cWriter);
            // Writing the new header row
            String[] hRow = new String[columnHeadings.size()];
            for (int i = 0; i < columnHeadings.size(); i++) {
                hRow[i] = columnHeadings.getString(i);
            }
            csvWriter.writeNext(hRow);
            // Writing each row that is found
            for (int i = 0; i < data2.size(); i++) {
                JsonArray rData = (JsonArray) data2.get(i);
                String[] Row = new String[columnHeadings.size()];
                Row[0] = producedNums.getString(i);
                // Adding the rest of the data needed
                for (int j = 0; j < rData.size(); j++) {
                    String columnName = columnHeadings.get(j + 1).toString();
                    
                    if (columnName.equals("Episode")) {
                        // Making sure that there is only 2 digits in episode
                        Row[j + 1] = formatEpisodeNumber(rData.get(j).toString());
                    } else if (columnName.equals("Season")) {
                        Row[j + 1] = rData.get(j).toString();
                    } else {
                        Row[j + 1] = rData.get(j).toString();
                    }
                }
                csvWriter.writeNext(Row);
            }

            result = cWriter.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }
}