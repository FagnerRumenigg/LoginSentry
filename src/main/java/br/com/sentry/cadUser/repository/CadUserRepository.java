package br.com.sentry.cadUser.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import br.com.sentry.cadUser.entity.CadUser;

@Repository
public interface CadUserRepository extends JpaRepository<CadUser,Integer>{

	CadUser findByLogin(String login);
	
	CadUser findByLoginAndSenha(String login, String senha);
	
}
