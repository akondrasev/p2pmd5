package inc.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Before
    public void setUp(){

    }

    @Test
    public void parseArrayToGetParamsTest(){
        String[] params = new String[3];
        params[0] = "param1=val1";
        params[1] = "param2=val2";
        params[2] = "param3=val3";
        String result = Util.parseArrayToGetParams(params);
        assertEquals("correct URL params", "param1=val1&param2=val2&param3=val3", result);
    }

    @Test
    public void testParseArrayToPostJson(){
        String[] params = new String[3];
        params[0] = "param1=val1";
        params[1] = "param2=val2";
        params[2] = "param3=[\"ax?o?ssss\",\"aa\",\"ab\",\"ac\",\"ad\"]";
        String result = Util.parseStringArrayToJson(params);
        assertEquals("correct URL params", "{\"param1\":\"val1\", \"param2\":\"val2\", \"param3\":[\"ax?o?ssss\",\"aa\",\"ab\",\"ac\",\"ad\"]}", result);
    }

    @Test
    public void testParseGetRequest() throws Exception {
        String request = "GET /resource?sendip=55.66.77.88&sendport=6788&ttl=5&id=wqeqwe23&noask=11.22.33.44_345&noask=111.222.333.444_223";
        Map<String, Object> result = Util.parseGetRequest(request);

        assertEquals("id param assertion","wqeqwe23", result.get("id"));
        assertEquals("sendip param assertion","55.66.77.88", result.get("sendip"));
        assertEquals("sendport param assertion","6788", result.get("sendport"));
        assertEquals("ttl param assertion","5", result.get("ttl"));
    }

    @Test
    public void testGetHostInUrl() throws Exception {
        String url = "http://www.facebook.com/lalallalal/lalal";
        String result = Util.getHostInUrl(url);
        assertEquals("facebook.com", result);


        url = "www.facebook.com/aasdasd/asdasd";
        result = Util.getHostInUrl(url);
        assertEquals("facebook.com", result);


        url = "facebook.com/aasdasd/asdasd";
        result = Util.getHostInUrl(url);
        assertEquals("facebook.com", result);


        url = "www.facebook.com/";
        result = Util.getHostInUrl(url);
        assertEquals("facebook.com", result);



        url = "www.facebook.com";
        result = Util.getHostInUrl(url);
        assertEquals("facebook.com", result);
    }

    @Test
    public void testGetCommandFromInput() throws Exception {
    }

    @Test
    public void testGetParamsFromInput() throws Exception {
        String input = "send post 192.168.10.101:1111/checkmd5 sendip=192.168.10.101 port=1111 id=asdsad md5=md5 ranges=[\"ax?o?ssss\", \"aa\", \"ab\", \"ab\"] wildcard=? symbolrange=[[3,10], [100,150]]";
        String[] result = Util.getParamsFromInput(input);

        assertEquals(9, result.length);
    }

    @Test
    public void testGetHostContext() throws Exception {
        String context = "/";
        assertEquals(context, Util.getHostContext("localhost"));
        assertEquals(context, Util.getHostContext("localhost/"));

        context = "/resource";
        assertEquals(context, Util.getHostContext("localhost/resource"));
    }


    @Test
    public void testParseStringArrayToJson() throws Exception {

    }

    @Test
    public void testParseArrayToGetParams() throws Exception {

    }

    @Test
    public void testGetCurrentHostIp() throws Exception {

    }

    @Test
    public void testGetRequestParamsFromJson() throws Exception {
        Map<String, Object> result = Util.getRequestParamsFromJson("{\"ip\":\"55.66.77.88\", \"port\":\"6788\", \"id\": \"asasasas\", \"resource\": 100, \"ranges\":[\"ax?o?ssss\",\"aa\",\"ab\",\"ac\",\"ad\"] }");
        assertEquals("55.66.77.88", result.get("ip"));
        assertEquals("6788", result.get("port"));
        assertEquals("asasasas", result.get("id"));
        assertEquals(100.0, result.get("resource"));

        ArrayList<String> rangesList = new ArrayList<>();
        rangesList.add("ax?o?ssss");
        rangesList.add("aa");
        rangesList.add("ab");
        rangesList.add("ac");
        rangesList.add("ad");
        assertEquals(rangesList, result.get("ranges"));
    }
}