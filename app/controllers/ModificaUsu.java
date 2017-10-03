package controllers;

import play.data.validation.Constraints;

//Usamos los constraints para que se validen automáticamente
//en el formulario: https://www.playframework.com/documentation/2.5.x/JavaForms
public class ModificaUsu{
  public String login;
  @Constraints.Required
  public String email;
  public String nombre;
  public String apellidos;
  public String fechaNacimiento;
}
