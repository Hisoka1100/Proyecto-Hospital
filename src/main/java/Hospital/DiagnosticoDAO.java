/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiagnosticoDAO {

    // Registra un diagnóstico y confirma la transacción solo si insertó una fila.
    public boolean registrar(int id, int idConsulta,
            String descripcion, String recomendaciones)
            throws SQLException {

        String sql = "INSERT INTO diagnostico "
                + "(id_diagnostico, id_consulta, descripcion, recomendaciones) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setInt(1, id);
            ps.setInt(2, idConsulta);
            ps.setString(3, descripcion);
            ps.setString(4, recomendaciones);

            try {
                int filasInsertadas = ps.executeUpdate();
                if (filasInsertadas != 1) {
                    con.rollback();
                    return false;
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        }
    }

    // Buscar diagnóstico por su identificador
    public String[] buscar(int id) throws SQLException {

        String sql = "SELECT id_diagnostico, id_consulta, "
                + "descripcion, recomendaciones "
                + "FROM diagnostico WHERE id_diagnostico = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id_diagnostico")),
                        String.valueOf(rs.getInt("id_consulta")),
                        rs.getString("descripcion"),
                        rs.getString("recomendaciones")
                    };
                }
            }
        }

        return null;
    }

    // Modificar un diagnóstico existente
    public boolean editar(int id, int idConsulta,
            String descripcion, String recomendaciones)
            throws SQLException {

        String sql = "UPDATE diagnostico SET "
                + "id_consulta = ?, descripcion = ?, recomendaciones = ? "
                + "WHERE id_diagnostico = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);
            ps.setString(2, descripcion);
            ps.setString(3, recomendaciones);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Eliminar un diagnóstico
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM diagnostico "
                + "WHERE id_diagnostico = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Comprobar que la consulta exista
    public boolean existeConsulta(int idConsulta)
            throws SQLException {

        String sql = "SELECT id_consulta FROM consulta "
                + "WHERE id_consulta = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
