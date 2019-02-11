import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MetodosAuxTest {
	MetodosAux metodosAux = new MetodosAux();
	private mainApp mainApp = new mainApp();
	
//	------------------------ checkLiteralesPARDB2 ------------------------------
	@Test
	public void testNoDetectarLiterales() {
		assertFalse(metodosAux.checkLiteralesPARDB2("&ads-&asda"));
	}
	@Test
	public void testDetectarLiterales() {
		assertTrue(metodosAux.checkLiteralesPARDB2("&ads-asda"));
		assertTrue(metodosAux.checkLiteralesPARDB2("asd"));
	}

//	------------------------ tratarLiteralesPARDB2 ------------------------------	
	@Test
	public void testTratarLiterales() {
		assertEquals("&FECHA1", metodosAux.tratarLiteralesPARDB2("FECHA=&FECHA1"));
		assertEquals("&FECHA1", metodosAux.tratarLiteralesPARDB2("&FECHA1"));
	}
	
//	------------------------ cabecera ------------------------------
	@SuppressWarnings("static-access")
	@Test
	public void testCabecera3Variables() throws IOException {
		mainApp.programa = "COM13P";
		String letraPaso = mainApp.programa.substring(5,6);
		Map<String, String> datos = new HashMap<String, String>();
		
		datos = metodosAux.cabecera(1, letraPaso);
		
		assertTrue(datos.containsKey("Variable2"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testCabeceraVariableVacia() throws IOException {
		mainApp.programa = "AGE21C";
		String letraPaso = mainApp.programa.substring(5,6);
		Map<String, String> datos = new HashMap<String, String>();
		
		datos = metodosAux.cabecera(1, letraPaso);
	    
		assertEquals("CODCIAS=''", datos.get("Variable0"));
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@Test
	public void testCabeceraVariables() throws IOException {
		mainApp.programa = "COM13P";
		String letraPaso = mainApp.programa.substring(5,6);
		Map<String, String> datos = new HashMap<String, String>();
		String[] esperados = {"ACC='3'", "FECHAM='&FDUANTP'", "GENEM=&GEUANTN"};
		String[] salida = new String[3];
		
		datos = metodosAux.cabecera(1, letraPaso);

	    salida[0] = datos.get("Variable0");
	    salida[1] = datos.get("Variable1");
	    salida[2] = datos.get("Variable2");
	    
		assertEquals(esperados, salida);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testCabeceraOPC() throws IOException {
		mainApp.programa = "COM13P";
		String letraPaso = mainApp.programa.substring(5,6);
		Map<String, String> datos = new HashMap<String, String>();
		String esperado = "OPC SEARCH NAME=GENER6P,FECHA6PO,FECHA8PO,FECHADB2,GENERLP";
		
		datos = metodosAux.cabecera(1, letraPaso);
		
		assertEquals(esperado, datos.get("OPC0"));
	}
	
	@SuppressWarnings("static-access")
	@Test(expected = FileNotFoundException.class)
	public void testCabeceraNoExisteFichero() throws IOException {
		mainApp.programa = "AAA50A";
		String letraPaso = mainApp.programa.substring(5,6);
		
		metodosAux.cabecera(1, letraPaso);
		
	}
	
//	------------------------ buscaInfoProc ------------------------------
	@SuppressWarnings("static-access")
	@Test(expected = FileNotFoundException.class)
	public void testBuscaInfoProcNoExisteFichero() throws IOException {
		mainApp.programa = "AAA99AA";
		String letraPaso = mainApp.programa.substring(5,6);
		
		metodosAux.buscaInfoProc(01, letraPaso, "");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testBuscaInfoProcSalidaPasoMenor10() throws IOException {
		mainApp.programa = "AUT21D";
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("//EMPLE    DD  DSN=AUT.EMPLE004.AUT21D.&GENEM,DISP=(NEW,CATLG,DELETE),");
		esperado.add("//             RECFM=FB,LRECL=191,SPACE=(4096,(234,71),RLSE),");
		esperado.add("//             MGMTCLAS=EXLI0300");
		
		ArrayList<String> salida = metodosAux.buscaInfoProc(0, "D", "EMPLE");
		
		assertEquals(esperado, salida);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testBuscaInfoProcSalidaPasoMayor10() throws IOException {
		mainApp.programa = "COM05F";
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("//SORTIDA  DD  DSN=COM.SORTI008.COM05F.&GENEM,DISP=(NEW,CATLG,DELETE),");
		esperado.add("//             RECFM=FB,LRECL=500,");
		esperado.add("//             SPACE=(4096,(122071,36622),RLSE,,ROUND),");
		esperado.add("//             MGMTCLAS=EXLI0450");

		ArrayList<String> salida = metodosAux.buscaInfoProc(10, "F", "SORTIDA");
		
		assertEquals(esperado, salida);
	}
	
//	------------------------ infoFichero ------------------------------
	@SuppressWarnings("static-access")
	@Test
	public void testInfoFicheroSinProc() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.withProc = false;
		datos = metodosAux.infoFichero(0, "F", "SORTIDA");
		
		assertEquals("SORTIDA", datos.get("DSN"));
		assertEquals("(LONGREG,(KKK,KK))", datos.get("Definicion"));
		assertEquals("NEW", datos.get("DISP"));
		assertEquals("NO", datos.get("LRECL"));		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoFicheroConProc() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.programa = "AGE01B";
		mainApp.withProc = true;
		datos = metodosAux.infoFichero(6, "B", "ASPPC");
		
		assertEquals("AGE.ASPPC000.AGE01B.&GENER", datos.get("DSN"));
		assertEquals("(154,(50,15))", datos.get("Definicion"));
		assertEquals("NEW", datos.get("DISP"));
		assertEquals("154", datos.get("LRECL"));
		assertEquals("EXLI0021", datos.get("MGMTCLAS"));
		assertEquals("FB", datos.get("RECFM"));
		assertEquals("4096", datos.get("SPACE"));
	}
	@SuppressWarnings("static-access")
	@Test
	public void testInfoFicheroConProcFicheroCyl() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.programa = "AGE01F";
		mainApp.withProc = true;
		datos = metodosAux.infoFichero(0, "F", "ZONAAND");
		
		assertEquals("AGE.ZONAAND.AGE01F.&GENER", datos.get("DSN"));
		assertEquals("(190,(15,1))", datos.get("Definicion"));
		assertEquals("NEW", datos.get("DISP"));
		assertEquals("190", datos.get("LRECL"));
		assertEquals("EXLI0007", datos.get("MGMTCLAS"));
		assertEquals("FB", datos.get("RECFM"));
		assertEquals("CYL", datos.get("SPACE"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoFicheroConProcDummy() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.programa = "AGE20G";
		mainApp.withProc = true;
		datos = metodosAux.infoFichero(0, "G", "TECNISG");
		
		assertEquals("//TECNISG  DD  DUMMY", datos.get("DUMMY"));

	}
	
//	------------------------ infoReportes ------------------------------
	@SuppressWarnings("static-access")
	@Test
	public void testInfoReporteSinProc() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.withProc = false;
		datos = metodosAux.infoReportes("name",0,"F");
		
		assertEquals("Sacar reporte del PROC - nombre:name", datos.get("ReportKey"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoReporteNoEncontrado() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.programa = "AGE01F";
		mainApp.withProc = true;
		datos = metodosAux.infoReportes("name",0,"F");
		
		assertEquals("* Error al leer línea de Reporte - Nombre reporte: name", datos.get("ReportKey"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoReporteEncontrado() throws IOException {
		Map<String, String> datos = new HashMap<String, String>();
		mainApp.programa = "AGE10M";
		mainApp.withProc = true;
		datos = metodosAux.infoReportes("SYSPRINT",2,"M");
		
		assertEquals("//SYSPRINT DD  SYSOUT=*", datos.get("ReportKey"));
	}

//	------------------------ infoFTP ------------------------------
	@SuppressWarnings("static-access")
	@Test
	public void testInfoFtpSinProc() throws IOException {		
		mainApp.withProc = false;
		
		assertEquals("name", metodosAux.infoFTP(0, "A", "name"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoFtpConProc() throws IOException {	
		mainApp.programa = "AUT21D";
		mainApp.withProc = true;
		
		assertEquals("AUT.EMPLE006.AUT21D.XP", metodosAux.infoFTP(8, "D", "EMPLE006"));
	}
	
//	------------------------ infoSORTIN ------------------------------
	@SuppressWarnings("static-access")
	@Test
	public void testInfoSORTINSinProc() throws IOException {		
		mainApp.withProc = false;
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("**** No encontrado fichero SORTIN");

		ArrayList<String> salida = metodosAux.infoSORTIN(4, "D");
		
		assertEquals(esperado, salida);
		
		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoSORTINConProc() throws IOException {	
		mainApp.programa = "AUT21D";
		mainApp.withProc = true;
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("//SORTIN   DD  DISP=SHR,DSN=Z.AUT.EMPLE004.AUT21D.&GENEM");
		esperado.add("//         DD  DISP=SHR,DSN=Z.AUT.EMPLE002.AUT21D.&GENEM");

		ArrayList<String> salida = metodosAux.infoSORTIN(4, "D");
		
		assertEquals(esperado, salida);
	}
	
//	------------------------ infoDSN ------------------------------
	@SuppressWarnings("static-access")
	@Test
	public void testInfoDSNSinProc() throws IOException {		
		mainApp.withProc = false;
		
		assertEquals("name", metodosAux.infoDSN(0, "D", "name"));
		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testInfoDSNConProc() throws IOException {	
		mainApp.programa = "AGE10M";
		mainApp.withProc = true;
		
		assertEquals("AGE.INCEN.AGE10M.XP", metodosAux.infoDSN(4, "M", "ENTRA1"));
	}
	
//	------------------------ infoJFUSION ------------------------------
	/**
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testInfoJFUSION() throws IOException {
		// ARRANGE
		mainApp.programa = "AUT60A";
		mainApp.withProc = true;
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("FICHA1", "ENTRADA=ASMED01,ASMED02,ASMED03");
		datos.put("FICHA2", "ENTRADA=ASMED04,ASMED05,ASMED06");
		datos.put("FICHA3", "SORTIDA=ASMEDS");
		
		// ACT
		metodosAux.infoJFUSION(datos, 2, "A");
		
		// ASSERT
		assertEquals("AUT.ASMED003.AUT02S.&GENE1", datos.get("DSN1"));
		assertEquals("ASMED01", datos.get("FICH1"));
		assertEquals("AUT.ASMED003.AUT02S.&GENE2", datos.get("DSN2"));
		assertEquals("ASMED02", datos.get("FICH2"));
		assertEquals("AUT.ASMED003.AUT02S.&GENE3", datos.get("DSN3"));
		assertEquals("ASMED03", datos.get("FICH3"));
		assertEquals("AUT.ASMED003.AUT02S.&GENE4", datos.get("DSN4"));
		assertEquals("ASMED04", datos.get("FICH4"));
		assertEquals("AUT.ASMED003.AUT02S.&GENE5", datos.get("DSN5"));
		assertEquals("ASMED05", datos.get("FICH5"));
		assertEquals("AUT.ASMED003.AUT02S.&GENE6", datos.get("DSN6"));
		assertEquals("ASMED06", datos.get("FICH6"));
		assertEquals("EXLI0060", datos.get("MGMTCLAS"));
		assertEquals("(28,(220,66))", datos.get("Definicion"));
		assertEquals("AUT.ASMED006.AUT60A.&GENER", datos.get("DSN"));
		assertEquals("ASMEDS", datos.get("SALIDA"));
		
	}

//	------------------------ ComprobarTamañoLinea ------------------------------
	
	@Test
	public void testComprobarTamañoLineaMayor() {
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("ADRDES", "eeeeeeeeeeeeeeeeeee@eeeeee.essss tyttttsdasdasdasdasdasdasdasdasdasttttt@sadsada.es");
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("DOMMADE=eeeeeeeeeeeeeeeeeee@eeeeee.essss");
		esperado.add("tyttttsdasdasdasdasdasdasdasdasdasttttt@sadsada.es ");
		
		
		ArrayList<String> salida = MetodosAux.ComprobarTamañoLinea("ADRDES", "DOMMADE=                                                               ",
										"", datos);
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void testComprobarTamañoLineaNull() {
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("ADRDES", "eeeeeeeeeeeeeeeeeee@eeeeee.essss tyttttsdasdasdasdasdasdasdasdasdasttttt@sadsada.es");
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("DOMMADE=");
		esperado.add("");
		
		
		ArrayList<String> salida = MetodosAux.ComprobarTamañoLinea("A", "DOMMADE=                                                               ",
										"", datos);
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void testComprobarTamañoLineaMenor() {
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("ADRDES", "eeeeeeeeeeeeeeeeeee@eeeeee.essss");
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("DOMMADE=eeeeeeeeeeeeeeeeeee@eeeeee.essss");
		esperado.add("");
		
		
		ArrayList<String> salida = MetodosAux.ComprobarTamañoLinea("ADRDES", "DOMMADE=                                                               ",
										"", datos);
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void testComprobarTamañoLineaMenorFiLleno() {
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("dato", "fue hacia su casa");
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("DOMMADE=Entonces fue hacia su casa");
		esperado.add("");
		
		
		ArrayList<String> salida = MetodosAux.ComprobarTamañoLinea("dato", "DOMMADE=                                                               ",
										"Entonces ", datos);
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void testComprobarTamañoLineaMayorFiLleno() {
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("dato", "fue hacia su casa despues de comer y empezó a llover. Cogio el paraguas");
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("DOMMADE=Entonces fue hacia su casa despues de comer y empezó a llover.");
		esperado.add("Cogio el paraguas ");
		
		
		ArrayList<String> salida = MetodosAux.ComprobarTamañoLinea("dato", "DOMMADE=                                                               ",
										"Entonces ", datos);
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void testComprobarTamañoLineaNullfiLleno() {
		Map<String, String> datos   = new HashMap<String, String>();
		datos.put("dato", "fue hacia su casa");
		ArrayList<String> esperado = new ArrayList<String>();
		esperado.add("DOMMADE=Entonces fue hacia su casa despues de comer y empezó a");
		esperado.add("llover.Cogio el paraguas ");
		
		
		ArrayList<String> salida = MetodosAux.ComprobarTamañoLinea("A", 
									"DOMMADE=                                                               ",
									"Entonces fue hacia su casa despues de comer y empezó a llover.Cogio el paraguas ",
									datos);
		
		assertEquals(esperado, salida);
	}
}
