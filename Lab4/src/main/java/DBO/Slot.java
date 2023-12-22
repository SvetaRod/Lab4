package DBO;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "slots")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @JsonIgnore
    @ManyToOne
    public Doctor doctor;

    @Column(name = "date", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    public Date date;
    @JsonIgnore
    @ManyToOne
    public User user;

    public Slot() {

    }

    public Slot(long id, Doctor doctor, Date date, User user) {
        this.doctor = doctor;
        this.date = date;
        this.user = user;
    }
}
