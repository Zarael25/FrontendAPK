package com.example.frontendapk.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen : AppScreens(route = "splash_screen")

    object RegisterScreen : AppScreens(route = "register_screen")
    object LoginScreen : AppScreens(route = "login_screen")


    object HomeScreen : AppScreens("home_screen/{nombre}") {
        fun createRoute(nombre: String) = "home_screen/$nombre"
    }


    object PerfilScreen : AppScreens(route = "perfil_screen")
    object TusNegociosScreen : AppScreens(route = "tus_negocios_screen")
    object DetalleNegocioScreen : AppScreens("detalle_negocio/{negocioId}") {
        fun createRoute(negocioId: Int) = "detalle_negocio/$negocioId"
    }
    object RegistroNegocioScreen : AppScreens("registro_negocio")

    object EditarNegocioScreen : AppScreens("editar_negocio/{negocioId}") {
        fun createRoute(negocioId: Int) = "editar_negocio/$negocioId"
    }
    object RegistroFilaScreen : AppScreens("registro_fila_screen/{negocioId}") {
        fun createRoute(negocioId: Int) = "registro_fila_screen/$negocioId"
    }

    object TusFilasScreen : AppScreens("tus_filas_screen/{negocioId}") {
        fun createRoute(negocioId: Int) = "tus_filas_screen/$negocioId"
    }

    object DetalleFilaScreen : AppScreens("detalle_fila/{filaId}") {
        fun createRoute(filaId: Int) = "detalle_fila/$filaId"
    }

    object EditarFilaScreen : AppScreens("editar_fila/{filaId}") {
        fun createRoute(filaId: Int) = "editar_fila/$filaId"
    }

    object NegociosVerificadosScreen : AppScreens("negocios_verificados_screen")

    object FilasVisiblesScreen : AppScreens("filas_visibles/{negocioId}") {
        fun createRoute(negocioId: Int) = "filas_visibles/$negocioId"
    }

    object GenerarTicketScreen {
        const val route = "generar_ticket/{filaId}"
        fun createRoute(filaId: Int) = "generar_ticket/$filaId"
    }

    object DetalleTicketScreen : AppScreens("detalle_ticket/{ticketId}") {
        fun createRoute(ticketId: Int) = "detalle_ticket/$ticketId"
    }

    object TusTicketsScreen : AppScreens("tus_tickets_screen")

    object PoliticasCancelacionReservaScreen : AppScreens("politicas_cancelacion_reserva/{negocioId}") {
        fun createRoute(negocioId: Int) = "politicas_cancelacion_reserva/$negocioId"
    }


    object TicketsFilaScreen {
        fun createRoute(filaId: Int) = "tickets_fila_screen/$filaId"
        const val route = "tickets_fila_screen/{filaId}"
    }


    object LoginAdminScreen : AppScreens("login_admin_screen")

    object HomeAdminScreen : AppScreens("admin_home_screen")

    object ListarUsuariosAdminScreen : AppScreens("listar_usuarios_admin_screen")

    object EditarUsuarioAdminScreen : AppScreens("editar_usuario_admin_screen/{usuarioId}") {
        fun createRoute(usuarioId: Int): String = "editar_usuario_admin_screen/$usuarioId"
    }

    object ListarNegociosAdminScreen : AppScreens("listar_negocios_admin_screen")

    object EditarNegocioAdminScreen : AppScreens("editar_negocio_admin_screen/{negocioId}") {
        fun createRoute(negocioId: Int): String = "editar_negocio_admin_screen/$negocioId"
    }


}