package com.kevin.aws_demo.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@RestController
@RequestMapping("secret")
public class SecretController {

  @GetMapping
  public String getSecret() throws URISyntaxException {
    String secretName = "/dev/test";
    Region region = Region.of("us-west-2");

    // Create a Secrets Manager client
    SecretsManagerClient client = SecretsManagerClient.builder()
        .region(region)
        .build();

    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
        .secretId(secretName)
        .build();

    GetSecretValueResponse getSecretValueResponse;

    try {
      getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
    } catch (Exception e) {
      // For a list of exceptions thrown, see
      // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
      throw e;
    }

    if (getSecretValueResponse != null){
      String secret = getSecretValueResponse.secretString();
      Map<String, String> map = JSON.parseObject(secret, new TypeReference<Map<String, String>>(){});

      String nestJsonValue = map.get("nest");
      Map<String, String> map2 = JSON.parseObject(nestJsonValue, new TypeReference<Map<String, String>>(){});

      map.forEach((k, v) -> System.out.println(k + ":" + v));
      System.out.println("=======");
      map2.forEach((k, v) -> System.out.println(k + ":" + v));
      System.out.println("=======");
      return map.toString();
    }
    return "error";
  }
}
