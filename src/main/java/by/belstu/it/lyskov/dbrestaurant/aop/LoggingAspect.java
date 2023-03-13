package by.belstu.it.lyskov.dbrestaurant.aop;

import by.belstu.it.lyskov.dbrestaurant.entity.User;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
public class LoggingAspect {

    @AfterThrowing(value = "within(by.belstu.it.lyskov.dbrestaurant.connection.ConnectionPool)", throwing = "throwable")
    void afterThrowingAtConnectionPool(Throwable throwable) {
        log.fatal(throwable);
    }

    @Pointcut("execution(* by.belstu.it.lyskov.dbrestaurant.service.UserService.addUser(..))")
    void callAtUserServiceAddUser() {
    }

    @Before("callAtUserServiceAddUser()")
    void callBeforeAddUser() {
        log.info("Attempting to add new " + User.class.getName() + " entry...");
    }

    @After("callAtUserServiceAddUser()")
    void callAfterAddUser() {
        log.info("New " + User.class.getName() + " entry is added!");
    }

    @AfterThrowing(value = "callAtUserServiceAddUser()", throwing = "throwable")
    void callAfterThrowingAddUser(Throwable throwable) {
        log.error(throwable.getMessage());
    }

    @Pointcut("execution(* by.belstu.it.lyskov.dbrestaurant.service.UserService.updateUser(..)) && args(id)")
    void callAtUserServiceUpdateUser(Long id) {
    }

    @Before(value = "callAtUserServiceUpdateUser(id)", argNames = "id")
    void callBeforeUpdateUser(Long id) {
        log.info("Attempting to update " + User.class.getName() + " entry with id=" + id + " ...");
    }

    @After(value = "callAtUserServiceUpdateUser(id)", argNames = "id")
    void callAfterUpdateUser(Long id) {
        log.info(User.class.getName() + " entry with id='" + id + "' is updated!");
    }

    @AfterThrowing(value = "callAtUserServiceUpdateUser(id)", throwing = "throwable", argNames = "id,throwable")
    void callAfterThrowingUpdateUser(Long id, Throwable throwable) {
        log.error(throwable.getMessage());
    }

    @Pointcut("execution(* by.belstu.it.lyskov.dbrestaurant.service.UserService.deleteUser(..)) && args(id)")
    void callAtUserServiceDeleteUser(Long id) {
    }

    @Before(value = "callAtUserServiceDeleteUser(id)", argNames = "id")
    void callBeforeDeleteUser(Long id) {
        log.info("Attempting to delete " + User.class.getName() + " entry with id=" + id + " ...");
    }

    @After(value = "callAtUserServiceDeleteUser(id)", argNames = "id")
    void callAfterDeleteUser(Long id) {
        log.info(User.class.getName() + " entry with id='" + id + "' is deleted!");
    }

