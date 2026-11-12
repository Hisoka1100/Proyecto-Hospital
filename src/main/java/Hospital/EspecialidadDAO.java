package Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class EspecialidadDAO {

    public void registrar(int id, String nombre, String descripcion) throws SQLException {
        String sql = "INSERT INTO especialidad "
                + "(id_especialidad, nombre, descripcion) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, nombre);
            setDescripcion(ps, 3, descripcion);
            ps.executeUpdate();
        }
    }

    // Retorna {id, nombre, descripcion} o null si no existe.
    public String[] buscar(int id) throws SQLException {
        String sql = "SELECT id_especialidad, nombre, descripcion "
                + "FROM especialidad WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id_especialidad")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    };
                }
            }
        }
        return null;
    }

    public boolean editar(int id, String nombre, String descripcion) throws SQLException {
        String sql = "UPDATE especialidad SET nombre = ?, descripcion = ? "
                + "WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            setDescripcion(ps, 2, descripcion);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM especialidad WHERE id_especialidad = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void setDescripcion(PreparedStatement ps, int indice, String descripcion)
            throws SQLException {
        if (descripcion == null || descripcion.isBlank()) {
            ps.setNull(indice, Types.LONGVARCHAR);
        } else {
            ps.setString(indice, descripcion);
        }
    }
}
