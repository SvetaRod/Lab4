package DBO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String name;
    public String spec;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Slot> slots = new ArrayList<Slot>();
    public Doctor(){

    }
    public Doctor(long id, String name, String spec, List<Slot> slots){
        this.id = id;
        this.spec = spec;
        this.name = name;
        if(slots != null)
            this.slots = slots;
    }
}
