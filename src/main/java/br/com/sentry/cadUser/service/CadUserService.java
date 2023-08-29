package br.com.sentry.cadUser.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.bind.ValidationException;

import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.sentry.cadUser.DTO.CadUserDTO;
import br.com.sentry.cadUser.DTO.LoginDTO;
import br.com.sentry.cadUser.entity.CadUser;
import br.com.sentry.cadUser.repository.CadUserRepository;
import io.sentry.Sentry;
import io.sentry.protocol.User;

@Service
public class CadUserService {

	@Autowired
	private CadUserRepository repository;

	private static final Logger logger = LoggerFactory.getLogger(CadUserService.class);
	
	Random rand = new Random();

	public String hideCpf(String cpf) {
		Pattern pattern = Pattern.compile("(^\\d{3}\\x2E\\d{3}\\x2E\\d{3}\\x2D\\d{2}$)");
		Matcher matcher = pattern.matcher(cpf);
		String cpfMasked = "";
		
		if(matcher.find()) {
			String cpfOnlyNumber = cpf.replaceAll("\\D", "");
			cpfMasked = "***."+cpfOnlyNumber.substring(3, 6)+"."+cpfOnlyNumber.substring(6, 9)+"-**";
		}else {
			cpfMasked = "***."+cpf.substring(3, 6)+"."+cpf.substring(7, 9)+"-**";
		}
		
		return cpfMasked;
	}

	public User genareteUser(String usuario, String userName) {
		User user = new User();
		
		user.setName(usuario);
		user.setEmail("teste@email.com");
		user.setId(String.valueOf(rand.nextInt()));
		user.setUsername(hideCpf(userName));

		return user;
	}
	
	public void saveOrUpdate(CadUserDTO cadUsuarioDto)throws Exception {
		Sentry.setUser(genareteUser(cadUsuarioDto.getUsuario(), cadUsuarioDto.getCpf()));
		try {
			if(!cadUsuarioDto.getSenha().matches("^(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
				throw new ValidationException("invalidPassword");
			}
			
			CadUser userExists = repository.findByLogin(cadUsuarioDto.getLogin());
	
			if(userExists != null) {
				throw new ValidationException("userExist");
			}
			
			CadUser user = new CadUser();
			
			user.setUsuario(cadUsuarioDto.getUsuario());
			user.setLogin(cadUsuarioDto.getLogin());
			user.setSenha(cadUsuarioDto.getSenha());
			user.setCpf(cadUsuarioDto.getCpf());
			user.setDatCadastro(LocalDate.now());
			
			repository.save(user);
			logger.info("Teste usuário criado");

		}catch(Exception e) {
			logger.error("Create: ",e);
			throw new Exception(e);
		}
	}
	
	public String getUrl() {
		return "url";
	}
	
	public CadUser saveOrUpdate(CadUserDTO cadUsuarioDto, String login)throws Exception {
		try {
			Sentry.setUser(genareteUser(cadUsuarioDto.getUsuario(), cadUsuarioDto.getCpf()));
			
			CadUser user = repository.findByLogin(login);
			User userTeste = new User();
			if(user == null) {
				userTeste.setName("Fagner");
				Sentry.setUser(userTeste);
				throw new AccountNotFoundException("LoginNotFound");
			}
			
			if(!cadUsuarioDto.getSenha().matches("^(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,}$")) {
				throw new ValidationException("invalidPassword");
			}
			
			
			user.setUsuario(cadUsuarioDto.getUsuario());
			user.setLogin(cadUsuarioDto.getLogin());
			user.setSenha(cadUsuarioDto.getSenha());
			user.setCpf(cadUsuarioDto.getCpf());
			user.setDatCadastro(LocalDate.now());
			
			return repository.save(user);
		}catch(Exception e) {
			logger.error("Update: ",e);
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
	
	public void login(LoginDTO login)throws NotFoundException, InvalidContentTypeException, Exception {
		CadUser user = repository.findByCpf(login.getLogin());
		Sentry.setUser(genareteUser(user.getCpf(), login.getLogin()));
		
		try {
			if(login.getLogin().isBlank()|| login.getSenha().isBlank()) {
				throw new InvalidContentTypeException();
			}
			
			if(repository.findByCpfAndSenha(login.getLogin(), login.getSenha()) == null) {
				throw new NotFoundException();
			}
			
		}catch(InvalidContentTypeException e) {
			logger.error("Login: ",e);
			throw new InvalidContentTypeException(e.getMessage());
		}catch(NotFoundException e) {
			logger.error("Login: ",e);
			throw new NotFoundException();
		}catch(Exception e) {
			logger.error("Login: ",e);
			throw new Exception(e);
		}
	}
}
