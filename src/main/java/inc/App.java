package inc;

//TODO simple UI / cmd implementation
//TODO reading config file 'machines.txt' and writing it to global properties
//TODO send get with url params, post with json

import inc.server.Server;

public class App {
    public static void main(String[] args) {
        Server server = new Server();
        server.start(1111);
    }
}
