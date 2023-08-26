package br.com.sentry.cadUser.controller;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sentry.cadUser.DTO.CadUserDTO;
import br.com.sentry.cadUser.entity.CadUser;
import br.com.sentry.cadUser.service.CadUserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class CadUserController {
	
	private final CadUserService service;
	
	@GetMapping
	public ResponseEntity<List<CadUser>> findAll(){
		return ResponseEntity.ok().body(service.findAll());
	}

	@PostMapping
	public ResponseEntity<String> save(@RequestBody CadUserDTO cadUsuarioDto) {
		try {
			System.out.println("caiu ");
			service.saveOrUpdate(cadUsuarioDto);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}catch(ValidationException e) {
			System.out.println("UE? ");
			if(e.getMessage().contains("invalidPassword")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
			}else if(e.getMessage().contains("UserExist")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());				
			}else {
				return ResponseEntity.internalServerError().body("Erro inesperado"); 
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage()); 
		}
	}
	
	@PutMapping("/{login}")
	public ResponseEntity<CadUser> updateUsuario(@PathVariable String login, @RequestBody CadUserDTO cadUsuarioDto){
		try {
			return ResponseEntity.ok().body(service.saveOrUpdate(cadUsuarioDto, login));		
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build(); 
		}
	
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUsuario(@PathVariable Integer id) {
		service.deleteUser(id);
		return ResponseEntity.ok().body("Deletado");
	}
}
