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

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import models.Usuario;
import models.UsuarioRepository;
import models.Etiqueta;

import services.UsuarioService;
import services.UsuarioServiceException;
import services.EtiquetaService;
import services.EtiquetaServiceException;


public class UsuarioServiceTest{
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

  private UsuarioService newUsuarioService(){
    return injector.instanceOf(UsuarioService.class);
  }

  private EtiquetaService newEtiquetaService(){
    return injector.instanceOf(EtiquetaService.class);
  }

  @Before
  public void initData() throws Exception{
    JndiDatabaseTester databaseTester=new JndiDatabaseTester("DBTest");
    IDataSet initialDataSet=new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/usuarios_dataset.xml"));
    databaseTester.setDataSet(initialDataSet);
    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
    databaseTester.onSetup();
  }

  //Test 5: crearNuevoUsuarioCorrectoTest
  @Test
  public void crearNuevoUsuarioCorrectoTest(){
    UsuarioService usuarioService=newUsuarioService();
    Usuario usuario=usuarioService.creaUsuario("luciaruiz","lucia.ruiz@gmail.com","123456");
    assertNotNull(usuario.getLogin());
    assertEquals("luciaruiz",usuario.getLogin());
    assertEquals("lucia.ruiz@gmail.com",usuario.getEmail());
    assertEquals("123456",usuario.getPassword());
  }

  //Test 6: crearNuevoUsuarioLoginRepetidoLanzaExcepcion
  @Test(expected = UsuarioServiceException.class)
  public void crearNuevoUsuarioLoginRepetidoLanzaExcepcion(){
    UsuarioService usuarioService = newUsuarioService();
    // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
    Usuario usuario = usuarioService.creaUsuario("juangutierrez", "juan.gutierrez@gmail.com", "123456");
  }

  //Test 7: findUsuarioPorLogin
  @Test
  public void findUsuarioPorLogin() {
    UsuarioService usuarioService = newUsuarioService();
    // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
    Usuario usuario = usuarioService.findUsuarioPorLogin("juangutierrez");
    assertNotNull(usuario);
    assertEquals((Long) 1000L, usuario.getId());
  }

  //Test 8: loginUsuarioExistenteTest
   @Test
   public void loginUsuarioExistenteTest() {
      UsuarioService usuarioService = newUsuarioService();
      // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
    Usuario usuario = usuarioService.login("juangutierrez", "123456789");
      assertEquals((Long) 1000L, usuario.getId());
   }

   //Test 9: loginUsuarioNoExistenteTest
   @Test
   public void loginUsuarioNoExistenteTest() {
      UsuarioService usuarioService = newUsuarioService();
      // En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
      Usuario usuario = usuarioService.login("juan", "123456789");
      assertNull(usuario);
   }

   //Test 10: findUsuarioPorId
   @Test
   public void findUsuarioPorId(){
     UsuarioService usuarioService=newUsuarioService();
     //En la BD de prueba usuarios_dataset se ha cargado el usuario juangutierrez
     Usuario usuario=usuarioService.findUsuarioPorId(1000L);
     assertNotNull(usuario);
     assertEquals("juangutierrez",usuario.getLogin());
   }

   //Test #24: creaUsuarioEmailIncorrecto lanza excepción
   @Test(expected = UsuarioServiceException.class)
   public void creaUsuarioEmailIncorrecto(){
     UsuarioService usuarioService=newUsuarioService();
     Usuario usuario=usuarioService.creaUsuario("eustaquio","eustaquio","123456");
   }

   //Test #25: modificaUsuarioDiferentesCasos
   @Test
   public void modificaUsuarioDiferentesCasos(){
     try{
       SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
       Date fecha=sdf.parse("01-12-1980");
       UsuarioService usuarioService=newUsuarioService();
       Usuario usuario=usuarioService.creaUsuario("roberto","roberto@gmail.com","123456");
       assertEquals("roberto",usuario.getLogin());
       assertEquals("roberto@gmail.com",usuario.getEmail());
       assertEquals("123456",usuario.getPassword());
       assertNull(usuario.getNombre());
       assertNull(usuario.getApellidos());
       assertNull(usuario.getFechaNacimiento());
       usuario=usuarioService.modificaUsuario("roberto","nuevo@gmail.com","654321","robert","Macia","01-12-1980");
       assertEquals("roberto",usuario.getLogin());
       assertEquals("nuevo@gmail.com",usuario.getEmail());
       assertEquals("654321",usuario.getPassword());
       assertEquals("robert",usuario.getNombre());
       assertEquals("Macia",usuario.getApellidos());
       assertEquals(fecha,usuario.getFechaNacimiento());
       usuario=usuarioService.modificaUsuario("roberto","nuevo@gmail.com","654321",null,null,null);
       assertEquals("roberto",usuario.getLogin());
       assertEquals("nuevo@gmail.com",usuario.getEmail());
       assertEquals("654321",usuario.getPassword());
       assertEquals("robert",usuario.getNombre());
       assertEquals("Macia",usuario.getApellidos());
       assertEquals(fecha,usuario.getFechaNacimiento());
     } catch (Exception e) {
       e.printStackTrace();
     }
   }

