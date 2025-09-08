import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Entrada principal
public class clinica{
    public static void main(String[] args) {
        Dueno[] duenos = new Dueno[500];
        Paciente[] pacientes = new Paciente[1000];

        for (int i = 0; i < duenos.length; i++) {
            long id = i + 1L;
            String nombre = "Dueno " + id;
            String telefono = generarTelefono(i);
            String email = (i % 5 == 0) ? null : ("Dueno" + id + "@gmail.com");
            String direccion = (i % 7 == 0) ? "no hay dirección registada" : ("Calle "+(100+i));

            duenos[i] = new Dueno(id, nombre, telefono, email, direccion);
        }

        for (int i = 0; i < pacientes.length; i++) {
            long id = i + 1L;
            Dueno dueno = duenos[i / 2];
            String nombre = ((i % 2) == 0 ? "Toby" : "Shaky") + "-" + id;
            String especie = (i % 2 == 0) ? "perro" : "gato";
            String raza = "mestizo";
            int edadMeses = (i % 120) + 1;
            double pesoKg = 1.0 + (i % 30);
            pacientes[i] = new Paciente(id, nombre, especie, raza, edadMeses, pesoKg, dueno);
        }


        System.out.println("Estos son los primeros 3 dueños");
        for (int i = 0; i < 3; i++) {
            System.out.println(duenos[i].resumen());
        }
        System.out.println("Estas son las primeras 6 mascotas");
        for (int i = 0; i < 6; i++) {
            System.out.println(pacientes[i].resumen());
        }


        System.out.println("Reporte");
        int cachorros = contarCachorros(pacientes);
        System.out.println("Cachorros (<=12 meses): " + cachorros);

        int perros = contarPorEspecie(pacientes, "perro");
        int gatos = contarPorEspecie(pacientes, "gato");
        System.out.println("Distribución por especie — perros: " + perros + ", gatos: " + gatos);

        double promPerro = pesoPromedioPorEspecie(pacientes, "perro");
        double promGato = pesoPromedioPorEspecie(pacientes, "gato");
        System.out.printf("Peso promedio — perro:  kg\n", promPerro);
        System.out.printf("Peso promedio — gato:  kg\n", promGato);

        System.out.println("\nTop 5 mascotas más grandes:");
        Paciente[] top5 = top5Longevos(pacientes);
        for (Paciente p : top5) {
            System.out.println(p.resumen());
        }

        Cita[] citas = new Cita[40];
        LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        int citaId = 1;
        for (int i = 0; i < 20; i++) {
            Dueno d = duenos[i];

            Paciente p1 = pacientes[i * 2];
            Paciente p2 = pacientes[i * 2 + 1];

            citas[(i * 2)] = new Cita(citaId++, d, p1, base);
            citas[(i * 2) + 1] = new Cita(citaId++, d, p2, base.plusHours(1));
        }


        for (Cita c : citas) {
            if (c.getId() % 3 == 0) {
                c.cancelar("no podra asistir");
            } else if (c.getId() % 2 == 0) {
                c.reagendar(1);
            } else {
                c.marcarAtendida();
            }
        }

        System.out.println("Historial de citas");
        for (Cita c : citas) {
            System.out.println(c.resumen() + " — " + c.getEstado());
        }
    }

    private static int contarCachorros(Paciente[] pacientes) {
        int count = 0;
        for (Paciente p : pacientes) {
            if (p != null && p.esCachorro()) count++;
        }
        return count;
    }

    private static int contarPorEspecie(Paciente[] pacientes, String especie) {
        int count = 0;
        for (Paciente p : pacientes) {
            if (p != null && p.getEspecie().equalsIgnoreCase(especie)) count++;
        }
        return count;
    }

    private static double pesoPromedioPorEspecie(Paciente[] pacientes, String especie) {
        double total = 0.0;
        int count = 0;
        for (Paciente p : pacientes) {
            if (p != null && p.getEspecie().equalsIgnoreCase(especie)) {
                total += p.getPesoKg();
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    private static Paciente[] top5Longevos(Paciente[] pacientes) {
        Paciente[] top = new Paciente[5];
        int[] edades = new int[5];
        for (Paciente p : pacientes) {
            if (p == null) continue;
            int edad = p.getEdadMeses();

            for (int k = 0; k < 5; k++) {
                if (top[k] == null || edad > edades[k]) {

                    for (int j = 4; j > k; j--) {
                        top[j] = top[j - 1];
                        edades[j] = edades[j - 1];
                    }
                    top[k] = p;
                    edades[k] = edad;
                    break;
                }
            }
        }
        return top;
    }

    private static String generarTelefono(int i) {

        int numero = (int) ((i * 9301L + 49297) % 1_0000_0000L);
        String ocho = String.format("%08d", Math.abs(numero));
        return "502-" + ocho;
    }
}


class Dueno {
    private long id;
    private String nombreCompleto;
    private String telefono; // formato libre, validación mínima
    private String email;    // puede ser null
    private String direccion; // puede ser "sin dirección"


    public Dueno(long id, String nombreCompleto, String telefono, String email, String direccion) {
        setId(id);
        setNombreCompleto(nombreCompleto);
        setTelefono(telefono);
        setEmail(email);
        setDireccion(direccion);
    }

    public Dueno(long id, String nombreCompleto, String telefono) {
        this(id, nombreCompleto, telefono, null, "sin dirección");
    }

    public long getId() { return id; }
    public void setId(long id) {
        if (id <= 0) throw new IllegalArgumentException("id debe ser > 0");
        this.id = id;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty())
            throw new IllegalArgumentException("nombreCompleto requerido");
        this.nombreCompleto = nombreCompleto.trim();
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty())
            throw new IllegalArgumentException("telefono requerido");
        this.telefono = telefono.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email != null && email.trim().isEmpty()) email = null;
        this.email = email;
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) direccion = "sin dirección";
        this.direccion = direccion.trim();
    }

    public String resumen() {
        return String.format("Dueno{id=%d, nombre='%s', tel=%s, email=%s, dir='%s'}",
                id, nombreCompleto, telefono, email, direccion);
    }
}

