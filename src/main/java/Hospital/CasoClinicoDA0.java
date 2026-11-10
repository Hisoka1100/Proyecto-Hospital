package Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CasoClinicoDA0 {

    // Mismo formato de fecha que usan los formularios
    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

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

    // Registrar un caso clínico (fecha_cierre queda vacía)
    public void registrar(int id, int idPaciente, String estado,
                          LocalDate fechaApertura) throws SQLException {

        String sql = "INSERT INTO caso_clinico "
                + "(id_caso, id_paciente, estado, fecha_apertura) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, idPaciente);
            ps.setString(3, estado);
            ps.setString(4, fechaApertura.toString());

            ps.executeUpdate();
        }
    }

    // Buscar un caso por ID. Retorna null si no existe.
    // Orden: id_caso, id_paciente, estado, fecha_apertura, fecha_cierre
    public String[] buscar(int id) throws SQLException {

        String sql = "SELECT id_caso, id_paciente, estado, "
                + "fecha_apertura, fecha_cierre "
                + "FROM caso_clinico WHERE id_caso = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                   LocalDate apertura = rs.getObject("fecha_apertura", LocalDate.class);
                   LocalDate cierre = rs.getObject("fecha_cierre", LocalDate.class);
                   return new String[]{
                       String.valueOf(rs.getInt("id_caso")),
                       String.valueOf(rs.getInt("id_paciente")),
                       rs.getString("estado"),
                       apertura.format(formato),
                       cierre == null ? "" : cierre.format(formato)
                   };
                }
            }
        }
        return null;
    }

    // Modificar paciente, estado y fecha de apertura de un caso
    public boolean editar(int id, int idPaciente, String estado,
                          LocalDate fechaApertura) throws SQLException {

        String sql = "UPDATE caso_clinico SET id_paciente = ?, estado = ?, "
                + "fecha_apertura = ? WHERE id_caso = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);
            ps.setString(2, estado);
            ps.setString(3, fechaApertura.toString());
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Cerrar un caso abierto. Retorna false si no existe o ya estaba cerrado.
    public boolean cerrar(int id, LocalDate fechaCierre) throws SQLException {

        String sql = "UPDATE caso_clinico SET estado = 'Cerrado', "
                + "fecha_cierre = ? WHERE id_caso = ? AND estado = 'Abierto'";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, fechaCierre.toString());
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

        // Consultas del paciente, para mostrarlas en el área de texto
    public List<String> consultasDePaciente(int idPaciente) throws SQLException {

        String sql = "SELECT fecha_hora, estado FROM consulta "
                + "WHERE id_paciente = ? ORDER BY fecha_hora";
        List<String> lista = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.time.LocalDateTime fechaHora =
                            rs.getObject("fecha_hora", java.time.LocalDateTime.class);
                    lista.add(fechaHora.format(
                            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"))
                            + " - " + rs.getString("estado"));
                }
            }
        }
        return lista;
    }
}