   //Test #26: modificaContrasenaUsuario
   @Test
   public void modificaContrasenaUsuario(){
     UsuarioService usuarioService=newUsuarioService();
     Usuario usuario=usuarioService.findUsuarioPorId(1000L);
     usuario=usuarioService.modificaPassword(usuario.getLogin(),"123456789","654321");
     assertEquals("654321",usuario.getPassword());
   }

    //Test #27: modificaContrasenaActualIncorrecta lanza excepción
    @Test(expected = UsuarioServiceException.class)
    public void modificaContrasenaActualIncorrecta(){
      UsuarioService usuarioService=newUsuarioService();
      Usuario usuario=usuarioService.findUsuarioPorId(1000L);
      usuario=usuarioService.modificaPassword(usuario.getLogin(),"12345","654321");
    }

    //Test #28: modificaContrasenaNuevaIgual lanza excepción
    @Test(expected = UsuarioServiceException.class)
    public void modificaContrasenaNuevaIgual(){
      UsuarioService usuarioService=newUsuarioService();
      Usuario usuario=usuarioService.findUsuarioPorId(1000L);
      usuario=usuarioService.modificaPassword(usuario.getLogin(),"123456789","123456789");
    }

    //Test #29: modificaContrasenaNuevaVacia lanza excepción
    @Test(expected = UsuarioServiceException.class)
    public void modificaContrasenaNuevaVacia(){
      UsuarioService usuarioService=newUsuarioService();
      Usuario usuario=usuarioService.findUsuarioPorId(1000L);
      usuario=usuarioService.modificaPassword(usuario.getLogin(),"123456789","");
    }

    //Test #30: modificaUsuarioFechaIncorrecta
    @Test(expected=UsuarioServiceException.class)
    public void modificaUsuarioFechaIncorrecta(){
        UsuarioService usuarioService=newUsuarioService();
        Usuario usuario=usuarioService.creaUsuario("roberto","roberto@gmail.com","123456");
        usuario=usuarioService.modificaUsuario("roberto","nuevo@gmail.com","654321","robert","Macia","01-13-1980");
    }

