package com.kevin.aws_demo.controller;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SECRETSMANAGER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MockAwsSecretControllerTest {
  private static final String SECRET_NAME = "/dev/test";
  private static final String SECRET_VALUE = "{\"password\": \"chivalry23\"}";
  private static final DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:latest");
  @ClassRule
  public static LocalStackContainer localstack = new LocalStackContainer(localstackImage).withServices(SECRETSMANAGER);
  private static SecretsManagerClient client;

  @Before
  public void before() {
    localstack.start();
    client = SecretsManagerClient.builder()
        .region(Region.US_EAST_1)
        .endpointOverride(localstack.getEndpointOverride(SECRETSMANAGER))
        .build();
    CreateSecretRequest build = CreateSecretRequest.builder()
        .name(SECRET_NAME)
        .secretString(SECRET_VALUE).build();
    client.createSecret(build);
  }

  @After
  public void after() {
    localstack.stop();
  }


  @Test
  public void testGetSecretValueFromAwsSecretsManager(){
    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
        .secretId(SECRET_NAME)
        .build();

    GetSecretValueResponse getSecretValueResponse;

    getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
    String expectSecretsValue = getSecretValueResponse.secretString();
    Assert.assertEquals(expectSecretsValue, SECRET_VALUE);
  }

  @Test
  public void testGetPasswordFromAwsSecretsManager() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(SECRET_VALUE);
    JsonNode actualValue = jsonNode.get("password");

    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
        .secretId(SECRET_NAME)
        .build();

    GetSecretValueResponse getSecretValueResponse;

    getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
    String secretsValue = getSecretValueResponse.secretString();

    jsonNode = objectMapper.readTree(secretsValue);
    JsonNode expectValue = jsonNode.get("password");

    Assert.assertEquals(expectValue, actualValue);
  }

}
