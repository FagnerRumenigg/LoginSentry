package br.com.sentry.cadUser.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.bind.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.sentry.cadUser.DTO.CadUserDTO;
import br.com.sentry.cadUser.entity.CadUser;
import br.com.sentry.cadUser.repository.CadUserRepository;
import io.sentry.Sentry;
import io.sentry.protocol.User;

@Service
public class CadUserService {

	@Autowired
	private CadUserRepository repository;
	
	
	public String encodePassword(String password) {
		 int strength = 10; // work factor of bcrypt
		 BCryptPasswordEncoder bCryptPasswordEncoder =
		  new BCryptPasswordEncoder(strength, new SecureRandom());
		 
		 return bCryptPasswordEncoder.encode(password);
	}
	
	public void saveOrUpdate(CadUserDTO cadUsuarioDto)throws Exception {
		try {
			if(!cadUsuarioDto.getDcrSenha().matches("^(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
				throw new ValidationException("invalidPassword");
			}

			CadUser userExists = repository.findByLogin(cadUsuarioDto.getDcrLogin());
	
			if(userExists != null) {
				throw new ValidationException("UserExist");
			}
			
			Boolean isDisabled = cadUsuarioDto.getIsDisabled();
			LocalDate disabledDate;
			
			if(isDisabled == null || !isDisabled) {
				disabledDate = null;
			}else {
				disabledDate = LocalDate.now();
			}
			
			CadUser user = new CadUser();
			
			user.setUsuario(cadUsuarioDto.getDcrUsuario());
			user.setLogin(cadUsuarioDto.getDcrLogin());
			user.setSenha(cadUsuarioDto.getDcrSenha());
			user.setDatCadastro(LocalDate.now());
			user.setDatDesativacao(disabledDate);
			
			 repository.save(user);
		}catch(Exception e) {
			Sentry.captureException(e);
			throw new Exception(e);
		}
	}
	
	public CadUser saveOrUpdate(CadUserDTO cadUsuarioDto, String login)throws Exception {
		try {
			CadUser user = repository.findByLogin(login);
			User userTeste = new User();
			if(user == null) {
				userTeste.setName("Fagner");
				Sentry.setUser(userTeste);
				Sentry.captureMessage("Não foi possível atualizar, login não encontrado: "+ login);
				throw new AccountNotFoundException("LoginNotFound");
			}
			
			if(!cadUsuarioDto.getDcrSenha().matches("^(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
				throw new ValidationException("invalidPassword");
			}
			
			Boolean isDisabled = cadUsuarioDto.getIsDisabled();
			LocalDate disabledDate;
			
			if(isDisabled == null || !isDisabled) {
				disabledDate = null;
			}else {
				disabledDate = LocalDate.now();
			}
			
			user.setUsuario(cadUsuarioDto.getDcrUsuario());
			user.setLogin(cadUsuarioDto.getDcrLogin());
			user.setSenha(encodePassword(cadUsuarioDto.getDcrSenha()));
			user.setDatCadastro(LocalDate.now());
			user.setDatDesativacao(disabledDate);
			
			return repository.save(user);
		}catch(Exception e) {
			Sentry.captureException(e);
			throw new Exception(e);
		}
	}
	
	public List<CadUser> findAll(){
		repository.flush();
		return repository.findAll(Sort.by("idCadUsuario"));
	}
	
	
	public void deleteUser(Integer id) {
		repository.deleteById(id);;
	}

}