    @AfterThrowing(value = "callAtUserServiceDeleteUser(id)", throwing = "throwable", argNames = "id,throwable")
    void callAfterThrowingDeleteUser(Long id, Throwable throwable) {
        log.error(throwable.getMessage());
    }

//
//    @AfterThrowing(value = "within(by.belstu.it.lyskov.listener.SetupDataLoader)", throwing = "throwable")
//    void afterThrowingAtSetupDataLoader(Throwable throwable) {
//        log.fatal(throwable);
//    }
//
//    @Around("execution(* by.belstu.it.lyskov.service.UserService.*(Long,..)) && args(id,..)")
//    Object callAtUserServiceIdArgument(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
//        log.info("Attempting to find " + User.class.getName() + "with id=" + id);
//        Object result;
//        try {
//            result = joinPoint.proceed();
//        } catch (Throwable throwable) {
//            log.error(throwable.getMessage());
//            throw throwable;
//        }
//        return result;
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.UserService.addUser(..))")
//    void callAtUserServiceAddUser() {
//    }
//
//    @Before("callAtUserServiceAddUser()")
//    void callBeforeAddUser() {
//        log.info("Attempting to add new " + User.class.getName() + " entry...");
//    }
//
//    @After("callAtUserServiceAddUser()")
//    void callAfterAddUser() {
//        log.info("New " + User.class.getName() + " entry is added!");
//    }
//
//    @AfterThrowing(value = "callAtUserServiceAddUser()", throwing = "throwable")
//    void callAfterThrowingAddUser(Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.UserService.updateUser(..)) && args(id)")
//    void callAtUserServiceUpdateUser(Long id) {
//    }
//
//    @Before(value = "callAtUserServiceUpdateUser(id)", argNames = "id")
//    void callBeforeUpdateUser(Long id) {
//        log.info("Attempting to update " + User.class.getName() + " entry with id=" + id + " ...");
//    }
//
//    @After(value = "callAtUserServiceUpdateUser(id)", argNames = "id")
//    void callAfterUpdateUser(Long id) {
//        log.info(User.class.getName() + " entry with id='" + id + "' is updated!");
//    }
//
//    @AfterThrowing(value = "callAtUserServiceUpdateUser(id)", throwing = "throwable", argNames = "id,throwable")
//    void callAfterThrowingUpdateUser(Long id, Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.UserService.deleteUser(..)) && args(id)")
//    void callAtUserServiceDeleteUser(Long id) {
//    }
//
//    @Before(value = "callAtUserServiceDeleteUser(id)", argNames = "id")
//    void callBeforeDeleteUser(Long id) {
//        log.info("Attempting to delete " + User.class.getName() + " entry with id=" + id + " ...");
//    }
//
//    @After(value = "callAtUserServiceDeleteUser(id)", argNames = "id")
//    void callAfterDeleteUser(Long id) {
//        log.info(User.class.getName() + " entry with id='" + id + "' is deleted!");
//    }
//
//    @AfterThrowing(value = "callAtUserServiceDeleteUser(id)", throwing = "throwable", argNames = "id,throwable")
//    void callAfterThrowingDeleteUser(Long id, Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//
//    @Around("execution(* by.belstu.it.lyskov.service.TripService.*(Long,..)) && args(id,..)")
//    Object callAtTripServiceWithIdArgument(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
//        log.info("Attempting to find " + Trip.class.getName() + "with id=" + id);
//        Object result;
//        try {
//            result = joinPoint.proceed();
//        } catch (Throwable throwable) {
//            log.error(throwable.getMessage());
//            throw throwable;
//        }
//        return result;
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.TripService.addTrip(..))")
//    void callAtTripServiceAddTrip() {
//    }
//
//    @Before("callAtTripServiceAddTrip()")
//    void callBeforeAddTrip() {
//        log.info("Attempting to add new " + Trip.class.getName() + " entry...");
//    }
//
//    @After("callAtTripServiceAddTrip()")
//    void callAfterAddTrip() {
//        log.info("New " + Trip.class.getName() + " entry is added!");
//    }
//
//    @AfterThrowing(value = "callAtTripServiceAddTrip()", throwing = "throwable")
//    void callAfterThrowingAddTrip(Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.TripService.updateTrip(..)) && args(id)")
//    void callAtTripServiceUpdateTrip(Long id) {
//    }
//
//    @Before(value = "callAtTripServiceUpdateTrip(id)", argNames = "id")
//    void callBeforeUpdateTrip(Long id) {
//        log.info("Attempting to update " + Trip.class.getName() + " entry with id=" + id + " ...");
//    }
//
//    @After(value = "callAtTripServiceUpdateTrip(id)", argNames = "id")
//    void callAfterUpdateTrip(Long id) {
//        log.info(Trip.class.getName() + " entry with id='" + id + "' is updated!");
//    }
//
//    @AfterThrowing(value = "callAtTripServiceUpdateTrip(id)", throwing = "throwable", argNames = "id,throwable")
//    void callAfterThrowingUpdateTrip(Long id, Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.TripService.deleteTrip(..)) && args(id)")
//    void callAtTripServiceDeleteTrip(Long id) {
//    }
//
//    @Before(value = "callAtTripServiceDeleteTrip(id)", argNames = "id")
//    void callBeforeDeleteTrip(Long id) {
//        log.info("Attempting to delete " + Trip.class.getName() + " entry with id=" + id + " ...");
//    }
//
//    @After(value = "callAtTripServiceDeleteTrip(id)", argNames = "id")
//    void callAfterDeleteTrip(Long id) {
//        log.info(Trip.class.getName() + " entry with id='" + id + "' is deleted!");
//    }
//
//    @AfterThrowing(value = "callAtTripServiceDeleteTrip(id)", throwing = "throwable", argNames = "id,throwable")
//    void callAfterThrowingDeleteTrip(Long id, Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//
//    @Around("execution(* by.belstu.it.lyskov.service.ApplicationService.*(Long,..)) && args(id,..)")
//    Object callAtApplicationServiceWithIdArgument(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
//        log.info("Attempting to find " + Application.class.getName() + "with id=" + id);
//        Object result;
//        try {
//            result = joinPoint.proceed();
//        } catch (Throwable throwable) {
//            log.error(throwable.getMessage());
//            throw throwable;
//        }
//        return result;
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.ApplicationService.addApplication(..))")
//    void callAtApplicationServiceAddApplication() {
//    }
//
//    @Before("callAtApplicationServiceAddApplication()")
//    void callBeforeAddApplication() {
//        log.info("Attempting to add new " + Application.class.getName() + " entry...");
//    }
//
//    @After("callAtApplicationServiceAddApplication()")
//    void callAfterAddApplication() {
//        log.info("New " + Application.class.getName() + " entry is added!");
//    }
//
//    @AfterThrowing(value = "callAtApplicationServiceAddApplication()", throwing = "throwable")
//    void callAfterThrowingAddApplication(Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.ApplicationService.updateApplication(..)) && args(id)")
//    void callAtApplicationServiceUpdateApplication(Long id) {
//    }
//
//    @Before(value = "callAtApplicationServiceUpdateApplication(id)", argNames = "id")
//    void callBeforeUpdateApplication(Long id) {
//        log.info("Attempting to update " + Application.class.getName() + " entry with id=" + id + " ...");
//    }
//
//    @After(value = "callAtApplicationServiceUpdateApplication(id)", argNames = "id")
//    void callAfterUpdateApplication(Long id) {
//        log.info(Application.class.getName() + " entry with id='" + id + "' is updated!");
//    }
//
//    @AfterThrowing(value = "callAtApplicationServiceUpdateApplication(id)", throwing = "throwable", argNames = "id,throwable")
//    void callAfterThrowingUpdateApplication(Long id, Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
//
//    @Pointcut("execution(* by.belstu.it.lyskov.service.ApplicationService.deleteApplication(..)) && args(id)")
//    void callAtApplicationServiceDeleteApplication(Long id) {
//    }
//
//    @Before(value = "callAtApplicationServiceDeleteApplication(id)", argNames = "id")
//    void callBeforeDeleteApplication(Long id) {
//        log.info("Attempting to delete " + Application.class.getName() + " entry with id=" + id + " ...");
//    }
//
//    @After(value = "callAtApplicationServiceDeleteApplication(id)", argNames = "id")
//    void callAfterDeleteApplication(Long id) {
//        log.info(Application.class.getName() + " entry with id='" + id + "' is deleted!");
//    }
//
//    @AfterThrowing(value = "callAtApplicationServiceDeleteApplication(id)", throwing = "throwable", argNames = "id,throwable")
//    void callAfterThrowingDeleteApplication(Long id, Throwable throwable) {
//        log.error(throwable.getMessage());
//    }
}
