package com.dpa.dpaidauth0backenddemo.services.clients.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionResponseDTO {
  private List<SessionDTO> sessions;
}
