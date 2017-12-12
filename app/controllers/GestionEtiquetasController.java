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
import services.UsuarioServiceException;
import services.EtiquetaService;
import services.EtiquetaServiceException;
import services.TableroService;
import services.TableroServiceException;

import models.Usuario;
import models.Etiqueta;
import models.Tablero;
import security.ActionAuthenticator;

public class GestionEtiquetasController extends Controller{

  @Inject FormFactory formFactory;
  @Inject UsuarioService usuarioService;
  @Inject EtiquetaService etiquetaService;
  @Inject TableroService tableroService;

  // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
  // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioNuevaEtiqueta(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      return ok(formNuevaEtiqueta.render(usuario, formFactory.form(Etiquetas.class),idTablero,""));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result creaNuevaEtiqueta(Long idUsuario, Long idTablero) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Usuario usuario;
    Etiqueta etiquetaNueva;
    long tab=0L;
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Form<Etiquetas> etiquetaForm = formFactory.form(Etiquetas.class).bindFromRequest();
      if (etiquetaForm.hasErrors()) {
        usuario = usuarioService.findUsuarioPorId(idUsuario);
        return badRequest(formNuevaEtiqueta.render(usuario,formFactory.form(Etiquetas.class),idTablero, "Hay errores en el formulario"));
      }
      Etiquetas etiqueta = etiquetaForm.get();
      DynamicForm requestData = formFactory.form().bindFromRequest();
      String color = requestData.get("color");
      String nombre = requestData.get("nombre");
      try{
        etiquetaNueva=etiquetaService.creaEtiqueta(color,nombre);
        if(idTablero!=0){
          tableroService.addEtiquetaATablero(idTablero,etiquetaNueva.getId());
        }
        else{
          usuarioService.addEtiquetaAUsuario(idUsuario,etiquetaNueva.getId());
        }
      } catch (EtiquetaServiceException e){
        usuario = usuarioService.findUsuarioPorId(idUsuario);
        return badRequest(formNuevaEtiqueta.render(usuario,formFactory.form(Etiquetas.class),idTablero, e.getMessage()));
      } catch (TableroServiceException e){
        usuario = usuarioService.findUsuarioPorId(idUsuario);
        return badRequest(formNuevaEtiqueta.render(usuario,formFactory.form(Etiquetas.class),idTablero, e.getMessage()));
      } catch (UsuarioServiceException e){
        usuario = usuarioService.findUsuarioPorId(idUsuario);
        return badRequest(formNuevaEtiqueta.render(usuario,formFactory.form(Etiquetas.class),idTablero, e.getMessage()));
      }
      flash("aviso", "La etiqueta se ha grabado correctamente");
      return idTablero==0 ? redirect(controllers.routes.GestionTareasController.listaTareas(idUsuario.toString(),0)) :
      redirect(controllers.routes.GestionTablerosController.detalleTablero(idTablero,idUsuario));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioEditaEtiqueta(Long idEtiqueta, Long idTablero){
    Etiqueta etiqueta=etiquetaService.obtenerEtiqueta(idEtiqueta);
    if(etiqueta==null){
      return notFound("Etiqueta no encontrada");
    } else {
      String connectedUserStr = session("connected");
      Long connectedUser =  Long.valueOf(connectedUserStr);
      if(idTablero!=0){
        if((long)idTablero==(long)etiqueta.getTablero().getId()){
          Logger.debug("El tablero es bueno");
          Tablero tablero=tableroService.findTableroPorId(idTablero);
          if (tablero==null){
            return notFound("Tablero no encontrado");
          }
          else {
            Usuario usuarioConectado=usuarioService.findUsuarioPorId(connectedUser);
            if((long)connectedUser != (long)etiqueta.getTablero().getAdministrador().getId() && !(etiqueta.getTablero().getParticipantes().contains(usuarioConectado))){
              return unauthorized("Lo siento, no estás autorizado");
            }
            else {
              return ok(formModificacionEtiqueta.render(connectedUser,etiqueta,idTablero,""));
            }
          }
        }
        else {
          return notFound("La etiqueta no pertenece a dicho tablero");
        }
      }
      else {
        if ((long)connectedUser != (long)etiqueta.getUsuario().getId()) {
          return unauthorized("Lo siento, no estás autorizado");
        }
        else{
            return ok(formModificacionEtiqueta.render(connectedUser,etiqueta,idTablero,""));
        }
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result grabaEtiquetaModificada(Long idEtiqueta, Long idTablero) {
    DynamicForm requestData = formFactory.form().bindFromRequest();
    String nuevoColor = requestData.get("color");
    String nuevoNombre = requestData.get("nombre");
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    Etiqueta etiqueta;
    try {
        etiqueta=etiquetaService.modificaEtiqueta(idEtiqueta, nuevoColor,nuevoNombre);
    } catch (EtiquetaServiceException e){
        etiqueta = etiquetaService.obtenerEtiqueta(idEtiqueta);
        return badRequest(formModificacionEtiqueta.render(connectedUser,etiqueta,idTablero,e.getMessage()));
    }
    return idTablero==0 ? redirect(controllers.routes.GestionTareasController.listaTareas(connectedUser.toString(),0)) :
    redirect(controllers.routes.GestionTablerosController.detalleTablero(idTablero,connectedUser));
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result borraEtiqueta(Long idEtiqueta,Long idTablero){
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    try{
      if(idTablero!=0){
        tableroService.borraEtiquetaATablero(idTablero,idEtiqueta);
      }
      else{
        usuarioService.borraEtiquetaAUsuario(connectedUser,idEtiqueta);
      }
    } catch (TableroServiceException e){
      Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
      return badRequest(formNuevaEtiqueta.render(usuario,formFactory.form(Etiquetas.class),idTablero, e.getMessage()));
    } catch (UsuarioServiceException e){
      Usuario usuario = usuarioService.findUsuarioPorId(connectedUser);
      return badRequest(formNuevaEtiqueta.render(usuario,formFactory.form(Etiquetas.class),idTablero, e.getMessage()));
    }
    flash("aviso","Etiqueta borrada correctamente");
    return ok();
  }
}
