package services;

import javax.inject.*;
import java.util.regex.*;

import models.Etiqueta;
import models.EtiquetaRepository;

public class EtiquetaService{
  EtiquetaRepository etiquetaRepository;

  @Inject
  public EtiquetaService(EtiquetaRepository etrepository){
    this.etiquetaRepository=etrepository;
  }

  public Etiqueta creaEtiqueta(String color, String nombre){
    if(color==null){
      throw new EtiquetaServiceException("El color es obligatorio");
    }
    try{
      Etiqueta etiqueta=new Etiqueta(color);
      if(nombre!=null){
        etiqueta.setNombre(nombre);
      }
      return etiquetaRepository.add(etiqueta);
    } catch (IllegalArgumentException e){
        throw new EtiquetaServiceException("Color incorrecto");
    }
  }

  public Etiqueta obtenerEtiqueta(Long idEtiqueta){
    return etiquetaRepository.findById(idEtiqueta);
  }

  public Etiqueta modificaEtiqueta(Long idEtiqueta, String nuevoColor, String nuevoNombre){
    Etiqueta etiqueta=etiquetaRepository.findById(idEtiqueta);
    if(etiqueta==null)
      throw new EtiquetaServiceException("No existe la etiqueta");
    try{
      if (nuevoColor!=null){
        etiqueta.setColor(nuevoColor);
      }
      if (nuevoNombre!=null){
        etiqueta.setNombre(nuevoNombre);
      }
      etiqueta=etiquetaRepository.update(etiqueta);
      return etiqueta;
    }catch (IllegalArgumentException e){
      throw new EtiquetaServiceException("Color incorrecto");
    }
  }

  public void borraEtiqueta(Long idEtiqueta){
    Etiqueta etiqueta=etiquetaRepository.findById(idEtiqueta);
    if(etiqueta==null)
      throw new EtiquetaServiceException("No existe la etiqueta");
    etiquetaRepository.delete(idEtiqueta);
  }
}
