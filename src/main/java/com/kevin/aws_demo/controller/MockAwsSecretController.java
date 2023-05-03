package com.kevin.aws_demo.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@RestController
@RequestMapping("localstack/secret")
public class MockAwsSecretController {

  private final String secretName = "/dev/test";
  private final Region region = Region.of("us-west-2");
  private final String url = "http://localhost:4566";


  @GetMapping("post")
  public void createMockSecret() throws URISyntaxException {
    SecretsManagerClient client = SecretsManagerClient.builder()
        .endpointOverride(new URI(url))
        .region(region)
        .build();
    createMockSecretData(client);
  }

  @GetMapping
  public String getSecret() throws URISyntaxException {

    SecretsManagerClient client = SecretsManagerClient.builder()
        .endpointOverride(new URI(url))
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
      Map<String, String> map = JSON.parseObject(secret, new TypeReference<>(){});
      return map.toString();
    }
    return "error";
  }

  private void createMockSecretData(SecretsManagerClient client) {
    CreateSecretRequest build = CreateSecretRequest.builder()
        .name(secretName)
        .secretString("{\"password\": \"chivalry23\"}").build();
    client.createSecret(build);
  }

}
