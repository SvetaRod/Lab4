import DTO.AddSlotDTO;
import DTO.AddUserToSlotDTO;
import DBO.Doctor;
import DBO.Slot;
import DBO.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Controller {

    static long interval = 5000;
    static Thread run;
    static SessionFactory sessionFactoryObj;

    public Controller(){

    }

    public static SessionFactory buildSessionFactory() {
        // Creating Configuration Instance & Passing Hibernate Configuration File
        Configuration configObj = new Configuration();
        configObj.addAnnotatedClass(Doctor.class);
        configObj.addAnnotatedClass(Slot.class);
        configObj.addAnnotatedClass(User.class);
        configObj.configure();

        // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();

        // Creating Hibernate SessionFactory Instance
        sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
        return sessionFactoryObj;
    }

    public static List<User> getAllUsers() {
        Session session = sessionFactoryObj.openSession();
        session.beginTransaction();
        List<User> users = session.createCriteria(User.class).list();
        session.getTransaction().commit();
        session.close();
        return users;
    }
    public static void addUser(User user){
        Session session = sessionFactoryObj.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }
    public static List<Doctor> getAllDoctors() {
        Session session = sessionFactoryObj.openSession();
        session.beginTransaction();
        List<Doctor> doctors = session.createCriteria(Doctor.class).list();
        session.getTransaction().commit();
        session.close();
        return doctors;
    }
    public static void addDoctor(Doctor doctor){
        Session session = sessionFactoryObj.openSession();
        session.beginTransaction();
        session.save(doctor);
        session.getTransaction().commit();
        session.close();
    }
    public static List<Slot> getAllSlots() {
        Session session = sessionFactoryObj.withOptions().
                jdbcTimeZone(TimeZone.getTimeZone("UTC")).openSession();
        session.beginTransaction();
        List<Slot> slots = session.createCriteria(Slot.class).list();
        session.getTransaction().commit();
        session.close();
        return slots;
    }
    public static boolean addUserToSlot(AddUserToSlotDTO addUserSlotDTO){
        List<Slot> slots = getAllSlots();
        for(Slot slot : slots){
            if(slot.doctor.id == addUserSlotDTO.doctorID && slot.date.getTime() == addUserSlotDTO.date.getTime()) {
                Session session = sessionFactoryObj.withOptions().
                        jdbcTimeZone(TimeZone.getTimeZone("UTC")).openSession();
                session.beginTransaction();
                slot.user = (User) session.get(User.class, addUserSlotDTO.userID);
                session.merge(slot);
                session.getTransaction().commit();
                session.close();
                return true;
            }
        }
        return false;
    }

    public static void addSlot(AddSlotDTO addSlotDTO){
        Session session = sessionFactoryObj.withOptions().
                jdbcTimeZone(TimeZone.getTimeZone("UTC")).openSession();
        session.beginTransaction();
        Doctor doctor = (Doctor) session.get(Doctor.class, addSlotDTO.doctorID);
        Slot slot = new Slot();
        slot.date = addSlotDTO.date;
        slot.doctor = doctor;
        session.save(slot);
        session.getTransaction().commit();
        session.close();
    }
    public static List<Slot> getNotificationSlots(Date date, long timeBefore){
        List<Slot> slots = getAllSlots();
        List<Slot> notifSlots = new ArrayList<>();
        for(Slot slot : slots){
            long time = slot.date.getTime() - date.getTime();
            if(slot.user != null && time >= timeBefore - interval && time <= timeBefore)
                notifSlots.add(slot);
        }
        return notifSlots;
    }

    public static void init(){
        initUsers();
        initDoctors();
        initSlots();
    }

    private static void initUsers(){
        User user1 = new User(1, "888-888", "Лебедев Михаил Петрович");
        addUser(user1);
        User user2 = new User(2, "232-323", "Иванов Иван Александрович");
        addUser(user2);
        User user3 = new User(3, "132-567", "Иванов Александр Иванович");
        addUser(user3);

    }
    private static void initDoctors(){
        Doctor doctor1 = new Doctor(1, "Глазнов Дмитрий Сергеевич", "Окулист", null);
        addDoctor(doctor1);
        Doctor doctor2 = new Doctor(2, "Лебедева Инна Степановна", "Терапевт", null);
        addDoctor(doctor2);
    }

    private static void initSlots(){
        AddSlotDTO slot = new AddSlotDTO();
        slot.doctorID = 1;
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.minuteOfHour().roundFloorCopy();
        dateTime = dateTime.plusHours(2);
        dateTime = dateTime.plusMinutes(1);
        slot.date = dateTime.toDate();
        addSlot(slot);
        AddUserToSlotDTO wrSlot = new AddUserToSlotDTO();
        wrSlot.date = slot.date;
        wrSlot.doctorID = 1;
        wrSlot.userID = 1;
        addUserToSlot(wrSlot);

        slot = new AddSlotDTO();
        slot.doctorID = 2;
        dateTime = dateTime.plusDays(1);
        dateTime = dateTime.minusHours(2);
        slot.date = dateTime.toDate();
        addSlot(slot);
        wrSlot = new AddUserToSlotDTO();
        wrSlot.date = slot.date;
        wrSlot.doctorID = 2;
        wrSlot.userID = 2;
        addUserToSlot(wrSlot);
    }

    public static void startThread(){
        run = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Date date = new Date(System.currentTimeMillis());
                        List<Slot> slots = Controller.getNotificationSlots(date, 7200000);
                        for(Slot slot : slots){
                            DateTime dateTime = new DateTime(slot.date);
                            System.out.println(date + " | Привет, " + slot.user.name + "! Вам через 2 часа к "+ slot.doctor.spec +"у к "+ dateTime.toLocalTime());
                        }

                        slots = Controller.getNotificationSlots(date, 86400000);
                        for(Slot slot : slots){
                            DateTime dateTime = new DateTime(slot.date);
                            System.out.println(date + " | Привет, " + slot.user.name + "! Напоминаем, что Вы записаны к "+ slot.doctor.spec +"у завтра на "+ dateTime.toLocalTime());
                        }
                        Thread.sleep(interval); //1000 - 1 сек
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        run.start();
    }
}
