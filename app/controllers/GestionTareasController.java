package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;
import play.data.Form;
import play.data.FormFactory;
import play.data.DynamicForm;
import play.Logger;

import java.util.List;

import services.UsuarioService;
import services.TareaService;
import services.TareaServiceException;
import models.Usuario;
import models.Tarea;
import security.ActionAuthenticator;

public class GestionTareasController extends Controller{

  @Inject FormFactory formFactory;
  @Inject UsuarioService usuarioService;
  @Inject TareaService tareaService;

  // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
  // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioNuevaTarea(Long idUsuario) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      return ok(formNuevaTarea.render(usuario, formFactory.form(Tareas.class),""));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result creaNuevaTarea(Long idUsuario) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Usuario usuario;
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Form<Tareas> tareaForm = formFactory.form(Tareas.class).bindFromRequest();
      if (tareaForm.hasErrors()) {
        usuario = usuarioService.findUsuarioPorId(idUsuario);
        return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tareas.class), "Hay errores en el formulario"));
      }
      Tareas tarea = tareaForm.get();
      tarea.fechaLimite=tarea.fechaLimite.equals("") ? null : tarea.fechaLimite;
      if (tarea.fechaLimite==null)
        tareaService.nuevaTarea(idUsuario, tarea.titulo,null);
      else {
        try {
          tareaService.nuevaTarea(idUsuario, tarea.titulo,tarea.fechaLimite);
        } catch (TareaServiceException e){
          usuario = usuarioService.findUsuarioPorId(idUsuario);
          return badRequest(formNuevaTarea.render(usuario, formFactory.form(Tareas.class), e.getMessage()));
        }
      }
      flash("aviso", "La tarea se ha grabado correctamente");
      return redirect(controllers.routes.GestionTareasController.listaTareas(idUsuario.toString(),0));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  //public Result listaTareas(String idUsuarioRecibida) {
  public Result listaTareas(String idUsuarioRecibida,Long ordenFecLimite) {
    Long idUsuario = Long.parseLong(idUsuarioRecibida);
    List<Tarea> tareas;
    Logger.debug("Login con usuario:"+idUsuario);
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Logger.debug("Usuario sesion:"+connectedUser);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      String aviso = flash("aviso");
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      if(ordenFecLimite==1)
        tareas = tareaService.allTareasUsuarioOrdenadasFechaLimite(idUsuario);
      else
        tareas = tareaService.allTareasUsuario(idUsuario);
      return ok(listaTareas.render(tareas, usuario, aviso));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioEditaTarea(Long idTarea){
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    if(tarea==null){
      return notFound("Tarea no encontrada");
    } else {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if ((long)connectedUser != (long)tarea.getUsuario().getId()) {
        return unauthorized("Lo siento, no estás autorizado");
      } else {
        return ok(formModificacionTarea.render(tarea.getUsuario().getId(),
        tarea,
        ""));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result grabaTareaModificada(Long idTarea) {
    DynamicForm requestData = formFactory.form().bindFromRequest();
    String nuevoTitulo = requestData.get("titulo");
    String nuevaFechaLimite = requestData.get("fechaLimite");
    Tarea tarea;
    Logger.info("fecha límite: "+nuevaFechaLimite);
    if (nuevaFechaLimite.equals(""))
      tarea=tareaService.modificaTarea(idTarea,nuevoTitulo,"01-01-1900");
    else {
      try {
        tarea=tareaService.modificaTarea(idTarea, nuevoTitulo,nuevaFechaLimite);
      } catch (TareaServiceException e){
        tarea = tareaService.obtenerTarea(idTarea);
        return badRequest(formModificacionTarea.render(tarea.getUsuario().getId(), tarea, e.getMessage()));
      }
    }
    return redirect(controllers.routes.GestionTareasController.listaTareas(tarea.getUsuario().getId().toString(),0));
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result borraTarea(Long idTarea){
    tareaService.borraTarea(idTarea);
    flash("aviso","Tarea borrada correctamente");
    return ok();
  }
}
