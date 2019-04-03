package com.cortex.project.template;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import com.cortex.project.ExceptionCortex;
import com.cortex.project.mainApp;
import com.cortex.project.templates.JFTPSEND;


public class JftpsendTest {
	String entrada;
	String salida;
	String esperado;
	
	String letraPaso = "H";
	String numeroPaso = "05";
	int pasoE = 10;

	private static final Map<String, String> datos; 
	static { datos = new HashMap<>(); 
			 datos.put("MSG", "PEQB"); 
			 datos.put("FHOST", "CLI11H02");
			 datos.put("FDEST", "CLI11H02.TXT");
			 datos.put("DES", "IN");
			 datos.put("NAME", "FTPSEND");
			}	
	// Prueba CLI11H
	JFTPSEND jftpsend1 = new JFTPSEND(letraPaso, numeroPaso, pasoE, datos);

	@Test
	public void jftpsendLine2Test() {
		entrada  = "//---    EXEC FTPSEND,                                                 ";
		esperado = "//H05    EXEC FTPSEND,";
		
		try {
			salida = jftpsend1.processJFTPSEND(entrada, 2);
		} catch (ExceptionCortex e) {
			e.printStackTrace();
		}
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jftpsendLine3Test() {
		entrada  = "// DES=destino,                            <== nombre destino red      ";
		esperado = "// DES=IN,                                 <== nombre destino red";
		
		try {
			salida = jftpsend1.processJFTPSEND(entrada, 3);
		} catch (ExceptionCortex e) {
			e.printStackTrace();
		}
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jftpsendLine4Test() {
		entrada  = "// HOST=,                                  <== nombre fich host        ";
		esperado = "// HOST=Z.CLI.CLI11H02.CLI11H.&GENER,      <== nombre fich host";
		mainApp.programa = "CLI11H";
		try {
			salida = jftpsend1.processJFTPSEND(entrada, 4);
		} catch (ExceptionCortex e) {
			e.printStackTrace();
		}
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jftpsendLine5Test() {
		entrada  = "// FIT=nomfichred                          <== nombre fich red         ";
		esperado = "// FIT=CLI11H02.TXT,                       <== nombre fich red";
		
		try {
			salida = jftpsend1.processJFTPSEND(entrada, 5);
		} catch (ExceptionCortex e) {
			e.printStackTrace();
		}
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jftpsendLine6Test() {
		entrada  = "//*DIR=XXX                                 <== directorio (opc.)       ";
		esperado = "//*DIR=XXX                                 <== directorio (opc.)";
		
		try {
			salida = jftpsend1.processJFTPSEND(entrada, 6);
		} catch (ExceptionCortex e) {
			e.printStackTrace();
		}
		
		assertEquals(esperado, salida);
	}
	
	@Test
	public void jftpsendLine7Test() {
		entrada  = "//*MSG='UE----,UE----'                     <== aviso usuario (opc.)    ";
		esperado = "// MSG='PEQB'                              <== aviso usuario (opc.)";
		
		try {
			salida = jftpsend1.processJFTPSEND(entrada, 7);
		} catch (ExceptionCortex e) {
			e.printStackTrace();
		}
		
		assertEquals(esperado, salida);
	}
	
}
