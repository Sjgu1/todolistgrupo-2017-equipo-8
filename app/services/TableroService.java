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

  public Tablero findTableroPorId(Long id){
    return tableroRepository.findById(id);
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
    tablero=tableroRepository.update(tablero);
    return tablero;
  }

  public Tablero addEtiquetaATablero(Long idTablero, Long idEtiqueta){
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero==null){
      throw new TableroServiceException("Error. Tablero no existente");
    }
    Etiqueta etiqueta= etiquetaRepository.findById(idEtiqueta);
    if (etiqueta==null){
      throw new TableroServiceException("Error. Etiqueta no existente");
    }
    Set<Etiqueta> etiquetas=tablero.getEtiquetas();
    etiquetas.add(etiqueta);
    tablero.setEtiquetas(etiquetas);
    tablero=tableroRepository.update(tablero);
    return tablero;
  }

  public Tablero borraEtiquetaATablero(Long idTablero, Long idEtiqueta){
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero==null){
      throw new TableroServiceException("Error. Tablero no existente");
    }
    Etiqueta etiqueta= etiquetaRepository.findById(idEtiqueta);
    if (etiqueta==null){
      throw new TableroServiceException("Error. Etiqueta no existente");
    }
    Set<Etiqueta> etiquetas=tablero.getEtiquetas();
    boolean borrado=etiquetas.remove(etiqueta);
    if(borrado){
      tablero.setEtiquetas(etiquetas);
      tablero=tableroRepository.update(tablero);
      return tablero;
    }
    else {
      throw new TableroServiceException("Error, la etiqueta a borrar no pertenece al tablero");
    }
  }

  public Tablero modificaEtiquetaATablero(Long idTablero, Long idEtiqueta,String color,String nombre){
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero==null){
      throw new TableroServiceException("Error. Tablero no existente");
    }
    Etiqueta etiqueta= etiquetaRepository.findById(idEtiqueta);
    if (etiqueta==null){
      throw new TableroServiceException("Error. Etiqueta no existente");
    }
    Set<Etiqueta> etiquetas=tablero.getEtiquetas();
    if(etiquetas.contains(etiqueta)){
      try{
        if(color!=null){
          etiqueta.setColor(color);
        }
        if(nombre!=null){
          etiqueta.setNombre(nombre);
        }
        etiquetaRepository.update(etiqueta);
      } catch (IllegalArgumentException e){
        throw new TableroServiceException("Error, el color no es válido");
      }
    }
    else{
      throw new TableroServiceException("Error, la etiqueta a modificar no pertenece al tablero");
    }
    tablero = tableroRepository.findById(idTablero);
    return tablero;
  }

  //Devuelve las etiquetas en una lista ordenada por color y nombre
  public List<Etiqueta> allEtiquetasTablero(Long idTablero){
    Tablero tablero=tableroRepository.findById(idTablero);
    if(tablero==null){
      throw new TableroServiceException("Tablero no existente");
    }
    List<Etiqueta> etiquetas=new ArrayList<Etiqueta>(tablero.getEtiquetas());
    Collections.sort(etiquetas,(a,b) -> (a.getColor().compareTo(b.getColor())<0 || (a.getColor().equals(b.getColor()) && a.getNombre().compareTo(b.getNombre())<0)) ? -1 : (a.getColor().equals(b.getColor()) && a.getNombre().equals(b.getNombre())) ? 0 : 1);
    return etiquetas;
  }

  public boolean EtiquetaPerteneceTablero(Long idTablero,String color, String nombre){
    Tablero tablero = tableroRepository.findById(idTablero);
    if (tablero==null){
      throw new TableroServiceException("Error. Tablero no existente");
    }
    Set<Etiqueta> etiquetas=tablero.getEtiquetas();
    return etiquetas.stream().filter(etiqueta -> etiqueta.getColor().equals(color) && etiqueta.getNombre().equals(nombre)).count()>0;
  }

  /*
  public boolean nombreTableroDuplicado(String nombreTablero){
    List<Tablero> tableros=new ArrayList<Tablero>(tableroRepository.allTableros());
    return (int)tableros.stream().filter(tablero -> tablero.getNombre().equals(nombreTablero)).count() > 0 ? true : false;
  }*/
}
