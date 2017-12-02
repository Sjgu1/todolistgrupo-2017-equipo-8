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
import models.Tablero;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.Logger;

import services.TareaService;
import services.TableroServiceException;
import services.TableroService;

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

  @Test
  public void nuevaTareaTablero(){
    TareaService tareaService=newTareaService();
    TableroService tableroService=newTableroService();
    long idUsuario=1000L;
    long idTablero=1000L;
    Tarea tarea=tareaService.nuevaTarea(idUsuario,"Pagar", null, null);
    long idTarea=tarea.getId();
    Tablero tab=tableroService.addTareaTablero(idTablero,idTarea);
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
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
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
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
    assertEquals(3,tareaService.allTareasUsuario(1000L).size());
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
}
