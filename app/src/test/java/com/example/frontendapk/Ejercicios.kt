package com.example.frontendapk


//1
fun esPar(numero: Int): Boolean {
    var par: Boolean = false
    if (numero % 2 == 0)
        par = true
    else
        par = false
    return par
}

//2
fun aplicarDescuento(monto: Double): Double {
    return when {
        monto > 500 -> monto * 0.8
        monto > 300 -> monto * 0.9
        else -> monto
    }
}


//3
fun calcularPromedio(notas: List<Int>): Double {
    return notas.average()
}


//4
fun generarFibonacci(n: Int): List<Int> {
    val lista = mutableListOf(0, 1)
    for (i in 2 until n) {
        lista.add(lista[i - 1] + lista[i - 2])
    }
    return lista
}

fun sumarNumFibonacci(numeros: List<Int>): Int {
    return numeros.sum()
}


//5
fun verificarEdad(edad: Int): String {
    return if (edad >= 18) "Eres adulto" else "Eres menor de edad"
}


//6
fun saludoSegunHora(hora: Int): String {
    return when (hora) {
        in 6..12 -> "Buenos días"
        in 13..22 -> "Buenas tardes"
        in 0..5 -> "Buenas noches"
        else -> "Hora inválida"
    }
}


//7
fun esContraseñaSegura(contraseña: String): Boolean {
    val tieneMayuscula = contraseña.any { it.isUpperCase() }
    val tieneNumero = contraseña.any { it.isDigit() }
    val tieneSimbolo = contraseña.any { !it.isLetterOrDigit() }
    return contraseña.length >= 8 && tieneMayuscula && tieneNumero && tieneSimbolo
}

//8
fun mostrarMenu() {
    println("************************************************************")
    println("Selecciona una opción:")
    println("1. Perfil")
    println("2. Ajustes")
    println("3. Salir")
    println("************************************************************")

    val opcion = readln().toIntOrNull()
    when (opcion) {
        1 -> println("Cargando perfil...")
        2 -> println("Abriendo ajustes...")
        3 -> println("Saliendo...")
        else -> println("Opción inválida")
    }
}

//9
fun determinarTrimestre(num: Int): String{
    val trimestre =when(num){
        in 1..3->"Primer Trimestre"
        in 4..6->"Segundo Trimestre"
        in 7..9->"Tercer Trimestre"
        else -> "Cuarto Trimestre"
    }
    return trimestre
}

//10
fun mostrarNotificaciones(numNotificaciones:Int):String{
    var mensaje = ""

    if (numNotificaciones>99)
        mensaje = "Usted tiene 99+ notificaciones"

    else
        mensaje = "Usted tiene $numNotificaciones notificaciones"

    return mensaje

}




//11
fun precioTicket(edad:Int,esLunes:Boolean):Int{
    val resultado= when(edad){
        in 0..12 ->15
        in 13..60 -> if(esLunes) 25 else 30
        in 61..100->20
        else -> -1
    }
    return resultado
}







fun main(){
    val child=5
    val adult=28
    val senior=87

    //1
    println("El numero $child es par?  ${esPar(child)}")
    println("El numero $adult es par?  ${esPar(adult)}")

    //2
    val montoCompra = 350.0
    println("Monto final con descuento: ${aplicarDescuento(montoCompra)}")


    //3
    val calificaciones = listOf(85, 90, 78, 92, 88)
    println("Promedio: ${calcularPromedio(calificaciones)}")


    //4
    val numeros = generarFibonacci(10)
    println("La suma es: ${sumarNumFibonacci(numeros)}")


    //5
    println(verificarEdad(child))
    println(verificarEdad(adult))


    //6

    println(saludoSegunHora(8))
    println(saludoSegunHora(15))
    println(saludoSegunHora(22))
    println(saludoSegunHora(3))

    //7
    val contraseña1 = "Abc123@"
    val contraseña2 = "Abc123%&/fdv124AF2@"
    println("¿La contraseña es segura? ${esContraseñaSegura(contraseña1)}")
    println("¿La contraseña es segura? ${esContraseñaSegura(contraseña2)}")




    //9
    println("${determinarTrimestre(child)} !!")


    //10
    println("Usted tiene  ${mostrarNotificaciones(senior)}")

    //11
    println("El precio para una persona de edad $child es ${precioTicket(child,true)}")
    println("El precio para una persona de edad $adult es ${precioTicket(adult,false)}")
    println("El precio para una persona de edad $senior es ${precioTicket(senior,true)}")


    //8

    mostrarMenu()

}




