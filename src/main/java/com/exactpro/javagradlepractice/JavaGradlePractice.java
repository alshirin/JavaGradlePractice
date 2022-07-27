package com.exactpro.javagradlepractice;

import com.exactpro.javagradlepractice.csv.WriteCSV;
import com.exactpro.javagradlepractice.parse.ParseRow;
import com.exactpro.javagradlepractice.structure.*;
import com.exactpro.javagradlepractice.structure.RowExtendedTrade;
import com.exactpro.javagradlepractice.structure.RowTrade;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class JavaGradlePractice {



    public static void main(String[] args) {
        //vars and structures to store data
        HashMap<Integer, String> rawData = new HashMap<>();
//        HashMap<Integer, LinkedList<String> > parsedData = new HashMap<>();

        LinkedList<RowHeader> headerList = new LinkedList<>();
        LinkedList<RowTrade> tradesList = new LinkedList<>();
        LinkedList<RowExtendedTrade> extendedTradesList = new LinkedList<>();
        LinkedList<RowFooter> footerList = new LinkedList<>();

        // Check if file were provided
        if (args.length > 0) {
            System.out.println("Opening file : " + args[0]);
        } else {
            throw new RuntimeException("No files were provided, please check input arguments");
        }

        //Open file for reading
        File file = new File(args[0]);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            int counter = 1;
            //parse line one by one and store them into separate linkedLists
            while (line != null) {
                if (!line.isBlank()) {
                    switch (ParseRow.detectType(line)) {
                        case "RowHeader": headerList.add(storeHeader(counter, line)); break;
                        case "RowFooter": footerList.add(storeFooter(counter, line)); break;
                        case "RowTrade": tradesList.add(storeTrade(counter, line)); break;
                        case "RowExtendedTrade": extendedTradesList.add(storeExTrade(counter, line)); break;
                        default:
                            System.out.println("Unrecognized row: \n" + "#" + counter + "    "+"\"" + line + "\"");
                            break;
                    }
                    rawData.put(counter, line);
                    counter++;
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Provided file were not found. Please check file name or file path" + Arrays.toString(ex.getStackTrace()));
        } catch (IOException ex) {
            throw new RuntimeException("Reading file failed" + Arrays.toString(ex.getStackTrace()));
        }


            System.out.println("Loaded data: ");
            System.out.println(rawData);

            //Write Header and Footer rows
            WriteCSV.writeHeaderFooterToCSV(headerList, footerList);
            //Write Trades rows
            WriteCSV.writeTradeExtTradeToCSV(tradesList, extendedTradesList);

        if (!rawData.isEmpty()) {
            // Check if the file contains HEADER and FOOTER
            if (!ParseRow.isHeader(rawData.get(1))) {
                System.out.println("There is no header row in datafile");
                System.exit(1);
            } else if (!ParseRow.isFooter(rawData.get(rawData.size()))) {
                System.out.println("There is no footer row in datafile");
                System.exit(1);
            }
        } else {
//            TODO: do this check earlier
            System.out.println("File is empty");
            System.exit(1);
        }
    }
    public static RowHeader storeHeader(int counter, String line) {
        //vars
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        RowHeader rowHeader = new RowHeader();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseHeader(line));

        // TAG
        rowHeader.setTag(parsedData.get(counter).get(0));
        // Version
        rowHeader.setVersion(parsedData.get(counter).get(1));
        //Creation Date
        try {
            datetime = formatter.parse(parsedData.get(counter).get(2));
            rowHeader.setCreationDate(datetime);
        } catch (ParseException pex) {
            throw new RuntimeException(" failed to parse datetime value" + Arrays.toString(pex.getStackTrace()));
        }
        // File comment
        if (parsedData.get(counter).get(1).equals("0005")) {
            rowHeader.setComment(parsedData.get(counter).get(3) + parsedData.get(counter).get(4));
        }
        return rowHeader;
    }

    public static RowFooter storeFooter(int counter, String line) {
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
//        Date datetime;
        RowFooter footer = new RowFooter();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseFooter(line));

        //TAG
        footer.setTag(parsedData.get(counter).get(0));
        // numberOfTrades;
        try {
            int recordsNumber = Integer.parseInt(parsedData.get(counter).get(1));
            footer.setNumberOfTrades(recordsNumber);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid records number: " + parsedData.get(counter).get(1));
            System.out.println(ParseRow.parseFooter(line));
        }
        // dataLength;
        try {
            int dataLength = Integer.parseInt(parsedData.get(counter).get(2));
            footer.setDataLength(dataLength);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid number of characters in RowTrade and RowExtendedTrade structures: " + parsedData.get(counter).get(2));
            System.out.println(ParseRow.parseFooter(line));
        }
        return footer;
    }

    public static RowTrade storeTrade(int counter, String line) {
        //vars
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        double price;
        int quantity;
        RowTrade rowTrade = new RowTrade();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");

        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseTrade(line));
        //TAG
        rowTrade.setTag(parsedData.get(counter).get(0));
        //dateTime
        try {
            datetime = formatter.parse(parsedData.get(counter).get(1));
            rowTrade.setTradeDateTime(datetime);
        } catch (ParseException pex) {
            throw new RuntimeException(" failed to parse datetime value" + Arrays.toString(pex.getStackTrace()));
        }
        //direction
        rowTrade.setDirection(parsedData.get(counter).get(2));
        //itemID
        rowTrade.setItemID(parsedData.get(counter).get(3));
        //price
        try {
            price = Double.parseDouble(parsedData.get(counter).get(4))/10000;
            rowTrade.setPrice(price);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Price value: " + parsedData.get(counter).get(4));
            System.out.println(ParseRow.parseFooter(line));
        }
        //quantity
        try {
            quantity = Integer.parseInt(parsedData.get(counter).get(5));
            rowTrade.setQuantity(quantity);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Quantity value: " + parsedData.get(counter).get(5));
            System.out.println(ParseRow.parseFooter(line));
        }
        //buyer
        rowTrade.setBuyer(parsedData.get(counter).get(6));
        //seller
        rowTrade.setSeller(parsedData.get(counter).get(7));
        //comment
        rowTrade.setComment(parsedData.get(counter).get(8));
        return rowTrade;
    }

    public static RowExtendedTrade storeExTrade(int counter, String line) {
        //vars
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        double price;
        int quantity;
        RowExtendedTrade rowExtendedTrade = new RowExtendedTrade();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");

        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseExtTrade(line));
        //tag
        rowExtendedTrade.setTag(parsedData.get(counter).get(0));
        //version
        rowExtendedTrade.setVersion(parsedData.get(counter).get(1));
        //datetime
        try {
            datetime = formatter.parse(parsedData.get(counter).get(2));
            rowExtendedTrade.setTradeDateTime(datetime);
        } catch (ParseException pex) {
            throw new RuntimeException(" failed to parse datetime value" + Arrays.toString(pex.getStackTrace()));
        }
        //direction
        rowExtendedTrade.setDirection(parsedData.get(counter).get(3));
        //itemID
        rowExtendedTrade.setItemID(parsedData.get(counter).get(4));
        //price
        try {
            price = Double.parseDouble(parsedData.get(counter).get(5))/10000;
            rowExtendedTrade.setPrice(price);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Price value: " + parsedData.get(counter).get(5));
            System.out.println(ParseRow.parseFooter(line));
        }
        //quantity
        try {
            quantity = Integer.parseInt(parsedData.get(counter).get(6));
            rowExtendedTrade.setQuantity(quantity);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Quantity value: " + parsedData.get(counter).get(6));
            System.out.println(ParseRow.parseFooter(line));
        }
        //buyer
        rowExtendedTrade.setBuyer(parsedData.get(counter).get(7));
        //seller
        rowExtendedTrade.setSeller(parsedData.get(counter).get(8));
        //nestedTags
        rowExtendedTrade.setNestedTags(ParseRow.parseNestedStructure(parsedData.get(counter).get(9)));
        return rowExtendedTrade;
    }

}