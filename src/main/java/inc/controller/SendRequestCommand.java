package inc.controller;

import inc.util.Commands;

public class SendRequestCommand implements Command {
    @Override
    public void execute(String... params) {
        if (params.length < 2){
            System.out.println("params missed");
            return;
        }

        if (params.length >= 3){
            String[] urlParams = new String[params.length - 2];

            for(int i = 2; i < params.length; i++){
                urlParams[i-2] = params[i];
            }

            new Commands().sendRequest(params[0], params[1], urlParams);
            return;
        }
        new Commands().sendRequest(params[0], params[1]);
    }
}
