/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinyql;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * 9 de diciembre 2020
 *
 * Programa que compila un lenguaje
 * inspirado en sql para la materia
 * de lenguajes y automatas
 *
 */
public class TinyQL {

    
    
    private Character[] lenguaje;// vector que almacena todos los simbolos del lenguaje
    private Integer pos; //entero que define la posicion del vector donde se alamcenara todo nuestro lenguaje
    static String archivo; //variable con la que se va a abrir un archivo un fichero de texto
    public String rutaTablaDeSImbolos; //localizacion de la tabla de simbolos
    static String bd, asm; //archivo de la BD
    public ArrayList<String> variables = new ArrayList<>();
    static ArrayList<String> tablasVariables=new ArrayList<>();
    static ArrayList<String> columnasVariables=new ArrayList<>();
    public static Boolean SegirCompilando = true;
    
    TinyQL() {
        rutaTablaDeSImbolos = "C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL\\src\\tinyql\\tablaDeSImbolos.txt";
        variables.add("");
    }

    public static void main(String[] args) throws IOException {
        SegirCompilando = true;
//        Pattern.matches("DE", "");

        archivo="C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL\\src\\tinyql\\CodigoFuente.txt";
        bd="C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL\\src\\tinyql\\Variables.txt";
        asm ="C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL\\src\\tinyql\\asm.txt";
        muestraContenido();// verificar si se pudo abrir el archivo
        TinyQL tinyQL = new TinyQL(); //instaciamiento de clase
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////INICIA PROCESO DE COMPILACION////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        tinyQL.CargarLenguaje(); //se carga el lenguaje en memoria
        if(!tinyQL.BarridoDeVerificacionDeSimbolos()) //verificar que todo lo que este escrito en el fichero de texto exista en nuestro lenguaje
            SegirCompilando=false;
            
        
        if(SegirCompilando){
        
            tinyQL.cargarPatrones();    // se cargan los patrones en memoria
//            PatronesSintatico();
            if(tinyQL.AnalisisSintatico2()){
            
                variablesCargadas();
                analisisSemantico();
                if (SegirCompilando){

                    GenerarCodigoIntermedio();
                    tinyQL.GenerarCodigoASM();

                }
            }
        }
        
        
    }
    public void GenerarCodigoASM() throws IOException {

        FileReader f = new FileReader(asm);
        BufferedReader b = new BufferedReader(f);
        String cadena,nombredb;
        String []splt;
        ArrayList<String> variablesCreate = new ArrayList<>(),
                variablesInsert = new ArrayList<>(),
                variablesSelect = new ArrayList<>(),
        variablesUpdate = new ArrayList<>(),
        variablesDelete = new ArrayList<>(),
        variablesDrop = new ArrayList<>();

        boolean isFirstLine = true;
        FileWriter writeASM = new FileWriter("C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL\\src\\tinyql\\asm.asm");
        writeASM.write(".model small\n" +
                ".stack\n" +
                ".data\n" +
                "lineBreak db 10,13,\"$\"\n" +
                "dospuntos db \" : $\"\n" +
                "deletedb db \"DB droped$\"\n");
        while((cadena=b.readLine()) !=null) {

            splt = cadena.split(" ");
            switch (splt[0]) {
                case "CREATE", "create" -> {
                    if (isFirstLine) {
                        nombredb = splt[1];
                        writeASM.write("dbname db \"DB:" + nombredb + "$\"\n");
                        isFirstLine = false;
                    }
                    writeASM.write("create" + variablesCreate.size() + " db \"" + splt[3] + "$\"\n");
                    variablesCreate.add(splt[3]);
                }
                case "INSERT", "insert" -> {
                    writeASM.write("insert" + variablesInsert.size() + " db \"" + splt[3] + "$\"\n");
                    variablesInsert.add(splt[3]);
                }
                case "SELECT", "select" -> {
                    writeASM.write("select" + variablesSelect.size() + " db \"" + splt[3] + "$\"\n");
                    variablesSelect.add(splt[3]);
                }
                case "Update", "UPDATE" -> {
                    writeASM.write("update" + variablesUpdate.size() + " db \"" + splt[3] + "$\"\n");
                    variablesUpdate.add(splt[3]);
                }
                case "DELETE", "delete" -> {
                    writeASM.write("delete" + variablesDelete.size() + " db \"" + splt[3] + "$\"\n");
                    variablesDelete.add(splt[3]);
                }
                case "DROP", "drop" -> {
                    writeASM.write("drop" + variablesDrop.size() + " db \"" + splt[3] + "$\"\n");
                    variablesDrop.add(splt[3]);
                }
            }
        }

        writeASM.write(".code\n" +
                            "MAIN PROC FAR  \n" +
                            "MOV AX,@DATA\n" +
                            "MOV DS,AX\n " +
                            "LEA DX,dbname\n" +
                            "MOV AH,09H \n" +
                            "INT 21H  \n" +
                            "LEA DX, lineBreak\n" +
                            "MOV AH,09H \n" +
                            "INT 21H  \n");

        //cargar create
        for (int i = 0 ; i < variablesCreate.size() ; i++){

            writeASM.write("LEA DX," + "create" + i + "  \n" +
                                        "MOV AH,09H \n" +
                                        " INT 21H  \n" +
                                        "LEA DX, lineBreak\n" +
                                        "MOV AH,09H \n" +
                                        "INT 21H  \n");
        }
        //cargar insert
        for (int i = 0 ; i < variablesInsert.size() ; i++){

            writeASM.write("LEA DX," + "create" + i + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX," + "dospuntos" + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX," + "insert" + i + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX, lineBreak\n" +
                    "MOV AH,09H \n" +
                    "INT 21H  \n");
        }

        //cargar select

        if (variablesSelect.contains("*"))

        for (int i = 0 ; i < variablesInsert.size() ; i++){

            writeASM.write("LEA DX," + "create" + i + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX," + "dospuntos" + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX," + "insert" + i + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX, lineBreak\n" +
                    "MOV AH,09H \n" +
                    "INT 21H  \n");

        }
        else
            for (int i = 0 ; i < variablesInsert.size()  ; i++){

                if (variablesSelect.get(i).contains(variablesSelect.get(i)))
                writeASM.write("LEA DX," + "create" + i + "  \n" +
                        "MOV AH,09H \n" +
                        " INT 21H  \n" +
                        "LEA DX," + "dospuntos" + "  \n" +
                        "MOV AH,09H \n" +
                        " INT 21H  \n" +
                        "LEA DX," + "insert" + i + "  \n" +
                        "MOV AH,09H \n" +
                        " INT 21H  \n" +
                        "LEA DX, lineBreak\n" +
                        "MOV AH,09H \n" +
                        "INT 21H  \n");
            }

            //cargar update
        for (int i = 0 ; i < variablesUpdate.size()  ; i++){

                writeASM.write("LEA DX," + "create" + i + "  \n" +
                        "MOV AH,09H \n" +
                        " INT 21H  \n" +
                        "LEA DX," + "dospuntos" + "  \n" +
                        "MOV AH,09H \n" +
                        " INT 21H  \n" +
                        "LEA DX," + "update" + i + "  \n" +
                        "MOV AH,09H \n" +
                        " INT 21H  \n" +
                        "LEA DX, lineBreak\n" +
                        "MOV AH,09H \n" +
                        "INT 21H  \n");
        }
        //cargar delete
        for (int i = 0 ; i < variablesCreate.size() ; i++){

            writeASM.write("LEA DX," + "create" + i + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX, lineBreak\n" +
                    "MOV AH,09H \n" +
                    "INT 21H  \n");
        }
        //cargar drop
        if (variablesDrop.size() > 0)
            writeASM.write("LEA DX," + "deletedb" + "  \n" +
                    "MOV AH,09H \n" +
                    " INT 21H  \n" +
                    "LEA DX, lineBreak\n" +
                    "MOV AH,09H \n" +
                    "INT 21H  \n");

        writeASM.write("MOV AH,4CH \n" +
                        " INT 21H  \n" +
                        "MAIN ENDP  \n" +
                        "END MAIN ");
        writeASM.close();
        System.out.println("8.- Generacion de codigo objeto completado");

    }
    public static void GenerarCodigoIntermedio() throws IOException {

        FileWriter WriteASM = new FileWriter("C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL\\src\\tinyql\\asm.txt");
//        WriteASM.write("Files in Java might be tricky, but it is fun enough!");
//        WriteASM.close();

        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        String cadena;
        String tipoDeOperacion;
        StringBuilder operando1;
        String operando2;
        String nombresColumnas;
        String valores;
        while((cadena=b.readLine()) !=null)
        {
            tipoDeOperacion = cadena.substring(0, cadena.indexOf(" "));

            switch (tipoDeOperacion) {
                case "INSERT":
                case "insert":

                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    operando1 = new StringBuilder(cadena.substring(0, cadena.indexOf('"')));
                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    if (cadena.contains(",")) { // quiere decir que tiene mas de un campo

                        nombresColumnas = cadena.substring(0, cadena.indexOf(")"));
                        cadena = cadena.substring(cadena.indexOf("(") + 2);
                        valores = cadena.substring(0, cadena.indexOf(")"));

                        while (nombresColumnas.contains(",")) {

                            operando2 = valores.substring(0, valores.indexOf('"'));
//                            System.out.println(tipoDeOperacion + " " + operando1 + "." + nombresColumnas.substring(0, nombresColumnas.indexOf('"')) + " , " + operando2);
                            WriteASM.write(tipoDeOperacion + " " + operando1 + "." + nombresColumnas.substring(0, nombresColumnas.indexOf('"')) + " , " + operando2 + "\n");
                            nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 3);
                            valores = valores.substring(valores.indexOf('"') + 3);

                        }
                        operando2 = valores.substring(0, valores.indexOf('"'));
//                        System.out.println(tipoDeOperacion + " " + operando1 + "." + nombresColumnas.substring(0, nombresColumnas.indexOf('"')) + " , " + operando2);
                        WriteASM.write(tipoDeOperacion + " " + operando1 + "." + nombresColumnas.substring(0, nombresColumnas.indexOf('"')) + " , " + operando2 + "\n");

                    } else { //si solo contien un campo a insertar

                        operando1.append(".").append(cadena, 0, cadena.indexOf('"'));
                        cadena = cadena.substring(cadena.indexOf("("));
                        operando2 = cadena.substring(0, cadena.length() - 3);
//                        System.out.println(tipoDeOperacion + " " + operando1 + "," + operando2);
                        WriteASM.write(tipoDeOperacion + " " + operando1 + "," + operando2 + "\n");
                    }


                    break;
                case "SELECT":
                case "select":

                    if (cadena.contains("*")) {
//                        System.out.println("SELECT " + cadena.substring(cadena.indexOf('"') + 1, cadena.indexOf(";") - 1) + " , *");
                        WriteASM.write("SELECT " + cadena.substring(cadena.indexOf('"') + 1, cadena.indexOf(";") - 1) + " , *" + "\n");
                    } else {
                        nombresColumnas = cadena.substring(cadena.indexOf('"') + 1, cadena.indexOf(")"));
                        cadena = cadena.substring(cadena.indexOf(")"));
                        cadena = cadena.substring(cadena.indexOf('"') + 1);
                        operando2 = cadena.substring(0, cadena.indexOf('"'));
                        while (nombresColumnas.contains(",")) {

//                            System.out.println(tipoDeOperacion + " " + operando2 + " , " + nombresColumnas.substring(0, nombresColumnas.indexOf('"')));
                            WriteASM.write(tipoDeOperacion + " " + operando2 + " , " + nombresColumnas.substring(0, nombresColumnas.indexOf('"')) + "\n");
                            nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 3);

                        }
//                        System.out.println(tipoDeOperacion + " " + operando2 + " , " + nombresColumnas.substring(0, nombresColumnas.indexOf('"')));
                        WriteASM.write(tipoDeOperacion + " " + operando2 + " , " + nombresColumnas.substring(0, nombresColumnas.indexOf('"')) + "\n");

                    }

                    break;
                case "UPDATE":
                case "update":

                    String columnaWhere, campoWhere ;
                    columnaWhere = cadena.substring(cadena.indexOf(")"));
                    columnaWhere = columnaWhere.substring(columnaWhere.indexOf('"') + 1);
                    campoWhere = columnaWhere;
                    columnaWhere = columnaWhere.substring(0,columnaWhere.indexOf('"'));
                    campoWhere = campoWhere.substring(campoWhere.indexOf('"') + 1);
                    campoWhere = campoWhere.substring(campoWhere.indexOf('"') + 1);
                    campoWhere = campoWhere.substring(0,campoWhere.indexOf('"'));

                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    operando1 = new StringBuilder(cadena.substring(0, cadena.indexOf('"')));
                    operando1.append(".").append(columnaWhere);
//                    System.out.println("1 WHERE " + operando1 + " , " + campoWhere);
                    WriteASM.write("1 WHERE " + operando1 + " , " + campoWhere + "\n");

                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                nombresColumnas = cadena.substring(cadena.indexOf('"') + 1, cadena.indexOf(")"));
                    while(nombresColumnas.contains(",")){

                    operando1 = new StringBuilder();
                    operando1.append(nombresColumnas,0,nombresColumnas.indexOf('"'));
                    nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 1);
                    nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 1);
                    operando2 = nombresColumnas.substring(0, nombresColumnas.indexOf('"'));
//                    System.out.println(tipoDeOperacion " "  + operando1 + " , " + operando2);
                    WriteASM.write(tipoDeOperacion + " " + operando1 + " , " + operando2 + "\n");

