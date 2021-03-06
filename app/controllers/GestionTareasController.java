package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;
import play.data.Form;
import play.data.FormFactory;
import play.data.DynamicForm;
import play.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import services.UsuarioService;
import services.TareaService;
import services.TableroService;
import services.EtiquetaService;
import services.EtiquetaServiceException;
import services.TareaServiceException;
import models.Usuario;
import models.Tarea;
import models.Comentario;
import models.Etiqueta;
import services.ComentarioService;
import services.ComentarioServiceException;
import models.Tablero;
//import controllers.EtiquetasAsignadas;
import security.ActionAuthenticator;

public class GestionTareasController extends Controller{

  @Inject FormFactory formFactory;
  @Inject UsuarioService usuarioService;
  @Inject TareaService tareaService;
  @Inject TableroService tableroService;
  @Inject ComentarioService comentarioService;
  @Inject EtiquetaService etiquetaService;
  //GestionTablerosController gestTab;

  // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
  // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioNuevaTarea(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      return ok(formNuevaTarea.render(usuario, formFactory.form(Tareas.class),idTablero,""));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result creaNuevaTarea(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Usuario usuario;
    Tarea newTarea;
    long tab=0L;
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Form<Tareas> tareaForm = formFactory.form(Tareas.class).bindFromRequest();
      if (tareaForm.hasErrors()) {
        usuario = usuarioService.findUsuarioPorId(idUsuario);
        return badRequest(formNuevaTarea.render(usuario,formFactory.form(Tareas.class),tab, "Hay errores en el formulario"));
      }
      Tareas tarea = tareaForm.get();
      DynamicForm requestData = formFactory.form().bindFromRequest();
      String descripcion = requestData.get("descripcion");

