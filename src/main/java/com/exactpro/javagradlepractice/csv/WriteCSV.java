package com.exactpro.javagradlepractice.csv;

import com.exactpro.javagradlepractice.parse.ParseRow;
import com.exactpro.javagradlepractice.structure.*;
import com.exactpro.javagradlepractice.structure.RowExtendedTrade;
import com.exactpro.javagradlepractice.structure.RowTrade;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class WriteCSV {
    public static void writeHeaderFooterToCSV(LinkedList<RowHeader> headerList, LinkedList<RowFooter> footerList) {
        //CSV File ro write data
        String outputFile = System.getProperty("user.dir") + "/header_and_footer.csv";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);)
//                     .withHeader(RowHeader.Header.class));)
        {


            for (RowHeader rows : headerList)
            {
                csvPrinter.print(rows.getTag());
                csvPrinter.print(rows.getVersion());
                csvPrinter.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(rows.getCreationDate()));
                csvPrinter.print(rows.getComment());
                csvPrinter.println();
            }

            for (RowFooter rows : footerList)
            {
                csvPrinter.print(rows.getTag());
                csvPrinter.print(rows.getNumberOfTrades());
                csvPrinter.print(rows.getDataLength());
                csvPrinter.println();
            }
            csvPrinter.flush();

        } catch (IOException ex)
        {
            System.out.println("Exception : " + ex);
        }
    }

    public static void writeTradeExtTradeToCSV(LinkedList<RowTrade> tradesList, LinkedList<RowExtendedTrade> extendedTradesList) {
        // Sort LinkedList descending by Quantity
        tradesList = ParseRow.sortTrades(tradesList);
        extendedTradesList = ParseRow.sortExTrades(extendedTradesList);
        //CSV File ro write data
        String outputFile = System.getProperty("user.dir") + "/trades.csv";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader(RowExtendedTrade.Header.class));)
        {
            for (RowTrade rows : tradesList)
            {
                csvPrinter.print(rows.getTag());
                csvPrinter.print("");
                csvPrinter.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(rows.getTradeDateTime()));
                csvPrinter.print(rows.getDirection());
                csvPrinter.print(rows.getItemID());
                csvPrinter.print(rows.getPrice());
                csvPrinter.print(rows.getQuantity());
                csvPrinter.print(rows.getBuyer());
                csvPrinter.print(rows.getSeller());
                csvPrinter.print(rows.getComment());
                csvPrinter.print("");
                csvPrinter.println();
            }

            for (RowExtendedTrade rows : extendedTradesList)
            {
                csvPrinter.print(rows.getTag());
                csvPrinter.print(rows.getVersion());
                csvPrinter.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(rows.getTradeDateTime()));
                csvPrinter.print(rows.getDirection());
                csvPrinter.print(rows.getItemID());
                csvPrinter.print(rows.getPrice());
                csvPrinter.print(rows.getQuantity());
                csvPrinter.print(rows.getBuyer());
                csvPrinter.print(rows.getSeller());
                csvPrinter.print("");
                csvPrinter.print(rows.getNestedTags());
                csvPrinter.println();
            }
            csvPrinter.flush();

        } catch (IOException ex)
        {
            System.out.println("Exception : " + ex);
        }

    }
}
