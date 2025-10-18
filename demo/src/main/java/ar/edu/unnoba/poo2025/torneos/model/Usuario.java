package ar.edu.unnoba.poo2025.torneos.model;
import jakarta.persistence.*;


@Entity
@Table(
        name = "Usuario", //@Table me permite definir el nombre de la tabla en la base de datos, sino usa el nombre de la clase
        uniqueConstraints ={@UniqueConstraint(name = "uk_user_email", columnNames = "email")
        },
        indexes ={@Index(name = "idx_user_email", columnList = "email")
        }
)
@Inheritance(strategy = InheritanceType.JOINED) // Con esto le digo a JPA cómo va a manejar la herencia, en este caso lo mejor es JOINED que crea una tabla por cada clase, también JPA genera automáticamente las FK en las tablas hijas
public abstract class Usuario{ //abstracta porque un usuario tiene que ser o admin o participante
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id autoincremental con estrategia IDENTITY que es usa incremento automático como en los DBGMS
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    protected Usuario() {} //constructor vacío protegido para JPA

    protected Usuario(String email, String password) { //constructor con parámetros protegido para las subclases
        this.email = email;
        this.password = password;
    }

    public long getId() { //sólo getId porque el id es autogenerado
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
