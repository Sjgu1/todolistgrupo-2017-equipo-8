import org.junit.*;
import static org.junit.Assert.*;

import play.db.Database;
import play.db.Databases;
import play.db.jpa.*;

import play.Logger;

import java.sql.*;

import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import org.dbunit.operation.*;
import java.io.FileInputStream;

import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import java.lang.Thread;
import java.lang.Exception;

import play.inject.guice.GuiceApplicationBuilder;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;
import play.Environment;

import models.Usuario;
import models.Tarea;
import models.Tablero;
import models.TableroRepository;
import models.UsuarioRepository;
import models.JPAUsuarioRepository;
import models.TareaRepository;
import models.JPATareaRepository;

public class TareaTest {
  static Database db;
  static private Injector injector;

  //Se ejecuta sólo una vez, al principio de todos los tests
  @BeforeClass
  static public void initApplication(){
    GuiceApplicationBuilder guiceApplicationBuilder =
      new GuiceApplicationBuilder().in(Environment.simple());
    injector=guiceApplicationBuilder.injector();
    db=injector.instanceOf(Database.class);
    //Necesario para inicializar JPA
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

  private TareaRepository newTareaRepository(){
    return injector.instanceOf(TareaRepository.class);
  }

  private UsuarioRepository newUsuarioRepository(){
    return injector.instanceOf(UsuarioRepository.class);
  }

  private TableroRepository newTableroRepository(){
    return injector.instanceOf(TableroRepository.class);
  }

 // Test #11: testCrearTarea
 @Test
 public void testCrearTarea() {
  Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
  Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

  assertEquals("juangutierrez", tarea.getUsuario().getLogin());
  assertEquals("juangutierrez@gmail.com", tarea.getUsuario().getEmail());
  assertEquals("Práctica 1 de MADS", tarea.getTitulo());
  }

  // Test #14: testEqualsTareasConId
  @Test
  public void testEqualsTareasConId() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS");
    Tarea tarea2 = new Tarea(usuario, "Renovar DNI");
    Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler");
    tarea1.setId(1000L);
    tarea2.setId(1000L);
    tarea3.setId(2L);
    assertEquals(tarea1, tarea2);
    assertNotEquals(tarea1, tarea3);
  }

  // Test #15
  @Test
  public void testEqualsTareasSinId() {
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea1 = new Tarea(usuario, "Renovar DNI");
    Tarea tarea2 = new Tarea(usuario, "Renovar DNI");
    Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler");
    assertEquals(tarea1, tarea2);
    assertNotEquals(tarea1, tarea3);
  }

  // Test #16: testAddTareaJPARepositoryInsertsTareaDatabase
  @Test
  public void testAddTareaJPARepositoryInsertsTareaDatabase() {
    UsuarioRepository usuarioRepository=newUsuarioRepository();
    TareaRepository tareaRepository=newTareaRepository();
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    usuario = usuarioRepository.add(usuario);
    Tarea tarea = new Tarea(usuario, "Renovar DNI");
    tarea = tareaRepository.add(tarea);
    Logger.info("Número de tarea: " + Long.toString(tarea.getId()));
    assertNotNull(tarea.getId());
    assertEquals("Renovar DNI", getTituloFromTareaDB(tarea.getId()));
  }

  private String getTituloFromTareaDB(Long tareaId) {
    String titulo = db.withConnection(connection -> {
    String selectStatement = "SELECT TITULO FROM Tarea WHERE ID = ? ";
    PreparedStatement prepStmt = connection.prepareStatement(selectStatement);
    prepStmt.setLong(1, tareaId);
    ResultSet rs = prepStmt.executeQuery();
    rs.next();
    return rs.getString("TITULO");
    });
    return titulo;
  }

  // Test #17 testFindTareaById
  @Test
  public void testFindTareaPorId(){
    TareaRepository repository=newTareaRepository();
    Tarea tarea=repository.findById(1000L);
    assertEquals("Renovar DNI",tarea.getTitulo());
  }

  // Test #18 testFindAllTareasUsuario
  @Test
  public void testFindAllTareasUsuario(){
    UsuarioRepository repository=newUsuarioRepository();
    Long idUsuario=1000L;
    Usuario usuario=repository.findById(idUsuario);
    assertEquals(2,usuario.getTareas().size());
  }


  //test para comprobar que una tarea esta en no terminada cuando se crea
  @Test
  public void testTareaNoTerminada(){
    Usuario usuario = new Usuario("juangutierrez", "juangutierrez@gmail.com");
    Tarea tarea = new Tarea(usuario, "Práctica 3 de MADS");

    assertEquals("Práctica 3 de MADS", tarea.getTitulo());
    assertFalse(tarea.getTerminada());
  }
  // Tests testCrearTareaCompruebaFechaCreacion
  @Test
  public void testCrearTareaCompruebaFechaCreacion() throws IllegalArgumentException, InterruptedException {
    UsuarioRepository repository=newUsuarioRepository();
    Long idUsuario=1000L;
    Usuario usuario=repository.findById(idUsuario);

    LocalDateTime antesTarea=LocalDateTime.now();
    //Pausamos para forzar fechas diferentes
    Thread.sleep(100);
    Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación");

    //Pausamos para forzar fechas diferentes
    Thread.sleep(100);
    LocalDateTime despuesTarea=LocalDateTime.now();

    assertNotNull(tarea.getFechaCreacion());
    assertTrue(antesTarea.isBefore(tarea.getFechaCreacion()));
    assertTrue(despuesTarea.isAfter(tarea.getFechaCreacion()));
    }

