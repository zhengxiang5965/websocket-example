package world.zhx.example.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import world.zhx.example.websocket.config.ChatTextHandler;

@RestController
public class WebSocketController {

    @Autowired
    private ChatTextHandler handler;

    @GetMapping("/open")
    public String open(String clientId){
        TextMessage message = new TextMessage("601");
        handler.sendMessageToClient(clientId,message);
        return "success";
    }

}
