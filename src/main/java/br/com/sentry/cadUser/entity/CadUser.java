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
@Entity
@Table(name="cadusuarios")
public class CadUser implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Getter @Setter
	@Column(name="idcadusuario")
	private Integer idUsuario;
	
	@Getter @Setter
	@Column(name="dcr_usuario")
	private String usuario;
	
	@Getter @Setter
	@Column(name="dcr_login")
	private String login;
	
	@Getter @Setter
	@Column(name="dcr_senha")
	private String senha;

	@Getter @Setter
	@Column(name="dat_cadastro")
	private LocalDate datCadastro;
	
	@Getter @Setter
	@Column(name="dat_desativacao")
	private LocalDate datDesativacao;
	
	private UserRoleEnum role;
}
