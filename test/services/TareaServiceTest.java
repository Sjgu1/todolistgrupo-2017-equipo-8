import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import java.util.Set;
import java.util.HashSet;

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

import services.UsuarioService;
import services.UsuarioServiceException;
import services.TareaService;
import services.TareaServiceException;
import services.TableroService;
import services.TableroServiceException;
import services.EtiquetaService;
import services.EtiquetaServiceException;

public class TareaServiceTest {
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

  private TareaService newTareaService() {
    return injector.instanceOf(TareaService.class);
  }
  private UsuarioService newUsuarioService() {
    return injector.instanceOf(UsuarioService.class);
  }

  private TableroService newTableroService() {
    return injector.instanceOf(TableroService.class);
  }

  private EtiquetaService newEtiquetaService() {
    return injector.instanceOf(EtiquetaService.class);
  }

  // Test #19: allTareasUsuarioEstanOrdenadas
  @Test
  public void allTareasUsuarioEstanOrdenadas() {
    TareaService tareaService = newTareaService();
    List<Tarea> tareas = tareaService.allTareasUsuario(1000L);
    assertEquals("Renovar DNI", tareas.get(0).getTitulo());
    assertEquals("Práctica 1 MADS", tareas.get(1).getTitulo());
  }

  // Test #19: allTareasUsuarioOrdenadasFechaLimite
  @Test
  public void allTareasUsuarioEstanOrdenadasFechaLimite() {
    TareaService tareaService = newTareaService();
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    long idUsuario=1000L;
    Calendar cal=Calendar.getInstance();
    cal.add(Calendar.DATE,1);
    Date fechaManana= cal.getTime();
    Date fechaHoy=new Date();
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler2",sdf.format(fechaManana).toString());
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler3",sdf.format(fechaHoy).toString());
    List<Tarea> tareas = tareaService.allTareasUsuarioOrdenadasFechaLimite(idUsuario);
    assertEquals("Renovar DNI", tareas.get(0).getTitulo());
    assertEquals("Práctica 1 MADS", tareas.get(1).getTitulo());
    assertEquals("Pagar el alquiler3", tareas.get(2).getTitulo());
    assertEquals("Pagar el alquiler2", tareas.get(3).getTitulo());
  }

  // Test #20: exceptionSiUsuarioNoExisteRecuperandoSusTareas
  @Test(expected = TareaServiceException.class)
  public void crearNuevoUsuarioLoginRepetidoLanzaExcepcion(){
    TareaService tareaService = newTareaService();
    List<Tarea> tareas = tareaService.allTareasUsuario(2000L);
  }

  // Test #21: nuevaTareaUsuario
  @Test
  public void nuevaTareaUsuario(){
    TareaService tareaService=newTareaService();
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler");
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
  }

