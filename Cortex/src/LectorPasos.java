import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LectorPasos {

	public Map<String, String> leerDB2(ArrayList<String> pasos) {
		// TODO Auto-generated method stub
		Map<String, String> datos = new HashMap<String, String>();
		String clave, valor;
		int index = 0;
		
		for(int i = 0; i < pasos.size(); i++) {
			if (!pasos.get(i).startsWith("CUADRE")) {
				while (index != -1) {
					index = pasos.get(i).indexOf('=', index);
					if (index != -1 && pasos.get(i).charAt(index + 1) != '(') {
						clave = leerClave(pasos.get(i), index);
						valor = leerValor(pasos.get(i), index);
						if (!clave.equals("") && !valor.equals("")) {
							datos.put(clave, valor);
						}
					}
					if (index != - 1) {
						index ++;
					}
				}
			}	
		}
				
		
		return datos;
	}

	private String leerValor(String linea, int index) {
		// TODO Auto-generated method stub
		String valor = "";
		int fin = 0;
		if(linea.charAt(index + 1) == '\''){
			for(int i = index + 2; i < linea.length(); i++) {
				if (linea.charAt(i) == '\'') {
					fin = i;					
					i = linea.length() + 1;
				}
			}
			valor = linea.substring(index + 2, fin).replace(',', '-');
		}else{
			for(int i = index; i < linea.length(); i++) {
				if (linea.charAt(i) == ',' || linea.charAt(i) == ' ') {
					fin = i;
					i = linea.length() + 1;
				}
				if (fin == 0) {
					fin = linea.length();
				}
			}
			valor = linea.substring(index + 1, fin);
		}
		System.out.println(valor);
		
		return valor;
	}

	private String leerClave(String linea, int index) {
		// TODO Auto-generated method stub
		String clave = "";
		int inicio = 0;

		for (int i = index; i > 0; i--) {
			if (linea.charAt(i) == ' ' || linea.charAt(i) == '(' || linea.charAt(i) == ',') {
				inicio = i;
				i = 0;
			}
		}
		clave = linea.substring(inicio + 1, index);
		System.out.println(clave);
		
		return clave;
	}

}
