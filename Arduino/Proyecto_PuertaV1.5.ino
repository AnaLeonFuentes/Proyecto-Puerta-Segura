#include <LiquidCrystal_I2C.h>
#include <Keypad.h>
#include <Servo.h>
#include <Password.h>
#include <Ethernet.h>
#include <AcceleroMMA7361.h>

#define PinTrigUltraSonido 43  // pin al que se conecta el ultrasonico
#define PinEchoUltraSonido 41  // pin al que se conecta el ultrasonico
#define PinServo  39           // pin digital para el servo
#define PinLedRojo  30             // pin digital del led Rojo
#define PinLedVerde  32       // pin digital del led Verde
#define PinBuzzer 49           // pin digital del buzzer
#define ServoCerrado  0      // posición inicial 
#define ServoAbierto  90      // posición de 0 grados

LiquidCrystal_I2C lcd(0x27, 16, 2);

//definimos las contraseñas necesarias
//password que se solicita al reconocer el dispositivo bluetooth
//luego deberiamos crear una variable global con la contraseña obtenida de algun lado
Password password_ingreso = Password ("1234"); 

//password que se ingresa para alertar de peligro
Password password_peligro = Password ("4321"); 

//# se valida la clave
//C se limpia la pantalla
//Matriz del teclado
const byte filas    = 4;  // 4 filas del teclado
const byte columnas = 4;  // 4 columnas del teclado
byte pinesFilas[filas]       = {37,35,33,31};   // pines de filas
byte pinesColumnas[columnas] = {29,27,25,23};   // pines de columnas

// declaramos las teclas que conforman la matriz
char teclas[filas][columnas] = { 
 {'1','2','3','A'},
 {'4','5','6','B'},
 {'7','8','9','C'},
 {'*','0','#','D'}
};

// esta instrución sirve para realizar el mapeo del teclado matricial 
Keypad teclado = Keypad(makeKeymap(teclas), pinesFilas, pinesColumnas, filas, columnas);

// variable tecla que se mostrará al tocar una tecla en el teclado
char tecla;
//variable para leer el buffer serial
char inbyte = 0; //Char para leer el led

// variable para controlar el servo
Servo servoMotor;

//variable para controlar el acelerometro
AcceleroMMA7361 accelero;

//variable para saber si se ingreso la clave falsa
boolean claveFalsa=false;
//contador para hacer sonar la alarma
int cont_Error = 3;  

//variables para obtener los valores de los ejes
int x;
int y;
int z;
double normal;
double normalInicial;
int rango=200;
boolean acel=true;
//Nos indica que el celular se conecto mediante bluetooth
boolean conectado = false;

long duracion, distancia;   

//-------------------------------------------------------------
//Declaración de la direcciones MAC
byte mac[]={0xDE,0xAD,0xBE,0xEF,0xFE,0xED}; //MAC
char servidor[] = "pruebapuerta1.000webhostapp.com";   // dirección del dominio de la lib para firebase
EthernetClient client;
IPAddress ip(192,168,0,10);
int puerto = 80;
char pagina[90];
//-------------------------------------------------------------

void setup()
{
  Serial.begin (9600);       // inicializa el puerto serie a 9600 baudios
  //---------------------------------ACELEROMETRO---------------------
  analogReference(EXTERNAL); // pin aref conectado a 3.3V
  accelero.begin(22, 24, 26, 28, A8, A9, A10);
  accelero.setSensitivity(HIGH);                   //sets the sensitivity to +/-6G
  accelero.calibrate();
  //obtenerNormal();
  //---------------------------INICIAMOS LA PANTALLA-----------------
  lcd.init();
  
  //-----------------EVENTO LISTENER PARA EL TECLADO--------------------
  teclado.addEventListener(tecladoEvent);
  //-----------------------SERVOMOTOR-----------------------------------
  servoMotor.attach(PinServo); // el servo trabajarservoá desde el pin definido como PinServo
  servoMotor.write(ServoCerrado);   // Desplazamos a la posición 0
  //----------------------BUZZER-----------------------------------------
  pinMode(PinBuzzer, OUTPUT);  // seteo el PinBuzzer como salida para el Buzzer
  //-------------------ULTRASONIDO------------------------------------  
  pinMode(PinEchoUltraSonido, INPUT);     // define PinEchoUltraSonido como entrada (echo)
  pinMode(PinTrigUltraSonido, OUTPUT);    // define inTrigUltraSonido como salida  (triger)
  //-------------------------LEDS-----------------------------------
  pinMode(PinLedRojo, OUTPUT);       // Define el pin PinLed como salida
   pinMode(PinLedVerde, OUTPUT);       // Define el pin PinLed como salida 
  //--------------------ETHERNET START-------------------------------------------
  Serial.println("Starting ethernet...");
   if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    
    Ethernet.begin(mac, ip);
     
   }
    
  Serial.println(Ethernet.localIP());
  Serial.println("Ready");
  //----------------------------------------------------------------
}

