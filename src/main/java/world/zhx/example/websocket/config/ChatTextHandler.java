package world.zhx.example.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChatTextHandler extends TextWebSocketHandler {


    //在线客户端列表
    private static final Map<String, WebSocketSession> clients;

    //客户端标识
    private static final String CLIENT_ID = "clientId";

    static {
        clients = new ConcurrentHashMap<>();
    }


    /**
     * 获取用户标识
     * @param session
     * @return
     */
    private String getClientId(WebSocketSession session) {
        try {
            String clientId = String.valueOf(session.getAttributes().get(CLIENT_ID));
            return clientId;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        log.info("成功建立连接");
        String clientId = getClientId(session);
        log.info("当前连接的client为："+clientId);
        if (clientId != null) {
            clients.put(clientId, session);
            session.sendMessage(new TextMessage("成功建立socket连接"));
            log.info("当前连接的对话session为："+session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {
        log.info("message.getPayload："+message.getPayload());
        WebSocketMessage message1 = new TextMessage("你好："+message.getPayload());
        try {
            session.sendMessage(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) throws Exception {
        log.info("连接已关闭：" + status);
        clients.remove(getClientId(session));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        log.info("连接出错");
        clients.remove(getClientId(session));
    }




    /**
     * 发送信息给指定用户
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToClient(String clientId, TextMessage message) {
        if (clients.get(clientId) == null) return false;
        WebSocketSession session = clients.get(clientId);
        log.info("消息接收方会话:" + session);
        if (!session.isOpen()) return false;
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     * @param message
     * @return
     */
    public boolean sendMessageToAllClients(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = clients.keySet();
        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                session = clients.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return  allSendSuccess;
    }
}