package it.florence.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String indirizzo;
    private LocalDate dataNascita;

}
