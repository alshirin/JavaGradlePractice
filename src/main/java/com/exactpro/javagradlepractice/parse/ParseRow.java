package com.exactpro.javagradlepractice.parse;

import com.exactpro.javagradlepractice.structure.RowExtendedTrade;
import com.exactpro.javagradlepractice.structure.RowTrade;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseRow {

//    public enum Tag {
//        RowHeader,
//        RowTrade,
//        RowExtendedTrade,
//        RowFooter,
//        UNRECOGNIZED
//
//    }

    public static boolean isHeader(String row) {
        return (row.startsWith("HEADR"));
    }

    public static boolean isFooter(String row) {
        return (row.startsWith("FOOTR"));
    }

    public static boolean isTrade(String row) {
        return (row.startsWith("TRADE"));
    }

    public static boolean isExTrade(String row) {
        return (row.startsWith("EXTRD"));
    }

    public static LinkedList<String> parseHeader(String row) {
        /*    parse HEADER:

                regex = "^(RowHeader)(0004|0005)(\d{4}[01]\d[0-3]\d[0-2]\d[0-5]\d[0-5]\d\d{3})(\{\d+\})?(.*?)$";
                row = "HEADR000521910120093025001{45}Have you watched C-beams glitter in the dark?";

              RegExp explaining:
                RowHeader = (RowHeader)
                0005 = (0004|0005)
                21910120093025001 = (\d{4}[01]\d[0-3]\d[0-2]\d[0-5]\d[0-5]\d\d{3})
                {45}Have you watched C-beams glitter in the dark? = (\{\d+\})?(.*?)
         */

        LinkedList<String> valuesList = new LinkedList<>();

        final String regex = "^(HEADR)(0004|0005)(\\d{4}[01]\\d[0-3]\\d[0-2]\\d[0-5]\\d[0-5]\\d\\d{3})(\\{\\d+\\})?(.*?)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(row);
        while (matcher.find()) {
            valuesList.addFirst(matcher.group(1));
            valuesList.add(matcher.group(2));

            // For File version 0005 only Comment parts #4 & #5 are available
            switch (matcher.group(2)) {
                case "0004":
                    valuesList.addLast(matcher.group(3));
                    break;
                case "0005":
                    valuesList.add(matcher.group(3));
                    if (matcher.group(4).charAt(1) != '0') {
                        valuesList.add(matcher.group(4));
                        valuesList.addLast(matcher.group(5).strip());
                    } else {
                        valuesList.addLast(matcher.group(4));
                    }
                    break;
                default:
                    System.out.println("Unsupported version value: " + matcher.group(2));
                    break;

            }
            //TODO: add logic for file comment in case different file version and existing the comment
        }

        return valuesList;
    }

    public static LinkedList<String> parseFooter(String row) {
        /*  parse RowFooter:

                regex = "^(RowFooter)(\d{10})(\d{10})$";
                row = "FOOTR00000000060000000655";

            RegExp explaining:
                RowFooter = (RowFooter)
                0000000006 = (\d{10})
                0000000655 = (\d{10})
         */

        LinkedList<String> valuesList = new LinkedList<>();
        final String regex = "^(FOOTR)(\\d{10})(\\d{10})$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(row);
        while (matcher.find()) {
            valuesList.addFirst(matcher.group(1));
            valuesList.add(matcher.group(2));
            valuesList.addLast(matcher.group(3));
            // TODO: add checksum verification for groups 2 & 3. Maybe outside of this method
        }
        return valuesList;
    }

    public static LinkedList<String> parseTrade(String row) {
        /*  parse RowTrade:

                regex = "^(RowTrade)(\d{4}[01]\d[0-3]\d[0-2]\d[0-5]\d[0-5]\d\d{3})(B|S)([A-Z0-9]{12})([+-]\d{14})(\+\d{9})([A-Za-z0-9_]{4})([A-Za-z0-9_]{4})(.*?)$";
                row = "TRADE21910120084505640BOCP0ED209MK1+00000000000000+000000010WEYU_OCP";

            RegExp explaining:
                RowTrade = (RowTrade)
                21910120084505640 = (\\d{4}[01]\\d[0-3]\\d[0-2]\\d[0-5]\\d[0-5]\\d\\d{3})
                B = (B|S)
                OCP0ED209MK1 = ([A-Z0-9]{12})
                +00000000000000 = ([+-]]\\d{14})
                +000000010 = (\\+\\d{10})
                WEYU = ([A-Za-z0-9_]{4})
                _OCP = ([A-Za-z0-9_]{4})
                Any text = (.*?)
         */

        LinkedList<String> valuesList = new LinkedList<>();
        final String regex = "^(TRADE)(\\d{4}[01]\\d[0-3]\\d[0-2]\\d[0-5]\\d[0-5]\\d\\d{3})(B|S)([A-Z0-9]{12})([+-]\\d{14})(\\+\\d{9})([A-Za-z0-9_]{4})([A-Za-z0-9_]{4})(.*?)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(row);
        while (matcher.find()) {
            valuesList.addFirst(matcher.group(1));
            valuesList.add(matcher.group(2));
            valuesList.add(matcher.group(3));
            valuesList.add(matcher.group(4));
            valuesList.add(matcher.group(5));
            valuesList.add(matcher.group(6));
            if (matcher.group(9) != null) {
                valuesList.add(matcher.group(7));
                valuesList.add(matcher.group(8));
                valuesList.addLast(matcher.group(9).strip());
            } else {
                valuesList.add(matcher.group(7));
                valuesList.addLast(matcher.group(8));
            }
        }
        return valuesList;
    }

    public static LinkedList<String> parseExtTrade(String row) {
        /*  parse ExtTrade:

                regex = "^(RowExtendedTrade)(0001)(\d{4}[01]\d[0-3]\d[0-2]\d[0-5]\d[0-5]\d\d{3})(BUY_|SELL)([A-Z0-9]{12})([+-]\d{14})(\+\d{9})([A-Za-z0-9_]{4})([A-Za-z0-9_]{4})([A-Z0-9|{}]+)$";
                row = "EXTRD000121910120090000001BUY_UMB00BOWTNMS+00000000055001+000000001_OCPUmbC{RE3{PSX|PC|DC|GC}RE3R{PS4|PS5|XBX1|XBXS|XBXS|PC}}";

            RegExp explaining:
                RowExtendedTrade = (RowExtendedTrade)
                0001 = (0001)
                21910120090000001 = (\d{4}[01]\d[0-3]\d[0-2]\d[0-5]\d[0-5]\d\d{3})
                BUY_ = (BUY_|SELL)
                UMB00BOWTNMS = ([A-Z0-9]{12})
                +00000000055001 = ([+-]\d{14})
                +000000001 = (\+\d{9})
                _OCP = ([A-Za-z0-9_]{4})
                UmbC = ([A-Za-z0-9_]{4})
                {RE3{PSX|PC|DC|GC}RE3R{PS4|PS5|XBX1|XBXS|XBXS|PC}} = ([A-Z0-9|{}]+)
         */
        LinkedList<String> valuesList = new LinkedList<>();

        final String regex = "^(EXTRD)(0001)(\\d{4}[01]\\d[0-3]\\d[0-2]\\d[0-5]\\d[0-5]\\d\\d{3})(BUY_|SELL)([A-Z0-9]{12})([+-]\\d{14})(\\+\\d{9})([A-Za-z0-9_]{4})([A-Za-z0-9_]{4})([A-Z0-9|{}]+)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(row);
        while (matcher.find()) {
            valuesList.addFirst(matcher.group(1));
            valuesList.add(matcher.group(2));
            valuesList.add(matcher.group(3));
            valuesList.add(matcher.group(4));
            valuesList.add(matcher.group(5));
            valuesList.add(matcher.group(6));
            valuesList.add(matcher.group(7));
            if (matcher.group(10) != null) {
                valuesList.add(matcher.group(8));
                valuesList.add(matcher.group(9));
                //valuesList.addLast(parseNestedStructure(matcher.group(10)));
                valuesList.addLast(matcher.group(10));
            } else {
                valuesList.add(matcher.group(8));
                valuesList.addLast(matcher.group(9));
            }
        }
        return valuesList;
    }

    public static String detectType(String row) {
        return row.substring(0, 5).equals("HEADR") ? "RowHeader" :
               row.substring(0, 5).equals("TRADE") ? "RowTrade" :
               row.substring(0, 5).equals("EXTRD") ? "RowExtendedTrade" :
               row.substring(0, 5).equals("FOOTR") ? "RowFooter" :
               "UNRECOGNIZED";
    }
//    public static String detectType(String row) {
//        String result;
//        switch (row.substring(0,5)) {
//            case "RowHeader" : result = "RowHeader"; break;
//            case "RowTrade" : result = "RowTrade"; break;
//            case "RowExtendedTrade" : result = "RowExtendedTrade"; break;
//            case "RowFooter" : result = "RowFooter"; break;
//            default      : result = "UNRECOGNIZED"; break;
//        }
//        return result;
//    }

    public static String parseNestedStructure(String row) {
        int braketsCounter = 0;
        StringBuilder result = new StringBuilder();

        for (char character: row.toCharArray()) {
            switch (character) {
                case '{':
                    if (braketsCounter > 0) {
                        result.append(":\n");
                        braketsCounter++;
                        // indention as per current level
                        for (int i=1;i<braketsCounter;i++) {
                            result.append("  ");
                        }
                    } else {
                        //first level
                        result.append("\n");
                        braketsCounter++;
                    }
                    break;
                case '}':
                    if (braketsCounter > 0) {
                        result.append("\n");
                        braketsCounter--;
                        // indention as per current level
                        for (int i=1;i<braketsCounter;i++) {
                            result.append("  ");
                        }
                    } else {
                        // go to first level
                        result.append("\n");
                        braketsCounter--;
                    }
                    break;
                case '|':
                    if (braketsCounter > 0) {
                        result.append("\n");
                        // indention as per current level
                        for (int i=1;i<braketsCounter;i++) {
                            result.append("  ");
                        }
                    } else {
                        //first level
                        result.append("\n");
                    }
                    break;
                default:
                    result.append(character);
            }
        }
        return result.toString();
    }
//    public static double parseDecimal(String number, int precision) {
//        double result = 0.0;
//        Double.parseDouble("-000000000500");
//
//        return result;
//    }
//
//    public static int parseInteger(String number) {
//        int result = 0;
//        return result;
//    }

//    public static Date parseDateTime(String datetime) {
//        Date result = new Date();
//        return result;
//    }

    public static LinkedList<RowTrade> sortTrades(LinkedList<RowTrade> rowTradeList) {
        Collections.sort(rowTradeList, new Comparator<RowTrade>() {

            @Override
            public int compare(RowTrade t1, RowTrade t2) {
                Double val2 = t2.getQuantity()*t2.getPrice();
                Double val1 = t1.getQuantity()*t1.getPrice();
                return val2.compareTo(val1);
            }
        });
        return rowTradeList;
    }
    public static LinkedList<RowExtendedTrade> sortExTrades(LinkedList<RowExtendedTrade> extradeList) {
        Collections.sort(extradeList, new Comparator<RowExtendedTrade>() {

            @Override
            public int compare(RowExtendedTrade t1, RowExtendedTrade t2) {
                Double val2 = t2.getQuantity()*t2.getPrice();
                Double val1 = t1.getQuantity()*t1.getPrice();
                return val2.compareTo(val1);
            }
        });
        return  extradeList;
    }
}
