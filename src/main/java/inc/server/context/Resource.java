package inc.server.context;

import inc.util.Commands;
import inc.util.Util;

import java.util.Map;

public class Resource implements ServerContext {

    @Override
    public String executeCommand(Map<String, String> request) {
        final Commands commander = new Commands();

        final String toIp = request.get("sendip");
        final String toPort = request.get("sendport");
        final String requestId = request.get("id");
        String ttl = request.get("ttl");
        final String noask = request.get("noask");

        final String[] noaskAddresses = getNoaskAddresses(noask);

        int ttlValue = 0;
        if (!(ttl == null || ttl.equals(""))) {
            ttlValue = Integer.parseInt(ttl);
            ttlValue--;
        }


        final String sendip = Util.getCurrentIp();
        final int port = commander.getServer().getPort();
        final int resource = commander.isWorking() ? 0 : 100;
        new Thread(new Runnable() {
            @Override
            public void run() {
                commander.sendRequest(
                        "POST", String.format("%s:%s/resourcereply", toIp, toPort),
                        String.format("ip=%s", sendip),
                        String.format("port=%s", port),
                        String.format("id=%s", requestId),
                        String.format("resource=%s", resource)
                );
            }
        }).start();


        if (ttlValue > 0 && Commands.computers != null) {
            final int inThreadTtlValue = ttlValue;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    int bonusLength = 0;
                    if (noaskAddresses != null) {
                        bonusLength = noaskAddresses.length;
                    }
                    final String[] allParamsForResourceRequest = new String[4 + bonusLength + 1];
                    allParamsForResourceRequest[0] = String.format("sendip=%s", toIp);
                    allParamsForResourceRequest[1] = String.format("sendport=%s", toPort);
                    allParamsForResourceRequest[2] = String.format("ttl=%s", inThreadTtlValue);
                    allParamsForResourceRequest[3] = String.format("id=%s", requestId);
                    populateNoaskParams(noask, allParamsForResourceRequest, 4);
                    allParamsForResourceRequest[allParamsForResourceRequest.length - 1] = "noask=" + sendip + "_" + port;

                    for (int i = 0; i < Commands.computers.length; i++) {
                        final String curent = Commands.computers[i];
                        if (validateAddress(curent, noaskAddresses)) {
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    commander.sendRequest("GET", String.format("%s/resource", curent),
                                            allParamsForResourceRequest);
                                }
                            }).start();

                        } else {
                            System.out.println(String.format("! noask %s -> do not send here", curent));
                        }
                    }
                }
            }).start();
        }

        return String.valueOf(ServerContext.OK_CODE);
    }

    protected void populateNoaskParams(String noask, String[] allParamsForResourceRequest, int i) {

        if (noask == null) {
            return;
        }

        String[] noaskParams = noask.split(",");

        for (String noaskParam : noaskParams) {
            allParamsForResourceRequest[i++] = "noask=" + noaskParam;
        }
    }

    protected boolean validateAddress(String address, String[] noAvailableAddresses) {

        if (noAvailableAddresses == null) {
            return true;
        }

        for (String s : noAvailableAddresses) {
            if (s.equals(address)) {
                return false;
            }
        }

        return true;
    }

    private String[] getNoaskAddresses(String noaskListCommaSeparated) {
        if (noaskListCommaSeparated == null) {
            return null;
        }

        String[] list = noaskListCommaSeparated.split(",");
        String[] result = new String[list.length];

        for (int i = 0; i < list.length; i++) {
            String[] ipPortPair = list[i].split("_");
            result[i] = ipPortPair[0] + ":" + ipPortPair[1];
        }

        return result;
    }
}
