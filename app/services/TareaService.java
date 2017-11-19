package services;

import javax.inject.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Date;

import models.Usuario;
import models.UsuarioRepository;
import models.Tarea;
import models.TareaRepository;


public class TareaService{
  UsuarioRepository usuarioRepository;
  TareaRepository tareaRepository;

  @Inject
  public TareaService(UsuarioRepository usuarioRepository,TareaRepository tareaRepository){
    this.usuarioRepository=usuarioRepository;
    this.tareaRepository=tareaRepository;
  }

  //Devuelve la lista de tareas de un usuario, ordenadas por su id
  //(equivalente al orden de creación)
  public List<Tarea> allTareasUsuario(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    List<Tarea> tareas=new ArrayList<Tarea>(usuario.getTareas());
    Collections.sort(tareas,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return tareas;
  }

  public Tarea nuevaTarea(Long idUsuario, String titulo){
    Usuario usuario= usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    Tarea tarea=new Tarea(usuario,titulo);
    return tareaRepository.add(tarea);
  }

  public Tarea nuevaTarea(Long idUsuario, String titulo,String fechaLimite){
    Usuario usuario= usuarioRepository.findById(idUsuario);
    Tarea tarea;
    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    if(fechaLimite==null){
      tarea=new Tarea(usuario,titulo);
    }
    else{
      try{
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        Date fechaLim=sdf.parse(fechaLimite);
        tarea=new Tarea(usuario,titulo,fechaLim);
      } catch (Exception e){
        throw new TareaServiceException("La fecha introducida es incorrecta, debe ser del tipo dd-MM-yyyy");
      }
    }
    return tareaRepository.add(tarea);
  }

  public Tarea obtenerTarea(Long idTarea){
    return tareaRepository.findById(idTarea);
  }

  public Tarea modificaTarea(Long idTarea,String nuevoTitulo){
    Tarea tarea=tareaRepository.findById(idTarea);
    if(tarea==null)
      throw new TareaServiceException("No existe tarea");
    tarea.setTitulo(nuevoTitulo);
    tarea=tareaRepository.update(tarea);
    return tarea;
  }

  public Tarea modificaTarea(Long idTarea,String nuevoTitulo,String fechaLimite){
    Tarea tarea=tareaRepository.findById(idTarea);
    if(tarea==null)
      throw new TareaServiceException("No existe tarea");
    tarea.setTitulo(nuevoTitulo);
    if(fechaLimite!=null){
      try{
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        Date fechaLim=sdf.parse(fechaLimite);
        tarea.setFechaLimite(fechaLim);
      } catch (Exception e){
        throw new TareaServiceException("La fecha introducida es incorrecta, debe ser del tipo dd-MM-yyyy");
      }
    }
    tarea=tareaRepository.update(tarea);
    return tarea;
  }

  public void borraTarea(Long idTarea){
    Tarea tarea=tareaRepository.findById(idTarea);
    if(tarea==null)
      throw new TareaServiceException("No existe tarea");
    tareaRepository.delete(idTarea);
  }
}
