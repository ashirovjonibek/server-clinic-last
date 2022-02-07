package uz.napa.clinic.service.iml.helper;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uz.napa.clinic.payload.ResTokenSms;
import uz.napa.clinic.repository.EskizRepository;

public class SmsSender {
    private final
    EskizRepository repository;

    public SmsSender(EskizRepository repository) {
        this.repository = repository;
    }

    public static void sendSms(String phone,String message){
        String url = "http://notify.eskiz.uz/api/auth/login";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> data= new LinkedMultiValueMap<String, String>();
        data.add ("email", "admin@proacademy.uz");
        data.add("password", "yCK9mG74RkTm5hSFvhoRwCuPRq5eciVhF8F7DNNB");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(data, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        System.out.println(response.getBody());
        String body = response.getBody();
        ResTokenSms resTokenSms=new ResTokenSms();

        resTokenSms.setMessage(body.substring(body.indexOf(":")+2,body.indexOf(",")-1));
        resTokenSms.setData(body.substring(body.indexOf("{\"token\":")+10,body.indexOf("}")-1));


        System.out.println(resTokenSms.toString());


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
        }catch (IllegalStateException e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
