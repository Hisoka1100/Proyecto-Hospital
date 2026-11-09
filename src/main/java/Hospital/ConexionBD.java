/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Hospital;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Datos de conexión a MySQL
    private static final String URL =
            "jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC";

    private static final String USUARIO = "root";

    // Reemplaza este valor por tu contraseña de MySQL
    private static final String PASSWORD = "Kurama1994";

    // Método para obtener una conexión con la base de datos
    public static Connection conectar() throws SQLException {

        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    // Prueba de conexión
    public static void main(String[] args) {

        try (Connection conexion = conectar()) {

            System.out.println("¡Conexión exitosa con hospital_db!");

        } catch (SQLException e) {

            System.out.println("Error al conectar con MySQL:");
            e.printStackTrace();
        }
    }
}