package com.example.frontendapk.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PartMap
interface ApiService {
    @POST("api/registro/")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/perfil/")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfileResponse>

    @GET("api/negocios/mis_negocios/") //Antes estaba con esto ()
    fun getMisNegocios(@Header("Authorization") token: String): Call<List<Negocio>>

    @GET("api/negocios/{id}/")
    fun getNegocioPorId(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") id: Int
    ): Call<Negocio>

    @Multipart
    @POST("api/negocios/")
    fun crearNegocioMultipart(
        @Header("Authorization") token: String,
        @PartMap campos: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part doc_respaldo: MultipartBody.Part?
    ): Call<Negocio>


    @PATCH("api/negocios/{id}/editar-parcial/")
    fun editarNegocioParcial(
        @Header("Authorization") token: String,
        @Path("id") negocioId: Int,
        @Body negocio: NegocioRequest
    ): Call<Negocio>

    @PATCH("api/negocios/{id}/ocultar/")
    fun ocultarNegocio(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Map<String, String>>

    @POST("api/filas/")
    fun crearFila(
        @Header("Authorization") token: String,
        @Body fila: FilaRequest
    ): Call<Unit>

    @GET("api/negocios/{id_negocio}/mis_filas/")
    fun getFilasPorNegocio(
        @Header("Authorization") token: String,
        @Path("id_negocio") idNegocio: Int
    ): Call<List<FilaAtencion>>

    @GET("api/filas/{id}/")
    fun getFilaPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<FilaAtencion>

    @PATCH("api/filas/{id_fila}/editar-parcial/")
    fun editarFilaParcial(
        @Header("Authorization") token: String,
        @Path("id_fila") filaId: Int,
        @Body datos: Map<String, @JvmSuppressWildcards Any>
    ): Call<Void>

    @GET("api/negocios/verificados/")
    fun getNegociosVerificados(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Call<List<Negocio>>

    @GET("api/negocios/{id}/filas_visibles/")
    fun getFilasVisibles(
        @Path("id") negocioId: Int,
        @Header("Authorization") token: String
    ): Call<List<FilaAtencion>>

    @POST("api/tickets/generar/")
    fun generarTicket(
        @Body body: Map<String, Int>,
        @Header("Authorization") token: String
    ): Call<TicketGenerado>


    @GET("api/usuario_tickets/detalle-por-ticket/{ticket_id}/")
    fun getDetalleTicket(
        @Path("ticket_id") ticketId: Int,
        @Header("Authorization") token: String
    ): Call<DetalleTicket>

    @GET("api/usuario_tickets/mis-tickets/")
    fun getMisTickets(
        @Header("Authorization") token: String
    ): Call<List<DetalleTicket>>

    @POST("api/usuario_tickets/{ticket_id}/cancelar/")
    fun cancelarTicket(
        @Path("ticket_id") ticketId: Int,
        @Header("Authorization") authHeader: String
    ): Call<Map<String, String>>



    @PATCH("/api/negocios/{id}/editar-politicas/")
    fun editarPoliticas(
        @Header("Authorization") token: String,
        @Path("id") negocioId: Int,
        @Body request: EditarPoliticasRequest
    ): Call<Map<String, String>>

    @GET("api/tickets/{filaId}/por-fila/")
    fun getTicketsPorFilaConFecha(
        @Header("Authorization") token: String,
        @Path("filaId") filaId: Int,
        @Query("fecha") fecha: String
    ): Call<List<Ticket>>


    @PATCH("api/tickets/{ticketId}/cambiar-estado/")
    fun cambiarEstadoTicket(
        @Header("Authorization") token: String,
        @Path("ticketId") ticketId: Int,
        @Body nuevoEstado: Map<String, String>
    ): Call<Void>


    @POST("api/adminlogin/")
    fun loginAdmin(@Body request: LoginRequest): Call<AdminLoginResponse>

    @GET("api/usuarios/buscar/")
    fun buscarUsuariosPorNombre(
        @Header("Authorization") token: String,
        @Query("search") nombre: String
    ): Call<List<Usuario>>

    @GET("api/usuarios/{id}/")
    fun getUsuarioById(
        @Header("Authorization") token: String,
        @Path("id") usuarioId: Int
    ): Call<Usuario>

    @PATCH("api/usuarios/{id_usuario}/admin_editar/")
    fun adminEditarUsuario(
        @Header("Authorization") token: String,
        @Path("id_usuario") idUsuario: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<Void>



}