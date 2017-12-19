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
import services.TableroService;
import services.TareaService;
import services.EtiquetaService;
import services.TableroServiceException;
import services.EtiquetaServiceException;
import models.Usuario;
import models.Tablero;
import models.Tarea;
import models.Etiqueta;
import models.TableroRepository;
import security.ActionAuthenticator;

public class GestionTablerosController extends Controller {
  @Inject FormFactory formFactory;
  @Inject UsuarioService usuarioService;
  @Inject TareaService tareaService;
  @Inject TableroService tableroService;
  @Inject EtiquetaService etiquetaService;
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
       if(tablero.getNombre().isEmpty()){
         Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
         return badRequest(formNuevoTablero.render(usuario, formFactory.form(Tablero.class), "El tablero no tiene nombre"));
       }
       tablero=tableroService.creaTablero(idUsuario, tablero.getNombre());
       //Logger.debug("Creado tablero: "+tablero.getId());
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
        String aviso = flash("aviso");
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);
        List<Tarea> tareas = tableroService.allTareasTablero(idTablero);
        return ok(detalleTablero.render(tablero,idUsuario,tareas,usuario,aviso));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result detalleTableroFiltrado(Long idTablero, Long idUsuario,String listaEtiquetas){
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    if ((long)connectedUser != (long)idUsuario) {
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Tablero tablero = tableroRepository.findById(idTablero);
      if (tablero == null) {
        return notFound("Tablero no encontrado");
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
        List<Tarea> tareas=tareaService.filtradoTareas(idTablero,0L,etiquetasFiltradas);
        String aviso = flash("aviso");
        Usuario usuario = usuarioService.findUsuarioPorId(idUsuario);

        return ok(detalleTableroFiltrado.render(tablero,idUsuario,tareas,usuario,aviso));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result filtradoTareas(Long idTablero){
    String connectedUserStr = session("connected");
    Long connectedUser =  Long.valueOf(connectedUserStr);
    DynamicForm requestData = formFactory.form().bindFromRequest();
    String etiquetasSel = requestData.get("etiquetasSel");
    return redirect(controllers.routes.GestionTablerosController.detalleTableroFiltrado(idTablero,connectedUser,etiquetasSel));
  }
}
