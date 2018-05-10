package edu.utep.cs.cs4330.notebookio.fileIo;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * @author: Jesus Chavez
 * @macuser: aex on 5/1/18.
 */
public class NotebookRTS extends Application{
    private Socket socket;{
        try {
            socket = IO.socket("http://172.20.10.2:3000/");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