      tarea.fechaLimite=tarea.fechaLimite.equals("") ? null : tarea.fechaLimite;
      if (tarea.fechaLimite==null)
        newTarea=tareaService.nuevaTarea(idUsuario, tarea.titulo,null, descripcion);
      else {
        try {
          newTarea=tareaService.nuevaTarea(idUsuario, tarea.titulo,tarea.fechaLimite, descripcion);
        } catch (TareaServiceException e){
          usuario = usuarioService.findUsuarioPorId(idUsuario);
          return badRequest(formNuevaTarea.render(usuario,formFactory.form(Tareas.class),tab, e.getMessage()));
        }
      }
      if(idTablero!=0){
        tableroService.addTareaTablero(idTablero,newTarea.getId());
      }
      flash("aviso", "La tarea se ha grabado correctamente");
      return idTablero==0 ? redirect(controllers.routes.GestionTareasController.listaTareas(idUsuario.toString(),0)) :
      redirect(controllers.routes.GestionTablerosController.detalleTablero(idTablero,idUsuario));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  //public Result listaTareas(String idUsuarioRecibida) {
  public Result listaTareas(String idUsuarioRecibida,Long ordenFecLimite) {
    Long idUsuario = Long.parseLong(idUsuarioRecibida);
    List<Tarea> tareas;
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      String aviso = flash("aviso");
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      if(ordenFecLimite==1)
        tareas = tareaService.allTareasUsuarioOrdenadasFechaLimite(idUsuario);
      else
        tareas = tareaService.allTareasUsuario(idUsuario);

      List<Tarea> tareasTab = tareaService.allTareasResponsable(idUsuario);
      return ok(listaTareas.render(tareas, tareasTab,usuario, aviso));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result listaTareasTerminadas(Long idUsuario) {
    //Long idUsuario = Long.parseLong(idUsuarioRecibida);
    Logger.debug("Login con usuario:"+idUsuario);
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Logger.debug("Usuario sesion:"+connectedUser);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      String aviso = flash("aviso");
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      List<Tarea> tareas = tareaService.tareasTerminadas(idUsuario);
      return ok(listaTareasTerminadas.render(tareas, usuario, aviso));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result listaTareasFiltradas(Long idUsuario,String listaEtiquetas) {
    Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      String[] etiquetasArray = listaEtiquetas.split("-");
      List<Etiqueta> etiquetasFiltradas = new ArrayList<Etiqueta>();
      for(String elemento:etiquetasArray){
        try{
          Long numEtiqueta=Long.parseLong(elemento);
          Etiqueta etiqaux= etiquetaService.obtenerEtiqueta(Long.parseLong(elemento));
          if(etiqaux!=null){
            etiquetasFiltradas.add(etiqaux);
          }
        } catch (NumberFormatException e){
          continue;
        } catch (EtiquetaServiceException e){
          continue;
        }
      }
      String aviso = flash("aviso");
      List<Tarea> tareasFiltradas=tareaService.filtradoTareas(0L,idUsuario,etiquetasFiltradas);
      List<Tarea> tareasTab = tareaService.allTareasResponsable(idUsuario);
      return ok(listaTareasFiltradas.render(tareasFiltradas, tareasTab, usuario, "Tareas filtradas"));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioEditaTarea(Long idTarea, Long idTablero){
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    Tablero tablero = tableroService.findTableroPorId(idTablero);
    if(tarea==null){
      return notFound("Tarea no encontrada");
    } else {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      Boolean participa = false ;
      if(tablero != null){

        for ( Usuario participante: tablero.getParticipantes()) {
          if (participante.getId() == (long)connectedUser){
            participa=true;

          }
        }
        if ((long)connectedUser != (long)tablero.getAdministrador().getId() && !participa) {
        return unauthorized("Lo siento, no estás autorizado");
        }
      }
      else {
        if ((long)connectedUser != (long)tarea.getUsuario().getId() && !participa) {
        return unauthorized("Lo siento, no estás autorizado");
        }
      }
        List<Comentario> comentarios = comentarioService.allComentariosTarea(idTarea);
        List<Etiqueta> etiqDisponibles = tareaService.allEtiquetasTareaSinAsignarDisponibles(idTarea);

        return ok(formModificacionTarea.render(tarea.getUsuario().getId(),tarea,idTablero,"", comentarios,etiqDisponibles));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result grabaTareaModificada(Long idTarea, Long idTablero) {
    DynamicForm requestData = formFactory.form().bindFromRequest();
    String nuevoTitulo = requestData.get("titulo");
    String nuevaFechaLimite = requestData.get("fechaLimite");
    String nuevaDescripcion = requestData.get("descripcion");
    Tarea tarea;
    if (nuevaFechaLimite.equals(""))
      tarea=tareaService.modificaTarea(idTarea,nuevoTitulo,"01-01-1900", nuevaDescripcion);
    else {
      try {
        tarea=tareaService.modificaTarea(idTarea, nuevoTitulo,nuevaFechaLimite, nuevaDescripcion);
      } catch (TareaServiceException e){
        tarea = tareaService.obtenerTarea(idTarea);
        List<Comentario> comentarios = comentarioService.allComentariosTarea(idTarea);
        List<Etiqueta> etiqDisponibles = tareaService.allEtiquetasTareaSinAsignarDisponibles(idTarea);

        return badRequest(formModificacionTarea.render(tarea.getUsuario().getId(),tarea,idTablero,e.getMessage(), comentarios,etiqDisponibles));
      }
    }
    return idTablero==0 ? redirect(controllers.routes.GestionTareasController.listaTareas(tarea.getUsuario().getId().toString(),0)) :
    redirect(controllers.routes.GestionTablerosController.detalleTablero(idTablero,tarea.getUsuario().getId()));
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result grabaComentario(Long idTarea, Long idUsuario) {
    DynamicForm requestData = formFactory.form().bindFromRequest();
    String mensaje = requestData.get("msg");
    Comentario comentario;
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Tarea tarea = tareaService.obtenerTarea(idTarea);
    Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);

    Tablero tablero = tableroService.findTableroPorId(tarea.getTablero().getId());

    Boolean participa = false ;
    if(tablero != null){
      for ( Usuario participante: tablero.getParticipantes()) {
        if (participante.getId() == connectedUser)
          participa=true;
      }
    }

    if ((long)connectedUser != (long)idUsuario && !participa) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
        try {
          comentario= comentarioService.crearComentario(mensaje, usuario.getLogin(), idTarea);
          System.out.println("dentro del try: "+ comentario.getComentario());
        } catch (TareaServiceException e){
          usuario = usuarioService.findUsuarioPorId(idUsuario);
        }

      return redirect(controllers.routes.GestionTareasController.formularioEditaTarea(tarea.getId(),tarea.getTablero().getId()));

    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result asignaEtiquetaTarea(Long idTarea,Long idEtiqueta){
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    Tablero tablero=null;
    Long idTablero;
    if(tarea.getTablero()!=null){
      tablero=tableroService.findTableroPorId(tarea.getTablero().getId());
      idTablero=tarea.getTablero().getId();
    }
    else{
      idTablero=0L;
    }

    if(tablero!=null){
      Boolean participa = false;
      for ( Usuario participante: tablero.getParticipantes()) {
        if (participante.getId() == (long)connectedUser){
            participa=true;
            break;
        }
      }
      if ((long)connectedUser != (long)tablero.getAdministrador().getId() && !participa) {
        return unauthorized("Lo siento, no estás autorizado");
      }
    }
    else{
      if ((long)connectedUser != (long)tarea.getUsuario().getId()) {
        return unauthorized("Lo siento, no estás autorizado");
      }
    }
    try{
      tareaService.addEtiquetaATarea(idTarea,idEtiqueta);
    } catch (TareaServiceException e){
        List<Comentario> comentarios = comentarioService.allComentariosTarea(idTarea);
        List<Etiqueta> etiqDisponibles = tareaService.allEtiquetasTareaSinAsignarDisponibles(idTarea);
        return badRequest(formModificacionTarea.render(connectedUser,tarea,idTablero,e.getMessage(), comentarios,etiqDisponibles));
    }
    return ok();
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result borraEtiquetaTarea(Long idTarea,Long idEtiqueta){
    tareaService.borraEtiquetaATarea(idTarea,idEtiqueta);
    flash("aviso","Etiqueta borrada correctamente");
    return ok();
  }

  public Result borraComentario(Long idComentario ,Long idUsu){
    Long idUsuario = Long.valueOf(session("connected"));
    Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
    Comentario comentario = comentarioService.obtenerComentario(idComentario);
    Tarea tarea = comentario.getTarea();
    Long idTablero = tarea.getTablero().getId();
    List<Comentario> comentarios = comentarioService.allComentariosTarea(tarea.getId());


    if(usuario.getLogin().equals(comentario.getUsuario())){
      comentarioService.borraComentario(idComentario);

      return ok();

    }
    flash("aviso","Solo puedes eliminar comentarios tuyos.");
    return ok();
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result borraTarea(Long idTarea){
    tareaService.borraTarea(idTarea);
    flash("aviso","Tarea borrada correctamente");
    return ok();
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result terminarTarea(Long idTarea){
    tareaService.tareaTerminada(idTarea);
    flash("aviso","La tarea se ha puesto a terminada correctamente");
    return ok();
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result asignarResponsableTarea(Long idTarea, Long idUsuario){
    tareaService.addResponsableTarea(idTarea,idUsuario);
    flash("aviso","Se ha asignado el responsable correctamente");
    return ok();
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result borrarResponsableTarea(Long idTarea,Long idUsuario){
    tareaService.borrarResponsableTarea(idTarea,idUsuario);
    flash("aviso","Se ha borrado el responsable correctamente");
    return ok();
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result reactivarTarea(Long idTarea){
    tareaService.reactivarTareaTerminada(idTarea);
    flash("aviso","La tarea se ha puesto a reactivado correctamente");
    return ok();
  }

    @Security.Authenticated(ActionAuthenticator.class)
    public Result filtradoTareas(Long idUsuario){
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      DynamicForm requestData = formFactory.form().bindFromRequest();
      String etiquetasSel = requestData.get("etiquetasSel");
      return redirect(controllers.routes.GestionTareasController.listaTareasFiltradas(idUsuario,etiquetasSel));
    }
}
