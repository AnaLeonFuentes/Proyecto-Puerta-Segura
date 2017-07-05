<?php
require_once 'firebaseLib.php';

// --- Firebase URL del proyecto
//$url = 'https://prueba-1-5403c.firebaseio.com/';
$url = 'https://proyectopuertasegura-ffb85.firebaseio.com/';

// --- Token Firebase del Proyecto
//$token = '1ul1qngiEYUA7d1wUepBItsEBuhDInqSaswC41LQ';
//$token = '0N4azvJ0cdo8KrgmN5P1CY14JHVRL5JgMLOFhGpD';


// --- PArámetros del http GET
$alarma_activada = $_GET['alarma_activada'];
$puerta_forzada = $_GET['puerta_forzada'];
$clave_emergencia = $_GET['clave_emergencia'];

// pruebas de valores que llegan

//echo "$alarma_activada";
//echo "$puerta_forzada";
//echo "$clave_emergencia";


// comento el token ya que está autorizado desde firebase
$fb = new fireBase($url);//, $token);

// seteo path y variable para armado del json
$firebasePath = 'alarma_activada';
$response = $fb->set($firebasePath, $alarma_activada);
print $response;

// seteo path y variable para armado del json
$firebasePath = 'puerta_forzada';
$response = $fb->set($firebasePath, $puerta_forzada);
print $response;


// seteo path y variable para armado del json
$firebasePath = 'clave_emergencia';
$response = $fb->set($firebasePath, $clave_emergencia);
print $response;


