package AdministratorServer.REST.Services;

import AdministratorServer.REST.Robots;
import AdministratorServer.REST.StartRobotListResponse;
import CleaningRobot.Initialization.Robot;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/robots")
public class RobotsService {

    //restituisce la lista di robot
    @GET
    @Path("robotlist")
    @Produces({"application/json", "application/xml"})
    public Response getRobotsList(){
        return Response.ok(Robots.getInstance()).build();
    }

    @GET
    @Path("hello")
    @Produces({"text/plain"})
    public String welcome() {
        return "Welcome Robot! Sign up in Greenfield:";
    }

    //permette di inserire un robot (id, porta e address)
    @POST
    @Path("add")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addRobot(Robot r){

        if (!Robots.getInstance().isRobotAlreadyRegistered(r.getID()))
        {
            Robots.getInstance().add(r);
            return Response.ok(new StartRobotListResponse(Robots.getInstance().getRobotsList())).build();
        }

        else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();   //the system blocks..
        }
    }

    @DELETE
    @Path("remove")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response removeRobot(Robot r){

        if (Robots.getInstance().isRobotAlreadyRegistered(r.getID()))
        {
            System.out.println("Service: Robot to remove: "+r);
            Robots.getInstance().remove(r);
            return Response.ok().build();
        }

        else {
            System.out.println("Robot already moved or never existed");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();   //the system blocks..
        }
    }

    //permette di prelevare un robot con un determinato id
    @Path("get/{id}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getByID(@PathParam("id") String ID){
        Robot r = Robots.getInstance().getByID(ID);
        if(r!=null)
            return Response.ok(r).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

}