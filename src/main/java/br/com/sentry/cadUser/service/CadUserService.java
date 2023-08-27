package br.com.sentry.cadUser.service;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.bind.ValidationException;

import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
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
	
	public String hideCpf(String cpf) {
		Pattern pattern = Pattern.compile("(^\\d{3}\\x2E\\d{3}\\x2E\\d{3}\\x2D\\d{2}$)");
		Matcher matcher = pattern.matcher(cpf);
		String cpfMasked = "";
		if(matcher.find()) {
			String cpfOnlyNumber = cpf.replaceAll("\\D", "");
			cpfMasked = "###."+cpfOnlyNumber.substring(3, 6)+"."+cpfOnlyNumber.substring(7, 9)+"-##";
		}else {
			cpfMasked = "###."+cpf.substring(3, 6)+"."+cpf.substring(7, 9)+"-##";
		}
		return cpfMasked;
	}

	
	public User genareteUser() {
		User user = new User();
		user.setName("Fagner");
		user.setEmail("teste@email.com");
		user.setId("1");
		user.setUsername(hideCpf("893.690.290-36"));
		
		return user;
	}
	
	public void saveOrUpdate(CadUserDTO cadUsuarioDto)throws Exception {
		try {
			Sentry.setUser(genareteUser());

			if(!cadUsuarioDto.getSenha().matches("^(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
				throw new ValidationException("invalidPassword");
			}
			CadUser userExists = repository.findByLogin(cadUsuarioDto.getLogin());
	
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
			
			user.setUsuario(cadUsuarioDto.getUsuario());
			user.setLogin(cadUsuarioDto.getLogin());
			user.setSenha(cadUsuarioDto.getSenha());
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
			Sentry.setUser(genareteUser());
			
			CadUser user = repository.findByLogin(login);
			User userTeste = new User();
			if(user == null) {
				userTeste.setName("Fagner");
				Sentry.setUser(userTeste);
				Sentry.captureMessage("Não foi possível atualizar, login não encontrado: "+ login);
				throw new AccountNotFoundException("LoginNotFound");
			}
			
			if(!cadUsuarioDto.getSenha().matches("^(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
				throw new ValidationException("invalidPassword");
			}
			
			Boolean isDisabled = cadUsuarioDto.getIsDisabled();
			LocalDate disabledDate;
			
			if(isDisabled == null || !isDisabled) {
				disabledDate = null;
			}else {
				disabledDate = LocalDate.now();
			}
			
			user.setUsuario(cadUsuarioDto.getUsuario());
			user.setLogin(cadUsuarioDto.getLogin());
			user.setSenha(cadUsuarioDto.getSenha());
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
		return repository.findAll(Sort.by("idUsuario"));
	}
	
	
	public void deleteUser(Integer id) {
		repository.deleteById(id);;
	}
	
	public void login(String login, String password)throws NotFoundException, InvalidContentTypeException, Exception {
		try {
			Sentry.setUser(genareteUser());

			if(login.isBlank()|| password.isBlank()) {
				throw new InvalidContentTypeException();
			}
			if(repository.findByLoginAndSenha(login, password) == null) {
				throw new NotFoundException();
			}
			
		}catch(InvalidContentTypeException e) {
			Sentry.captureException(e);
			throw new InvalidContentTypeException(e.getMessage());
		}catch(NotFoundException e) {
			Sentry.captureException(e);
			throw new NotFoundException();
		}catch(Exception e) {
			Sentry.captureException(e);
			throw new Exception(e);
		}
	}
}
