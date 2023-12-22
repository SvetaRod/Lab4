import DTO.AddSlotDTO;
import DTO.AddUserToSlotDTO;
import DBO.Doctor;
import DBO.Slot;
import DBO.User;
import org.hibernate.SessionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("")
public class API {

    static SessionFactory sessionFactoryObj;

    @GET
    @Path("/doctors")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Doctor> getAllDoctors() {
        return Controller.getAllDoctors();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return Controller.getAllUsers();
    }

    @GET
    @Path("/slots")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Slot> getAllSlots() {
        return Controller.getAllSlots();
    }

    @POST
    @Path("/slots/write")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSlot(AddUserToSlotDTO addSlotDTO) {
        if(Controller.addUserToSlot(addSlotDTO))
            return Response.ok("Запись прошла успешно").build();
        return Response.status(Response.Status.FORBIDDEN).entity("Такого слота не существует").build();
    }

    @POST
    @Path("/slots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSlot(AddSlotDTO addSlotDTO) {
        Controller.addSlot(addSlotDTO);
        return Response.ok("Слот успешно добавлен").build();
    }
}