  // Test : nuevaTareaUsuarioConFechaLimite
  @Test
  public void nuevaTareaUsuarioConFechaLimite(){
    TareaService tareaService=newTareaService();
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=new Date();
    String fechaLimite=sdf.format(fecha).toString();
    Logger.info("fecha limite: "+fechaLimite);
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite);
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
  }

  // Test : nuevaTareaUsuarioConFechaLimiteNull
  @Test
  public void nuevaTareaUsuarioConFechaLimiteNull(){
    TareaService tareaService=newTareaService();
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",null);
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
  }

  // Test : nuevaTareaUsuarioConFechaLimiteIncorrectaExcepcion
  @Test(expected=TareaServiceException.class)
  public void nuevaTareaUsuarioConFechaLimiteIncorrectaExcepcion(){
    TareaService tareaService=newTareaService();
    String fechaLimite="25-13-2018";
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite);
  }

  // Test : nuevaTareaUsuarioConFechaLimiteAnteriorExcepcion
  @Test(expected=TareaServiceException.class)
  public void nuevaTareaUsuarioConFechaLimiteAnteriorExcepcion(){
    TareaService tareaService=newTareaService();
    String fechaLimite="25-10-2010";
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite);
  }

  // Test #22: modificación de tareas
  @Test
  public void modificacionTarea(){
    TareaService tareaService=newTareaService();
    long idTarea=1000L;
    tareaService.modificaTarea(idTarea,"Pagar el alquiler");
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    assertEquals("Pagar el alquiler",tarea.getTitulo());
  }

  // Test: modificación fechaLimite en Tarea
  @Test
  public void modificacionFechaLimiteTarea(){
    TareaService tareaService=newTareaService();
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    try{
      Date fecha=new Date();
      String fechaLimite=sdf.format(fecha).toString();
      long idTarea=1000L;
      tareaService.modificaTarea(idTarea,"Pagar el alquiler",fechaLimite);
      Tarea tarea=tareaService.obtenerTarea(idTarea);
      assertEquals("Pagar el alquiler",tarea.getTitulo());
      assertEquals(fechaLimite,sdf.format(tarea.getFechaLimite()).toString());
    } catch (Exception e) {}
  }

  // Test: modificación fechaLimite null en Tarea
  @Test
  public void modificacionFechaLimiteTareaNull(){
    TareaService tareaService=newTareaService();
    Date fecha;
    long idTarea=1000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    fecha=tarea.getFechaLimite();
    tareaService.modificaTarea(idTarea,"Pagar el alquiler",null);
    tarea=tareaService.obtenerTarea(idTarea);
    assertEquals("Pagar el alquiler",tarea.getTitulo());
    assertEquals(fecha,tarea.getFechaLimite());
  }

  // Test: modificación fechaLimite incorrecta en Tarea excepcion
  @Test(expected=TareaServiceException.class)
  public void modificacionFechaLimiteIncorrectaTareaExcepcion(){
    TareaService tareaService=newTareaService();
    String fechaLimite="28-13-2018";
    long idTarea=1000L;
    tareaService.modificaTarea(idTarea,"Pagar el alquiler",fechaLimite);
  }

  // Test: modificación fechaLimite anterior en Tarea excepcion
  @Test(expected=TareaServiceException.class)
  public void modificacionFechaLimiteAnteriorTareaExcepcion(){
    TareaService tareaService=newTareaService();
    String fechaLimite="28-10-2010";
    long idTarea=1000L;
    tareaService.modificaTarea(idTarea,"Pagar el alquiler",fechaLimite);
  }

  // Test #23: borrado tarea
  @Test
  public void borradoTarea(){
    TareaService tareaService=newTareaService();
    long idTarea=1000L;
    tareaService.borraTarea(idTarea);
    assertNull(tareaService.obtenerTarea(idTarea));
  }

  @Test
  public void ponerTareaTerminada(){
    TareaService tareaService=newTareaService();
    long idTarea =1000L;
    tareaService.tareaTerminada(idTarea);
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    assertTrue(tarea.getTerminada());
  }

  @Test(expected = TareaServiceException.class)
  public void ponerTareaNoexisteTerminada(){
    TareaService tareaService=newTareaService();
    long idTarea =8000L;
    tareaService.tareaTerminada(idTarea);
  }

  @Test
  public void listarTareasNoTerminadasUsuario(){
    TareaService tareaService=newTareaService();
    long idUsuario=1000L;
    long idTarea =1001L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler");
    tareaService.nuevaTarea(idUsuario,"Pagar la coca");
    tareaService.tareaTerminada(idTarea);
    assertEquals(1,tareaService.tareasTerminadas(1000L).size());
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
  }

  // Test : nuevaTareaUsuarioConDescripcion
  @Test
  public void nuevaTareaUsuarioConDescripcion(){
    TareaService tareaService=newTareaService();
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=new Date();
    String fechaLimite=sdf.format(fecha).toString();
    Logger.info("fecha limite: "+fechaLimite);
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite, "descripcion");
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
  }
