package inc.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String CRLF = "\r\n";

    public static synchronized String getCmd(String input) {
        String[] inputWords = input.trim().split(" ");
        return inputWords[0].trim();
    }

    public static synchronized String[] getKnownComputersFromJson(String json) {
        json = json.replaceAll(" ", "");
        json = json.replaceAll("\"", "");
        json = json.substring(1, json.length() - 1);

        String[] tmp = json.split("\\],\\[");
        tmp[0] = tmp[0].replace("[", "");
        tmp[tmp.length - 1] = tmp[tmp.length - 1].replace("]", "");


        for (int i = 0; i < tmp.length; i++) {
            String current = tmp[i];
            String[] ipPortPair = current.split(",");
            tmp[i] = ipPortPair[0] + ":" + ipPortPair[1];
        }

        return tmp;
    }

    public static synchronized String getHostFromUrl(String url) {
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


    public static synchronized Map<String, String> getRequestFromJson(String json) {//FIXME
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

    private static synchronized String clearListsFromSpaces(String input, int startIndex) {
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

    public static synchronized String[] getCmdParams(String input) {
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


    public static synchronized String parseStringArrayToJson(String... params) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");
        for (int i = 0; i < params.length; i++) {
            String[] keyValue_pair = params[i].split("=");
            String key = keyValue_pair[0];
            String value = keyValue_pair[1];
            stringBuilder.append("\"");
            stringBuilder.append(key);
            stringBuilder.append("\":");
            if (!value.startsWith("[")) {
                stringBuilder.append("\"");
            }
            stringBuilder.append(value);
            if (!value.endsWith("]")) {
                stringBuilder.append("\"");
            }


            if (i != params.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public static synchronized String parseArrayToGetParams(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            stringBuilder.append(String.valueOf(params[i]));

            if (i != params.length - 1) {
                stringBuilder.append("&");
            }
        }

        return stringBuilder.toString().length() > 0 ? stringBuilder.toString() : null;
    }

    public static synchronized Map<String, String> getRequestFromStringQuery(String request) {
        request = request.split("GET ")[1].split(" HTTP")[0].trim();

        if (request.equals("/") || request.equals("")) {
            return null;
        }

        String[] requestParams = request.split("&");

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

    public static synchronized String getRequestContext(String host) {
        String[] arr = host.split("/");

        if (arr.length <= 1) {
            return "/";
        }

        return "/" + arr[1].split("\\?")[0].split("HTTP")[0].trim();
    }

    public static synchronized String readJsonFromFile(String file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException ignored) {
            ignored.printStackTrace();
        }
        StringBuilder result = new StringBuilder();

        if (fileInputStream != null) {
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
        }

        return result.toString();
    }
}
