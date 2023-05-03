package com.kevin.aws_demo.controller;


import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.aws_demo.modal.Employee;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest")
public class RestEasyController {

  private final static String EXTERNAL_URL = "http://localhost:8080/external";


  @GetMapping("name/{name}")
  public ResponseEntity<Employee> request(@PathVariable("name") String name) throws URISyntaxException {
    System.out.println("GET !!!");
    ClientBuilder clientBuilder = ResteasyClientBuilder.newBuilder();
    ResteasyClient client = (ResteasyClient)clientBuilder.build();
    MultivaluedMap<String, Object> map = new MultivaluedHashMap() {};
    map.putSingle("name", name);

    ResteasyWebTarget resteasyWebTarget = client.target(new URI(EXTERNAL_URL)).queryParams(map);
    Response response = resteasyWebTarget.request().get();
    if (response.getStatus() != 200) {
      return ResponseEntity.notFound().build();
    }

    String s = response.readEntity(String.class);
    Employee employee = JSON.parseObject(s, Employee.class);
    response.close();
    return ResponseEntity.ok(employee);
  }

  @PostMapping("name")
  public ResponseEntity<String> request2(@RequestParam("name") String name) throws URISyntaxException, JsonProcessingException {
    System.out.println("POST !!!");
    ClientBuilder clientBuilder = ResteasyClientBuilder.newBuilder();
    ResteasyClient client = (ResteasyClient)clientBuilder.build();
    MultivaluedMap<String, Object> map = new MultivaluedHashMap() {};
    map.putSingle("name", name);

    ResteasyWebTarget resteasyWebTarget = client.target(new URI(EXTERNAL_URL)).queryParams(map);
    Response response = resteasyWebTarget.request().post(null);
    if (response.getStatus() != 200) {
      return ResponseEntity.notFound().build();
    }

    ObjectMapper o = new ObjectMapper();
    String s = response.readEntity(String.class);
    Map<String, String> mm = o.readValue(s, Map.class);
    String accessToken = mm.get("access_token");
    Employee employee = JSON.parseObject(s, Employee.class);
    response.close();
    return ResponseEntity.ok(accessToken);
  }

}
