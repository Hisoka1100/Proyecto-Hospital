package Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfesionalDAO {

    // Convierte el nombre de la especialidad en su ID
    private int idEspecialidad(Connection con, String nombre) throws SQLException {
        String sql = "SELECT id_especialidad FROM especialidad WHERE nombre = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_especialidad");
                }
            }
        }
        throw new SQLException("La especialidad '" + nombre
                + "' no existe en la base de datos.");
    }

    public void registrar(int id, String nombre, String tipo, String especialidad)
            throws SQLException {

        String sql = "INSERT INTO profesional "
                + "(id_profesional, nombre, tipo_profesional, id_especialidad) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar()) {
            int idEsp = idEspecialidad(con, especialidad);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, nombre);
                ps.setString(3, tipo);
                ps.setInt(4, idEsp);
                ps.executeUpdate();
            }
        }
    }

    // Retorna {id, nombre, tipo, especialidad} o null si no existe
    public String[] buscar(int id) throws SQLException {

        String sql = "SELECT p.id_profesional, p.nombre, p.tipo_profesional, "
                + "e.nombre AS especialidad "
                + "FROM profesional p "
                + "JOIN especialidad e ON p.id_especialidad = e.id_especialidad "
                + "WHERE p.id_profesional = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                            String.valueOf(rs.getInt("id_profesional")),
                            rs.getString("nombre"),
                            rs.getString("tipo_profesional"),
                            rs.getString("especialidad")
                    };
                }
            }
        }
        return null;
    }

    public boolean editar(int id, String nombre, String tipo, String especialidad)
            throws SQLException {

        String sql = "UPDATE profesional SET nombre = ?, tipo_profesional = ?, "
                + "id_especialidad = ? WHERE id_profesional = ?";

        try (Connection con = ConexionBD.conectar()) {
            int idEsp = idEspecialidad(con, especialidad);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, tipo);
                ps.setInt(3, idEsp);
                ps.setInt(4, id);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM profesional WHERE id_profesional = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}