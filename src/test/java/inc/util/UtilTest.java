package inc.util;

import inc.dto.CrackResult;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testCheckMd5(){
//        32 - 127 ASCII chars
        int[] range1 = new int[]{32, 64};
        int[] range2 = new int[]{32, 126};
        int[][] symbolrange = new int[][]{range2};
        Long time = System.currentTimeMillis();
        CrackResult result = Util.checkMd5("68e1c85222192b83c04c0bae564b493d", " ", new String[]{" oe "}, symbolrange);
        time = System.currentTimeMillis() - time;
        double seconds = time / 1000.0;
        double minutes = seconds / 60;

        if(seconds >= 60){
            System.out.println(String.format("Time: %f minutes", minutes));
        } else {
            System.out.println(String.format("Time: %f seconds", seconds));
        }

        assertEquals("koer", result.getResultstring());
    }

    @Test
    public void testMd5(){
        String md5 = Util.md5("koer");//68e1c85222192b83c04c0bae564b493d
        assertEquals("68e1c85222192b83c04c0bae564b493d", md5);
    }

    @Test
    public void testGetStringTemplatesFromRanges(){
        String[] result = Util.getStringTemplatesFromRanges("[\"k??r\", \"aa\"]");
        assertEquals("k??r", result[0]);
        assertEquals("aa", result[1]);
    }


    @Test
    public void testGetKnownComputersFromJson(){
        String[] result = Util.getKnownComputersFromJson("[[\"127.0.0.1\",\"1111\"],[\"127.0.0.1\",\"2222\"],[\"127.0.0.1\",\"3333\"]]");
        assertEquals("127.0.0.1:1111", result[0]);
    }


    @Test
    public void parseArrayToGetParamsTest() {
        String[] params = new String[3];
        params[0] = "param1=val1";
        params[1] = "param2=val2";
        params[2] = "param3=val3";
        String result = Util.parseArrayToGetParams(params);
        assertEquals("correct URL params", "param1=val1&param2=val2&param3=val3", result);
    }


    @Test
    public void testGetRequestFromStringQuery() throws Exception {
        String request = "GET /resource?sendip=55.66.77.88&sendport=6788&ttl=5&id=wqeqwe23&noask=11.22.33.44_345&noask=111.222.333.444_223";
        Map<String, String> result = Util.getRequestFromStringQuery(request);

        assert result != null;
        assertEquals("id param assertion", "wqeqwe23", result.get("id"));
        assertEquals("sendip param assertion", "55.66.77.88", result.get("sendip"));
        assertEquals("sendport param assertion", "6788", result.get("sendport"));
        assertEquals("ttl param assertion", "5", result.get("ttl"));
        assertEquals("noask list", "11.22.33.44_345,111.222.333.444_223", result.get("noask"));
    }

    @Test
    public void testGetHostInUrl() throws Exception {
        String url = "http://www.facebook.com/lalallalal/lalal";
        String result = Util.getHostFromUrl(url);
        assertEquals("facebook.com", result);


        url = "www.facebook.com/aasdasd/asdasd";
        result = Util.getHostFromUrl(url);
        assertEquals("facebook.com", result);


        url = "facebook.com/aasdasd/asdasd";
        result = Util.getHostFromUrl(url);
        assertEquals("facebook.com", result);


        url = "www.facebook.com/";
        result = Util.getHostFromUrl(url);
        assertEquals("facebook.com", result);


        url = "www.facebook.com";
        result = Util.getHostFromUrl(url);
        assertEquals("facebook.com", result);
    }

    @Test
    public void testGetCommandFromInput() throws Exception {
    }

    @Test
    public void testGetHostContext() throws Exception {
        String context = "/";
        assertEquals(context, Util.getRequestContext("localhost"));
        assertEquals(context, Util.getRequestContext("localhost/"));

        context = "/resource";
        assertEquals(context, Util.getRequestContext("localhost/resource"));
    }


    @Test
    public void testParseStringArrayToJson() throws Exception {
        String[] params = new String[4];
        params[0] = "param1=val1";
        params[1] = "param2=val2";
        params[2] = "param3=[\"ax?o?ssss\",\"aa\",\"ab\",\"ac\",\"ad\"]";
        params[3] = "param4=100";
        JSONObject result = Util.parseStringArrayToJson(params);

        JSONObject expected = new JSONObject("{\"param1\":\"val1\", \"param2\":\"val2\", \"param3\":[\"ax?o?ssss\",\"aa\",\"ab\",\"ac\",\"ad\"], \"param4\":100}");
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseArrayToGetParams() throws Exception {

    }

    @Test
    public void testGetCurrentHostIp() throws Exception {

    }

    @Test
    public void testGetRequestFromJson() throws Exception {
        Map<String, String> result = Util.getRequestFromJson(
                "{\"ip\":\"192.168.10.76\", \"port\":\"1111\", \"id\":\"123\", \"md5\":\"hash\", \"ranges\":[\"ax?o?ssss\", \"aa\", \"ab\", \"ab\"], \"wildcard\":\"?\", \"symbolrange\":[[3,10], [100,150]], \"resultstring\":\"result string on selline\"}");

        assertEquals("192.168.10.76", result.get("ip"));
        assertEquals("1111", result.get("port"));
        assertEquals("123", result.get("id"));
        assertEquals("[\"ax?o?ssss\",\"aa\",\"ab\",\"ab\"]", result.get("ranges"));
        assertEquals("[[3,10],[100,150]]", result.get("symbolrange"));
        assertEquals("result string on selline", result.get("resultstring"));
    }

    @Test
    public void testReadJsonFromFile() {
        File newFile = new File("test.txt");
        String containment = "[[\"127.0.0.1\",\"1111\" ],[ \"127.0.0.1\", \"2222\"],[\"127.0.0.1\", \"3333\" ] ]";
        try {
            boolean isFileCreated = newFile.createNewFile();
            if(isFileCreated){
                boolean exists = newFile.exists();
                assertTrue(exists);

                BufferedWriter toFile = new BufferedWriter(new FileWriter(newFile));
                toFile.write(containment);
                toFile.flush();
                toFile.close();

                String result = Util.readJsonFromFile("test.txt");
                assertEquals(containment, result);

                boolean deleted = newFile.delete();
                assertTrue(deleted);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSymbolrange() throws Exception {
        int[][] result = Util.getSymbolrange("[ [10, 100] ,[1, 9], [ 2 , 8]]");
        assertEquals(10, result[0][0]);
        assertEquals(100, result[0][1]);
        assertEquals(1, result[1][0]);
        assertEquals(9, result[1][1]);
        assertEquals(2, result[2][0]);
        assertEquals(8, result[2][1]);
    }
}
