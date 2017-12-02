package services;

import javax.inject.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;

import models.Usuario;
import models.UsuarioRepository;
import models.Tablero;
import models.TableroRepository;
import models.Tarea;
import models.TareaRepository;

import play.Logger;


public class TareaService{
  UsuarioRepository usuarioRepository;
  TareaRepository tareaRepository;
  TableroRepository tableroRepository;

  @Inject
  public TareaService(UsuarioRepository usuarioRepository,TareaRepository tareaRepository, TableroRepository tableroRepository){
    this.usuarioRepository=usuarioRepository;
    this.tareaRepository=tareaRepository;
    this.tableroRepository=tableroRepository;
  }

  //Devuelve la lista de tareas de un usuario, ordenadas por su id
  //(equivalente al orden de creación)
  public List<Tarea> allTareasUsuario(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    List<Tarea> tareas=new ArrayList<Tarea>(usuario.getTareas());
    List<Tarea> result = new ArrayList<Tarea>();
    for(Tarea task: tareas){
        if(!task.getTerminada()){
          result.add(task);
        }
    }
    Collections.sort(result,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return result;
  }

  public List<Tarea> tareasTerminadas(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    List<Tarea> tareas=new ArrayList<Tarea>(usuario.getTareas());
    List<Tarea> result = new ArrayList<Tarea>();
    for(Tarea task: tareas){
        if(task.getTerminada()){
          result.add(task);
        }
    }
    Collections.sort(result,(a,b) -> a.getId() > b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return result;
  }

  //Devuelve la lista de tareas de un usuario, ordenadas por su fecha límite
  public List<Tarea> allTareasUsuarioOrdenadasFechaLimite(Long idUsuario){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    List<Tarea> tareas=new ArrayList<Tarea>(usuario.getTareas());
    List<Tarea> result = new ArrayList<Tarea>();
    for(Tarea task: tareas){
        if(!task.getTerminada()){
          result.add(task);
        }
    }
    Collections.sort(result,(a,b) -> a.getFechaLimite().before(b.getFechaLimite()) ? -1 : a.getFechaLimite().equals(b.getFechaLimite()) ? 0 : 1);
    return result;
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
        Date fecdefecto=sdf.parse("01-01-1900");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date fechaAyer= cal.getTime();
        if (fechaAyer.after(fechaLim) && (!(fechaLim.equals(fecdefecto)))){
          throw new TareaServiceException("No se puede establecer una fecha límite inferior a hoy");
        }
        tarea=new Tarea(usuario,titulo,fechaLim);
      } catch (Exception e){
        throw new TareaServiceException("La fecha introducida es incorrecta, debe ser del tipo dd-MM-yyyy");
      }
    }
    return tareaRepository.add(tarea);
  }

  public Tarea nuevaTarea(Long idUsuario, String titulo,String fechaLimite, String descripcion){
    Usuario usuario= usuarioRepository.findById(idUsuario);
    Tarea tarea;

    if(usuario==null){
      throw new TareaServiceException("Usuario no existente");
    }
    if(fechaLimite==null && descripcion ==null){
      tarea=new Tarea(usuario,titulo);
    }
    else{
      try{
        Date fechaLim;
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        if (fechaLimite !=null){
          fechaLim=sdf.parse(fechaLimite);
        }else{
          fechaLim = null;
        }

        Date fecdefecto=sdf.parse("01-01-1900");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date fechaAyer= cal.getTime();
        if (fechaLim != null)
          if (fechaAyer.after(fechaLim) && (!(fechaLim.equals(fecdefecto)))){
            throw new TareaServiceException("No se puede establecer una fecha límite inferior a hoy");
          }
        if(descripcion ==null)
          tarea=new Tarea(usuario,titulo,fechaLim);
        else
          tarea=new Tarea(usuario,titulo,fechaLim, descripcion);
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
        Date fecdefecto=sdf.parse("01-01-1900");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date fechaAyer= cal.getTime();
        if (fechaAyer.after(fechaLim) && (!(fechaLim.equals(fecdefecto)))){
          throw new TareaServiceException("La fecha introducida es incorrecta, debe ser como mínimo igual al día de hoy");
        }
        tarea.setFechaLimite(fechaLim);
      } catch (Exception e){
        throw new TareaServiceException("La fecha introducida es incorrecta, debe ser del tipo dd-MM-yyyy");
      }
    }
    tarea=tareaRepository.update(tarea);
    return tarea;
  }
  public Tarea modificaTarea(Long idTarea,String nuevoTitulo,String fechaLimite, String descripcion){
    Tarea tarea=tareaRepository.findById(idTarea);
    if(tarea==null)
      throw new TareaServiceException("No existe tarea");
    tarea.setTitulo(nuevoTitulo);
    tarea.setDescripcion(descripcion);
    if(fechaLimite!=null){
      try{
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        Date fechaLim=sdf.parse(fechaLimite);
        Date fecdefecto=sdf.parse("01-01-1900");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date fechaAyer= cal.getTime();
        if (fechaAyer.after(fechaLim) && (!(fechaLim.equals(fecdefecto)))){
          throw new TareaServiceException("La fecha introducida es incorrecta, debe ser como mínimo igual al día de hoy");
        }
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

  public void tareaTerminada(Long idTarea){
    Tarea tarea=tareaRepository.findById(idTarea);
    if(tarea==null)
      throw new TareaServiceException("No existe tarea");
    tarea.setTerminada(true);
    tareaRepository.update(tarea);
  }

  public List<Tarea> allTareasTablero(Long idUsuario, Long idTablero){
    Usuario usuario=usuarioRepository.findById(idUsuario);
    if(usuario==null){
      throw new TableroServiceException("Usuario no existente");
    }
    Tablero tablero=tableroRepository.findById(idTablero);
    if(tablero==null){
      throw new TableroServiceException("Tablero no existente");
    }
    List<Tarea> tareas=new ArrayList<Tarea>(tablero.getTareas());
    Logger.info("tareas "+tablero.getTareas());
    Collections.sort(tareas,(a,b) -> a.getId() < b.getId() ? -1 : a.getId()==b.getId() ? 0 : 1);
    return tareas;
  }
}
