import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mainApp {

	public static String programa = "COM05IPCL";
	public Map<String, String> datos = new HashMap<String, String>();
	static String letraPaso = programa.substring(5,6);
	static int paso = 0;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String extension = ".txt";
		String linea;
		ArrayList<String> lineasPaso;
		int index = 0;
//-------------------------------------Ficheros-------------------------------------------------		
	    FileReader ficheroPCL = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\" + programa + extension);
	    BufferedReader lectorPCL = new BufferedReader(ficheroPCL);
	    
	    FileWriter ficheroCortex = new FileWriter("C:\\Users\\0014879\\Desktop\\Cortex\\Migrados\\" + programa.substring(0,6) + ".txt");
	    BufferedWriter writerCortex = new BufferedWriter(ficheroCortex);
//----------------------------------------------------------------------------------------------	    
	    
	    
	    
	    
//------------------------------------PROGRAMA--------------------------------------------------
	    
	    
//--------------Escribimos la cabecera
	    escribeJJOB(writerCortex);
	    
//--------------Recorremos el fichero PCL aislando por pasos	    
	    while((linea = lectorPCL.readLine())!=null) {
			lectorPCL.mark(0);
	    	lineasPaso = aislamientoDePaso(linea, lectorPCL);
	    	for(int i = 0; i < lineasPaso.size(); i++) {
	    		//Tenemos el Paso aislado en el array lineasPaso. Lo leemos y sacamos sus variables
	    		lecturaPaso(lineasPaso);
	    	}
	    	try {
		    	lectorPCL.reset();
			} catch (Exception e) {
				//Capturamos el error de si está marcada la última línea no falle el programa
			}

	    }
	    lectorPCL.close();
	    writerCortex.close();
	}
	
	
	private static void lecturaPaso(ArrayList<String> lineasPaso) {
		// TODO Auto-generated method stub
		int index = 0;
		for(int i = 0; i < lineasPaso.size(); i++) {
			do {
				System.out.println(index);
				index = lineasPaso.get(i).indexOf("=", index);

				System.out.println(index);
				if (index != -1) {
					if (lineasPaso.get(i).charAt(index + 1) != '(') {
						String clave;
						clave = lecturaClave(lineasPaso.get(i), index);
					}
					index ++;
				}
			} while (index != -1);			
		}
		
	}


	private static String lecturaClave(String linea, int fin) {
		// TODO Auto-generated method stub
		String clave = null;
		
		for(int i = fin; i >= 0; i--) {
			if (linea.charAt(i) == ' ' || linea.charAt(i) == ',' || linea.charAt(i) == '(') {
				clave = linea.substring(i + 1,fin);
				System.out.println(clave);
				i = -1;
			}
		}
		return clave;
	}


	private static void escribeJJOB(BufferedWriter writerCortex) throws IOException {
		// TODO Auto-generated method stub
		//----------------Fichero de plantilla JJOB--------------------------
	    FileReader ficheroJJOB = new FileReader("C:\\Users\\0014879\\Desktop\\Cortex\\Plantillas\\JJOB.txt");
	    BufferedReader lectorJJOB = new BufferedReader(ficheroJJOB);
	    //----------------Variables------------------------------------------
	    String linea;
	    int contadorLinea = 0;
    	
	    //----------------Método---------------------------------------------
	    while((linea = lectorJJOB.readLine()) != null) {
	    	contadorLinea ++;
	    	switch (contadorLinea) {
			//Solo modificamos la línea 1 de la plantilla
	    	case 1:
				linea = linea.replace("AAAAAA", programa.substring(0,6));
				break;

			default:
				break;
			}
	    	System.out.println("Escribimos: " + linea);
	    	writerCortex.write(linea);
	    	writerCortex.newLine();
	    }	
	}


	private static ArrayList<String> aislamientoDePaso(String linea, BufferedReader lectorPCL) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<String> lineasPaso = new ArrayList<String>();	
		boolean seguir = true;
		String numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
		System.out.println("entra" + letraPaso + String.valueOf(numeroPaso)+linea);
		if(linea.startsWith(letraPaso + String.valueOf(numeroPaso))) {
			paso += 2;
			numeroPaso = (paso < 10) ? "0" + String.valueOf(paso) : String.valueOf(paso) ;
			for(int i = 0; seguir; i++) {
				//System.out.println(linea);
				lineasPaso.add(linea);
				lectorPCL.mark(0);
				linea = lectorPCL.readLine();
				if (linea == null || linea.startsWith(letraPaso + String.valueOf(numeroPaso))) {
					seguir = false;
				}
			}
		}
		System.out.println("sale");
		return lineasPaso;
	}
}
