package CSVTools;


import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.codec.digest.DigestUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Provides methods and tools for working with CSV files.
 */
public class CSV_API {
    private Logger logger;
    private static MessageDigest crypto;

    public CSV_API() {
        this.logger = Logger.getLogger(this.getClass().getName());
        try {
            this.crypto = MessageDigest.getInstance("SHA-1");


        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        crypto.reset();
    }

    /**
     * Return coma seperated list of header names
     *
     * @throws java.io.IOException
     */
    public String getHeadersOfCSV(String inFile) {
        String[] header = null;
        CsvListReader reader = null;
        try {
            reader = new CsvListReader(new FileReader(inFile),
                    CsvPreference.STANDARD_PREFERENCE);


            header = reader.getHeader(false);
        System.out.println("Reader: " + header.length);


        // Read headers from file and remove spaces
            header = this.replaceSpaceWithDash(reader.getHeader(true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String headerNames = "(";
        for (String columnName : header) {
            System.out.println("current header: " + columnName);
            headerNames += columnName.toUpperCase() + " VARCHAR NOT NULL,";
        }

        if (headerNames.lastIndexOf(',') == headerNames.length() - 1) {
            int index = headerNames.lastIndexOf(',');
            if (index != -1) {
                headerNames = headerNames.substring(0, index);
            }
        }

        headerNames += ")";
        System.out.println("HEADER NAMES " + headerNames);
        return headerNames;

    }

    /**
     * Return coma seperated list of header names
     *
     * @throws java.io.IOException
     */
    public String[] getArrayOfHeadersCSV(String inFile) {


        String[] header = null;
        CsvListReader reader;
        try {
            reader = new CsvListReader(new FileReader(inFile),
                    CsvPreference.STANDARD_PREFERENCE);

            header = this.replaceSpaceWithDash(reader.getHeader(true));

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Read headers from file and remove spaces
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.logger.info("There are " + header.length + " headers");
        this.printArray(header);
        return header;

    }

    public void printArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            this.logger.info(array[i]);
        }

    }

    /**
     * Get the headers as List
     * @param inFile
     * @return
     */
    public List<String> getListOfHeadersCSV(String inFile) {


        List<String> headersList = new ArrayList<>();
        String[] headerArray = this.getArrayOfHeadersCSV(inFile);
        for (String columnHeader : headerArray) {
            headersList.add(columnHeader);
            this.logger.info("Added " + columnHeader + " size is now " + headersList.size());
        }
        return headersList;


    }

    /**
     * Remove all spaces from header names by removing leading spaces and
     * replace other spaces with underscore
     *
     * @param headers
     * @return
     */
    private String[] replaceSpaceWithDash(String[] headers) {
        String[] headersWithNoSpaces = headers;
        System.out.println("replace spaces in " + headers.length + " headers ");

        for (int i = 0; i < headers.length; i++) {

            //TODO check the regular expression for table headers
            if (headers[i] != "") {
                // System.out.println("Header: " + headers[i]);
                // String regex = "[-\\w._+%]";

                // headersWithNoSpaces[i] = headers[i].replaceAll(regex, "_");
                headersWithNoSpaces[i] = headers[i].replaceAll("\\s", "_");
                //TODO dummy test
                //headersWithNoSpaces[i] = headers[i];

            }
        }
        return headersWithNoSpaces;

    }

    /**
     * Remove all spaces from header names by removing leading spaces and
     * replace other spaces with underscore
     *
     * @param headers
     * @return
     */
    private List<String> replaceSpaceWithDash(List<String> headers) {
        List<String> headersWithNoSpaces = headers;


        for (int i = 0; i < headers.size(); i++) {
            headersWithNoSpaces.set(i, headers.get(i).replaceAll("^\\s+", "").replace(" ", "_"));
            headersWithNoSpaces.set(i, headers.get(i).replace("(", "_").replace(")", "_"));
            headersWithNoSpaces.set(i, headers.get(i).replace("/", "_").replace("\\", "_"));
        }
        return headersWithNoSpaces;

    }


    /**
     * replace Blanks with dashes
     *
     * @param withSpaceCharacter
     * @return
     */
    public String replaceSpaceWithDash(String withSpaceCharacter) {
        String noSpaceString = withSpaceCharacter.replaceAll("^\\s+", "").replace(" ", "_");
        noSpaceString = noSpaceString.replace("(", "_").replace(")", "_");
        return noSpaceString;


    }

    private static void appendMetadataToCSV(String inFile, String outFile)
            throws Exception {
        CsvListReader reader = new CsvListReader(new FileReader(inFile),
                CsvPreference.TAB_PREFERENCE);
        CsvListWriter writer = new CsvListWriter(new FileWriter(outFile),
                CsvPreference.TAB_PREFERENCE);
        final String[] header = reader.getHeader(true);

        // Create the new header array including sequence number and hash
        String[] newHeader = new String[reader.length() + 2];
        // Prepend the new column header for the sequence number
        newHeader[0] = "SequenceNumber";
        //
        for (int i = 1; i < newHeader.length - 1; i++) {
            newHeader[i] = header[i - 1];
        }

        newHeader[newHeader.length - 1] = "Hash";

        writer.writeHeader(newHeader);
        List<String> columns;
        while ((columns = reader.read()) != null) {
            // Add new columns
            String appendedColumns = convertStringListToAppendedString(columns);
            String hash = calculateSHA1HashFromString(appendedColumns);
            // System.out.println(appendedColumns);
            // System.out.println(hash);
            columns.add(0, Integer.toString(reader.getRowNumber() - 1));

            columns.add(hash);

            writer.write(columns);
        }
        reader.close();
        writer.close();
    }

    private static void createSeperateMetadataFile(String inFile, String outFile)
            throws Exception {
        deleteOutputFile(outFile + "Metadata.csv");

        CsvListReader reader = new CsvListReader(new FileReader(inFile),
                CsvPreference.TAB_PREFERENCE);
        CsvListWriter writer = new CsvListWriter(new FileWriter(outFile
                + "Metadata.csv"), CsvPreference.TAB_PREFERENCE);

        final String[] header = reader.getHeader(true);

        // Create the new header array including sequence number and hash
        String[] newHeader = new String[2];
        // Prepend the new column header for the sequence number
        newHeader[0] = "SequenceNumber";
        newHeader[1] = "Hash";

        writer.writeHeader(newHeader);
        List<String> columns;

        while ((columns = reader.read()) != null) {
            List<String> sequenceAndHashList = new ArrayList();
            // Calculate the hash from one row
            String appendedColumns = convertStringListToAppendedString(columns);
            String hash = calculateSHA1HashFromString(appendedColumns);
            // System.out.println(appendedColumns);
            // System.out.println(hash);
            sequenceAndHashList.add(0,
                    Integer.toString(reader.getRowNumber() - 1));

            sequenceAndHashList.add(hash);

            writer.write(sequenceAndHashList);
        }
        reader.close();
        writer.close();
    }

    /**
     * Append all elements of a list in one string
     *
     * @param columns
     * @return
     */
    public static String convertStringListToAppendedString(List<String> columns) {
        String allColumnsAppended = "";
        for (int i = 0; i < columns.size(); i++) {
            allColumnsAppended += columns.get(i);
            // System.out.println("pos " +i + " val: " +columns.get(i) );

        }

        return allColumnsAppended;
    }

    /**
     * Calculate a SHA1-Hash from a string
     * @param inputString
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String calculateSHA1HashFromString(String inputString)
            throws NoSuchAlgorithmException {

        try {
            crypto.update(inputString.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String hash = DigestUtils.sha1Hex(crypto.digest());
        return hash;

    }

    private static void deleteOutputFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete(); // you might want to check if delete was successfull
        }

    }

    private static void hashFile(String fileName)
            throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis;
        fis = new FileInputStream(fileName);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        ;

        byte[] mdbytes = md.digest();

        // convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }

        System.out.println("Digest(in hex format):: " + sb.toString());

    }

    /**
     * Analyse the CSV file. if it has headers, read them and store the
     * metadata. Then check for the longest entry per column and store the
     * length in the metadata.
     *
     * @param hasHeader
     * @param fileName
     * @return
     * @throws java.io.IOException
     */

    // TODO
    public Column[] analyseColumns(boolean hasHeader, String fileName)
            throws IOException {
        CsvListReader reader = new CsvListReader(new FileReader(fileName),
                CsvPreference.STANDARD_PREFERENCE);
        this.logger.info("Analyzing columns...");


        // String[] header = reader.getHeader(true);

        List<String> rowAsTokens;
        List<String> header = new ArrayList<String>(reader.read());
        header = this.replaceSpaceWithDash(header);

        header = this.replaceReservedKeyWords(header);

        int columnCount = header.size();

        Column[] columnsMetadata = new Column[columnCount];

        for (int i = 0; i < columnCount; i++) {
            String currentHeader = null;
            if (hasHeader) {

                columnsMetadata[i] = new Column(header.get(i), 0);

            } else {
                columnsMetadata[i] = new Column(
                        "Column_" + Integer.toString(i), 0);

            }

        }

        // Read the CSV as List of Maps where each Map represents row data
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        Map<String, String> row = null;

        // read each row as a map <headername, value>
        while ((rowAsTokens = reader.read()) != null) {

            // Create hash map of each row
            row = new HashMap<String, String>();
            int contentLength = 0;
            String currentToken = "";
            for (int i = 0; i < header.size(); i++) {

                currentToken = rowAsTokens.get(i);
                if (currentToken != null) {
                    contentLength = rowAsTokens.get(i).length() + 1;
                } else {
                    contentLength = 1;
                }


                row.put(header.get(i), rowAsTokens.get(i));



                columnsMetadata[i].setMaxContentLength(contentLength);

            }

            // add Row map to list of rows
            rows.add(row);
        }


        return columnsMetadata;

    }

    // TODO umschreiben in die db importier funktion

    private List<String> replaceReservedKeyWords(List<String> header) {


        List<String> reservedKeyWords = new ArrayList<String>();

        reservedKeyWords.add("release");

        for (String currentHeader : header) {
            if (reservedKeyWords.contains(currentHeader.toLowerCase().trim())) {
                this.logger.info("Reserved keyword found");
                int index = header.indexOf(currentHeader);
                // replace the header with backticks
                header.set(index, ("`" + currentHeader + "`"));
            }
        }
        return header;


    }

    /**
     * Get List of all rows
     *
     * @param fileName
     * @return
     * @throws Exception
     */

    public List<String> readWithCsvListReaderAsStrings(String fileName)
            throws Exception {
        List<String> columns;
        List<String[]> csvRow;
        CsvListReader reader = new CsvListReader(new FileReader(fileName),
                CsvPreference.TAB_PREFERENCE);
        ICsvListReader listReader = null;
        try {
            final String[] header = reader.getHeader(true);

            // Create the new header array including sequence number and hash
            String[] newHeader = new String[reader.length() + 2];
            // Prepend the new column header for the sequence number
            newHeader[0] = "SequenceNumber";
            //
            for (int i = 1; i < newHeader.length - 1; i++) {
                newHeader[i] = header[i - 1];
            }

            newHeader[newHeader.length - 1] = "Hash";

            while ((columns = reader.read()) != null) {
                // Add new columns
                String appendedColumns = convertStringListToAppendedString(columns);
                String hash = calculateSHA1HashFromString(appendedColumns);
                //System.out.println(appendedColumns);
                // System.out.println(hash);
                columns.add(0, Integer.toString(reader.getRowNumber() - 1));

                columns.add(hash);

            }
            reader.close();
        } finally {
            if (listReader != null) {
                listReader.close();
            }
        }

        return columns;
    }

    public void writeResultSetIntoCSVFile(CachedRowSet resultSet, String path) {
        /*
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(path), '\t');
            this.logger.info(" CSV API size: "+ resultSet.size());
            csvWriter.writeAll(resultSet, true);
            this.logger.info("Wrote CSV file to : " + path
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    */


        PrintWriter csvWriter = null;
        try {
            csvWriter = new PrintWriter(new File(path));

            ResultSetMetaData meta = resultSet.getMetaData();
            int numberOfColumns = meta.getColumnCount();
            String dataHeaders = "\"" + meta.getColumnName(1) + "\"";
            for (int i = 2; i < numberOfColumns + 1; i++) {
                dataHeaders += ",\"" + meta.getColumnName(i) + "\"";
            }
            csvWriter.println(dataHeaders);
            resultSet.beforeFirst();
            while (resultSet.next()) {
                String row = "\"" + resultSet.getString(1) + "\"";
                for (int i = 2; i < numberOfColumns + 1; i++) {
                    row += ",\"" + resultSet.getString(i) + "\"";
                }
                csvWriter.println(row);
            }
            csvWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