                    nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 1);
                    nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 1);

                }
                    operando1 = new StringBuilder();
                    operando1.append(nombresColumnas,0,nombresColumnas.indexOf('"'));
                    nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 1);
                    nombresColumnas = nombresColumnas.substring(nombresColumnas.indexOf('"') + 1);
                    operando2 = nombresColumnas.substring(0, nombresColumnas.indexOf('"'));
//                    System.out.println(tipoDeOperacion + " " + operando1 + " , " + operando2);
                    WriteASM.write(tipoDeOperacion + " " + operando1 + " , " + operando2 + "\n");

                    break;
                case "DELETE":
                case "delete":

                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    operando1 = new StringBuilder(cadena.substring(0, cadena.indexOf('"')));
                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    operando1.append(".").append(cadena, 0, cadena.indexOf('"'));
                    cadena = cadena.substring(cadena.indexOf('"') + 1);
//                    System.out.println("DELETE " + operando1 + " , " + cadena.substring(cadena.indexOf('"') + 1, cadena.length() - 2));
                    WriteASM.write("DELETE " + operando1 + " , " + cadena.substring(cadena.indexOf('"') + 1, cadena.length() - 2) + "\n");

                    break;

                case "CREATE":
                case "create":

                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    operando1 = new StringBuilder(cadena.substring(0, cadena.indexOf('"')));
                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    cadena = cadena.substring(cadena.indexOf('"') + 1);

                    while(cadena.contains(",")){

//                        System.out.println(tipoDeOperacion + " " + operando1 + " , " + cadena.substring(0, cadena.indexOf('"')));
                        WriteASM.write(tipoDeOperacion + " " + operando1 + " , " + cadena.substring(0, cadena.indexOf('"')) + "\n");
                        cadena = cadena.substring(cadena.indexOf('"') + 3);

                    }
