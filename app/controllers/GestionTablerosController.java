package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;
import play.data.Form;
import play.data.FormFactory;
import play.Logger;

import java.util.List;

import services.UsuarioService;
import services.TableroService;
import services.TableroServiceException;
import models.Usuario;
import models.Tablero;
import models.TableroRepository;
import security.ActionAuthenticator;

public class GestionTablerosController extends Controller {
  @Inject FormFactory formFactory;
  @Inject UsuarioService usuarioService;
  @Inject TableroService tableroService;
  @Inject TableroRepository tableroRepository;

  // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
  // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioNuevoTablero(Long idUsuario) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      return ok(formNuevoTablero.render(usuario, formFactory.form(Tablero.class),""));
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result creaNuevoTablero(Long idUsuario) {
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
     return unauthorized("Lo siento, no estás autorizado");
    } else {
     Form<Tablero> tableroForm = formFactory.form(Tablero.class).bindFromRequest();
     if (tableroForm.hasErrors()) {
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      return badRequest(formNuevoTablero.render(usuario, formFactory.form(Tablero.class), "Hay errores en el formulario"));
     }
     Tablero tablero = tableroForm.get();
     try{
       tablero=tableroService.creaTablero(idUsuario, tablero.getNombre());
       Logger.debug("Creado tablero: "+tablero.getId());
       flash("aviso", "El tablero se ha grabado correctamente");
       return redirect(controllers.routes.GestionTablerosController.listaTableros(idUsuario.toString()));
     }catch (TableroServiceException t){
       flash("aviso", "El nombre del tablero está repetido");
       return redirect(controllers.routes.GestionTablerosController.listaTableros(idUsuario.toString()));
     }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result listaTableros(String idUsuarioRecibida) {
    Long idUsuario = Long.parseLong(idUsuarioRecibida);
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      String aviso = flash("aviso");
      Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
      List<Tablero> tablerosAdministrados = tableroService.allTablerosAdministradosUsuario(idUsuario);
      List<Tablero> tablerosParticipa = tableroService.allTablerosParticipanteUsuario(idUsuario);
      List<Tablero> tablerosResto = tableroService.allTablerosNoUsuario(idUsuario);
      return ok(listaTableros.render(tablerosAdministrados,tablerosParticipa,tablerosResto, usuario, aviso));
       }
    }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result asignaParticipanteTablero(Long idTablero,Long idUsuario){
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      tableroService.addParticipanteaTablero(idTablero,idUsuario);
      flash("aviso", "Se ha unido al tablero con éxito");
      //return redirect(controllers.routes.GestionTablerosController.listaTableros(idUsuario));
      return ok();
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result detalleTablero(Long idTablero, Long idUsuario){
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Tablero tablero = tableroRepository.findById(idTablero);
      if (tablero == null) {
        return notFound("Tablero no encontrado");
      } else {
        return ok(detalleTablero.render(tablero,idUsuario));
      }
    }
  }
}
