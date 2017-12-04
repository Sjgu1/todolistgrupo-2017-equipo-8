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

import models.Tarea;
import models.TareaRepository;
import models.Etiqueta;
import models.EtiquetaRepository;

public class TableroService{
  UsuarioRepository usuarioRepository;
  TableroRepository tableroRepository;
  TareaRepository tareaRepository;
  EtiquetaRepository etiquetaRepository;

  @Inject
  public TableroService(UsuarioRepository usurepository,TableroRepository tabrepository, TareaRepository tareaRepository,EtiquetaRepository etrepository){
    this.usuarioRepository=usurepository;
    this.tableroRepository=tabrepository;
    this.tareaRepository=tareaRepository;
    this.etiquetaRepository=etrepository;
  }

  public Tablero creaTablero(long idUsuario, String titulo){
    Usuario usuario= usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero=tableroRepository.findByNombre(titulo);
    if(tablero!=null){
      throw new TableroServiceException("Nombre de tablero duplicado");
    }
    //haciendo uso del método declarado en service
    /*if(nombreTableroDuplicado(titulo)){
      throw new TableroServiceException("Nombre de tablero duplicado");
    }*/
    tablero=new Tablero(usuario,titulo);
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
    if ((long)idUsuario==(long)tableroRepository.findById(idTablero).getAdministrador().getId()){
      throw new TableroServiceException("El administrador del tablero no puede a la vez participar");
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

  //Devuelve la lista de tableros en los que el usuario ni administra ni participa, ordenados por su id
  //(equivalente al orden de creación)
  public List<Tablero> allTablerosNoUsuario(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    List<Tablero> tableros=new ArrayList<Tablero>(tableroRepository.allTableros());
    tableros.removeAll(this.allTablerosAdministradosUsuario(idUsuario));
    tableros.removeAll(this.allTablerosParticipanteUsuario(idUsuario));
    Collections.sort(tableros,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return tableros;
  }

  public Tablero obtenerTablero(Long idTablero){
    return tableroRepository.findById(idTablero);
  }

  public Tablero addTareaTablero(Long idTablero,Long idTarea){
    Tablero tablero=tableroRepository.findById(idTablero);
    if(tablero==null){
      throw new TableroServiceException("Tablero no existente");
    }
    Tarea tarea=tareaRepository.findById(idTarea);
    if(tarea==null){
      throw new TableroServiceException("Tarea no existente");
    }
    Set<Tarea> tareas=tablero.getTareas();
    tareas.add(tarea);
    tablero.setTareas(tareas);
    tarea.setTablero(tablero);
    tablero=tableroRepository.update(tablero);
    tareaRepository.update(tarea);
    return tablero;
  }

  public List<Tarea> allTareasTablero(Long idTablero){
    Tablero tablero=tableroRepository.findById(idTablero);
    if(tablero==null){
      throw new TableroServiceException("Tablero no existente");
    }

    List<Tarea> tareas=new ArrayList<Tarea>(tablero.getTareas());
    List<Tarea> result = new ArrayList<Tarea>();
    for(Tarea task: tareas){
        if(!task.getTerminada()){
          result.add(task);
        }
    }
    Collections.sort(result,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return result;
  }

  /*
  public boolean nombreTableroDuplicado(String nombreTablero){
    List<Tablero> tableros=new ArrayList<Tablero>(tableroRepository.allTableros());
    return (int)tableros.stream().filter(tablero -> tablero.getNombre().equals(nombreTablero)).count() > 0 ? true : false;
  }*/
}
