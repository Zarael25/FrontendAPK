package com.example.frontendapk

/*fun main() {
    println("¿El número 7 es par? ${esPar(7)}") // false
    println("¿El número 10 es par? ${esPar(10)}") // true
}
fun esPar(numero: Int): Boolean {
    val indicador= "si"
    if (numero % 2 ==0){
        indicador="si"
    }
    else{

    }
}*/

//1
fun precioTicket(edad:Int,esLunes:Boolean):Int{
    val resultado= when(edad){
        in 0..12 ->15
        in 13..60 -> if(esLunes) 25 else 30
        in 61..100->20
        else -> -1
    }
    return resultado
}

//2
fun mostrarNotificaciones(numMensajes:Int){
    if (numMensajes>99)
        println("Usted tiene 99+ Notificaciones")
    else
        println("Usted tiene $numMensajes")
}

//3
fun determinarTrimestre(num: Int): String{
    val trimestre =when(num){
        in 1..3->"Primer Trimestre"
        in 4..6->"Segundo Trimestre"
        in 7..9->"Tercer Trimestre"
        else -> "Cuarto Trimestre"
    }
    return trimestre
}



fun main(){
    val child=5
    val adult=28
    val senior=87


    println("El precio para una persona de edad $child es ${precioTicket(child,true)}")
    println("El precio para una persona de edad $adult es ${precioTicket(adult,false)}")
    println("El precio para una persona de edad $senior es ${precioTicket(senior,true)}")

    println("Usted tiene  ${mostrarNotificaciones(senior)}")

    println("${determinarTrimestre(child)} !!")
}




