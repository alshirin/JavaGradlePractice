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

        System.out.println("Opening file : " + args[0]);

        HashMap<Integer, String> rawData = new HashMap<>();
        HashMap<Integer, LinkedList<String> > parsedData = new HashMap<>();

        LinkedList<HEADR> headerList = new LinkedList<>();
        LinkedList<TRADE> tradesList = new LinkedList<>();
        LinkedList<EXTRD> extendedTradesList = new LinkedList<>();
        LinkedList<FOOTR> footerList = new LinkedList<>();

        // vars used for converting values into internal data types
        Date datetime;
        double price;
        int quantity;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmssSSS");

        try  {
            //Open file for reading
            File file = new File(args[0]);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            //Read the first line
            String line;
            line = reader.readLine();
            Integer counter = 1;

            while (line != null) {
                if (!line.isBlank()) {
                    switch (ParseRow.detectType(line)) {
                        case "HEADR":
//                            System.out.println("Header row: " + ParseRow.parseHeader(line));
                            parsedData.put(counter, ParseRow.parseHeader(line));
                            HEADR headr = new HEADR();
                            // TAG
                            headr.setTag(parsedData.get(counter).get(0));
                            // Version
                            headr.setVersion(parsedData.get(counter).get(1));
                            //Creation Date
                            datetime = formatter.parse(parsedData.get(counter).get(2));
                            headr.setCreationDate(datetime);
                            // File comment
                            if (parsedData.get(counter).get(1).equals("0005")) {
                                headr.setComment(parsedData.get(counter).get(3) + parsedData.get(counter).get(4));
                            }
                            //put finally parsed row into the list
                            headerList.add(headr);
                            break;
                        case "FOOTR":
//                            System.out.println("Footer row: " + ParseRow.parseFooter(line));
                            parsedData.put(counter, ParseRow.parseFooter(line));
                            FOOTR footer = new FOOTR();
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
                            //put finally parsed row into the list
                            footerList.add(footer);
                            break;
                        case "EXTRD":
//                            System.out.println("ExrTrade row: " + ParseRow.parseExtTrade(line));
                            parsedData.put(counter, ParseRow.parseExtTrade(line));
                            EXTRD extrd = new EXTRD();
                            //tag
                            extrd.setTag(parsedData.get(counter).get(0));
                            //version
                            extrd.setVersion(parsedData.get(counter).get(1));
                            //dateTime
                            datetime = formatter.parse(parsedData.get(counter).get(2));
                            extrd.setTradeDateTime(datetime);
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
                            //put finally parsed row into the list
                            extendedTradesList.add(extrd);
                            break;
                        case "TRADE":
//                            System.out.println("Trade row: " + ParseRow.parseTrade(line));
                            parsedData.put(counter, ParseRow.parseTrade(line));
                            TRADE trade = new TRADE();

                            //tag
                            trade.setTag(parsedData.get(counter).get(0));
                            //dateTime
                            datetime = formatter.parse(parsedData.get(counter).get(1));
                            trade.setTradeDateTime(datetime);
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
                            //put finally parsed row into the list
                            tradesList.add(trade);
                            break;
                        default:
                            System.out.println("Unrecognized row: \n" + line);
                            LinkedList<String> tmpList = new LinkedList<>();
                            tmpList.add("UNRECOGNIZED");
                            tmpList.add(line);
                            parsedData.put(counter, tmpList);
                            break;
                    }

                    rawData.put(counter, line);
                    counter++;
                }
                line = reader.readLine();
            }

//            System.out.println("FirstLine: >> "+ rawData.get(1));
//            System.out.println("Last Line : >> "+ rawData.get(rawData.size()));

            System.out.println("Loaded data: ");
            System.out.println(rawData);

            CSV.writeHeaderFooterToCSV(headerList, footerList);
            CSV.writeTradeExtTradeToCSV(tradesList, extendedTradesList);

            System.out.println("PreParsed data: ");
            System.out.println(parsedData);


        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found: " + fileNotFoundException.toString()) ;
        } catch (IOException ioException) {
            System.out.println("Reading file failed" + ioException.toString()) ;
        }
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
}