    // Tests testCrearTareaSinFechaLimite
    @Test
    public void testCrearTareaSinFechaLimite() {
      try{
        UsuarioRepository repository=newUsuarioRepository();
        Long idUsuario=1000L;
        Usuario usuario=repository.findById(idUsuario);
        SimpleDateFormat formateador=new SimpleDateFormat("dd-MM-yyyy");

        Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación");

        assertNotNull(tarea.getFechaLimite());
        assertTrue(tarea.getFechaLimite().compareTo(formateador.parse("01-01-1900"))==0);
      }catch (Exception e){}
    }

    // Tests testCrearTareaConFechaLimite
    @Test
    public void testCrearTareaConFechaLimite() {
      try{
        UsuarioRepository repository=newUsuarioRepository();
        Long idUsuario=1000L;
        Usuario usuario=repository.findById(idUsuario);
        SimpleDateFormat formateador=new SimpleDateFormat("dd-MM-yyyy");

        Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación",formateador.parse("25-12-2018"));

        assertNotNull(tarea.getFechaLimite());
        assertTrue(tarea.getFechaLimite().compareTo(formateador.parse("25-12-2018"))==0);
      }catch (Exception e){}
    }

    // Tests testFechaCaducadaConFechaTareaAnterioroIgualaHoy -- deben estar caducadas
    @Test
    public void testFechaCaducadaConFechaTareaAnterioroIgualaHoy(){
      UsuarioRepository repository=newUsuarioRepository();
      Long idUsuario=1000L;
      Usuario usuario=repository.findById(idUsuario);

      Calendar cal=Calendar.getInstance();
      cal.add(Calendar.DATE,-1);
      Date fechaAyer= cal.getTime();
      Date fechaHoy=new Date();
      fechaHoy.setHours(0);
      fechaHoy.setMinutes(0);
      fechaHoy.setSeconds(0);

      Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación",fechaHoy);
      Tarea tarea1 = new Tarea(usuario, "Práctica con fecha de creación",fechaAyer);

      Boolean caducadaAyer=tarea.tareaCaducada();
      Boolean caducadaHoy=tarea1.tareaCaducada();

      assertTrue(caducadaAyer);
      assertTrue(caducadaHoy);
    }

    // Tests testFechaCaducadaConFechaManana -- devuelve no está caducada
    @Test
    public void testFechaCaducadaConFechaTareaManana(){
      UsuarioRepository repository=newUsuarioRepository();
      Long idUsuario=1000L;
      Usuario usuario=repository.findById(idUsuario);

      Calendar cal=Calendar.getInstance();
      cal.add(Calendar.DATE,1);
      Date fechaManana= cal.getTime();


      Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación",fechaManana);

      Boolean caducadaManana=(!(tarea.tareaCaducada()));

      assertTrue(caducadaManana);
    }

    // Tests testFechaCaducadaPorDefecto -- devuelve no está caducada
    @Test
    public void testFechaCaducadaPorDefecto(){
      UsuarioRepository repository=newUsuarioRepository();
      Long idUsuario=1000L;
      Usuario usuario=repository.findById(idUsuario);

      Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación",null);
      Tarea tarea1 = new Tarea(usuario, "Práctica con fecha de creación");

      Boolean caducadaConNull=(!(tarea.tareaCaducada()));
      Boolean caducadaSinNull=(!(tarea1.tareaCaducada()));

      assertTrue(caducadaConNull);
      assertTrue(caducadaSinNull);
    }

    @Test
    public void testCrearDescripcionTareaSinFecha(){
      UsuarioRepository repository=newUsuarioRepository();
      Long idUsuario=1000L;
      Usuario usuario=repository.findById(idUsuario);

      Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación",null,"Descripción tarea");

      assertEquals("Descripción tarea", tarea.getDescripcion());

    }

    @Test
    public void testCreardescripcionNull(){
      UsuarioRepository repository=newUsuarioRepository();
      Long idUsuario=1000L;
      Usuario usuario=repository.findById(idUsuario);
      Calendar cal=Calendar.getInstance();
      cal.add(Calendar.DATE,1);
      Date fechaManana= cal.getTime();

      Tarea tarea = new Tarea(usuario, "Práctica con fecha de creación",fechaManana,null);
      assertEquals("", tarea.getDescripcion());
      Boolean caducadaManana=(!(tarea.tareaCaducada()));
      assertTrue(caducadaManana);

    }

    /*@Test
    public void testCrearTareaTablero(){
      UsuarioRepository repository=newUsuarioRepository();
      Long idUsuario=1000L;
      Usuario usuario=repository.findById(idUsuario);
      TableroRepository tabrepo=newTableroRepository();
      Tablero tablero=tabrepo.findById(1000L);

      Tarea tarea = new Tarea(usuario, "Tarea con tablero",null,null,tablero);
      assertEquals("Tarea con tablero", tarea.getTitulo());
      assertEquals("Tablero test 1", tarea.getTablero().getNombre());
    }

    //test que prueba crear tarea con todos los parametros posibles
    @Test
    public void testCrearTareaTableroConTodosParametros(){
      try{
        UsuarioRepository repository=newUsuarioRepository();
        Long idUsuario=1000L;
        Usuario usuario=repository.findById(idUsuario);
        TableroRepository tabrepo=newTableroRepository();
        Tablero tablero=tabrepo.findById(1000L);
        SimpleDateFormat formateador=new SimpleDateFormat("dd-MM-yyyy");

        Tarea tarea = new Tarea(usuario, "Tarea con tablero",formateador.parse("01-12-2022"),"Descripción tarea",tablero);
        assertEquals("Tarea con tablero", tarea.getTitulo());
        assertTrue(tarea.getFechaLimite().compareTo(formateador.parse("01-12-2022"))==0);
        assertEquals("Descripción tarea", tarea.getDescripcion());
        assertEquals("Tablero test 1", tarea.getTablero().getNombre());
      }catch(Exception e){}
    }*/




}
