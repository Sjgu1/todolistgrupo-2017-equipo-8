package services;

import javax.inject.*;
import java.util.regex.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Set;
import java.util.HashSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import play.Logger;

import models.Usuario;
import models.UsuarioRepository;
import models.Etiqueta;
import models.EtiquetaRepository;

public class UsuarioService{
  UsuarioRepository repository;
  EtiquetaRepository etiqRepository;

  //Play proporcionará automáticamente el UsuarioRepository necesario
  //usando inyección de dependencias
  @Inject
  public UsuarioService(UsuarioRepository repository,EtiquetaRepository etiqRepository){
    this.repository=repository;
    this.etiqRepository=etiqRepository;
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
        try{
          SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
          sdf.setLenient(false);
          Date fechaNac=sdf.parse(fechaNacimiento);
          usuario.setFechaNacimiento(fechaNac);
        } catch (Exception e){
          throw new UsuarioServiceException("La fecha introducida es incorrecta, debe ser del tipo dd-MM-yyyy");
        }
      }
    }
    return repository.modify(usuario);
  }

  public Usuario modificaPassword(String login,String passold,String passnew){
    Usuario usuario=repository.findByLogin(login);
    if(usuario==null){
      throw new UsuarioServiceException("Login no existente");
    } else {
      if (!(passold.equals(usuario.getPassword()))){
        throw new UsuarioServiceException("La contraseña actual no es correcta");
      }
      if(passnew.equals(null) || passnew.equals("")){
        throw new UsuarioServiceException("La contraseña no puede ser vacía");
      }
      if (passold.equals(passnew)){
        throw new UsuarioServiceException("La contraseña nueva no puede coincidir con la antigua");
      }
      usuario.setPassword(passnew);
      return repository.modify(usuario);
    }
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

  public Usuario addEtiquetaAUsuario(Long idUsuario, Long idEtiqueta){
    Usuario usuario = repository.findById(idUsuario);
    if (usuario==null){
      throw new UsuarioServiceException("Error. Usuario no existente");
    }
    Etiqueta etiqueta= etiqRepository.findById(idEtiqueta);
    if (etiqueta==null){
      throw new UsuarioServiceException("Error. Etiqueta no existente");
    }
    Set<Etiqueta> etiquetas=usuario.getEtiquetas();
    etiquetas.add(etiqueta);
    usuario.setEtiquetas(etiquetas);
    usuario=repository.modify(usuario);
    return usuario;
  }

  public Usuario borraEtiquetaAUsuario(Long idUsuario, Long idEtiqueta){
    Usuario usuario = repository.findById(idUsuario);
    if (usuario==null){
      throw new UsuarioServiceException("Error. Usuario no existente");
    }
    Etiqueta etiqueta= etiqRepository.findById(idEtiqueta);
    if (etiqueta==null){
      throw new UsuarioServiceException("Error. Etiqueta no existente");
    }
    Set<Etiqueta> etiquetas=usuario.getEtiquetas();
    boolean borrado=etiquetas.remove(etiqueta);
    if(borrado){
      usuario.setEtiquetas(etiquetas);
      usuario=repository.modify(usuario);
      return usuario;
    }
    else {
      throw new UsuarioServiceException("Error, la etiqueta a borrar no pertenece al usuario");
    }
  }

  public Usuario modificaEtiquetaAUsuario(Long idUsuario, Long idEtiqueta,String color,String nombre){
    Usuario usuario = repository.findById(idUsuario);
    if (usuario==null){
      throw new UsuarioServiceException("Error. Usuario no existente");
    }
    Etiqueta etiqueta= etiqRepository.findById(idEtiqueta);
    if (etiqueta==null){
      throw new UsuarioServiceException("Error. Etiqueta no existente");
    }
    Set<Etiqueta> etiquetas=usuario.getEtiquetas();
    if(etiquetas.contains(etiqueta)){
      try{
        if(color!=null){
          etiqueta.setColor(color);
        }
        if(nombre!=null){
          etiqueta.setNombre(nombre);
        }
        etiqRepository.update(etiqueta);
      } catch (IllegalArgumentException e){
        throw new UsuarioServiceException("Error, el color no es válido");
      }
    }
    else{
      throw new UsuarioServiceException("Error, la etiqueta a modificar no pertenece al usuario");
    }
    usuario = repository.findById(idUsuario);
    return usuario;
  }

  public List<Etiqueta> allEtiquetasUsuario(Long idUsuario){
    Usuario usuario=repository.findById(idUsuario);
    if(usuario==null){
      throw new UsuarioServiceException("Usuario no existente");
    }
    List<Etiqueta> etiquetas=new ArrayList<Etiqueta>(usuario.getEtiquetas());
    Collections.sort(etiquetas,(a,b) -> (a.getColor().compareTo(b.getColor())<0 || (a.getColor().equals(b.getColor()) && a.getNombre().compareTo(b.getNombre())<0)) ? -1 : (a.getColor().equals(b.getColor()) && a.getNombre().equals(b.getNombre())) ? 0 : 1);
    return etiquetas;
  }

  public boolean EtiquetaPerteneceUsuario(Long idUsuario,String color, String nombre){
    Usuario usuario = repository.findById(idUsuario);
    if (usuario==null){
      throw new UsuarioServiceException("Error. Usuario no existente");
    }
    Set<Etiqueta> etiquetas=usuario.getEtiquetas();
    return etiquetas.stream().filter(etiqueta -> etiqueta.getColor().equals(color) && etiqueta.getNombre().equals(nombre)).count()>0;
  }
}
