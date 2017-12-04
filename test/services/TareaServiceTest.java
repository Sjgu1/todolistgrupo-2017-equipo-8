import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;

import models.Usuario;
import models.Tarea;

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
  public void listarTareasSinTablero() {
    TareaService tareaService = newTareaService();
    TableroService tableroService = newTableroService();
    long idUsuario=1000L;
    long idTablero=1000L;
    List<Tarea> tareas = tareaService.allTareasUsuario(idUsuario);
    assertEquals(2, tareas.size());
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar las medicinas jeje");
    long idTarea=tarea.getId();
    tableroService.addTareaTablero(idTablero,idTarea);
    tareas = tareaService.allTareasUsuario(idUsuario);
    assertEquals(2, tareas.size());
  }

}
