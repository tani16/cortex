import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class LectorPasosTest {
	LectorPasos lectorPasos = new LectorPasos();

//	------------------ leerPaso -----------------
	@Test
	public void testLeerPasoDB2() {
		mainApp.letraPaso = "D";
		mainApp.programa = "AUT21D";
		Map<String, String> salida = new HashMap<String, String>();
		Map<String, String> esperado = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("D00      STEP  PGM=AUT21D01,PATTERN=DB2,PATKW=(PARDB2='&FECHAM'),DYNAMNBR=20,REGION=4M,LANG=DB2,TIME=45");
		pasos.add("EMPLE    FILE  NAME=EMPLE004,MODE=O,REST=(YES,DELETE)");
		
		esperado.put("Borrar1", "EMPLE");
		esperado.put("PATTERN", "DB2");
		esperado.put("DYNAMNBR", "20");
		esperado.put("PGM", "AUT21D01");
		esperado.put("Salida1", "EMPLE");
		esperado.put("TIME", "45");
		esperado.put("LANG", "DB2");
		esperado.put("PARDB2", "&FECHAM");
		esperado.put("REGION", "4M");
		
		salida = lectorPasos.leerPaso(pasos);
		
		assertEquals(esperado, salida);		
	}
	
	@Test
	public void testLeerPasoDB2Comentario() {
		mainApp.letraPaso = "D";
		mainApp.programa = "AUT21D";
		Map<String, String> salida = new HashMap<String, String>();
		Map<String, String> esperado = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("D02      STEP  PGM=DIV21D02,PATTERN=DB2,PATKW=(PARDB2='&FECHAM'),DYNAMNBR=20,REGION=4M,LANG=DB2,TIME=45");
		pasos.add("DIVEMPLE FILE  NAME=EMPLE002,MODE=O,REST=(YES,DELETE)");
		pasos.add("*TITLE SORT DEL FITXER SALID002");
		
		esperado.put("Borrar1", "DIVEMPLE");
		esperado.put("PATTERN", "DB2");
		esperado.put("DYNAMNBR", "20");
		esperado.put("PGM", "DIV21D02");
		esperado.put("Salida1", "DIVEMPLE");
		esperado.put("Comentario1",	"*TITLE SORT DEL FITXER SALID002");
		esperado.put("TIME", "45");
		esperado.put("LANG", "DB2");
		esperado.put("PARDB2", "&FECHAM");
		esperado.put("REGION", "4M");
		
		salida = lectorPasos.leerPaso(pasos);
		
		assertEquals(esperado, salida);		
	}
	
	@Test
	public void testLeerPasoDB2Reporte() {
		mainApp.letraPaso = "C";
		mainApp.programa = "AUT01C";
		Map<String, String> salida = new HashMap<String, String>();
		Map<String, String> esperado = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("C02      STEP  PGM=AUT01B08,PATTERN=DB2,PATKW=(RESUMEN=RESUM001,PARDB2='01',RESTO=RESTO001,PARTE=PARTE001,SYSOUT1=P),LANG=DB2");
		pasos.add("RESUMEN  FILE  NAME=RESUM002,MODE=I");
		pasos.add("PARTE    FILE  NAME=PARTE001,MODE=O,REST=(YES,DELETE)");
		pasos.add("RESTO    FILE  NAME=RESTO001,MODE=O,REST=(YES,DELETE)");
		pasos.add("SYSOUT   REPORT SYSOUT=*IF C02.RC = 0 THEN");
		
		esperado.put("Salida2", "RESTO");
		esperado.put("Reporte1", "SYSOUT  ");
		esperado.put("Salida1", "PARTE");
		esperado.put("SYSOUT1", "P");
		esperado.put("LANG", "DB2");
		esperado.put("PARDB2", "01");
		esperado.put("SYSOUT", "*IF");
		esperado.put("Borrar1", "PARTE");
		esperado.put("PATTERN", "DB2");
		esperado.put("PARTE", "PARTE001");
		esperado.put("Borrar2", "RESTO");
		esperado.put("PGM", "AUT01B08");
		esperado.put("RESUMEN", "RESUM001");
		esperado.put("IF", "//         IF C02.RC = 0 THEN");
		esperado.put("Entrada1","RESUMEN");
		esperado.put("RESTO", "RESTO001");

		salida = lectorPasos.leerPaso(pasos);
		
		assertEquals(esperado, salida);		
	}
	
	@Test
	public void testLeerPasoDB2Condicional() {
		mainApp.letraPaso = "C";
		mainApp.programa = "AUT01C";
		Map<String, String> salida = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("A00      STEP  PGM=AGE01A21,PATTERN=DB2,PATKW=(PARDB2='FECHA=&FECHAD,&GENER,&ACT'),REGION=6M,DYNAMNBR=20,SORT=(100,367,K),COND1=(0,LT)");
		
		salida = lectorPasos.leerPaso(pasos);
		
		assertEquals("FECHA=&FECHAD-&GENER-&ACT", salida.get("PARDB2"));
		assertEquals("=(0,LT)", salida.get("COND1"));
	}
	
	@Test
	public void testLeerPasoMail() {
		mainApp.letraPaso = "E";
		mainApp.programa = "AGE04E";
		Map<String, String> salida = new HashMap<String, String>();
		Map<String, String> esperado = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("E02      PATTERN NAME=MAIL,PATKW=(ADREMI='Operacion.Grupo@catalanaoccidente.com',ASUNTO='ALTAS Y CESES DE PEAP',SORTIDA=PEAPES.TXT,DATENVI='',HORENVI='',TIPMAIL=AGE,DADA721='RELACION DE ALTAS Y CESES DE PEAP. ',DADA722='',DADA723='',DADA724='',ADRDE1='comercial.informatica@catalanaocci.es',ADRDES=' ',ENTRADA=PEAPES01)END");
		
		esperado.put("ASUNTO", "ALTAS Y CESES DE PEAP");
		esperado.put("DADA721", "RELACION DE ALTAS Y CESES DE PEAP. ");
		esperado.put("SORTIDA", "PEAPES.TXT");
		esperado.put("TIPMAIL", "AGE");
		esperado.put("ENTRADA", "PEAPES01");
		esperado.put("ADRDES", " ");
		esperado.put("ADREMI", "Operacion.Grupo@catalanaoccidente.com");
		esperado.put("ADRDE1", "comercial.informatica@catalanaocci.es");
		esperado.put("NAME", "MAIL");

		salida = lectorPasos.leerPaso(pasos);
		
		assertEquals(esperado, salida);		
	}
//	------------------ leerPasoSort -----------------
	@Test
	public void testLeerPaso() {
		mainApp.letraPaso = "D";
		mainApp.programa = "AUT21D";
		Map<String, String> salida = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("D04      SORT  TIME=60,REGION=6M");
		pasos.add("SYSIN    DATA  *");
		pasos.add(" SORT FIELDS=(16,3,CH,A,19,4,CH,A)");
		pasos.add("         DATAEND");
		pasos.add("SORTIN   FILE  NAME=EMPLE004,MODE=I");
		pasos.add("         FILE  NAME=EMPLE002,MODE=I");
		pasos.add("SORTOUT  FILE  NAME=EMPLE005,MODE=O,REST=(YES,DELETE)");
		
		salida = lectorPasos.leerPasoSort(pasos);
		
		assertEquals(" SORT FIELDS=(16,3,CH,A,19,4,CH,A)", salida.get("SORT1"));		
	}
	
//	------------------ leerCond -----------------
	@Test
	public void testLeerCond() {
		String linea = "A00      STEP  PGM=AGE01A21,PATTERN=DB2,PATKW=(PARDB2='FECHA=&FECHAD,&GENER,&ACT'),REGION=6M,DYNAMNBR=20,SORT=(100,367,K),COND1=(0,LT)";

		assertEquals("=(0,LT)", lectorPasos.leerCond(linea, 127));
	}
	
//	------------------ leerCond -----------------
	@Test
	public void testLeerPasoJOPCREC() {
		mainApp.letraPaso = "J";
		mainApp.programa = "AUT01J";
		Map<String, String> salida = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("J04      STEP  PGM=EQQEVPGM,REGION=4M");
		pasos.add("EQQMLIB  FILE  NAME=OPCIMESP,MODE=IFILE  NAME=OPCIMSG0,MODE=I");
		pasos.add("EQQMLOG  REPORT SYSOUT=*");
		pasos.add("SYSIN    DATA  *");
		pasos.add("SRSTAT 'AUT.ASITU12.AUT01J' SUBSYS(OPAP) AVAIL(NO)DATAEND");
		pasos.add("*----------------------------------------------------------------------");
		pasos.add("* VALIDACION FICHERO RETORNO ASITUR,");
		pasos.add("* RESPUESTA A ENVIOS INFORMACION.");
		pasos.add("*----------------------------------------------------------------------");
		
		salida = lectorPasos.leerPasoJOPCREC(pasos);
		
		assertEquals("AUT.ASITU12.AUT01J", salida.get("SRSTAT"));
	}
	
//	------------------ leerPasoJFusionGenquad -----------------
	@Test
	public void testLeerPasoJFusionGenquad() {
		mainApp.letraPaso = "S";
		mainApp.programa = "DIV21S";
		Map<String, String> salida = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("S00      STEP  PGM=SOF07200,PATTERN=DB2,PATKW=(PARDB2='GENER=&GENER,APL");
		pasos.add("               =DIV,QUADRE=DIV21SQ0'),LANG=CBL");
		pasos.add("FICHA    DATA  *,MBR=DIV21S00");
		pasos.add("ENTRADA=CPOSU01,CPOSU02,CPOSU03,CPOSU04,CPOSU05");
		pasos.add("SORTIDA=CPOSU");
		pasos.add("         DATAEND");
		pasos.add("CPOSU01  FILE  NAME=CPOSU000,MODE=I,USERDATA=GENE1");
		pasos.add("CPOSU02  FILE  NAME=CPOSU000,MODE=I,USERDATA=GENE2");
		pasos.add("CPOSU03  FILE  NAME=CPOSU000,MODE=I,USERDATA=GENE3");
		pasos.add("CPOSU04  FILE  NAME=CPOSU000,MODE=I,USERDATA=GENE4");
		pasos.add("CPOSU05  FILE  NAME=CPOSU000,MODE=I,USERDATA=GENE5");
		pasos.add("CPOSU    FILE  NAME=CPOSU010,MODE=O,REST=(YES,DELETE),MGMTCLAS=EXLI0060");
		pasos.add("CUADRE   REPORT SYSOUT=*");
		
		salida = lectorPasos.leerPasoJFusionGenquad(pasos);
		
		assertEquals("DIV", salida.get("APL"));
		assertEquals("DIV21SQ0", salida.get("QUADRE"));
		assertEquals("SORTIDA=CPOSU", salida.get("FICHA2"));
		assertEquals("ENTRADA=CPOSU01,CPOSU02,CPOSU03,CPOSU04,CPOSU05", salida.get("FICHA1"));
	}
	
//	------------------ leerPasoJPAUSA -----------------
	@Test
	public void testLeerPasoJPAUSA() {
		mainApp.letraPaso = "S";
		mainApp.programa = "SIN04S";
		Map<String, String> salida = new HashMap<String, String>();
		ArrayList<String> pasos = new ArrayList<String>();
		pasos.add("S00      STEP  PGM=SOF07070,PARM=('600')");
		pasos.add("**---------------------------------------------------------------------");
		pasos.add("**    CIERRE MENSUAL SDM DE S.BILBAO");
		pasos.add("**    VIGILAR RESTART TIENE IF ANIDADO");
		pasos.add("**    PRIMER PASO ES UN  DELAY DE 10 MINUTOS SOLICITADO POR MARGA");
		pasos.add("**---------------------------------------------------------------------");
		
		salida = lectorPasos.leerPasoJPAUSA(pasos);
		
		assertEquals("600", salida.get("PARM"));
	}
}
