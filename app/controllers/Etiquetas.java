package controllers;

import play.data.validation.Constraints;

//Usamos los constraints para que se validen automáticamente
//en el formulario: https://www.playframework.com/documentation/2.5.x/JavaForms
public class Etiquetas{
  @Constraints.Required
  public String color;
  public String nombre;
}
