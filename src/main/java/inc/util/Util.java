package inc.util;

import inc.dto.CrackResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String CRLF = "\r\n";

    public static CrackResult checkMd5(String md5, String wildcard, String[] ranges, int[][] symbolrange) {
        CrackResult reslut = new CrackResult();
        for (String template : ranges) {
            String resultstring = new Md5Worker(md5, template, symbolrange, wildcard).work();

            if (resultstring != null) {
                reslut.setResultCode(0);
                reslut.setResultstring(resultstring);
                break;
            }

            reslut.setResultCode(1);
        }
        return reslut;
    }


    public static String[] getKnownComputersFromJson(String json) {
        JSONArray jsonArray = new JSONArray(json);
        String[] result = new String[jsonArray.length()];

        for (int i = 0; i < result.length; i++) {
            JSONArray currentComp = jsonArray.getJSONArray(i);
            result[i] = currentComp.getString(0) + ":" + currentComp.getString(1);
        }

        return result;
    }

    public static String getHostFromUrl(String url) {
        String result;

        String[] tmp = url.split("http://");
        result = tmp[tmp.length - 1];

        tmp = result.split("www.");
        result = tmp[tmp.length - 1];

        tmp = result.split("/");
        result = tmp[0];

        return result;
    }

    public static String getCurrentIp() {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Cannot get current host address");
        }

        return host;
    }


    public static Map<String, String> getRequestFromJson(String json) {
        Map<String, String> result = new TreeMap<>();
        JSONObject jsonObject = new JSONObject(json);

        for (String key : jsonObject.keySet()) {
            result.put(key, jsonObject.get(key).toString());
        }

        return result;
    }

    public static JSONObject parseStringArrayToJson(String... params) {
        JSONObject jsonObject = new JSONObject();

        for (String string : params) {
            String[] keyValue = string.split("=");
            String value = keyValue[1];
            String key = keyValue[0];
            if (value.startsWith("[")) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(value);
                } catch (JSONException exception){
                    jsonArray = new JSONArray();
                    jsonArray.put("\"   ");
                }


                jsonObject.put(key, jsonArray);
                continue;
            }

            try {
                int test = Integer.parseInt(value);
                jsonObject.put(key, test);
            } catch (NumberFormatException e) {
                jsonObject.put(key, value);
            }
        }

        return jsonObject;
    }

    public static String parseArrayToGetParams(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            stringBuilder.append(String.valueOf(params[i]));

            if (i != params.length - 1) {
                stringBuilder.append("&");
            }
        }

        return stringBuilder.toString().length() > 0 ? stringBuilder.toString() : null;
    }

    public static Map<String, String> getRequestFromStringQuery(String request) {
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

    public static String getRequestContext(String host) {
        String[] arr = host.split("/");

        if (arr.length <= 1) {
            return "/";
        }

        return "/" + arr[1].split("\\?")[0].split("HTTP")[0].trim();
    }

    public static String readJsonFromFile(String file) {
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

    public static String[] getStringTemplatesFromRanges(String ranges) {
        JSONArray jsonArray = new JSONArray(ranges);
        String[] result = new String[jsonArray.length()];

        for (int i = 0; i < result.length; i++) {
            result[i] = jsonArray.getString(i);
        }
        return result;
    }

    public static String md5(String value) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }

        assert mdEnc != null;
        mdEnc.update(value.getBytes(), 0, value.length());
        return new BigInteger(1, mdEnc.digest()).toString(16);
    }

    public static int[][] getSymbolrange(String symbolrange) {
        String tempjson = symbolrange.replaceAll(" ", "");
        tempjson = tempjson.substring(1, tempjson.length() - 1);// now: [10,10],[10,10],[10,10]
        tempjson = tempjson.replaceAll("\\[", "");
        tempjson = tempjson.replaceAll("\\]", "");//now: 10,10,10,10,10,10
        String[] numbers = tempjson.split(",");

        int[][] asciiRanges = new int[numbers.length / 2][2];

        int currentIndex = 0;
        for (int i = 0; i < numbers.length; i++) {
            int secondIndex = 0;
            if ((i % 2) == 1) {
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
