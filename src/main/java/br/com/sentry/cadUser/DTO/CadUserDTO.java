package br.com.sentry.cadUser.DTO;

import java.io.Serializable;
import java.time.LocalDate;

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
public class CadUserDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private Integer idCadUsuario;
	
	@Getter @Setter
	private String dcrUsuario;
	
	@Getter @Setter
	private String dcrLogin;
	
	@Getter @Setter
	private String dcrSenha;

	@Getter @Setter
	private LocalDate datCadastro;
	
	@Getter @Setter
	private LocalDate datDesativacao;
	
	@Getter @Setter
	private Boolean isDisabled;
}
