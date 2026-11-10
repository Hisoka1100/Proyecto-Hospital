package Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDA0 {

    // Formato con que se envía la fecha y hora a MySQL
    private final DateTimeFormatter formatoSql =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Formato con que se muestra la fecha y hora en el formulario
    private final DateTimeFormatter formatoPantalla =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    // Pacientes para llenar el combo: cada elemento es {id, nombre}
    public List<String[]> listarPacientes() throws SQLException {

        String sql = "SELECT id_paciente, nombre FROM paciente ORDER BY nombre";
        List<String[]> lista = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_paciente")),
                    rs.getString("nombre")
                });
            }
        }
        return lista;
    }

    // Profesionales para llenar el combo: cada elemento es {id, nombre}
    public List<String[]> listarProfesionales() throws SQLException {

        String sql = "SELECT id_profesional, nombre FROM profesional ORDER BY nombre";
        List<String[]> lista = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_profesional")),
                    rs.getString("nombre")
                });
            }
        }
        return lista;
    }

    // Indica si el profesional ya tiene una consulta a esa fecha y hora.
    // idExcluir permite ignorar la propia consulta al editar (use 0 si no aplica).
    public boolean existeHorario(int idProfesional, LocalDateTime fechaHora,
                                 int idExcluir) throws SQLException {

        String sql = "SELECT COUNT(*) FROM consulta "
                + "WHERE id_profesional = ? AND fecha_hora = ? AND id_consulta <> ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProfesional);
            ps.setString(2, fechaHora.format(formatoSql));
            ps.setInt(3, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Registrar una consulta
    public void registrar(int id, int idPaciente, int idProfesional,
                          LocalDateTime fechaHora, String motivo)
            throws SQLException {

        String sql = "INSERT INTO consulta "
                + "(id_consulta, id_paciente, id_profesional, fecha_hora, motivo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, idPaciente);
            ps.setInt(3, idProfesional);
            ps.setString(4, fechaHora.format(formatoSql));
            ps.setString(5, motivo);

            ps.executeUpdate();
        }
    }

    // Buscar una consulta por ID. Retorna null si no existe.
    // Orden: id_consulta, id_paciente, id_profesional, fecha_hora, motivo
    public String[] buscar(int id) throws SQLException {

        String sql = "SELECT id_consulta, id_paciente, id_profesional, "
                + "fecha_hora, motivo FROM consulta WHERE id_consulta = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime fechaHora =
                            rs.getObject("fecha_hora", LocalDateTime.class);
                    return new String[]{
                        String.valueOf(rs.getInt("id_consulta")),
                        String.valueOf(rs.getInt("id_paciente")),
                        String.valueOf(rs.getInt("id_profesional")),
                        fechaHora.format(formatoPantalla),
                        rs.getString("motivo")
                    };
                }
            }
        }
        return null;
    }

    // Modificar paciente, profesional, fecha y hora, y motivo de una consulta
    public boolean editar(int id, int idPaciente, int idProfesional,
                          LocalDateTime fechaHora, String motivo)
            throws SQLException {

        String sql = "UPDATE consulta SET id_paciente = ?, id_profesional = ?, "
                + "fecha_hora = ?, motivo = ? WHERE id_consulta = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            ps.setInt(2, idProfesional);
            ps.setString(3, fechaHora.format(formatoSql));
            ps.setString(4, motivo);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Eliminar una consulta por su ID
    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM consulta WHERE id_consulta = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}