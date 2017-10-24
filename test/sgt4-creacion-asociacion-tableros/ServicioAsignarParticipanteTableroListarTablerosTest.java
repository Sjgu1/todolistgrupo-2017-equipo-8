import org.junit.*;
import static org.junit.Assert.*;

import play.db.jpa.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import play.db.jpa.JPAApi;

import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.List;

import models.Tablero;

import services.TableroService;
import services.TableroServiceException;
import services.UsuarioService;

public class ServicioAsignarParticipanteTableroListarTablerosTest{
  static private Injector injector;

  //Se ejecuta  sólo una vez, al princiio de todos los tests
  @BeforeClass
  static public void initApplication(){
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
    //Instanciamos un JPAApi para que inicialice JPA
    injector.instanceOf(JPAApi.class);
  }

  @Before
  public void initData() throws Exception{
    JndiDatabaseTester databaseTester=new JndiDatabaseTester("DBTest");
    IDataSet initialDataSet=new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
    databaseTester.setDataSet(initialDataSet);
    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
    databaseTester.onSetup();
  }

  private TableroService newTableroService(){
    return injector.instanceOf(TableroService.class);
  }

  //comprueba asignación participante a tablero
  @Test
  public void asignarParticipanteTableroTest(){
    long idUsuario=1001L;
    long idUsuario2=1000L;
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.creaTablero(idUsuario2,"tablero 1");
    tablero=tableroService.addParticipanteaTablero(tablero.getId(),idUsuario);
    assertEquals(1,tablero.getParticipantes().size());
  }

  //comprueba excepción asignar participante no existente a tableros
  @Test(expected=TableroServiceException.class)
  public void asignarParticipanteNoExisteTableroExcepcionTest(){
    long idUsuario=1000L;
    long idUsuario2=5000L;
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.creaTablero(idUsuario,"tablero 1");
    tablero=tableroService.addParticipanteaTablero(tablero.getId(),idUsuario2);
  }

  //comprueba excepción asignar participante a tablero no existente
  @Test(expected=TableroServiceException.class)
  public void asignarParticipanteTableroNoExisteExcepcionTest(){
    long idUsuario=1000L;
    long idTablero=5000L;
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.addParticipanteaTablero(idTablero,idUsuario);
  }

  //comprueba listado tableros en los que un usuario es participante, están ordenados
  @Test
  public void devuelveTablerosParticipanteUsuarioTest(){
    long idUsuario=1000L;
    long idUsuario2=1001L;
    String tituloTablero="Tablero urgente";
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.creaTablero(idUsuario,tituloTablero);
    tablero=tableroService.addParticipanteaTablero(tablero.getId(),idUsuario2);
    List<Tablero> nombreTableros=tableroService.allTablerosParticipanteUsuario(idUsuario);
    assertEquals(0,nombreTableros.size());
    nombreTableros=tableroService.allTablerosParticipanteUsuario(idUsuario2);
    assertEquals(1,nombreTableros.size());
  }

  //comprueba listar tableros usuario no existente genera excepción
  @Test(expected=TableroServiceException.class)
  public void devuelveTablerosParticipanteNoExisteExcepcionTest(){
    long idUsuario=5000L;
    TableroService tableroService=newTableroService();
    List<Tablero> tablero=tableroService.allTablerosParticipanteUsuario(idUsuario);
  }

  //comprueba listado tableros que no administra ni participa usuario concreto
  @Test
  public void devuelveTablerosNoParticipaNiAdministraTest(){
    long idUsuario=1000L;
    long idUsuario2=1001L;
    String tituloTablero="Tablero urgente";
    TableroService tableroService=newTableroService();
    List<Tablero> listadoTableros=tableroService.allTablerosNoUsuario(idUsuario);
    assertEquals(0,listadoTableros.size());
    Tablero tablero=tableroService.creaTablero(idUsuario2,tituloTablero);
    listadoTableros=tableroService.allTablerosNoUsuario(idUsuario);
    assertEquals(1,listadoTableros.size());
    tablero=tableroService.addParticipanteaTablero(tablero.getId(),idUsuario);
    listadoTableros=tableroService.allTablerosNoUsuario(idUsuario);
    assertEquals(0,listadoTableros.size());
  }

  //solicitar listado tableros no administra ni participa con usuario no existente
  //genera excepción
  @Test(expected=TableroServiceException.class)
  public void devuelveTablerosNoParticipaNiAdministraUsuarioNoExisteExcepcionTest(){
    long idUsuario=5000L;
    TableroService tableroService=newTableroService();
    List<Tablero> listadoTableros=tableroService.allTablerosNoUsuario(idUsuario);
  }

  //usuario no puede participar en tablero que administra, genera excepción
  @Test(expected=TableroServiceException.class)
  public void administradorTableroNoPuedeParticiparGeneraExcepcionTest(){
    long idUsuario=1000L;
    TableroService tableroService=newTableroService();
    Tablero tablero=tableroService.creaTablero(idUsuario,"tablero 1");
    tablero=tableroService.addParticipanteaTablero(tablero.getId(),idUsuario);
  }
}
