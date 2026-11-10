package Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EspecialidadDA0 {

    // Registrar una especialidad
    public void registrar(int id, String nombre, String descripcion)
            throws SQLException {

        String sql = "INSERT INTO especialidad "
                + "(id_especialidad, nombre, descripcion) "
                + "VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.executeUpdate();
        }
    }

    // Retorna {id, nombre, descripcion} o null si no existe
    public String[] buscar(int id) throws SQLException {

        String sql = "SELECT id_especialidad, nombre, descripcion "
                + "FROM especialidad WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String descripcion = rs.getString("descripcion");
                    return new String[] {
                            String.valueOf(rs.getInt("id_especialidad")),
                            rs.getString("nombre"),
                            descripcion == null ? "" : descripcion
                    };
                }
            }
        }
        return null;
    }

    // Modificar una especialidad (true si se actualizó alguna fila)
    public boolean editar(int id, String nombre, String descripcion)
            throws SQLException {

        String sql = "UPDATE especialidad SET nombre = ?, descripcion = ? "
                + "WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Eliminar una especialidad (true si se eliminó alguna fila)
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM especialidad WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.conectar();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}