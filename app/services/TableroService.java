package services;

import javax.inject.*;
import java.util.regex.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import play.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Set;
import java.util.HashSet;

import models.Usuario;
import models.UsuarioRepository;

import models.Tablero;
import models.TableroRepository;

public class TableroService{
  UsuarioRepository usuarioRepository;
  TableroRepository tableroRepository;

  @Inject
  public TableroService(UsuarioRepository usurepository,TableroRepository tabrepository){
    this.usuarioRepository=usurepository;
    this.tableroRepository=tabrepository;
  }

  public Tablero creaTablero(long idUsuario, String titulo){
    Usuario usuario= usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero=new Tablero(usuario,titulo);
    return tableroRepository.add(tablero);
  }

  public Tablero addParticipanteaTablero(Long idTablero,Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero=tableroRepository.findById(idTablero);
    if(tablero==null){
      throw new TableroServiceException("Tablero no existente");
    }
    Set<Usuario> participantes=tablero.getParticipantes();
    participantes.add(usuario);
    tablero.setParticipantes(participantes);
    tablero=tableroRepository.update(tablero);
    return tablero;
  }

  //Devuelve la lista de tableros administrados por un usuario, ordenadas por su id
  //(equivalente al orden de creación)
  public List<Tablero> allTablerosAdministradosUsuario(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    List<Tablero> tableros=new ArrayList<Tablero>(usuario.getAdministrados());
    Collections.sort(tableros,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return tableros;
  }

  //Devuelve la lista de tableros en los que el usuario es participante, ordenados por su id
  //(equivalente al orden de creación)
  public List<Tablero> allTablerosParticipanteUsuario(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    List<Tablero> tableros=new ArrayList<Tablero>(usuario.getTableros());
    Collections.sort(tableros,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return tableros;
  }
}
