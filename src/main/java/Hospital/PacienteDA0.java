/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Hospital;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

 public class PacienteDA0 {

    // Formato de fecha que utiliza el formulario
    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    // Registrar un paciente
    public void registrar(int id, String rut, String nombre,
                          LocalDate fecha, String contacto)
            throws SQLException {

        String sql = "INSERT INTO paciente "
                + "(id_paciente, rut, nombre, fecha_nacimiento, contacto) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, rut);
            ps.setString(3, nombre);
            ps.setDate(4, java.sql.Date.valueOf(fecha));
            ps.setString(5, contacto);

            ps.executeUpdate();
        }
        
    }

    // Buscar un paciente por su ID
    // Retorna null si no existe
    public String[] buscar(int id) throws SQLException {

        String sql = "SELECT id_paciente, rut, nombre, "
                + "fecha_nacimiento, contacto "
                + "FROM paciente WHERE id_paciente = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id_paciente")),
                        rs.getString("rut"),
                        rs.getString("nombre"),
                        rs.getDate("fecha_nacimiento")
                                .toLocalDate().format(formato),
                        rs.getString("contacto")
                    };
                }
            }
        }

        return null;
    }

    // Modificar los datos de un paciente
    public boolean editar(int id, String rut, String nombre,
                          LocalDate fecha, String contacto)
            throws SQLException {

        String sql = "UPDATE paciente SET rut = ?, nombre = ?, "
                + "fecha_nacimiento = ?, contacto = ? "
                + "WHERE id_paciente = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, rut);
            ps.setString(2, nombre);
            ps.setDate(3, java.sql.Date.valueOf(fecha));
            ps.setString(4, contacto);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Eliminar un paciente por su ID
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM paciente WHERE id_paciente = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
    
// Listar todos los pacientes registrados
public java.util.List<String[]> listar() throws SQLException {

    java.util.List<String[]> pacientes = new java.util.ArrayList<>();

    String sql = "SELECT id_paciente, rut, nombre, "
            + "fecha_nacimiento, contacto "
            + "FROM paciente ORDER BY id_paciente";

    try (Connection conexion = ConexionBD.conectar();
         PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            pacientes.add(new String[]{
                String.valueOf(rs.getInt("id_paciente")),
                rs.getString("rut"),
                rs.getString("nombre"),
                rs.getDate("fecha_nacimiento")
                        .toLocalDate().format(formato),
                rs.getString("contacto")
            });
        }
    }

    return pacientes;
}

}
