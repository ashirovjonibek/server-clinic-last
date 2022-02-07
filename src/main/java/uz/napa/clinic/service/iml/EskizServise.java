package uz.napa.clinic.service.iml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uz.napa.clinic.entity.EskizToken;
import uz.napa.clinic.payload.ResTokenSms;
import uz.napa.clinic.repository.EskizRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class EskizServise {
    @Autowired
    EskizRepository eskizRepository;

    public ResTokenSms getToken() {
        ResTokenSms resTokenSms = new ResTokenSms();
        List<EskizToken> all = eskizRepository.findAll();
        System.out.println(all.size());
        if (all.size() != 0) {
            EskizToken eskizToken = all.get(0);
            if (checkTokenTime(eskizToken)){
                String url = "http://notify.eskiz.uz/api/auth/login";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
                data.add("email", "admin@proacademy.uz");
                data.add("password", "yCK9mG74RkTm5hSFvhoRwCuPRq5eciVhF8F7DNNB");
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(data, headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                System.out.println(response.getBody());
                String body = response.getBody();

                resTokenSms.setMessage(body.substring(body.indexOf(":") + 2, body.indexOf(",") - 1));
                resTokenSms.setData(body.substring(body.indexOf("{\"token\":") + 10, body.indexOf("}") - 1));
                eskizToken.setToken(resTokenSms.getData());
                eskizToken.setMessage(resTokenSms.getMessage());
                eskizToken.setCreatedAt(new Timestamp(new Date().getTime()));
                eskizRepository.save(eskizToken);
            }else {
                resTokenSms.setMessage(eskizToken.getMessage());
                resTokenSms.setData(eskizToken.getToken());
            }

        }else {
            String url = "http://notify.eskiz.uz/api/auth/login";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
            data.add("email", "admin@proacademy.uz");
            data.add("password", "yCK9mG74RkTm5hSFvhoRwCuPRq5eciVhF8F7DNNB");
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(data, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            System.out.println(response.getBody());
            String body = response.getBody();

            resTokenSms.setMessage(body.substring(body.indexOf(":") + 2, body.indexOf(",") - 1));
            resTokenSms.setData(body.substring(body.indexOf("{\"token\":") + 10, body.indexOf("}") - 1));
            EskizToken eskizToken=new EskizToken();
            eskizToken.setToken(resTokenSms.getData());
            eskizToken.setMessage(resTokenSms.getMessage());
            eskizToken.setCreatedAt(new Timestamp(new Date().getTime()));
            eskizRepository.save(eskizToken);
        }

        return resTokenSms;
    }

    public String sendSms(String phone,String message){

        ResTokenSms resTokenSms=getToken();

        String url1= "http://notify.eskiz.uz/api/message/sms/send";
        RestTemplate restTemplate1= new RestTemplate();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers1.add("Authorization","Bearer "+resTokenSms.getData());
        MultiValueMap<String, String> data1= new LinkedMultiValueMap<String, String>();
        data1.add ("mobile_phone", phone.substring(1));
        data1.add("message", message);
        data1.add("from", "4546");
        HttpEntity<MultiValueMap<String, String>> requestEntity1 = new HttpEntity<MultiValueMap<String, String>>(data1, headers1);
        try {
            ResponseEntity<String> exchange = restTemplate1.exchange(url1, HttpMethod.POST, requestEntity1, String.class);
            System.out.println(exchange);
            return "sms send successfully";
        }catch (IllegalStateException e){
            System.out.println(e);
            e.printStackTrace();
            return "Sms not send";
        }
    }

    private boolean checkTokenTime(EskizToken eskizToken){
        Timestamp date=new Timestamp(new Date().getTime());
        long l = date.getTime() - eskizToken.getCreatedAt().getTime();
        return l<5;
    }
}
