import org.junit.*;
import static org.junit.Assert.*;

import java.lang.IllegalArgumentException;

import play.Logger;

import models.Etiqueta;

public class EtiquetaTest {

  @Test
  public void testCrearEtiquetaColor(){
    String color="#d93f0b";
    Etiqueta etiqueta =new Etiqueta(color);
    assertEquals(etiqueta.getColor(),color);
    assertEquals(etiqueta.getNombre(),"");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testCrearEtiquetaColorIncorrectoExcepcion(){
    String color="d93f0b";
    Etiqueta etiqueta =new Etiqueta(color);
  }

  @Test
  public void testCrearEtiquetaColorNombre(){
    String color="#d93f0b";
    String nombre="urgente";
    Etiqueta etiqueta =new Etiqueta(color,nombre);
    assertEquals(etiqueta.getColor(),color);
    assertEquals(etiqueta.getNombre(),nombre);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testCrearEtiquetaColorIncorrectoNombreExcepcion(){
    String color="d93f0b";
    String nombre="urgente";
    Etiqueta etiqueta =new Etiqueta(color,nombre);
    assertEquals(etiqueta.getColor(),color);
    assertEquals(etiqueta.getNombre(),nombre);
  }

  @Test
  public void testModificarEtiqueta(){
    String color="#d93f0b";
    String color2="#ffffff";
    String nombre="urgente";
    Etiqueta etiqueta =new Etiqueta(color);
    assertEquals(etiqueta.getColor(),color);
    assertEquals(etiqueta.getNombre(),"");
    etiqueta.setNombre(nombre);
    etiqueta.setColor(color2);
    assertEquals(etiqueta.getColor(),color2);
    assertEquals(etiqueta.getNombre(),nombre);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testModificarEtiquetaColorIncorrecto(){
    String color="#d93f0b";
    String color2="ffffff";
    Etiqueta etiqueta =new Etiqueta(color);
    etiqueta.setColor(color2);
  }

}