class Paciente {
    private long id;
    private String nombre;
    private String especie;
    private String raza;
    private int    edadMeses;
    private double pesoKg;
    private Dueno dueno;


    public Paciente(long id, String nombre, String especie, String raza, int edadMeses, double pesoKg) {
        this(id, nombre, especie, raza, edadMeses, pesoKg, null);
    }

    public Paciente(long id, String nombre, String especie, String raza, int edadMeses, double pesoKg, Dueno dueno) {
        setId(id);
        setNombre(nombre);
        setEspecie(especie);
        setRaza(raza);
        setEdadMeses(edadMeses);
        setPesoKg(pesoKg);
        setDueno(dueno);
    }

    public Paciente(long id, String nombre, String especie) {
        this(id, nombre, especie, "mestizo", 1, 1.0, null);
    }

    public long getId() { return id; }
    public void setId(long id) {
        if (id <= 0) throw new IllegalArgumentException("id debe ser > 0");
        this.id = id;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("Se requiere un nombre");
        this.nombre = nombre.trim();
    }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) {
        if (especie == null) throw new IllegalArgumentException("Se requiere una especie");
        String e = especie.trim().toLowerCase();
        if (!(e.equals("perro") || e.equals("gato")))
            throw new IllegalArgumentException("La especie tiene que ser 'perro' o 'gato'");
        this.especie = e;
    }

    public String getRaza() { return raza; }
    public void setRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) raza = "mestizo";
        this.raza = raza.trim();
    }

    public int getEdadMeses() { return edadMeses; }
    public void setEdadMeses(int edadMeses) {
        if (edadMeses < 1) throw new IllegalArgumentException("edadMeses debe ser >= 1");
        this.edadMeses = edadMeses;
    }

    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) {
        if (pesoKg <= 0) throw new IllegalArgumentException("pesoKg debe ser > 0");
        this.pesoKg = pesoKg;
    }

    public Dueno getDueno() { return dueno; }
    public void setDueno(Dueno dueno) {
        this.dueno = dueno;
    }

    public boolean esCachorro() {
        return this.edadMeses <= 12;
    }

    public String resumen() {
        String d = (dueno == null) ? "sin dueño" : ("Dueno{" + dueno.getId() + ", " + dueno.getNombreCompleto() + "}");
        return String.format("Paciente{id=%d, nombre='%s', especie='%s', raza='%s', edadMeses=%d, peso=%.1f, %s}",
                id, nombre, especie, raza, edadMeses, pesoKg, d);
    }
}

enum EstadoCita {
    PROGRAMADA,
    REAGENDADA,
    CANCELADA,
    ATENDIDA
}

class Cita {
    private long id;
    private Dueno dueno;
    private Paciente paciente;
    private LocalDateTime fechaHora; // futura
    private EstadoCita estado;
    private String motivoCancelacion;

    public Cita(long id, Dueno dueno, Paciente paciente, LocalDateTime fechaHora) {
        setId(id);
        setDueno(dueno);
        setPaciente(paciente);
        setFechaHora(fechaHora);
        this.estado = EstadoCita.PROGRAMADA;
    }

    public long getId() { return id; }
    public void setId(long id) {
        if (id <= 0) throw new IllegalArgumentException("id debe ser > 0");
        this.id = id;
    }

    public Dueno getDueno() { return dueno; }
    public void setDueno(Dueno dueno) {
        this.dueno = Objects.requireNonNull(dueno, "dueno requerido");
    }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) {
        this.paciente = Objects.requireNonNull(paciente, "paciente requerido");
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) throw new IllegalArgumentException("fechaHora requerida");
        if (!fechaHora.isAfter(LocalDateTime.now().minusSeconds(1)))
            throw new IllegalArgumentException("fechaHora debe ser futura");
        this.fechaHora = fechaHora;
    }

    public EstadoCita getEstado() { return estado; }

    public String getMotivoCancelacion() { return motivoCancelacion; }

    public void reagendar(int dias) {
        if (estado == EstadoCita.CANCELADA || estado == EstadoCita.ATENDIDA)
            return;
        if (dias < 1) throw new IllegalArgumentException("dias debe ser >=1");
        setFechaHora(this.fechaHora.plusDays(dias));
        this.estado = EstadoCita.REAGENDADA;
    }

    public void cancelar(String motivo) {
        if (estado == EstadoCita.ATENDIDA) return;
        this.estado = EstadoCita.CANCELADA;
        this.motivoCancelacion = (motivo == null || motivo.isBlank()) ? "sin motivo" : motivo.trim();
    }

    public void marcarAtendida() {
        if (estado == EstadoCita.CANCELADA) return;
        this.estado = EstadoCita.ATENDIDA;
    }

    public String resumen() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Cita{id=%d, fecha=%s, dueno=%s, paciente=%s}",
                id,
                fechaHora.format(fmt),
                dueno.getNombreCompleto(),
                paciente.getNombre());
    }
}
