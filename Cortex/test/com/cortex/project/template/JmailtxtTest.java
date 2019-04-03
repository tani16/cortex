package com.cortex.project.template;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import com.cortex.project.ExceptionCortex;
import com.cortex.project.templates.JMAILTXT;


public class JmailtxtTest {
	
	String entrada;
	String salida;
	String esperado;
	
	String letraPaso = "N";
	String numeroPaso = "89";
	
	private static final Map<String, String> datos; 
	static { datos = new HashMap<>(); 
			 datos.put("DADA723", "SALUDOS"); 
			 datos.put("ASUNTO", "PETADA HOST REC01N - RECIBOS Y CARTAS");
			 datos.put("DADA721", "EL PROCESO DE RECIBOS Y CARTAS DE PAGO ESTA EN ERROR");
			 datos.put("TIPMAIL", "REC");
			 datos.put("NAME", "MAILTXT");
			 datos.put("ADRDES", "Operacion.Grupo@catalanaoccidente.com");
			 datos.put("ADREMI", "Explotacion.SCO@catalanaoccidente.com");
			 datos.put("ADRDE2", "oscar.subirana@catalanaoccidente.com");
			 datos.put("ADRDE1", "impresion.inocsa@catalanaoccidente.com");
			}	
	JMAILTXT jmailtxt = new JMAILTXT(letraPaso, numeroPaso, datos);
	
	@Test
	public void jmailtxtLine2Test() {
		entrada  = "//---      EXEC MAILTXT                                                ";
		esperado = "//N89      EXEC MAILTXT";
		
		salida = jmailtxt.processJMAILTXT(entrada, 2);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine3Test() {
		entrada  = "//SYSIN    DD  *                                                       ";
		esperado = "//SYSIN    DD  *";
		
		salida = jmailtxt.processJMAILTXT(entrada, 3);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine4Test() {
		entrada  = "DESASSU=                                                               ";
		esperado = "DESASSU=PETADA HOST REC01N - RECIBOS Y CARTAS";
		
		salida = jmailtxt.processJMAILTXT(entrada, 4);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine5Test() {
		entrada  = "DOMMARE=                                                               ";
		esperado = "DOMMARE=Explotacion.SCO@catalanaoccidente.com";
		
		salida = jmailtxt.processJMAILTXT(entrada, 5);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine6Test() {
		entrada  = "DOMMADE=                                                               ";
		esperado = "DOMMADE=Operacion.Grupo@catalanaoccidente.com";
		
		salida = jmailtxt.processJMAILTXT(entrada, 6);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine7Test() {
		entrada  = "DOMMAD1=                                                               ";
		esperado = "DOMMAD1=impresion.inocsa@catalanaoccidente.com";
		
		salida = jmailtxt.processJMAILTXT(entrada, 7);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine8Test() {
		entrada  = "DOMMAD2=                                                               ";
		esperado = "DOMMAD2=oscar.subirana@catalanaoccidente.com";
		
		salida = jmailtxt.processJMAILTXT(entrada, 8);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine9Test() {
		entrada  = "DOMMAD3=                                                               ";
		esperado = "DOMMAD3=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 9);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine10Test() {
		entrada  = "TIPMAIL=                                                               ";
		esperado = "TIPMAIL=REC";
		
		salida = jmailtxt.processJMAILTXT(entrada, 10);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine12Test() {
		entrada  = "UIDPETI=                                                               ";
		esperado = "UIDPETI=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 12);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine13Test() {
		entrada  = "IDEANEX=                                                               ";
		esperado = "IDEANEX=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 13);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine14Test() {
		entrada  = "DATENVI=                                                               ";
		esperado = "DATENVI=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 14);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine15Test() {
		entrada  = "HORENVI=                                                               ";
		esperado = "HORENVI=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 15);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine16Test() {
		entrada  = "DADES01=                                                ";
		esperado = "DADES01=EL PROCESO DE RECIBOS Y CARTAS DE PAGO ESTA EN ERROR";
		
		salida = jmailtxt.processJMAILTXT(entrada, 16);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine17Test() {
		entrada  = "DADES02=     ";
		esperado = "DADES02=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 17);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine18Test() {
		entrada  = "DADES03=            ";
		esperado = "DADES03=SALUDOS";
		
		salida = jmailtxt.processJMAILTXT(entrada, 18);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine19Test() {
		entrada  = "DADES04=            ";
		esperado = "DADES04=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 19);
				
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jmailtxtLine20Test() {
		entrada  = "DADES05=            ";
		esperado = "DADES05=";
		
		salida = jmailtxt.processJMAILTXT(entrada, 19);
				
		assertEquals(esperado, salida);
	}
	
	
	
}
