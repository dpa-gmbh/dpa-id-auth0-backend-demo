package com.dpa.dpaidauth0backenddemo.services.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionDTO {
  private String user_id;
  private String expires_at;
  private String idle_expires_at;
  private List<ClientDTO> clients;
}
