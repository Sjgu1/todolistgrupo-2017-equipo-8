package services;

import javax.inject.*;
import java.util.regex.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    if(!(validaEmail(email))){
        throw new UsuarioServiceException("Email no válido, debe ser del tipo email@dominio.extension");
    }
    Usuario usuario=new Usuario(login,email);
    usuario.setPassword(password);
    return repository.add(usuario);
  }

  public Usuario modificaUsuario(String login,String email, String password, String nombre, String apellidos, String fechaNacimiento){
    Usuario usuario=repository.findByLogin(login);
    if(usuario==null){
      throw new UsuarioServiceException("Login no existente");
    } else {
      if(!(validaEmail(email))){
          throw new UsuarioServiceException("Email no válido, debe ser del tipo email@dominio.extension");
      }
      usuario.setEmail(email);
      usuario.setPassword(password);
      if(nombre!=null)
        usuario.setNombre(nombre);
      if(apellidos!=null)
        usuario.setApellidos(apellidos);
      if(fechaNacimiento!=null){
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        try{
          Date fechaNac=sdf.parse(fechaNacimiento);
          usuario.setFechaNacimiento(fechaNac);
        } catch (Exception e){
          throw new UsuarioServiceException("La fecha introducida es incorrecta, debe ser del tipo dd-MM-yyyy");
        }
      }
    }
    return repository.modify(usuario);
  }

  public Usuario findUsuarioPorLogin(String login){
    return repository.findByLogin(login);
  }

  public Usuario findUsuarioPorId(Long id){
    return repository.findById(id);
  }

  public boolean validaEmail(String email){
    boolean valido;
    Pattern p=Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    Matcher m=p.matcher(email);
    valido=m.matches();
    return valido;
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
