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

}