void loop()
{   
  if(acel){
     obtenerNormal();
     acel=false;
  }
  digitalWrite(PinTrigUltraSonido, LOW);
  delayMicroseconds(2);
  digitalWrite(PinTrigUltraSonido, HIGH);   // genera el pulso de triger por 10ms
  delayMicroseconds(10);
  digitalWrite(PinTrigUltraSonido, LOW);

  duracion = pulseIn(PinEchoUltraSonido, HIGH);
  distancia = (duracion/2) / 29;  
//  Serial.println(distancia);
  //para detectar si la puerta se movio de lugar
  x = accelero.getXAccel();
  y = accelero.getYAccel();
  z = accelero.getZAccel();
/*
  Serial.print("\nx: ");
  Serial.print(x);
  Serial.print(" \ty: ");
  Serial.print(y);
  Serial.print(" \tz: ");
  Serial.print(z);
  Serial.print("\tG*10^-2");
  */
  normal=sqrt(pow(x,2)+pow(y,2)+pow(z,2));
 // Serial.println(normal);
 // delay(2000);

  if(normal<(normalInicial-rango)||normal>(normalInicial+rango)){
      Serial.println(normal);
      Serial.println("PUERTA FORZADA");
      //se enciende el buzzer(alarma) 
     // digitalWrite(PinBuzzer,100);  //se enciende el buzzer
        digitalWrite(PinLedRojo, HIGH);                   
      //-----------------------------------------------------------------------------------------------------
              
      //Alarma activada actualizo base de datos
                        
      sprintf(pagina,"/firebaseTest.php?alarma_activada=%d&clave_emergencia=%d&puerta_forzada=%d",1,0,1);
      //sprintf(pagina,"/firebaseTest.php?alarma_activada=%d",1);
      Serial.print(pagina);
      if(!get_pagina(servidor, puerto, pagina)) 
          Serial.print("Error Página");
      else 
          Serial.print("Ok Página");
       //-----------------------------------------------------------------------------------------------------
              
       apagarAlarma();
       digitalWrite(PinLedRojo,LOW);
  }
  //si se detecta la persona entonces se enciende el lcd y se toma las teclas ingresadas
  if (distancia <= 15 && distancia >= 0){
    //digitalWrite(PinLed, HIGH);                         // en alto el pin PinLed si la distancia es menor a 30cm
    lcd.backlight();  // enciendo la pantalla lcd   
    teclado.getKey();
  }

} /////////////////////////////////FIN LOOP
  void obtenerNormal(){
    int i=100;
    double promedio=0;
    while(i>=0){
       x = accelero.getXAccel();
      y = accelero.getYAccel();
      z = accelero.getZAccel();
      normalInicial=sqrt(pow(x,2)+pow(y,2)+pow(z,2));
      Serial.println(normalInicial);
      promedio+=normalInicial;
      i--;
    }
    normalInicial=promedio/100;
     
  }
  //funcion que se hace cada vez que se ingresa un digito en el teclado
  void tecladoEvent(KeypadEvent eKey){
    switch(teclado.getState()){
      case PRESSED:
      //  lcd.print("Pressed: ");
      Serial.println("Se oprimio la tecla:");
      Serial.println(eKey);
      //lcd.print(eKey);
      lcd.print("*"); // muestra un * para ocultar la tecla oprimida
      switch (eKey){
          case '#': checkPassword();break; // confirma lo ingresado
          case 'C': passwordReset();break; // limpia la pantalla
          default:                          
            password_ingreso.append(eKey); //agrega el digito a la contraseña
            password_peligro.append(eKey); //agrega el digito a la contraseña
          break;
      }
    }
  }
  
  void checkPassword(){
    if(password_ingreso.evaluate()){
      Serial.println("ingreso correcto");      
      lcd.setCursor(0,1); //saltamos a la segunda linea
      Serial.println("validando Clave");
      lcd.print("Validando Clave"); 
      delay(3000); 
    if(estaConectado()){ // si se encontro la x quiere decir que alguien se conecto al bluetooth
        ingresar();
        
      }
      else{ // si no se encontro la x, quiere decir que nadie se conecto al bluetooth, entonces aunque la contraseña sea correcta, no se le debe permitir el acceso
        lcd.clear(); // limpiamos el led
        lcd.print("No Autorizado");
        delay(3000);
      }
      

    }
    else{
        if(password_peligro.evaluate()){
            claveFalsa=true;                 
            Serial.println("ingreso correcto");      
            
            lcd.setCursor(0,1); //saltamos a la segunda linea
            Serial.println("validando Clave");
            lcd.print("Validando Clave"); 
            delay(3000); 
          if(estaConectado()){ // si se encontro la x quiere decir que alguien se conecto al bluetooth
              ingresar();
              
            }
            else{ // si no se encontro la x, quiere decir que nadie se conecto al bluetooth, entonces aunque la contraseña sea correcta, no se le debe permitir el acceso
              lcd.clear(); // limpiamos el led
              lcd.print("No Autorizado");
              delay(3000);
            }                                
          }
         else{           
            digitalWrite(PinLedRojo, HIGH);
            lcd.setCursor(0,1); //saltamos a la segunda linea
            lcd.print("Validando Clave"); 
            delay(3000);
            lcd.clear(); // limpiamos el led
            lcd.print("Clave Incorrecta.");
            delay(3000);
            //se decrementa en uno el contador que hace sonar la alarma
            cont_Error = cont_Error - 1;
            Serial.println(cont_Error);
            if(cont_Error==0){ 
              // funcion sonar alarma!
               Serial.println("*** Sonar alarma por 3 intentos fallidos ***");
              digitalWrite(PinBuzzer, 100);  //se enciende el buzzer
              //-----------------------------------------------------------------------------------------------------
              
              //Alarma activada actualizo base de datos
                        
              sprintf(pagina,"/firebaseTest.php?alarma_activada=%d&clave_emergencia=%d&puerta_forzada=%d",1,0,0);
              //sprintf(pagina,"/firebaseTest.php?alarma_activada=%d",1);
              Serial.print(pagina);
              if(!get_pagina(servidor, puerto, pagina)) 
                Serial.print("Error Página");
              else 
                Serial.print("Ok Página");
              //-----------------------------------------------------------------------------------------------------
               //se enciende el buzzer 
               
              apagarAlarma();
            }
            digitalWrite(PinLedRojo,LOW);
            
           
         }      
      }
      lcd.clear();
      password_peligro.reset();
      password_ingreso.reset(); 
  }

  void passwordReset(){
    lcd.clear();
    password_peligro.reset();
    password_ingreso.reset();
  }
