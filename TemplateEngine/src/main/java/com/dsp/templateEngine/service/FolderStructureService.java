package com.dsp.templateEngine.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.dsp.templateEngine.dto.FolderCreateDTO;
import com.dsp.templateEngine.response.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;
@Service
public class FolderStructureService {
        private final static String OKM_ROOT = "/okm:root";
  @Autowired
    private RestTemplate restTemplate;

    @Value("${service.url}")
    private String openKmUrl;

    // private String openKmUrl="http://localhost:8889/dms/openkm";
    // @Autowired
    // private UserUtils utils;
    private static final Logger logger = LoggerFactory.getLogger(FolderStructureService.class);

    @Autowired
    private HttpServletRequest httpRequest;
      public String getTokenFromRequest() {

        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
    
      
      
      public String createDmsFolderStructure (List<String> folders) {

          String path =  OKM_ROOT ;

          String token = getTokenFromRequest();
          if (StringUtils.isBlank(token)) {
              logger.error("Authorization token is missing. Cannot upload file.");
              return "";
          }
          System.out.println("folder " + folders);
          String folderName="";
          for (int i = 0; i < folders.size(); i++) {
              int size = folders.size();
              if(i>0 && !folderName.endsWith("/"))
              {
                  folderName+="/";
              }
              folderName+=folders.get(i);
              try{
                System.out.println("folder "+folderName);
                System.out.println("path "+path);
                  createFolderIfNotExists(path, folderName, token);   
              }
              catch(Exception e)
              {
                  System.out.println("error");
              }
              System.out.println("path " + path);
              System.out.println("folderName " + folderName);  
          }
          System.out.println("complete path "+path+ " createdfolders "+folderName);
          if(path.endsWith("/"))
          return path+""+folderName;
          else
          return path+"/"+folderName;
      }

      private String createFolderIfNotExists(String parentPath, String folderName, String token) {

        System.out.println("parent path " + parentPath);
          if (StringUtils.isBlank(parentPath))
              parentPath = "/okm:root";
          
          String url = openKmUrl + "/folders/create?parentPath=" + parentPath;
          //System.out.println("Hardcoded Final URL: " + url);

          if (token == null || token.isBlank()) {
              throw new IllegalStateException("Authorization token is missing");
          }
          HttpHeaders headers = new HttpHeaders();
          headers.set("Authorization", "Bearer " + token);
          headers.setContentType(MediaType.APPLICATION_JSON);

          FolderCreateDTO dto = FolderCreateDTO.builder()
                  .folderName(folderName)
                  .build();
          
          HttpEntity<FolderCreateDTO> entity = new HttpEntity<>(dto, headers);
          String folderPath = parentPath + "/" + folderName;
          System.out.println("Folder Path: " + folderPath);
          
          try {
              ResponseEntity<GenericResponse<String>> response = restTemplate.exchange(
                      url,
                      HttpMethod.POST,
                      entity,
                      new ParameterizedTypeReference<GenericResponse<String>>() {
                      });
              //System.out.println("response of folder creation "+response);
              GenericResponse<String> responseBody = response.getBody();
              //System.out.println("status " + responseBody);
              if (responseBody != null) {
              System.out.println("status >>> " + responseBody.getStatusCode());
                  if (responseBody.getStatusCode() == 200)
                  {
                      System.out.println("Response: " + responseBody.getData());

                      return responseBody.getData();
                  } 
                  else if (responseBody.getStatusCode() == 409) {
                	  
                      System.out.println("Response" + responseBody.getData());
                      return folderPath;
                  } 
                  else {
                      System.out.println("Unexpected response: " + responseBody.getMessage());
                      return null;
                  }
              } else {
                  System.out.println("Empty response received from folder creation API");
                  return null;
              }

          } catch (HttpClientErrorException.Conflict e) {
              System.out.println("status already exist conflict exception: " + e.getMessage());
            //   e.printStackTrace();
              return folderPath;
          } catch (HttpClientErrorException e) {
              System.out.println("status already exist exception: "  + e.getMessage());
              e.printStackTrace();
              return folderPath;
          } catch (Exception e) {
              e.printStackTrace();
              System.out.println("Error occurred while creating folder: "  + e.getMessage());
              throw e;
              // return folderPath;
          }
      }      
      

}
