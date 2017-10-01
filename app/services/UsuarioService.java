package services;

import javax.inject.*;
import java.util.regex.*;

import models.Usuario;
import models.UsuarioRepository;

public class UsuarioService{
  UsuarioRepository repository;

  //Play proporcionará automáticamente el UsuarioRepository necesario
  //usando inyección de dependencias
  @Inject
  public UsuarioService(UsuarioRepository repository){
    this.repository=repository;
  }

  public Usuario creaUsuario(String login,String email, String password){
    if(repository.findByLogin(login)!=null){
      throw new UsuarioServiceException("Login ya existente");
    }
    Pattern p=Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    Matcher m=p.matcher(email);
    if(!(m.matches())){
        throw new UsuarioServiceException("Email no válido, debe ser del tipo email@dominio.extension");
    }
    Usuario usuario=new Usuario(login,email);
    usuario.setPassword(password);
    return repository.add(usuario);
  }

  public Usuario findUsuarioPorLogin(String login){
    return repository.findByLogin(login);
  }

  public Usuario findUsuarioPorId(Long id){
    return repository.findById(id);
  }

  public Usuario login(String login,String password){
    Usuario usuario=repository.findByLogin(login);
    if (usuario!=null && usuario.getPassword().equals(password)){
      return usuario;
    } else {
      return null;
    }
  }
}