void ingresar(){
      digitalWrite(PinLedVerde, HIGH);
      // Desplazamos a la posición 90
      servoMotor.write(ServoAbierto);
      lcd.clear(); // limpiamos el led
      lcd.print("Puerta Abierta");
      if(claveFalsa){
        //comunicarse con la aplicacion e informar del peligro
            
            //-----------------------------------------------------------------------------------------------------
              //Clave Emergencia ingresada actualizo base de datos
              sprintf(pagina,"/firebaseTest.php?alarma_activada=%d&clave_emergencia=%d&puerta_forzada=%d",0,1,0);
              if(!get_pagina(servidor, puerto, pagina)) 
                Serial.print("Error Página");
              else 
                Serial.print("Ok Página");
            //-----------------------------------------------------------------------------------------------------
                  delay(3000);
            
            //-----------------------------------------------------------------------------------------------------
              //actualizo base de datos
              sprintf(pagina,"/firebaseTest.php?alarma_activada=%d&clave_emergencia=%d&puerta_forzada=%d",0,0,0);
              if(!get_pagina(servidor, puerto, pagina)) 
                Serial.print("Error Página");
              else 
                Serial.print("Ok Página");
            //-----------------------------------------------------------------------------------------------------
            claveFalsa=false;
      }
    delay(5000);
      //tomo los valores del acelerometro para saber cuando se cerro la puerta y "bloquearla" con el servo
      accelero.calibrate();
      obtenerNormal();
      x = accelero.getXAccel();
      y = accelero.getYAccel();
      z = accelero.getZAccel();
      normal=sqrt(pow(x,2)+pow(y,2)+pow(z,2));
      //Serial.print(normal);
      while(normal<(normalInicial-rango)||normal>(normalInicial+rango)){
          x = accelero.getXAccel();
          y = accelero.getYAccel();
          z = accelero.getZAccel();
          normal=sqrt(pow(x,2)+pow(y,2)+pow(z,2));
         // Serial.print(normal);
      }
      cerrarPuerta();
      accelero.calibrate();
      obtenerNormal();
      
      
}
void cerrarPuerta(){
      servoMotor.write(ServoCerrado);
      lcd.clear(); // limpiamos el led  
      digitalWrite(PinLedVerde,  LOW);    
      lcd.print("Puerta Cerrada");
      delay(3000);
      conectado = false; //despues de ingresar la persona se debe desconectar
      lcd.noBacklight();  // apago la pantalla lcd 
      //se vuelve a poner en 3 el contador de la alarma para que la siguiente persona en querer entrar tenga de nuevo 3 intentos
      cont_Error=3;
}
boolean estaConectado(){
  //si el celular esta conectado via bluetooth
  //se leen los byte del buffer serial buscando una x que manda la aplicacion Android cuando el celular se conecta al bluetooth
      
  while(Serial.available() > 0 && !conectado){ //si hay algo que leer en el buffer y todavia no se encontro la "x"
           inbyte = Serial.read();  //leo un byte del buffer
           Serial.println(inbyte);
          if (inbyte == 'x'){
             conectado = true; 
             Serial.println("encontre el conectado");
           }
      }  
}

