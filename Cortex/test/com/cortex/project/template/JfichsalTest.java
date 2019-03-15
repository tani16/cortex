package com.cortex.project.template;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import com.cortex.project.templates.JFICHSAL;


public class JfichsalTest {
	
	String letra; 
	String nombre;
	String esperado;
	String resultado;
	String linea;
	int pasoE = 0;
	Map<String, String> infoFich = new HashMap<>();
	Map<String, String> datos = new HashMap<>();
		
	@Test
	public void testJfichsalLine3() {
		
		linea = "//DDNAME-- DD DSN=Z.APL.XXXXXXXX.NOMMEM.&FAAMMDDV,";
		esperado = "//FREBULT  DD DSN=Z.COM.FREBULT2.COM13P.&GENEM,";
		letra = "P";
		nombre = "FREBULT ";
		infoFich.put("DISP", "NEW");
		infoFich.put("DSN", "COM.FREBULT2.COM13P.&GENEM");

		JFICHSAL jfichsal = new JFICHSAL(letra, nombre, pasoE, infoFich, datos);
		
		
		resultado = jfichsal.processJFICHSAL(linea, 3);
		
		assertEquals(esperado, resultado);
		
	}
}
