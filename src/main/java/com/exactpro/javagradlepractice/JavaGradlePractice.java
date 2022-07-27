package com.exactpro.javagradlepractice;

import com.exactpro.javagradlepractice.CSV.CSV;
import com.exactpro.javagradlepractice.parse.ParseRow;
import com.exactpro.javagradlepractice.structure.EXTRD;
import com.exactpro.javagradlepractice.structure.FOOTR;
import com.exactpro.javagradlepractice.structure.HEADR;
import com.exactpro.javagradlepractice.structure.TRADE;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class JavaGradlePractice {



    public static void main(String[] args) throws ParseException {
        //vars and structures to store data
        HashMap<Integer, String> rawData = new HashMap<>();
        HashMap<Integer, LinkedList<String> > parsedData = new HashMap<>();

        LinkedList<HEADR> headerList = new LinkedList<>();
        LinkedList<TRADE> tradesList = new LinkedList<>();
        LinkedList<EXTRD> extendedTradesList = new LinkedList<>();
        LinkedList<FOOTR> footerList = new LinkedList<>();

        String line;
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");

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
            line = reader.readLine();
            int counter = 1;
            //parse line one by one and store them into separate linkedLists
            while (line != null) {
                if (!line.isBlank()) {
                    switch (ParseRow.detectType(line)) {
                        case "HEADR":
                            headerList.add(storeHeader(counter, line));
                            break; case "FOOTR": footerList.add(storeFooter(counter, line));
                            break; case "TRADE": tradesList.add(storeTrade(counter, line));
                            break; case "EXTRD": extendedTradesList.add(storeExTrade(counter, line));
                            break; default: System.out.println("Unrecognized row: \n" + line);
                            LinkedList<String> tmpList = new LinkedList<>();
                            tmpList.add("UNRECOGNIZED");
                            tmpList.add(line);
//                               parsedData.put(counter, tmpList);
                            break;
                    }
                    rawData.put(counter, line);
                    counter++;
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Provided file were not found. Please check file name or file path" + ex.getStackTrace());
        } catch (IOException ex) {
            throw new RuntimeException("Reading file failed" + ex.getStackTrace());
        }



            System.out.println("Loaded data: ");
            System.out.println(rawData);

            CSV.writeHeaderFooterToCSV(headerList, footerList);
            CSV.writeTradeExtTradeToCSV(tradesList, extendedTradesList);

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
    public static HEADR storeHeader(int counter, String line) {
        //vars
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        HEADR headr = new HEADR();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseHeader(line));

        // TAG
        headr.setTag(parsedData.get(counter).get(0));
        // Version
        headr.setVersion(parsedData.get(counter).get(1));
        //Creation Date
        try {
            datetime = formatter.parse(parsedData.get(counter).get(2));
            headr.setCreationDate(datetime);
        } catch (ParseException pex) {
            throw new RuntimeException(" failed to parse datetime value" + pex.getStackTrace());
        }
        // File comment
        if (parsedData.get(counter).get(1).equals("0005")) {
            headr.setComment(parsedData.get(counter).get(3) + parsedData.get(counter).get(4));
        }
        return headr;
    }

    public static FOOTR storeFooter(int counter, String line) {
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        FOOTR footer = new FOOTR();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");
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
            System.out.println("Line number [" + counter + "] contains invalid number of characters in TRADE and EXTRD structures: " + parsedData.get(counter).get(2));
            System.out.println(ParseRow.parseFooter(line));
        }
        return footer;
    }

    public static TRADE storeTrade(int counter, String line) {
        //vars
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        double price;
        int quantity;
        TRADE trade = new TRADE();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");

        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseTrade(line));
        //TAG
        trade.setTag(parsedData.get(counter).get(0));
        //dateTime
        try {
            datetime = formatter.parse(parsedData.get(counter).get(1));
            trade.setTradeDateTime(datetime);
        } catch (ParseException pex) {
            throw new RuntimeException(" failed to parse datetime value" + pex.getStackTrace());
        }
        //direction
        trade.setDirection(parsedData.get(counter).get(2));
        //itemID
        trade.setItemID(parsedData.get(counter).get(3));
        //price
        try {
            price = Double.parseDouble(parsedData.get(counter).get(4))/10000;
            trade.setPrice(price);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Price value: " + parsedData.get(counter).get(4));
            System.out.println(ParseRow.parseFooter(line));
        }
        //quantity
        try {
            quantity = Integer.parseInt(parsedData.get(counter).get(5));
            trade.setQuantity(quantity);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Quantity value: " + parsedData.get(counter).get(5));
            System.out.println(ParseRow.parseFooter(line));
        }
        //buyer
        trade.setBuyer(parsedData.get(counter).get(6));
        //seller
        trade.setSeller(parsedData.get(counter).get(7));
        //comment
        trade.setComment(parsedData.get(counter).get(8));
        return trade;
    }

    public static EXTRD storeExTrade(int counter, String line) {
        //vars
        HashMap<Integer, LinkedList<String>> parsedData = new HashMap<>();
        Date datetime;
        double price;
        int quantity;
        EXTRD extrd = new EXTRD();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");

        // Pre-parsed line
        parsedData.put(counter, ParseRow.parseExtTrade(line));
        //tag
        extrd.setTag(parsedData.get(counter).get(0));
        //version
        extrd.setVersion(parsedData.get(counter).get(1));
        //datetime
        try {
            datetime = formatter.parse(parsedData.get(counter).get(2));
            extrd.setTradeDateTime(datetime);
        } catch (ParseException pex) {
            throw new RuntimeException(" failed to parse datetime value" + pex.getStackTrace());
        }
        //direction
        extrd.setDirection(parsedData.get(counter).get(3));
        //itemID
        extrd.setItemID(parsedData.get(counter).get(4));
        //price
        try {
            price = Double.parseDouble(parsedData.get(counter).get(5))/10000;
            extrd.setPrice(price);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Price value: " + parsedData.get(counter).get(5));
            System.out.println(ParseRow.parseFooter(line));
        }
        //quantity
        try {
            quantity = Integer.parseInt(parsedData.get(counter).get(6));
            extrd.setQuantity(quantity);
        } catch (Exception e) {
            System.out.println("Line number [" + counter + "] contains invalid Quantity value: " + parsedData.get(counter).get(6));
            System.out.println(ParseRow.parseFooter(line));
        }
        //buyer
        extrd.setBuyer(parsedData.get(counter).get(7));
        //seller
        extrd.setSeller(parsedData.get(counter).get(8));
        //nestedTags
        extrd.setNestedTags(ParseRow.parseNestedStructure(parsedData.get(counter).get(9)));
        return extrd;
    }

}