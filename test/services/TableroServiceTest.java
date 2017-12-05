import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;

import models.Usuario;
import models.Tarea;
import models.Tablero;
import models.Etiqueta;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.Logger;

import services.TareaService;
import services.TableroServiceException;
import services.TableroService;
import services.EtiquetaService;
import services.EtiquetaServiceException;


public class TableroServiceTest {
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

  private TableroService newTableroService() {
    return injector.instanceOf(TableroService.class);
  }
  private TareaService newTareaService() {
    return injector.instanceOf(TareaService.class);
  }

  private EtiquetaService newEtiquetaService() {
    return injector.instanceOf(EtiquetaService.class);
  }

  @Test
  public void nuevaTareaTablero(){
    TareaService tareaService=newTareaService();
    TableroService tableroService=newTableroService();
    long idUsuario=1000L;
    long idTablero=1000L;
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar", null, null);
    long idTarea=tarea.getId();
    Tablero tab=tableroService.obtenerTablero(idTablero);
    assertEquals(0,tab.getTareas().size());
    tab=tableroService.addTareaTablero(idTablero,idTarea);
    //assertEquals(3,tareaService.allTareasUsuario(1000L).size());
    assertEquals(1,tab.getTareas().size());
  }

  @Test
  public void nuevaTareaTableroFecha(){
    TareaService tareaService=newTareaService();
    TableroService tableroService=newTableroService();
    long idUsuario=1000L;
    long idTablero=1000L;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=new Date();
    String fechaLimite=sdf.format(fecha).toString();
    Logger.info("fecha limite: "+fechaLimite);
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite, null);
    long idTarea=tarea.getId();
    Tablero tab=tableroService.addTareaTablero(idTablero,idTarea);
    //assertEquals(3,tareaService.allTareasUsuario(1000L).size());
    assertEquals(1,tab.getTareas().size());
  }

  @Test
  public void nuevaTareaTableroFechaDescripcion(){
    TareaService tareaService=newTareaService();
    TableroService tableroService=newTableroService();
    long idUsuario=1000L;
    long idTablero=1000L;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=new Date();
    String fechaLimite=sdf.format(fecha).toString();
    Logger.info("fecha limite: "+fechaLimite);
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite,"hola que tal");
    long idTarea=tarea.getId();
    Tablero tab=tableroService.addTareaTablero(idTablero,idTarea);
    //assertEquals(3,tareaService.allTareasUsuario(1000L).size());
    assertEquals(1,tab.getTareas().size());
  }

  @Test(expected=TableroServiceException.class)
  public void nuevaTareaTableroNoExistente(){
    TareaService tareaService=newTareaService();
    TableroService tableroService=newTableroService();
    long idUsuario=1000L;
    long idTablero=8000L;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=new Date();
    String fechaLimite=sdf.format(fecha).toString();
    Logger.info("fecha limite: "+fechaLimite);
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite,"hola que tal");
    long idTarea=tarea.getId();
    Tablero tab=tableroService.addTareaTablero(idTablero,idTarea);
  }

  @Test
  public void anadeEtiquetaTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    int numEtiquetas=tablero.getEtiquetas().size();
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    int numEtiquetas2=tablero.getEtiquetas().size();
    assertTrue(numEtiquetas2>numEtiquetas);
    assertTrue(tablero.getEtiquetas().contains(etiqueta));
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    int numEtiquetas3=tablero.getEtiquetas().size();
    assertEquals(numEtiquetas2,numEtiquetas3);
  }

  @Test(expected=TableroServiceException.class)
  public void anadeEtiquetaNoExisteTableroExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Long idEtiqueta=10000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),idEtiqueta);
  }

  @Test(expected=TableroServiceException.class)
  public void anadeEtiquetaTableroNoExisteExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=10000L;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tableroService.addEtiquetaATablero(idTablero,etiqueta.getId());
  }

  @Test
  public void borraEtiquetaTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    int numEtiquetas=tablero.getEtiquetas().size();
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    Long etiquetaId=etiqueta.getId();
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    int numEtiquetas2=tablero.getEtiquetas().size();
    assertTrue(numEtiquetas2>numEtiquetas);
    assertTrue(tablero.getEtiquetas().contains(etiqueta));
    assertNotNull(etiquetaService.obtenerEtiqueta(etiquetaId));
    tablero=tableroService.borraEtiquetaATablero(tablero.getId(),etiqueta.getId());
    int numEtiquetas3=tablero.getEtiquetas().size();
    assertEquals(numEtiquetas,numEtiquetas3);
    assertTrue(!(tablero.getEtiquetas().contains(etiqueta)));
    assertNull(etiquetaService.obtenerEtiqueta(etiquetaId));
  }

  @Test
  public void borraEtiquetaTableroBorraEtiquetasTareasAsociadas(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    TareaService tareaService=newTareaService();
    Long idTablero=1000L;
    Long idTarea=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    Long etiquetaId=etiqueta.getId();
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tablero=tableroService.addTareaTablero(tablero.getId(),tarea.getId());
    tarea=tareaService.addEtiquetaATarea(tarea.getId(),etiqueta.getId());
    assertTrue(tablero.getEtiquetas().contains(etiqueta));
    assertTrue(tarea.getEtiquetas().contains(etiqueta));
    assertNotNull(etiquetaService.obtenerEtiqueta(etiquetaId));
    tablero=tableroService.borraEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tarea=tareaService.obtenerTarea(idTarea);
    assertTrue(!(tablero.getEtiquetas().contains(etiqueta)));
    assertTrue(!(tarea.getEtiquetas().contains(etiqueta)));
    assertNull(etiquetaService.obtenerEtiqueta(etiquetaId));
  }

  @Test(expected=TableroServiceException.class)
  public void borraEtiquetaNoExistenteTableroExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Long idEtiqueta=10000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    tablero=tableroService.borraEtiquetaATablero(tablero.getId(),idEtiqueta);
  }

  @Test(expected=TableroServiceException.class)
  public void borraEtiquetaTableroNoExisteExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=10000L;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tableroService.borraEtiquetaATablero(idTablero,etiqueta.getId());
  }

  @Test(expected=TableroServiceException.class)
  public void borraEtiquetaNoPerteneceTableroExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tablero=tableroService.borraEtiquetaATablero(idTablero,etiqueta.getId());
  }

  @Test
  public void etiquetaExisteTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    assertTrue(tableroService.EtiquetaPerteneceTablero(idTablero,etiqueta.getColor(),etiqueta.getNombre()));
  }

  @Test
  public void etiquetaNoExisteTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    assertTrue(!(tableroService.EtiquetaPerteneceTablero(idTablero,etiqueta.getColor(),etiqueta.getNombre())));
  }

  @Test
  public void modificaEtiquetaTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    String color="#ffffff";
    String nombre="testEspecial";
    String nuevoColor="#000000";
    String nuevoNombre="testEspecial2";
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    assertTrue(!(tableroService.EtiquetaPerteneceTablero(idTablero,nuevoColor,nuevoNombre)));
    assertTrue(tableroService.EtiquetaPerteneceTablero(idTablero,color,nombre));
    tablero=tableroService.modificaEtiquetaATablero(tablero.getId(),etiqueta.getId(),nuevoColor,nuevoNombre);
    assertTrue(!(tableroService.EtiquetaPerteneceTablero(idTablero,color,nombre)));
    assertTrue(tableroService.EtiquetaPerteneceTablero(idTablero,nuevoColor,nuevoNombre));
  }

  @Test(expected=TableroServiceException.class)
  public void modificaEtiquetaTableroNoExistenteExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    String color="#ffffff";
    String nombre="testEspecial";
    String nuevoColor="#000000";
    String nuevoNombre="testEspecial2";
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tablero=tableroService.modificaEtiquetaATablero(10000L,etiqueta.getId(),nuevoColor,nuevoNombre);
  }

  @Test(expected=TableroServiceException.class)
  public void modificaEtiquetaNoExistenteTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    String color="#ffffff";
    String nombre="testEspecial";
    String nuevoColor="#000000";
    String nuevoNombre="testEspecial2";
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tablero=tableroService.modificaEtiquetaATablero(tablero.getId(),10000L,nuevoColor,nuevoNombre);
  }

  @Test(expected=TableroServiceException.class)
  public void modificaEtiquetaTableroColorIncorrectoExcepcion(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    String color="#ffffff";
    String nombre="testEspecial";
    String nuevoColor="ffffff";
    String nuevoNombre="testEspecial2";
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tablero=tableroService.modificaEtiquetaATablero(tablero.getId(),etiqueta.getId(),nuevoColor,nuevoNombre);
  }

  @Test
  public void listaEtiquetasTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTablero=1000L;
    String color1="#ffffff";
    String nombre1="testEspecial";
    String color2="#000000";
    String nombre2="testEspecial2";
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta(color1,nombre1);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    etiqueta=etiquetaService.creaEtiqueta(color1,null);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    etiqueta=etiquetaService.creaEtiqueta(color2,nombre1);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    etiqueta=etiquetaService.creaEtiqueta(color2,nombre2);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    etiqueta=etiquetaService.creaEtiqueta(color1,nombre2);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    etiqueta=etiquetaService.creaEtiqueta(color2,null);
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    List<Etiqueta> etiquetas=tableroService.allEtiquetasTablero(idTablero);
    assertEquals(etiquetas.get(0).getColor(),color2);
    assertEquals(etiquetas.get(1).getColor(),color2);
    assertEquals(etiquetas.get(2).getColor(),color2);
    assertEquals(etiquetas.get(3).getColor(),color1);
    assertEquals(etiquetas.get(4).getColor(),color1);
    assertEquals(etiquetas.get(5).getColor(),color1);
    assertEquals(etiquetas.get(0).getNombre(),"");
    assertEquals(etiquetas.get(1).getNombre(),nombre1);
    assertEquals(etiquetas.get(2).getNombre(),nombre2);
    assertEquals(etiquetas.get(3).getNombre(),"");
    assertEquals(etiquetas.get(4).getNombre(),nombre1);
    assertEquals(etiquetas.get(5).getNombre(),nombre2);
  }

  @Test
  public void listarTareasTablero(){
    TareaService tareaService=newTareaService();
    TableroService tableroService=newTableroService();
    long idUsuario=1000L;
    long idTablero=1000L;
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar",null,null);
    long idTarea=1000L;
    long idTarea2=tarea.getId();
    Tablero tablero=tableroService.addTareaTablero(idTablero,idTarea);
    List<Tarea> tareas=tableroService.allTareasTablero(idTablero);
    assertEquals(1,tareas.size());
    tableroService.addTareaTablero(idTablero,idTarea2);
    tareas=tableroService.allTareasTablero(idTablero);
    assertEquals(2,tareas.size());
  }
}