// Test : nuevaTareaUsuarioConDescripcionNull
  @Test
  public void nuevaTareaUsuarioConDescripcionNull(){
    TareaService tareaService=newTareaService();
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=new Date();
    String fechaLimite=sdf.format(fecha).toString();
    Logger.info("fecha limite: "+fechaLimite);
    long idUsuario=1000L;
    tareaService.nuevaTarea(idUsuario,"Pagar el alquiler",fechaLimite, null);
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
  }

  // Test: modificación descripcion en Tarea
  @Test
  public void modificacionDescripcionTarea(){
    TareaService tareaService=newTareaService();
    Date fecha;
    long idTarea=1000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    tareaService.modificaTarea(idTarea,"Pagar el alquiler",null, "nueva Descripcion");
    tarea=tareaService.obtenerTarea(idTarea);
    assertEquals("nueva Descripcion",tarea.getDescripcion());
  }

  // Test: modificación descripcion en Tarea null
  @Test
  public void modificacionDescripcionTareaNull(){
    TareaService tareaService=newTareaService();
    Date fecha;
    long idTarea=1000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    tareaService.modificaTarea(idTarea,"Pagar el alquiler",null, null);
    tarea=tareaService.obtenerTarea(idTarea);
    assertEquals("",tarea.getDescripcion());
  }

  @Test
  public void anadeEtiquetasVariasTareasPerteneceTablero(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    TareaService tareaService=newTareaService();

    Long idTablero=1000L;
    Long idUsuario=1000L;

    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Etiqueta etiqueta1=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    Etiqueta etiqueta2=etiquetaService.creaEtiqueta("#000000","testEspecial2");
    Tarea tarea1=tareaService.nuevaTarea(idUsuario,"Titulo tarea1");
    Tarea tarea2=tareaService.nuevaTarea(idUsuario,"Titulo tarea2");
    Logger.info(tarea1.toString());
    Logger.info(tarea1.getUsuario().toString());
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta1.getId());
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta2.getId());
    Set<Etiqueta> etiquetas=tablero.getEtiquetas();
    for(Etiqueta etiqueta:etiquetas){
      Logger.info(etiqueta.toString());
    }
    tablero=tableroService.addTareaTablero(tablero.getId(),tarea1.getId());
    tablero=tableroService.addTareaTablero(tablero.getId(),tarea2.getId());
    Set<Tarea> tareas=tablero.getTareas();

    for (Tarea tarea : tareas){
        //tareaService.addEtiquetaATarea(tarea.getId(),etiqueta1.getId());
        //tareaService.addEtiquetaATarea(tarea.getId(),etiqueta2.getId());
        Logger.info(tarea.toString());
    }

    //tarea1=tareaService.obtenerTarea(tarea1.getId());
    //tarea2=tareaService.obtenerTarea(tarea2.getId());

    /*tarea1=tareaService.addEtiquetaATarea(tarea1.getId(),etiqueta1.getId());
    tarea1=tareaService.addEtiquetaATarea(tarea1.getId(),etiqueta2.getId());*/
    tarea2=tareaService.addEtiquetaATarea(tarea2.getId(),etiqueta1.getId());

    //assertTrue(tarea1.getEtiquetas().contains(etiqueta1));
    //assertTrue(tarea1.getEtiquetas().contains(etiqueta2));
    assertTrue(tarea2.getEtiquetas().contains(etiqueta1));
  }

  @Test(expected=TareaServiceException.class)
  public void anadeEtiquetaNoExisteTareaExcepcion(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTarea=1000L;
    Long idEtiqueta=10000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    tarea=tareaService.addEtiquetaATarea(tarea.getId(),idEtiqueta);
  }

  @Test(expected=TareaServiceException.class)
  public void anadeEtiquetaTareaNoExisteExcepcion(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTarea=10000L;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tareaService.addEtiquetaATarea(idTarea,etiqueta.getId());
  }
/*
  @Test
  public void borraEtiquetaTarea(){
    TableroService tableroService=newTableroService();
    EtiquetaService etiquetaService=newEtiquetaService();
    TareaService tareaService=newTareaService();
    Long idTablero=1000L;
    Long idTarea=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    int numEtiquetas=tarea.getEtiquetas().size();
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tarea=tareaService.addEtiquetaATarea(tarea.getId(),etiqueta.getId());
    int numEtiquetas2=tarea.getEtiquetas().size();
    assertTrue(numEtiquetas2>numEtiquetas);
    assertTrue(tarea.getEtiquetas().contains(etiqueta));
    tarea=tareaService.borraEtiquetaATarea(tarea.getId(),etiqueta.getId());
    int numEtiquetas3=tarea.getEtiquetas().size();
    assertEquals(numEtiquetas,numEtiquetas3);
    assertTrue(!(tarea.getEtiquetas().contains(etiqueta)));
  }

  @Test(expected=TareaServiceException.class)
  public void borraEtiquetaNoExistenteTareaExcepcion(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTarea=1000L;
    Long idEtiqueta=10000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    tarea=tareaService.borraEtiquetaATarea(tarea.getId(),idEtiqueta);
  }

  @Test(expected=TareaServiceException.class)
  public void borraEtiquetaTareaNoExisteExcepcion(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTarea=10000L;
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tareaService.borraEtiquetaATarea(idTarea,etiqueta.getId());
  }

  @Test(expected=TareaServiceException.class)
  public void borraEtiquetaNoPerteneceTareaExcepcion(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTarea=1000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tarea=tareaService.borraEtiquetaATarea(idTarea,etiqueta.getId());
  }

  @Test
  public void etiquetaExisteTarea(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    TableroService tableroService=newTableroService();
    Long idTablero=1000L;
    Long idTarea=1000L;
    Tablero tablero=tableroService.findTableroPorId(idTablero);
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    tablero=tableroService.addEtiquetaATablero(tablero.getId(),etiqueta.getId());
    tarea=tareaService.addEtiquetaATarea(tarea.getId(),etiqueta.getId());
    assertTrue(tareaService.EtiquetaPerteneceTarea(idTarea,etiqueta.getColor(),etiqueta.getNombre()));
  }

  @Test
  public void etiquetaNoExisteTarea(){
    TareaService tareaService=newTareaService();
    EtiquetaService etiquetaService=newEtiquetaService();
    Long idTarea=1000L;
    Tarea tarea=tareaService.obtenerTarea(idTarea);
    Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
    assertTrue(!(tareaService.EtiquetaPerteneceTarea(idTarea,etiqueta.getColor(),etiqueta.getNombre())));
  }

  @Test
  public void modificaEtiquetaTarea(){
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
  public void listaEtiquetasTarea(){
    TareaService tareaSer=newTableroService();
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
  }*/

}
