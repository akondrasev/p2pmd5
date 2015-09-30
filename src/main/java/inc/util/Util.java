package inc.util;

import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String CRLF = "\r\n";

    public static String getCommandFromInput(String input) {
        String[] inputWords = input.trim().split(" ");
        return inputWords[0].trim();
    }

    public static String getHostInUrl(String url) {
        String result;

        String[] tmp = url.split("http://");
        result = tmp[tmp.length - 1];

        tmp = result.split("www.");
        result = tmp[tmp.length - 1];

        tmp = result.split("/");
        result = tmp[0];

        return result;
    }

    public static String getCurrentHostIp(){
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Cannot get current host address");
        }

        return host;
    }


    public static Map<String, Object> getRequestParamsFromJson(String json){
        Gson gson = new Gson();
        Class type = TreeMap.class;

        Map<String, Object> result = (Map<String, Object>) gson.fromJson(json, type);
        return result;
    }

    private static String clearListsFromSpaces(String input, int startIndex){
        int openList = input.indexOf('[', startIndex);
        if(openList > -1){
            int closeList = input.indexOf(']', startIndex);
            String tmp = input.substring(openList-1, closeList+1);

            input = input.replace(tmp, tmp.trim().replaceAll(" ", ""));
            return clearListsFromSpaces(input, closeList +1);
        } else {
            return input;
        }
    }

    public static String[] getParamsFromInput(String input) {
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


    public static String parseStringArrayToJson(String... params) {//FIXME
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");
        for (int i = 0; i < params.length; i++) {
            String[] keyValue_pair = params[i].split("=");
            String key = keyValue_pair[0];
            String value = keyValue_pair[1];
            stringBuilder.append("\"");
            stringBuilder.append(key);
            stringBuilder.append("\":");
            if(!value.startsWith("[")){
                stringBuilder.append("\"");
            }
            stringBuilder.append(value);
            if(!value.endsWith("]")){
                stringBuilder.append("\"");
            }


            if (i != params.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public static String parseArrayToGetParams(String... params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            stringBuilder.append(String.valueOf(params[i]));

            if (i != params.length - 1) {
                stringBuilder.append("&");
            }
        }

        String result = stringBuilder.toString().length() > 0 ? stringBuilder.toString() : null;

        return result;
    }

    public static Map<String, Object> parseGetRequest(String request) {
        if (request == null || request.equals("/") || request.equals("")) {
            return null;
        }

        String[] requestParams = request.split("&");

        int index = requestParams[0].indexOf("?") + 1;
        requestParams[0] = requestParams[0].substring(index);

        Map<String, Object> result = new TreeMap<>();
        for (String requestParam : requestParams) {
            String[] keyValue_pair = requestParam.split("=");
            String key = keyValue_pair[0];
            String value = keyValue_pair[1];

            result.put(key, value);
        }

        return result;
    }

    public static String getHostContext(String host) {
        String[] arr = host.split("/");

        if (arr.length <= 1) {
            return "/";
        }

        return "/" + arr[1].split("\\?")[0];
    }
}
