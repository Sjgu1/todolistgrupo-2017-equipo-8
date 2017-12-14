package controllers;

import play.mvc.*;

import views.html.*;
import javax.inject.*;
import play.data.Form;
import play.data.FormFactory;
import play.Logger;

import services.UsuarioService;
import models.Usuario;
import security.ActionAuthenticator;

public class UsuarioController extends Controller {

  @Inject FormFactory formFactory;

  //Play injecta un usuarioService junto con todas las dependencias necesarias:
  //UsuarioRepository y JPAApi
  @Inject UsuarioService usuarioService;

  public Result acercaDe(){
    return ok(acercaDe.render());
  }

  public Result saludo(String mensaje){
    return ok(saludo.render("El mensaje que he recibido es: "+mensaje));
  }

  public Result formularioRegistro(){
    session().clear();
    return ok(formRegistro.render(formFactory.form(Registro.class),""));
  }

  public Result registroUsuario(){
    session().clear();
    Form<Registro> form=formFactory.form(Registro.class).bindFromRequest();
    if(form.hasErrors()){
      return badRequest(formRegistro.render(form,"Hay errores en el formulario"));
    }
    Registro datosRegistro=form.get();

    if(usuarioService.findUsuarioPorLogin(datosRegistro.login)!=null){
      return badRequest(formRegistro.render(form,"Login ya existente: escoge otro"));
    }

    if(!datosRegistro.password.equals(datosRegistro.confirmacion)){
      return badRequest(formRegistro.render(form,"No coinciden la contraseña y la confirmación"));
    }
    try{
      Usuario usuario=usuarioService.creaUsuario(datosRegistro.login,datosRegistro.email,datosRegistro.password);
    } catch (services.UsuarioServiceException u){
    return badRequest(formRegistro.render(form,u.getMessage()));
  }
    return redirect(controllers.routes.UsuarioController.formularioLogin());
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioModificaUsu(Long id){
    String connectedUserStr=session("connected");
    Long connectedUser=Long.valueOf(connectedUserStr);
    if((long)connectedUser!=(long)id){
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(id);
      if (usuario == null) {
        return notFound("Usuario no encontrado");
      } else {
        //me apoyo en usuario sin nulos para poder cargar en formulario
        usuario=usuario.UsuarioSinNulos();
        return ok(formModificaUsuario.render(usuario,formFactory.form(ModificaUsu.class),""));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result modificaUsuario(Long id){
    String connectedUserStr=session("connected");
    Long connectedUser=Long.valueOf(connectedUserStr);
    if((long)connectedUser!=(long)id){
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(id);
      if (usuario == null) {
        return notFound("Usuario no encontrado");
      } else {
          Form<ModificaUsu> form=formFactory.form(ModificaUsu.class).bindFromRequest();
          if(form.hasErrors()){
            return badRequest(formModificaUsuario.render(usuario,form,"Hay errores en el formulario"));
          }
          ModificaUsu datosModificaUsu=form.get();
          //si alguno de los datos no se ha tocado devuelvo valor null al predeterminado
          datosModificaUsu.nombre=datosModificaUsu.nombre.equals("** Sin asignar **")?null:datosModificaUsu.nombre;
          datosModificaUsu.apellidos=datosModificaUsu.apellidos.equals("** Sin asignar **")?null:datosModificaUsu.apellidos;
          datosModificaUsu.fechaNacimiento=datosModificaUsu.fechaNacimiento.equals("Mon Jan 01 00:00:00 GMT 1900")?null:datosModificaUsu.fechaNacimiento;
          try{
            usuario=usuarioService.modificaUsuario(usuario.getLogin(),datosModificaUsu.email,usuario.getPassword(),datosModificaUsu.nombre,datosModificaUsu.apellidos,datosModificaUsu.fechaNacimiento);
          } catch (services.UsuarioServiceException u){
            return badRequest(formModificaUsuario.render(usuario,form,u.getMessage()));
          }
          return redirect(controllers.routes.UsuarioController.detalleUsuario(Long.toString(usuario.getId())));
        }
      }
    }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result formularioCambioPassword(Long id){
    String connectedUserStr=session("connected");
    Long connectedUser=Long.valueOf(connectedUserStr);
    if((long)connectedUser!=(long)id){
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(id);
      if (usuario == null) {
        return notFound("Usuario no encontrado");
      } else {
        return ok(formModificaPassword.render(usuario,formFactory.form(ModificaPassWordUsuario.class),""));
      }
    }
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result modificaPassword(Long id){
    String connectedUserStr=session("connected");
    Long connectedUser=Long.valueOf(connectedUserStr);
    if((long)connectedUser!=(long)id){
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(id);
      if (usuario == null) {
        return notFound("Usuario no encontrado");
      } else {
          Form<ModificaPassWordUsuario> form=formFactory.form(ModificaPassWordUsuario.class).bindFromRequest();
          if(form.hasErrors()){
            return badRequest(formModificaPassword.render(usuario,form,"Hay errores en el formulario"));
          }
          ModificaPassWordUsuario datosModificaPass=form.get();
          if(!datosModificaPass.passold.equals(usuario.getPassword())){
            return badRequest(formModificaPassword.render(usuario,form,"La contraseña actual es incorrecta"));
          }
          if(!datosModificaPass.passnew.equals(datosModificaPass.confirmacion)){
            return badRequest(formModificaPassword.render(usuario,form,"No coinciden la contraseña y la confirmación"));
          }
          try{
            usuario=usuarioService.modificaPassword(usuario.getLogin(),datosModificaPass.passold,datosModificaPass.passnew);
          } catch (services.UsuarioServiceException u){
            return badRequest(formModificaPassword.render(usuario,form,u.getMessage()));
          }
          return redirect(controllers.routes.UsuarioController.detalleUsuario(Long.toString(usuario.getId())));
        }
      }
    }

  public Result formularioLogin(){
    session().clear();
    return ok(formLogin.render(formFactory.form(Login.class),""));
  }

  public Result loginUsuario(){
    Form<Login> form= formFactory.form(Login.class).bindFromRequest();
    if(form.hasErrors()){
      return badRequest(formLogin.render(form,"Hay errores en el formulario"));
    }
    Login login=form.get();
    Usuario usuario=usuarioService.login(login.username,login.password);
    if (usuario==null){
      return notFound(formLogin.render(form,"Login y contraseña no existentes"));
    } else {
      //Añadimos el id del usuario a la clve 'connected' de
      //la sesión de Play
      // https://www.playframework.com/documentation/2.5.x/JavaSessionFlash
      session().clear();
      session("connected",usuario.getId().toString());
      session("username",usuario.getLogin().toString());
      //session("connected", usuario.getLogin().toString());
      return redirect(controllers.routes.GestionTareasController.listaTareas(usuario.getId().toString(),0));
    }
  }

  // Comprobamos si hay alguien logeado con @Security.Authenticated(ActionAuthenticator.class)
  // https://alexgaribay.com/2014/06/15/authentication-in-play-framework-using-java/
  @Security.Authenticated(ActionAuthenticator.class)
  public Result logout(){
    String connectedUserStr=session("connected");
    session().clear();
    //return ok(saludo.render("Adiós usuario " + connectedUserStr));
    return ok(formLogin.render(formFactory.form(Login.class),"Adiós usuario " + connectedUserStr));
  }

  @Security.Authenticated(ActionAuthenticator.class)
  public Result detalleUsuario(String idRecibido){
    Long id = Long.parseLong(idRecibido);
    String connectedUserStr=session("connected");
    Long connectedUser=Long.valueOf(connectedUserStr);
    if((long)connectedUser!=(long)id){
      return unauthorized("Lo siento, no estás autorizado");
    } else {
      Usuario usuario = usuarioService.findUsuarioPorId(id);
      if (usuario == null) {
        return notFound("Usuario no encontrado");
      } else {
        Logger.debug("Encontrado usuario "+usuario.getId()+": "+usuario.getLogin());
        return ok(detalleUsuario.render(usuario));
      }
    }
  }
}
