/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinyql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jdk.nashorn.internal.runtime.ListAdapter;

/**
 *
 * @author alfre
 */
public class TinyQL {

    
    
    private Character[] lenguaje;// vector que almacena todos los simbolos del lenguaje
    private Character[][] patrones;// vector que almacena los patrones del lenguaje
    private Integer pos; //entero que define la posicion del vector donde se alamcenara todo nuestro lenguaje
    static String archivo; //variable con la que se va a abrir un archivo un fichero de texto
    public String rutaTablaDeSImbolos; //localizacion de la tabla de simbolos
    public ArrayList<String> variables = new ArrayList<String>();
    static ArrayList<String> patronesSintaticos = new ArrayList<String>();
    static ArrayList<String> ListaAnalisisSintatico = new ArrayList<String>();
    
    TinyQL() throws IOException{
        rutaTablaDeSImbolos = "C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL-master\\src\\tinyql\\tablaDeSImbolos.txt";
        variables.add("");
    }
    public static void main(String[] args) throws IOException {
        Boolean SegirCompilando = true;
//        Pattern.matches("DE", "");
//        la puta clave 

        archivo="C:\\Users\\alfre\\OneDrive\\Documents\\Compilador-TinyQL-master\\src\\tinyql\\CodigoFuente.txt";
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
            
                //Analisis Semantico
            
            }
        }
        
        
    }
    public boolean AnalisisSintatico2() throws FileNotFoundException, IOException{
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
        Integer lineNumber = 1; // contador de linea del documento
        
        while((cadena = b.readLine()) != null && isPattern) { //&& flag
            
            isPattern = false;
            if(Pattern.matches(patronInsert, cadena)||
               Pattern.matches(patronSelect1, cadena)||
               Pattern.matches(patronSelect2, cadena)||
               Pattern.matches(patronUpdate, cadena)||
               Pattern.matches(patronDelete, cadena)||
               Pattern.matches(patronCreate, cadena)||
               Pattern.matches(patronDrop, cadena))
                    
                isPattern = true;
            
            if (!isPattern){
                
                System.out.println("Error sintatico en la linea: " + lineNumber);
            }
           
            lineNumber++;
            
        }
        if (isPattern)
            System.out.println("5.- Analisis sintatico Completa sin errores");
        
        
        return !isPattern;
    }
    private static boolean AnalisisSintatico() throws FileNotFoundException, IOException {
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        int contador=1;//esta linea nos servira para decir en que line esta el error
        Integer numeroDePalabras = 1;//contar en que palabra voy de la linea del documento
        String cadena, ss; // ss significa SubString
        boolean flag = true;//bandera que se activa si detecta errores
        while((cadena = b.readLine()) != null && flag) { //ciclo para leer el documento entero
            ListaAnalisisSintatico.clear();
            numeroDePalabras = 1;
            boolean banSelect=false,banInsert=false,banDelete=false,banUpdate=false;
            for(int i = 0; i < cadena.length(); i++){ //ciclo para leer una linea del documento
                if(numeroDePalabras == 1 && cadena.charAt(cadena.length()-1) != ';'){//detectar que haya un ; al final de cada linea 
                    System.out.println("ERRROR SINTATICO: Falta: ; en la linea "+contador);
                    flag = false; //cerrar ciclo 2
                    break; ////cerrar ciclo 1
                }
                else if(cadena.charAt(i) == ' ' || cadena.charAt(i) == ';'){ //identifico donde termina cada palabra de la cadena 
                    ss = cadena.substring(0, i); // ss se vuelve igual a la palabra encontrada
                    if(numeroDePalabras.equals(1)){
                        flag = false;
                        for (int j = 0; j < 4; j++) // verifico si la primera palabra palabra es un insert, update, delete o select
                            if(patronesSintaticos.get(j).equals(ss)){
                                flag = true;//notifica que si se inicio con lo que se debe
                                ListaAnalisisSintatico.add(ss); // agrego la primera palabra al un arreglo, esto me permite manipular mas facil los datos 
                            }                                
                        if(!flag){//mensaje de error
                            System.out.println("ERRROR SINTATICO: No se inicio con un operador sql(insert, update, delete o select) en la linea "+contador);
                            break;
                        }
                        
                    }else if(numeroDePalabras.equals(2)){ //la segunda palabra del lenguaje solo puede contener o variables o un "*"
                    
                        if(!ss.equals("*") && !ss.equals("into") && !ss.equals("from")){ // si la segunda palabra no es un * no puede ser ninguna otra palabra reservada
                            if(patronesSintaticos.contains(ss)){// si hay una palabra reservada en la segunda palabra hay un error
                                    System.out.println("ERROR SINTATICO: palabra reservada " + ss + " fuera de lugar, se esperaba '*', into, from o una variable en la linea "+contador);
                                    flag = false; //cerrar ciclo 2
                                    break; ////cerrar ciclo 1
                            }
                            else{// en caso de que todo este vien hay que analizar que la variable este escrita correctamente
                                //NOTA: no hay parentesis en este lenguaje, por lo que una variable se tiene ver de la siguiente manera
                                // select "id","name","adress" from "table" // solo se evaluara que tenga un numero par de " y que esten separados por ,
                                //System.out.println(ss);
                                String subSS = ss;
                                int contarComillas = 0;
                                for (int j = 0; j < subSS.length(); j++) { // reviso toda la variable para saber si tiene un numero par de "
                                   if(subSS.charAt(j) == '"'){// si en la variable existe un " 
                                       subSS = subSS.substring(j + 1);// la cadena se vuelve igual a la cadena si en " encontrado para poder seguir buscando mas 
                                       contarComillas++;
                                       j=0; // se reinicia el ciclo hasta que el tamano de la cadena sea 0, la funcion chartAt rgresa -1 si la cadena no 
                                      //contiene lo que se busca por lo que al hacerla substring de esta hasta -1 retorta una cadena vacia por lo cual 
                                      //nunca se va a hacer un ciclo infinito 
//                                       System.out.println(subSS + " " + contarComillas);
                                   }
                                   }
                                    if(subSS.length()==1)
                                    {
                                        System.out.println("ERROR SINTATICO: se esperaba ELEMENTO entre las comillas en variable en la linea "+contador);
                                        flag=false;
                                    }
                                    else if(contarComillas % 2 == 0 ){ // si tienes las comilla necesarias se procede a revisar las comas
                                         subSS = ss;// reinicio la variable auxiliar
                                         if(subSS.charAt(subSS.length() - 1) == ','){ //la variable no puede terminar con una , ya que harian falta elementos
                                             System.out.println("ERROR SINTATICO: se esperaba elemeto despues de un ',' en la linea "+contador);
                                             flag=false;
                                         }
                                         else{
                                             if(contarComillas>0)
                                             {
                                            ListaAnalisisSintatico.add(ss);
                                             //System.out.println("se agrego palablabra 2");
                                             if(ListaAnalisisSintatico.get(0).equals("update"))
                                                 banUpdate=true;//bandera que me ayudara a saber como va la estrucutra de la sentencia
                                             }
                                             else
                                             {
                                                System.out.println("ERROR SINTATICO: No tiene variables agregadas en la linea "+contador);
                                                flag=false; 
                                             }
                                         } 
                                     }
                                     else{
                                            System.out.println("ERROR SINTATICO: se esperaba COMILLA DOBLE en variable en la linea "+contador);
                                            flag=false;
                                     }
                                }
                                
                                String cad1=ListaAnalisisSintatico.get(0),
                                        cad2=ListaAnalisisSintatico.get(1);
                                if(cad1.equals("insert") && !cad2.equals("into"))
                                {
                                    System.out.println("ERROR SINTATICO: se esperaba INTO despues de INSERT en la linea "+contador);
                                    flag=false;
                                }
                                if(cad1.equals("insert") && cad2.equals("into"))
                                    banInsert=true;//bandera que me ayudara a saber como va la estrucutra de la sentencia
                                if(cad1.equals("delete") && !cad2.equals("from"))
                                {
                                    System.out.println("ERROR SINTATICO: se esperaba FROM despues de DELETE en la linea "+contador);
                                    flag=false;
                                }
                                if(cad1.equals("delete") && cad2.equals("from"))
                                    banDelete=true;//bandera que me ayudara a saber como va la estrucutra de la sentencia
                                if(cad1.equals("select") && cad2.equals("*"))
                                    banSelect=true;//bandera que me ayudara a saber como va la estrucutra de la sentencia
                            } 
                        }
                    else if(numeroDePalabras.equals(3))
                    {
                        if(banSelect && ss.equals("from"))//con esto tendre la secuencia select * from
                            ListaAnalisisSintatico.add(ss);
                        else if(banUpdate && ss.equals("set"))//con esto tendre la secuencia update variables set
                            ListaAnalisisSintatico.add(ss);
                        else if(banSelect && !ss.equals("from"))
                        {
                            System.out.println("ERROR SINTATICO: se esperaba FROM despues de Select * en la linea "+contador);
                            flag=false;
                        }
                        else if(banUpdate && !ss.equals("set"))
                        {
                            System.out.println("ERROR SINTATICO: se esperaba SET despues de variables en la linea "+contador);
                            flag=false;
                        }
                        else
                        {
                            String subSS = ss;
                            int contarComillas = 0;
                            for (int j = 0; j < subSS.length(); j++) // reviso toda la variable para saber si tiene un numero par de "
                            { 
                                if(subSS.charAt(j) == '"')// si en la variable existe un " 
                                {
                                    subSS = subSS.substring(j + 1);// la cadena se vuelve igual a la cadena si en " encontrado para poder seguir buscando mas 
                                    contarComillas++;
                                    j=0; // se reinicia el ciclo hasta que el tamano de la cadena sea 0, la funcion chartAt rgresa -1 si la cadena no 
                                      //contiene lo que se busca por lo que al hacerla substring de esta hasta -1 retorta una cadena vacia por lo cual 
                                      //nunca se va a hacer un ciclo infinito 
                                }
                            }
                            if(subSS.length()==1)
                            {
                                System.out.println("ERROR SINTATICO: se esperaba ELEMENTO entre las comillas en variable en la linea "+contador);
                                flag=false;
                            }
                            else if(contarComillas % 2 == 0)
                            {
                                subSS = ss;// reinicio la variable auxiliar
                                if(subSS.charAt(subSS.length() - 1) == ',')
                                { //la variable no puede terminar con una , ya que harian falta elementos
                                    System.out.println("ERROR SINTATICO: se esperaba elemeto despues de un ',' en la linea "+contador);
                                    flag=false;
                                }
                            }
                            else
                            {
                                if(banInsert)//tengo la secuencia de insert into y variables
                                    ListaAnalisisSintatico.add(ss);
                                else if(banDelete)//tendre la secuencia de delete from y variables
                                    ListaAnalisisSintatico.add(ss);
                            }
                        }
                    }
                    
                        
                    numeroDePalabras++;
                    cadena = cadena.substring(i + 1); // ya que se guardo la palabra se elimina la misma de la cadena
                    i = 0;// se reinicia el contador ya que el inicio de la cadena tambien cambio
                    //System.out.println(cadena);
                    }
                    
                }
                contador++;
                System.out.println("salto de linea");
        }
        
        return true;   
    }
     public static void muestraContenido() throws FileNotFoundException, IOException {
//        String cadena;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
//        while((cadena = b.readLine())!=null) {
//            System.out.println(cadena);
//        }
        System.out.println("1.- Codigo fuente abierto correctamente");
        b.close();
    }
     public static void PatronesSintatico(){
         Character aux = (char)34;
        patronesSintaticos.add("select");
        patronesSintaticos.add("insert");
        patronesSintaticos.add("update");
        patronesSintaticos.add("delete");
        patronesSintaticos.add("*");
        patronesSintaticos.add("from");
        patronesSintaticos.add("where");
        patronesSintaticos.add(aux.toString());
        patronesSintaticos.add("=");
        patronesSintaticos.add("into");
        patronesSintaticos.add(",");
        patronesSintaticos.add("set");
        patronesSintaticos.add("'");
        
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
        patrones=new Character[15][10];
        
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
    
    public void verificacionPatrones() throws FileNotFoundException, IOException
    {
        String cadena;
        FileReader f = new FileReader(archivo);
        File tablaDS = new File(rutaTablaDeSImbolos);//para la tabla de simbolos
                // Si el archivo no existe es creado
            if (!tablaDS.exists()) {
                tablaDS.createNewFile();
            }
        FileWriter fw = new FileWriter(tablaDS);
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine()) != null) { //ciclo para leer el documento entero
            compararPatron(cadena,tablaDS,bw,b);
        }
        b.close();
        bw.close();
        System.out.println("5.- Verificacion de patrones terminada");
    }
    
    public void compararPatron(String cad,File tablaDS,BufferedWriter bw, BufferedReader b) throws IOException
    {
        
        
        boolean variable=false;//bandera para decir si pertenece a una variable
        boolean reservada=false;//bandera para decir si pertenece a palabra o caracter especial
        boolean fina=false,ban=true;
        Boolean variableYaExistenteEnTablaDeSImbolos=false;
        Character simbolo=' ';
        String palabra="",pal="";
        int aux=0,ini=0,fin=0,comparador=cad.length()-1;
        while(aux<cad.length())
        {
            if(aux==comparador)
                fina=true;
            palabra=palabra+cad.charAt(aux);
            simbolo=cad.charAt(aux);
            if(simbolo.equals((char)32) || fina==true)
                for(int i=0;i<14;i++)
                {
                    pal=palabra;
                    fin=aux+1;
                    simbolo=cad.charAt(ini);
                    if(patrones[i][0].equals(simbolo))
                    {
                        for(int j=0;j<fin;j++)
                        {
                            simbolo=cad.charAt(ini+j);
                            if(patrones[i][j].equals(simbolo))
                            {
                                reservada=true;
                                variable=false;
                            }
                            else
                            {
                                variable=true;
                                reservada=false;
                                break;
                            }
                        }
                        ban=false;
                    }
                        if(i==13)
                        {
                            if(reservada==true){
                                System.out.println(palabra+" es una palabra reservada");
                                ListaAnalisisSintatico.add(palabra);
                            }
                            
                            if(variable==true){
                                System.out.println(palabra+" es una variable");
                                for(int n=0;n<variables.size();n++){
                                    if(variables.get(n).equals(palabra)){
                                        variableYaExistenteEnTablaDeSImbolos=true;
                                    }
                                        
                                }
                                if(!variableYaExistenteEnTablaDeSImbolos){
                                    variables.add(palabra);
                                    bw.write(palabra+"\n");
                                }
                                variableYaExistenteEnTablaDeSImbolos=false;
                            }
                            if(variable==false && reservada==false){
                                System.out.println(pal+" es una variable");
                                for(int n=0;n<variables.size();n++){
                                    if(variables.get(n).equals(palabra)){
                                        variableYaExistenteEnTablaDeSImbolos=true;   
                                    }
                                        
                                }
                                if(!variableYaExistenteEnTablaDeSImbolos){
                                    variables.add(palabra);
                                    bw.write(palabra+"\n");
                                }
                                variableYaExistenteEnTablaDeSImbolos=false;
                            }
                            palabra="";
                            ini=fin;
                            reservada=false;
                            variable=false;
                        }
                }
            aux++;
        }
        
    }
    public Boolean BarridoDeVerificacionDeSimbolos() throws FileNotFoundException, IOException{
        String cadena;
        Character simbolo=' ';
        Boolean simboloEncontrado = false; //bandera que se activa si un simbolo no se escuentra en el alfabeto
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        
        while((cadena = b.readLine()) != null) { //ciclo para leer el documento entero
            for (Integer i = 0 ; i < cadena.length() ; i++){ //ciclo para evaluar la cadena caracter por caracter
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
            System.out.println(simbolo.toString()+" NO es parte del alfabeto");
            return false;
        }
        else{
            System.out.println("3.- Barrido de verificacion de simbolos completado sin errores");
            return true;
        }
        
    }
    public Boolean CompararSimbolo(Character ch){
        
        Boolean simboloEncontrado = false; //bandera que se activa si un simbolo no se escuentra en el alfabeto
        for(pos=0; pos<73;pos++){
            if(lenguaje[pos].equals(ch))
                simboloEncontrado=true;
        }
        
        return simboloEncontrado;
    }
    }
    
