package br.com.sentry.cadUser.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.sentry.enums.UserRoleEnum;
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
@Entity
@Table(name="cadusuarios")
public class CadUser implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idcadusuario")
	private Integer idUsuario;
	
	@Column(name="dcr_usuario")
	private String usuario;
	
	@Column(name="dcr_login")
	private String login;
	
	@Column(name="dcr_senha")
	private String senha;

	@Column(name="cpf")
	private String cpf;

	@Column(name="dat_cadastro")
	private LocalDate datCadastro;
	
	
	private UserRoleEnum role;
}
