package uz.napa.clinic.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.napa.clinic.entity.User;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseForMessage {

    private UUID id;

    private String image;

    private String fullName;

    private int count;

    private UUID chatId;

    public static UserResponseForMessage response(User user,int count,UUID chatId,UUID id){
        UserResponseForMessage response=new UserResponseForMessage();
        response.setCount(count);
        response.setFullName(user.getFullName());
        response.setId(user.getId());
        response.setImage(user.getAttachment()!=null?String.format("%s%s","/attach/",user.getAttachment().getId()):null);
        response.setChatId(chatId);
        return response;
    }

}
