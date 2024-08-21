package com.dpa.dpaidauth0backenddemo.services.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccessTokenPayloadDTO {
  private String iss;
  private String sub;
  private List<String> aud;
  private Long iat;
  private Long exp;
  private String scope;
  private String azp;
}