    @Test
    public void anadeEtiquetaUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      int numEtiquetas=usuario.getEtiquetas().size();
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      int numEtiquetas2=usuario.getEtiquetas().size();
      assertTrue(numEtiquetas2>numEtiquetas);
      assertTrue(usuario.getEtiquetas().contains(etiqueta));
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      int numEtiquetas3=usuario.getEtiquetas().size();
      assertEquals(numEtiquetas2,numEtiquetas3);
    }

    @Test(expected=UsuarioServiceException.class)
    public void anadeEtiquetaNoExisteUsuarioExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Long idEtiqueta=10000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),idEtiqueta);
    }

    @Test(expected=UsuarioServiceException.class)
    public void anadeEtiquetaUsuarioNoExisteExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=10000L;
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      usuarioService.addEtiquetaAUsuario(idUsuario,etiqueta.getId());
    }

    @Test
    public void borraEtiquetaUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      int numEtiquetas=usuario.getEtiquetas().size();
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      int numEtiquetas2=usuario.getEtiquetas().size();
      assertTrue(numEtiquetas2>numEtiquetas);
      assertTrue(usuario.getEtiquetas().contains(etiqueta));
      usuario=usuarioService.borraEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      int numEtiquetas3=usuario.getEtiquetas().size();
      assertEquals(numEtiquetas,numEtiquetas3);
      assertTrue(!(usuario.getEtiquetas().contains(etiqueta)));
    }

    @Test(expected=UsuarioServiceException.class)
    public void borraEtiquetaNoExistenteUsuarioExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Long idEtiqueta=10000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      usuario=usuarioService.borraEtiquetaAUsuario(usuario.getId(),idEtiqueta);
    }

    @Test(expected=UsuarioServiceException.class)
    public void borraEtiquetaUsuarioNoExisteExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=10000L;
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      usuarioService.borraEtiquetaAUsuario(idUsuario,etiqueta.getId());
    }

    @Test(expected=UsuarioServiceException.class)
    public void borraEtiquetaNoPerteneceUsuarioExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      usuario=usuarioService.borraEtiquetaAUsuario(idUsuario,etiqueta.getId());
    }

    @Test
    public void etiquetaExisteUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      int numEtiquetas=usuario.getEtiquetas().size();
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      assertTrue(usuarioService.EtiquetaPerteneceUsuario(idUsuario,etiqueta.getColor(),etiqueta.getNombre()));
    }

    @Test
    public void etiquetaNoExisteUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      int numEtiquetas=usuario.getEtiquetas().size();
      Etiqueta etiqueta=etiquetaService.creaEtiqueta("#ffffff","testEspecial");
      assertTrue(!(usuarioService.EtiquetaPerteneceUsuario(idUsuario,etiqueta.getColor(),etiqueta.getNombre())));
    }

    @Test
    public void modificaEtiquetaUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      String color="#ffffff";
      String nombre="testEspecial";
      String nuevoColor="#000000";
      String nuevoNombre="testEspecial2";
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      assertTrue(!(usuarioService.EtiquetaPerteneceUsuario(idUsuario,nuevoColor,nuevoNombre)));
      assertTrue(usuarioService.EtiquetaPerteneceUsuario(idUsuario,color,nombre));
      usuario=usuarioService.modificaEtiquetaAUsuario(usuario.getId(),etiqueta.getId(),nuevoColor,nuevoNombre);
      assertTrue(!(usuarioService.EtiquetaPerteneceUsuario(idUsuario,color,nombre)));
      assertTrue(usuarioService.EtiquetaPerteneceUsuario(idUsuario,nuevoColor,nuevoNombre));
    }

    @Test(expected=UsuarioServiceException.class)
    public void modificaEtiquetaUsuarioNoExistenteExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      String color="#ffffff";
      String nombre="testEspecial";
      String nuevoColor="#000000";
      String nuevoNombre="testEspecial2";
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      usuario=usuarioService.modificaEtiquetaAUsuario(10000L,etiqueta.getId(),nuevoColor,nuevoNombre);
    }

    @Test(expected=UsuarioServiceException.class)
    public void modificaEtiquetaNoExistenteUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      String color="#ffffff";
      String nombre="testEspecial";
      String nuevoColor="#000000";
      String nuevoNombre="testEspecial2";
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      usuario=usuarioService.modificaEtiquetaAUsuario(usuario.getId(),10000L,nuevoColor,nuevoNombre);
    }

    @Test(expected=UsuarioServiceException.class)
    public void modificaEtiquetaUsuarioColorIncorrectoExcepcion(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      String color="#ffffff";
      String nombre="testEspecial";
      String nuevoColor="ffffff";
      String nuevoNombre="testEspecial2";
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      Etiqueta etiqueta=etiquetaService.creaEtiqueta(color,nombre);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      usuario=usuarioService.modificaEtiquetaAUsuario(usuario.getId(),etiqueta.getId(),nuevoColor,nuevoNombre);
    }

    @Test
    public void listaEtiquetasUsuario(){
      UsuarioService usuarioService=newUsuarioService();
      EtiquetaService etiquetaService=newEtiquetaService();
      Long idUsuario=1000L;
      String color1="#ffffff";
      String nombre1="testEspecial";
      String color2="#000000";
      String nombre2="testEspecial2";
      Usuario usuario=usuarioService.findUsuarioPorId(idUsuario);
      Etiqueta etiqueta=etiquetaService.creaEtiqueta(color1,nombre1);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      etiqueta=etiquetaService.creaEtiqueta(color1,null);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      etiqueta=etiquetaService.creaEtiqueta(color2,nombre1);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      etiqueta=etiquetaService.creaEtiqueta(color2,nombre2);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      etiqueta=etiquetaService.creaEtiqueta(color1,nombre2);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      etiqueta=etiquetaService.creaEtiqueta(color2,null);
      usuario=usuarioService.addEtiquetaAUsuario(usuario.getId(),etiqueta.getId());
      List<Etiqueta> etiquetas=usuarioService.allEtiquetasUsuario(idUsuario);
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
}
