import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class WebAppSingletons extends Application {
    private Set<Object> singletons = new HashSet<Object>();

    public WebAppSingletons() {
        singletons.add(new API());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
