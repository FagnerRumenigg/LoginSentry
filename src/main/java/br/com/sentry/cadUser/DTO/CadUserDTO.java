package br.com.sentry.cadUser.DTO;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter @Setter
public class CadUserDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String usuario;
	
	private String login;
	
	private String senha;

	private String cpf;
	
}
