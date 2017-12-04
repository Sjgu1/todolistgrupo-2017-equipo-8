import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import models.Etiqueta;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.Logger;

import services.EtiquetaService;
import services.EtiquetaServiceException;

public class EtiquetaServiceTest {
  static private Injector injector;

  @BeforeClass
  static public void initApplication() {
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
    injector.instanceOf(JPAApi.class);
  }

  @Before
  public void initData() throws Exception {
    JndiDatabaseTester databaseTester = new JndiDatabaseTester("DBTest");
    IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
    databaseTester.setDataSet(initialDataSet);
    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
    databaseTester.onSetup();
  }

  private EtiquetaService newEtiquetaService() {
    return injector.instanceOf(EtiquetaService.class);
  }

  @Test
  public void nuevaEtiqueta(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String nombre=null;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    assertEquals(color,etiqueta.getColor());
    assertEquals("",etiqueta.getNombre());
  }

  @Test
  public void nuevaEtiquetaConNombre(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String nombre="tech";
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    assertEquals(color,etiqueta.getColor());
    assertEquals(nombre,etiqueta.getNombre());
  }

  @Test(expected=EtiquetaServiceException.class)
  public void nuevaEtiquetaColorIncorrectoExcepcion(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="ffffff";
    String nombre=null;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
  }

  @Test(expected=EtiquetaServiceException.class)
  public void nuevaEtiquetaSinColorExcepcion(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color=null;
    String nombre="tech";
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
  }

  @Test
  public void encuentraEtiqueta(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String nombre=null;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    Long idEtiqueta=etiqueta.getId();
    etiqueta=etiquetaService.obtenerEtiqueta(idEtiqueta);
    assertEquals(color,etiqueta.getColor());
    assertEquals("",etiqueta.getNombre());
    assertEquals(idEtiqueta,etiqueta.getId());
  }

  @Test
  public void encuentraEtiquetaNoExiste(){
    EtiquetaService etiquetaService=newEtiquetaService();
    Etiqueta etiqueta=etiquetaService.obtenerEtiqueta(10000L);
    assertNull(etiqueta);
  }

  @Test
  public void modificarEtiqueta(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String color2="#000000";
    String nombre="tech";
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,null);
    Long idEtiqueta=etiqueta.getId();
    assertEquals(color,etiqueta.getColor());
    assertEquals("",etiqueta.getNombre());
    etiqueta=etiquetaService.modificaEtiqueta(idEtiqueta,color2,nombre);
    assertEquals(color2,etiqueta.getColor());
    assertEquals(nombre,etiqueta.getNombre());
  }

  @Test
  public void modificarEtiquetaParametrosNull(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String nombre="tech";
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    Long idEtiqueta=etiqueta.getId();
    assertEquals(color,etiqueta.getColor());
    assertEquals(nombre,etiqueta.getNombre());
    etiqueta=etiquetaService.modificaEtiqueta(idEtiqueta,null,null);
    assertEquals(color,etiqueta.getColor());
    assertEquals(nombre,etiqueta.getNombre());
  }

  @Test(expected=EtiquetaServiceException.class)
  public void modificarEtiquetaColorIncorrecto(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String color2="ffffff";
    String nombre="tech";
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    Long idEtiqueta=etiqueta.getId();
    etiqueta=etiquetaService.modificaEtiqueta(idEtiqueta,color2,nombre);
  }

  @Test(expected=EtiquetaServiceException.class)
  public void modificarEtiquetaNoExiste(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String nombre="tech";
    Long idEtiqueta=10000L;
    Etiqueta etiqueta=etiquetaService.modificaEtiqueta(idEtiqueta,color,nombre);
  }

  @Test
  public void eliminarEtiqueta(){
    EtiquetaService etiquetaService=newEtiquetaService();
    String color="#ffffff";
    String nombre="tech";
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    Long idEtiqueta=etiqueta.getId();
    etiqueta=etiquetaService.obtenerEtiqueta(idEtiqueta);
    assertNotNull(etiqueta);
    etiquetaService.borraEtiqueta(idEtiqueta);
    etiqueta=etiquetaService.obtenerEtiqueta(idEtiqueta);
    assertNull(etiqueta);
  }

  @Test(expected=EtiquetaServiceException.class)
  public void borrarEtiquetaNoExiste(){
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idEtiqueta=10000L;
    etiquetaService.borraEtiqueta(idEtiqueta);
  }

}