//                    System.out.println(tipoDeOperacion + " " + operando1 + " , " + cadena.substring(0, cadena.indexOf('"')));
                    WriteASM.write(tipoDeOperacion + " " + operando1 + " , " + cadena.substring(0, cadena.indexOf('"')) + "\n");
                    break;

                case "DROP":
                case "drop":

                    cadena = cadena.substring(cadena.indexOf('"') + 1);
                    operando1 = new StringBuilder(cadena.substring(0, cadena.indexOf('"')));
//                    System.out.println(tipoDeOperacion + " " + operando1 + " , 0");
                    WriteASM.write(tipoDeOperacion + " " + operando1 + " , 0" + "\n");
                    break;
            }


        }
        WriteASM.close();
        System.out.println("7.- Generacion de codigo intermedio completado");

    }
    static void analisisSemantico() throws IOException
    {
        SegirCompilando = true;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        String cadena;
        int linea=1;
        boolean flag=false;//bandera que me ayudara a salirme del while por si el break no puede hacerlo
        while((cadena=b.readLine()) !=null && !flag)
        {
            String []parts=cadena.split(" ");
           
            if(parts[0].equals("CREATE") || parts[0].equals("create"))
            {//Este me permitira identificar si ya existe la tabla y que no se pueda guardar 2 veces la misma tabla
                if(tablaExiste(parts[2]))
                {
                    System.out.println("Error en la linea "+linea+", ya existe la tabla "+parts[2]+" en la base de datos");
                    SegirCompilando = false;
                    break;
                }else{
                    tablasVariables.add(parts[2].substring(1, parts[2].length()-1));
                    columnasVariables.add(parts[3]);
                }
            }
            
            if(parts[0].equals("INSERT") || parts[0].equals("insert"))
            {//Este me permitira identificar si existe la tabla en la que se desea guardar datos
                if(!tablaExiste(parts[2]))
                {
                    System.out.println("Error en la linea "+linea+", no existe la tabla "+parts[2]+" en la base de datos");
                    SegirCompilando = false;
                    break;
                }
                else
                {
                    String []columnas=parts[3].split(",");
                    if(columnaExiste(columnas[0].substring(2, columnas[0].length() - 1)))
                    {
                        System.out.println("Error en la linea "+linea+", no existe la columna '"+columnas[0].substring(2,columnas[0].length()-1)+"' en la base de datos");
                        SegirCompilando = false;
                        break;
                    }
                    for(int i=1;i<columnas.length-1;i++)
                    {
//                        System.out.println(columnas[i].substring(1,columnas[i].length()-1));
                        if(columnaExiste(columnas[i].substring(1, columnas[i].length() - 1)))
                        {
                            System.out.println("Error en la linea "+linea+", no existe la columna '"+columnas[i].substring(1,columnas[i].length()-1)+"' en la base de datos");
                            SegirCompilando = false;
                            flag=true;
                            break;
                        }
                    }
                    if(columnaExiste(columnas[columnas.length - 1].substring(1, columnas[columnas.length - 1].length() - 2)))
                    {
                        System.out.println("Error en la linea "+linea+", no existe la columna '"+columnas[columnas.length-1].substring(1,columnas[columnas.length-1].length()-2)+"' en la base de datos");
                        SegirCompilando = false;
                        break;
                    }
                }
            }
           
            if(parts[0].equals("DELETE") || parts[0].equals("delete"))
            {
                if(!tablaExiste(parts[2]))
                {
                    System.out.println("Error en la linea "+linea+", no existe la tabla "+parts[2]+" en la base de datos");
                    SegirCompilando = false;
                    break;
                }
                if(columnaExiste(parts[4].substring(1, parts[4].length() - 1)))
                {
                    System.out.println("Error en la linea "+linea+", no existe la columna '"+parts[4].substring(1,parts[4].length()-1)+"' en la base de datos");
                    SegirCompilando = false;
                    break;
                }
            }
            
            if(parts[0].equals("SELECT") || parts[0].equals("select"))
            {
                if(!tablaExiste(parts[3]))
                {
                    System.out.println("Error en la linea "+linea+", no existe la tabla "+parts[3]+" en la base de datos");
                    SegirCompilando = false;
                    break;
                }
                else
                {
                    if(!parts[1].equals("*"))
                    {
                        String []columnas=parts[1].split(",");
                        if(columnaExiste(columnas[0].substring(2, columnas[0].length() - 1)))
                        {
                            System.out.println("Error en la linea "+linea+", no existe la columna '"+columnas[0].substring(2,columnas[0].length()-1)+"' en la base de datos");
                            SegirCompilando = false;
                            break;
                        }
                        for(int i=1;i<columnas.length-1;i++)
                        {
//                            System.out.println(columnas[i].substring(1,columnas[i].length()-1));
                            if(columnaExiste(columnas[i].substring(1, columnas[i].length() - 1)))
                            {
                                System.out.println("Error en la linea "+linea+", no existe la columna '"+columnas[i].substring(1,columnas[i].length()-1)+"' en la base de datos");
                                SegirCompilando = false;
                                flag=true;
                                break;
                            }
                        }
                        if(columnaExiste(columnas[columnas.length - 1].substring(1, columnas[columnas.length - 1].length() - 2)))
                        {
                            System.out.println("Error en la linea "+linea+", no existe la columna '"+columnas[columnas.length-1].substring(1,columnas[columnas.length-1].length()-2)+"' en la base de datos");
                            SegirCompilando = false;
                            break;
                        }
                    }
                }
            }
            
            if(parts[0].equals("UPDATE") || parts[0].equals("update"))
            {
                if(!tablaExiste(parts[1]))
                {
                    System.out.println("Error en la linea "+linea+", no existe la tabla "+parts[1]+" en la base de datos");
                    SegirCompilando = false;
                    break;
                }
                else
                {
                    int aux=0;
                    for(int i=3;i<=parts.length;i++)
                        if(parts[i].equals("WHERE") || parts[i].equals("where"))
                        {
                            aux=i-1;
                            break;
                        }
                    if(columnaExiste(parts[3].substring(2, parts[3].length() - 1)))
                    {
                        System.out.println("Error en la linea "+linea+", no existe la columna "+parts[3].substring(2,parts[3].length()-1)+" en la base de datos");
                        SegirCompilando = false;
                        break;
                    }
                    for(int i=6;i<=aux;i+=3)
                        if(columnaExiste(parts[i].substring(1, parts[i].length() - 1)))
                        {
                            System.out.println("Error en la linea "+linea+", no existe la columna '"+parts[i].substring(1,parts[i].length()-1)+"' en la base de datos");
                            SegirCompilando = false;
                            flag=true;
                            break;
                        }
                    aux+=2;
                    if(columnaExiste(parts[aux].substring(1, parts[aux].length() - 1)))
                    {
                        System.out.println("Error en la linea "+linea+", no existe la columna "+parts[aux].substring(1,parts[aux].length()-1)+" en la base de datos");
                        SegirCompilando = false;
                        break;
                    }
                }
            }
            linea++;
        }
    }
    static boolean tablaExiste(String a)
    {
        /*
        Esta funcion me permitira buscar en mi ArrayList si es que existe una tabla con dicho nombre
        en caso de que lo encuentre regresara una bandera en TRUE y en caso contrario regresara un FALSE
        */
        boolean ban=false;
        for(String tabla:tablasVariables)
        {
            if(a.contains(tabla))
            {
                ban=true;
                break;
            }
        }
        return ban;
    }
    
    static boolean columnaExiste(String a)
    {
        /*
        Esta funcion me permitira buscar en mi ArrayList si es que existe una columna con dicho nombre
        en caso de que lo encuentre regresara una bandera en TRUE y en caso contrario regresara un FALSE
        */
        boolean ban=false;
        for(String columna:columnasVariables)
        {
            if(columna.contains(a))
            {
                ban=true;
                break;
            }
        }
        return !ban;
    }
    static void variablesCargadas() throws IOException
    {
        FileReader f = new FileReader(bd);
        BufferedReader b = new BufferedReader(f);
        String cadena;
        while((cadena=b.readLine()) !=null)
        {
            String []parts=cadena.split(" ");
            tablasVariables.add(parts[0].substring(0, parts[0].length()-1));
            columnasVariables.add(parts[1]);
        }
        System.out.println("6.- Variables Cargadas");
    }
    public boolean AnalisisSintatico2() throws IOException {
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        String cadena;
        String patronInsert = "(INSERT|insert)( )+(INTO|into)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*(\\()( )*\"[a-zA-Z][a-zA-Z0-9]*\"( )*(( )*,( )*\"[a-zA-Z][a-zA-Z0-9]*\")*( )*(\\))( )*(VALUES|values)( )*(\\()( )*\"[a-zA-Z0-9 ]+\"( )*(( )*,( )*\"[a-zA-Z0-9 ]+\")*( )*(\\))( )*;",
        patronSelect1 = "(SELECT|select)( )*(\\()( )*\"[a-zA-Z][a-zA-Z0-9]*\"( )*(( )*,( )*\"[a-zA-Z][a-zA-Z0-9]*\")*( )*(\\))( )*(FROM|from)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*;",
        patronSelect2 ="(SELECT|select)( )*\\*( )*(FROM|from)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*;",
        patronUpdate = "(UPDATE|update)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )+(SET|set)( )*(\\()( )*\"[a-zA-Z][a-zA-Z0-9]*\"( )*=( )*\"[a-zA-Z0-9 ]+\"( )*(( )*,( )*\"[a-zA-Z][a-zA-Z0-9]*\"( )*=( )*\"[a-zA-Z0-9 ]+\")*( )*(\\))( )*(WHERE|where)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*=( )*\"[a-zA-Z0-9 ]+\"( )*;",
        patronDelete = "(DELETE|delete)( )+(FROM|from)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )+(WHERE|where)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*=( )*\"[a-zA-Z0-9 ]+\"( )*;",
        patronCreate = "(CREATE|create)( )+(TABLE|table)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*(\\()( )*\"[a-zA-Z][a-zA-Z0-9]*\"( )*(( )*,( )*\"[a-zA-Z][a-zA-Z0-9]*\")*( )*(\\))( )*;",
        patronDrop = "(DROP|drop)( )+(TABLE|table)( )+\"[a-zA-Z][a-zA-Z0-9]*\"( )*;"; 
        
        boolean isPattern = true;//bandera que me dice si cada una de las lineas no esta bien escrita,bandera que se activa si detecta errores
        int lineNumber = 1; // contador de linea del documento
        
        while((cadena = b.readLine()) != null && isPattern) { //&& flag
            isPattern = Pattern.matches(patronInsert, cadena) ||
                    Pattern.matches(patronSelect1, cadena) ||
                    Pattern.matches(patronSelect2, cadena) ||
                    Pattern.matches(patronUpdate, cadena) ||
                    Pattern.matches(patronDelete, cadena) ||
                    Pattern.matches(patronCreate, cadena) ||
                    Pattern.matches(patronDrop, cadena);
            
            if (!isPattern){
                
                System.out.println("Error sintatico en la linea: " + lineNumber);
            }
           
            lineNumber++;
            
        }
        if (isPattern)
            System.out.println("5.- Analisis sintatico Completa sin errores");
        
        
        return isPattern;
    }

    public static void muestraContenido() throws IOException {
//        String cadena;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
//        while((cadena = b.readLine())!=null) {
//            System.out.println(cadena);
//        }
        System.out.println("1.- Codigo fuente abierto correctamente");
        b.close();
    }

    public void CargarLenguaje(){
        pos = 0; //entero que define la posicion del vector donde se alamcenara todo nuestro lenguaje
        lenguaje = new Character[73];// tamano definido por todos los simbolos que representa el lenguaje
        
        for(int i=65;i<=90;i++){//ascii de las letras mayusculas del español NO incluye "Ñ"
            
            lenguaje[pos]=(char)i;
            //stem.out.println(lenguaje[pos]);
            pos++;
        
        }
        for(int i=97;i<=122;i++){//ascii de las letras minusculas del español NO incluye la "ñ" 
            
            lenguaje[pos]=(char)i;
            //System.out.println(lenguaje[pos]);
            pos++;
        
        }
//        se añaden las "ñ"
        lenguaje[pos]='ñ';
        pos++;
        lenguaje[pos]='Ñ';
        pos++;
        for(int i=48;i<=57;i++){//se agregan los numeros 0-9 en ascii
            
            lenguaje[pos]=(char)i;
            //System.out.println(lenguaje[pos]);
            pos++;
        
        }
//        adicion de caracteres especiales
        lenguaje[pos] = (char)39;// ' pero no me deja ponerlo manualmente
        
        pos++;
        lenguaje[pos] = '*';
        
        pos++;
        lenguaje[pos] = '"';
        
        pos++;
        lenguaje[pos] = ' ';
        
        pos++;
        lenguaje[pos] = ';';
        
        pos++;
        lenguaje[pos] = '=';
        
        pos++;
        lenguaje[pos] = ',';
        
        pos++;
        lenguaje[pos] = '(';
        
        pos++;
        lenguaje[pos] = ')';
        pos=0;
        System.out.println("2.- Alfabeto cargado");
    
    }
    public void cargarPatrones()
    {
        pos=0;
        char[] palabra;
        // vector que almacena los patrones del lenguaje
        Character[][] patrones = new Character[15][10];
        
        patrones[pos][0]='"';
        pos++;
        patrones[pos][0]=(char)39;//"'"
        pos++;
        patrones[pos][0]='*';
        pos++;
        patrones[pos][0]=',';
        pos++;
        patrones[pos][0]=';';
        pos++;
        patrones[pos][0]='=';
        pos++;
        
        palabra=new char[]{'s','e','t'};
        for(int i=0;i<3;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
        
        palabra=new char[]{'f','r','o','m'};
        for(int i=0;i<4;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
                
        palabra=new char[]{'i','n','t','o'};
        for(int i=0;i<4;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++; 
        
        palabra=new char[]{'w','h','e','r','e'};
        for(int i=0;i<5;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
        
        palabra=new char[]{'i','n','s','e','r','t'};
        for(int i=0;i<6;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
        
        palabra=new char[]{'s','e','l','e','c','t'};
        for(int i=0;i<6;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
        
        palabra=new char[]{'u','p','d','a','t','e'};
        for(int i=0;i<6;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
        
        palabra=new char[]{'d','e','l','e','t','e'};
        for(int i=0;i<6;i++)
        {
            patrones[pos][i]=palabra[i];
        }
        pos++;
        System.out.println("4.- Patrones cargados");
    }

    public Boolean BarridoDeVerificacionDeSimbolos() throws IOException{
        String cadena;
        char simbolo=' ';
        boolean simboloEncontrado = false; //bandera que se activa si un simbolo no se escuentra en el alfabeto
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        
        while((cadena = b.readLine()) != null) { //ciclo para leer el documento entero
            for (int i = 0; i < cadena.length() ; i++){ //ciclo para evaluar la cadena caracter por caracter
                simbolo = cadena.charAt(i);
                if(!CompararSimbolo(simbolo)){
                    simboloEncontrado=false;
                    break;
                } else
                    simboloEncontrado=true;
            }
            if(!simboloEncontrado)
                break;
        }
        b.close();
        if(!simboloEncontrado){
            System.out.println(simbolo+" NO es parte del alfabeto");
            return false;
        }
        else{
            System.out.println("3.- Barrido de verificacion de simbolos completado sin errores");
            return true;
        }
        
    }
    public Boolean CompararSimbolo(Character ch){
        
        boolean simboloEncontrado = false; //bandera que se activa si un simbolo no se escuentra en el alfabeto
        for(pos=0; pos<73;pos++){
            if(lenguaje[pos].equals(ch))
                simboloEncontrado=true;
        }
        
        return simboloEncontrado;
    }
    }
    
