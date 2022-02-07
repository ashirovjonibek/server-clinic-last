//package uz.napa.clinic.controller;
//
//import com.corundumstudio.socketio.HandshakeData;
//import com.corundumstudio.socketio.SocketIOClient;
//import com.corundumstudio.socketio.SocketIONamespace;
//import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.listener.ConnectListener;
//import com.corundumstudio.socketio.listener.DataListener;
//import com.corundumstudio.socketio.listener.DisconnectListener;
//import com.corundumstudio.socketio.protocol.Packet;
//import com.corundumstudio.socketio.protocol.PacketType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import uz.napa.clinic.entity.User;
//import uz.napa.clinic.payload.ApiResponse;
//import uz.napa.clinic.payload.Message;
//import uz.napa.clinic.payload.MessageHelper;
//import uz.napa.clinic.payload.UserResponseForMessage;
//import uz.napa.clinic.repository.UserRepository;
//import uz.napa.clinic.security.JwtTokenProvider;
//import uz.napa.clinic.service.MessageCenterService;
//import uz.napa.clinic.service.iml.CustomUserDetailsService;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class ChatModule {
//    private String token = "chat";
//
//    @Autowired
//    JwtTokenProvider tokenProvider;
//
//    @Autowired
//    CustomUserDetailsService userDetailsService;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    MessageCenterService messageCenterService;
//
//    private static final Logger log = LoggerFactory.getLogger(ChatModule.class);
//
//
//    private final SocketIONamespace namespace;
//
//    @Autowired
//    public ChatModule(SocketIOServer server) {
//
//        this.namespace = server.addNamespace("/chat");
//        this.namespace.addConnectListener(onConnected());
//        this.namespace.addDisconnectListener(onDisconnected());
//        this.namespace.addEventListener("all-messages", ApiResponse.class, getAllChats());
//    }
//
////    private DataListener<Typing> onTyping() {
////        return (client,data,askSender)->{
////            log.debug("Client[{}] -Received chat typing {} {} askSender {}",client.getSessionId().toString(),data.isStatus(),data.getStatusMessage(),client.getAllRooms().toString());
////            namespace.getBroadcastOperations().sendEvent("typing",data);
////        };
////    }
//
//    private DataListener<MessageHelper> onChatReceived() {
//        return (client, data, ackSender) -> {
//            log.debug("Client[{}] - Received chat message '{}'", client.getSessionId().toString(), data);
//            String token = client.getHandshakeData().getSingleUrlParam("token").substring(6);
//            User user = getUserFromToken(token);
//            ApiResponse apiResponse = messageCenterService.saveMessage(
//                    user,
//                    null,
//                    data.getMessageId() != null ? data.getMessageId().toString() : null,
//                    user.getId(),
//                    data.getToId() != null ? data.getToId().toString() : null,
//                    data.getMessage(),
//                    data.getChatId() != null ? data.getChatId().toString() : null
//            );
//            System.out.println("this is a room "+namespace.getRoomOperations(user.getId().toString()));
//            namespace.getBroadcastOperations().sendEvent(data.getChatId().toString(), apiResponse);
//        };
//    }
//
//    private DataListener<ApiResponse> getAllChats() {
//        return (client, data, askSender) -> {
//            HandshakeData handshakeData = client.getHandshakeData();
//            String token = handshakeData.getSingleUrlParam("token").substring(6);
//            User user = getUserFromToken(token);
//            namespace.getBroadcastOperations().sendEvent("all-messages", messageCenterService.userMessages(user));
//        };
//    }
//
//    private ConnectListener onConnected() {
//        return (client) -> {
//            HandshakeData handshakeData = client.getHandshakeData();
//            String token = handshakeData.getSingleUrlParam("token").substring(6);
//            User user = getUserFromToken(token);
//            log.debug("Client[{}] - Connected to chat module through '{}'", client.getSessionId().toString(), handshakeData.getUrl());
//            ApiResponse apiResponse = messageCenterService.userMessages(user);
//            List<UserResponseForMessage> object = (List<UserResponseForMessage>) apiResponse.getObject();
//            if (object.size() > 0) {
//                object.stream().forEach(chat -> {
//                    this.namespace.addEventListener(chat.getChatId().toString(), MessageHelper.class, onChatReceived());
//                });
//            }
//            namespace.getBroadcastOperations().sendEvent("all-messages", apiResponse);
//        };
//    }
//
//    private DisconnectListener onDisconnected() {
//        return client -> {
//            log.debug("Client[{}] - Disconnected from chat module.", client.getSessionId().toString());
//        };
//    }
//
//
//    private User getUserFromToken(String token) {
//        String id = tokenProvider.getUserIdFromJWT(token);
//        return userRepository.findById(UUID.fromString(id)).get();
//    }
//
//
//}