void apagarAlarma(){
          
   boolean apagado=false;
   while(apagado==false){
       if (Serial.available() > 0)  //when serial values have been received this will be true
        {
          inbyte = Serial.read();
          Serial.println(inbyte);
         
           if (inbyte == '2')
          {
            //apaga el buzzer
            digitalWrite(PinBuzzer, 0);  
            //se apaga el led rojo 
            //digitalWrite(PinLedRojo,LOW);
            cont_Error=3;
            apagado=true;
             //-----------------------------------------------------------------------------------------------------
              
              //Alarma activada actualizo base de datos
                        
              sprintf(pagina,"/firebaseTest.php?alarma_activada=%d&clave_emergencia=%d&puerta_forzada=%d",0,0,0);
              //sprintf(pagina,"/firebaseTest.php?alarma_activada=%d",1);
              Serial.print(pagina);
              if(!get_pagina(servidor, puerto, pagina)) 
                Serial.print("Error Página");
              else 
                Serial.print("Ok Página");
              //-----------------------------------------------------------------------------------------------------
          }
        }
  }
  accelero.calibrate();
  obtenerNormal();
}

byte get_pagina(char *p_servidor,int p_puerto, char *p_pagina)
{
  int inChar;
  char buff[128];
 
  Serial.print("conectando...");
 
  if(client.connect(p_servidor, p_puerto))
  {
    Serial.println("conectado");
    sprintf(buff,"GET %s HTTP/1.1", p_pagina);
    client.println(buff);
    sprintf(buff,"Host: %s", servidor);
    client.println(buff);
    client.println("Connection: cerrada\r\n");
  } 
  else
  {
    Serial.println("Error.");
    return 0;
  }

  // connectLoop controls the hardware fail timeout
  int connectLoop = 0;
 
  while(client.connected())
  {
    while(client.available())
    {
      inChar = client.read();
      Serial.write(inChar);
      // set connectLoop to zero if a packet arrives
      connectLoop = 0;
    }
 
    connectLoop++;
 
    // if more than 10000 milliseconds since the last packet
    if(connectLoop > 10000)
    {
      // then close the connection from this end.
      Serial.println();
      Serial.println("Timeout");
      client.stop();
    }
    // this is a delay for the connectLoop timing
    delay(1);
  }
 
  Serial.println();
  Serial.println("desconectando.");
  
  client.stop();
 
  return 1;
}
 


