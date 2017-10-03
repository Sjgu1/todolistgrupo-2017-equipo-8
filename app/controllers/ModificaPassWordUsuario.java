package controllers;

import play.data.validation.Constraints;

//Usamos los constraints para que se validen automáticamente
//en el formulario: https://www.playframework.com/documentation/2.5.x/JavaForms
public class ModificaPassWordUsuario{
  @Constraints.Required
  public String passold;
  @Constraints.Required
  public String passnew;
  @Constraints.Required
  public String confirmacion;

}
