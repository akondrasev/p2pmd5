package inc.util;

import inc.dto.CrackResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String CRLF = "\r\n";

    public synchronized static String getCmd(String input) {
        String[] inputWords = input.trim().split(" ");
        return inputWords[0].trim();
    }

    public synchronized static CrackResult checkMd5(String md5, String wildcard, String[] ranges, int[][] symbolrange) {
        CrackResult reslut = new CrackResult();
        //TODO check md5
        for (int i = 0; i < ranges.length; i++) {
            String template = ranges[i];
            String resultstring = new Md5Worker(md5, template, symbolrange, wildcard).work();
            reslut.setResultCode("0");
            reslut.setResultstring(resultstring);
        }
        return reslut;
    }

    public synchronized static int[] checkWildcards(String word, char wildcard) {
        int count = 0;
        int lastIndex = -1;
        String indexes = "";

        do {
            lastIndex = word.indexOf(wildcard, lastIndex + 1);
            if (lastIndex != -1) {
                count++;
                indexes += String.valueOf(lastIndex);
            }
        } while (lastIndex != -1);

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = Integer.parseInt(indexes.substring(i, i + 1));
        }

        return result;
    }


    private synchronized static String checkMd5(String md5, char wildcard, char[] word, int[] symbolrange) {
        int maxRange = symbolrange[1];
        int minRange = symbolrange[0];

        if (md5.equals(md5(String.valueOf(word)))) {
            return String.valueOf(word);
        }

        for (int i = 0; i < word.length; i++) {
            if (word[i] == wildcard) {
                while (minRange <= maxRange) {
                    char[] tempWord = word.clone();
                    char tempChar = (char) minRange;

                    if (tempChar == wildcard) {
                        minRange++;
                        continue;
                    }

                    tempWord[i] = tempChar;

                    println(String.valueOf(tempWord));

                    String currentMd5 = md5(String.valueOf(tempWord));

                    if (currentMd5.equals(md5)) {
                        return String.valueOf(tempWord);
                    }
                    minRange++;
                    return checkMd5(md5, wildcard, tempWord, symbolrange);
                }
            }
        }

        if (md5(String.valueOf(word)).equals(md5)) {
            return String.valueOf(word);
        }

        return null;

    }

    public synchronized static void println(String msg, Object... params) {
        System.out.println(String.format(msg, params));
    }

    public synchronized static String[] getKnownComputersFromJson(String json) {
        String tempjson = json.replaceAll(" ", "");
        tempjson = tempjson.replaceAll("\"", "");
        tempjson = tempjson.substring(1, tempjson.length() - 1);

        String[] tmp = tempjson.split("\\],\\[");
        tmp[0] = tmp[0].replace("[", "");
        tmp[tmp.length - 1] = tmp[tmp.length - 1].replace("]", "");


        for (int i = 0; i < tmp.length; i++) {
            String current = tmp[i];
            String[] ipPortPair = current.split(",");
            tmp[i] = ipPortPair[0] + ":" + ipPortPair[1];
        }

        return tmp;
    }

    public synchronized static String getHostFromUrl(String url) {
        String result;

        String[] tmp = url.split("http://");
        result = tmp[tmp.length - 1];

        tmp = result.split("www.");
        result = tmp[tmp.length - 1];

        tmp = result.split("/");
        result = tmp[0];

        return result;
    }

    public static synchronized String getCurrentIp() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Cannot get current host address");
        }

        return host;
    }


    public synchronized static Map<String, String> getRequestFromJson(String json) {
        Map<String, String> result = new TreeMap<>();
        String tempJson = json.trim();
        tempJson = tempJson.replaceAll("\"", "");
//        tempJson = tempJson.replaceAll(" ", "");
        tempJson = tempJson.substring(1, tempJson.length() - 1);


        String[] tmp = tempJson.split(",");
        String lastKey = null;
        for (String currentString : tmp) {
            String[] key_valuePair = currentString.split(":");

            if (key_valuePair.length == 1) {
                String value = result.get(lastKey);
                value = value + "," + key_valuePair[0].trim();
                result.put(lastKey, value);
                continue;
            }

            String existingValue = result.get(key_valuePair[0]);
            if (existingValue != null) {
                existingValue += "," + key_valuePair[1].trim();
                result.put(key_valuePair[0].trim(), existingValue);
            } else {
                result.put(key_valuePair[0].trim(), key_valuePair[1].trim());
            }
            lastKey = key_valuePair[0].trim();
        }
        return result;
    }

    private synchronized static String clearListsFromSpaces(String input, int startIndex) {
        int openList = input.indexOf('[', startIndex);
        if (openList > -1) {
            int closeList = input.indexOf(']', startIndex);
            String tmp = input.substring(openList - 1, closeList + 1);

            input = input.replace(tmp, tmp.trim().replaceAll(" ", ""));
            return clearListsFromSpaces(input, closeList + 1);
        } else {
            return input;
        }
    }

    public synchronized static String[] getCmdParams(String input) {
        input = clearListsFromSpaces(input, 0);

        String[] inputWords = input.trim().split(" ");
        int length = inputWords.length - 1;

        for (int i = 0; i < inputWords.length; i++) {
            inputWords[i] = inputWords[i].trim();
        }

        String[] result = new String[length];
        System.arraycopy(inputWords, 1, result, 0, length);

        return result;
    }


    public synchronized static String parseStringArrayToJson(String... params) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");
        for (int i = 0; i < params.length; i++) {
            String[] keyValue_pair = params[i].split("=");
            String key = keyValue_pair[0];
            String value = keyValue_pair[1];
            boolean isNumeric = true;

            try {
                int tmp = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                isNumeric = false;
            }

            stringBuilder.append("\"");
            stringBuilder.append(key);
            stringBuilder.append("\":");
            if (!value.startsWith("[") && !isNumeric) {
                stringBuilder.append("\"");
            }
            stringBuilder.append(value);
            if (!value.endsWith("]") && !isNumeric) {
                stringBuilder.append("\"");
            }


            if (i != params.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public synchronized static String parseArrayToGetParams(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            stringBuilder.append(String.valueOf(params[i]));

            if (i != params.length - 1) {
                stringBuilder.append("&");
            }
        }

        return stringBuilder.toString().length() > 0 ? stringBuilder.toString() : null;
    }

    public synchronized static Map<String, String> getRequestFromStringQuery(String request) {
        String tmp = request.split("GET ")[1].split(" HTTP")[0].trim();

        if (tmp.equals("/") || tmp.equals("")) {
            return null;
        }

        String[] requestParams = tmp.split("&");

        int index = requestParams[0].indexOf("?") + 1;

        if (index == 0) {
            return null;
        }

        requestParams[0] = requestParams[0].substring(index);

        Map<String, String> result = new TreeMap<>();
        for (String requestParam : requestParams) {
            String[] keyValue_pair = requestParam.split("=");
            String key = keyValue_pair[0];
            String value = result.get(key);

            if (value == null) {
                value = keyValue_pair[1];
            } else {
                value = value + "," + keyValue_pair[1];
            }

            result.put(key, value);
        }

        return result;
    }

    public synchronized static String getRequestContext(String host) {
        String[] arr = host.split("/");

        if (arr.length <= 1) {
            return "/";
        }

        return "/" + arr[1].split("\\?")[0].split("HTTP")[0].trim();
    }

    public synchronized static String readJsonFromFile(String file) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException ignored) {
            return null;
        }
        StringBuilder result = new StringBuilder();

        int available = -1;
        try {
            available = fileInputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (available > -1) {
            for (int i = 0; i < available; i++) {
                try {
                    char c = (char) fileInputStream.read();
                    result.append(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public synchronized static String[] getStringTemplatesFromRanges(String ranges) {
        String tmp = ranges.replaceAll("\"", "");
        tmp = tmp.substring(1, tmp.length() - 1);
        String[] templates = tmp.split(",");
        for (int i = 0; i < templates.length; i++) {
            templates[i] = templates[i].trim();
        }

        return templates;
    }

    public synchronized static String md5(String value) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }

        assert mdEnc != null;
        mdEnc.update(value.getBytes(), 0, value.length());
        return new BigInteger(1, mdEnc.digest()).toString(16);
    }

    public synchronized static int[][] getSymbolrange(String symbolrange) {
        String tempjson = symbolrange.replaceAll(" ", "");
        tempjson = tempjson.substring(1, tempjson.length() - 1);// now: [10,10],[10,10],[10,10]
        tempjson = tempjson.replaceAll("\\[", "");
        tempjson = tempjson.replaceAll("\\]", "");//now: 10,10,10,10,10,10
        String[] numbers = tempjson.split(",");

        int[][] asciiRanges = new int[numbers.length / 2][2];

        int currentIndex = 0;
        for (int i = 0; i < numbers.length; i++) {
            int secondIndex = 0;
            if((i % 2) == 1){
                secondIndex = 1;
            }
            if ((i % 2) == 0 && i > 1) {
                currentIndex++;
            }
            asciiRanges[currentIndex][secondIndex] = Integer.parseInt(numbers[i]);
        }
        return asciiRanges;
    }
}
