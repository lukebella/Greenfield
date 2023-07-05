package AdministratorServer.REST.Services;

import AdministratorClient.Statistics;
import AdministratorServer.REST.RESTServer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/statistics")
public class ClientService {

    @POST
    @Path("npollutions")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response airPollutions(Statistics s){
        return Response.ok(RESTServer.getArrange().getNAirPollutions(s.getID(), s.getN())).build();
        //else return Response.status(Response.Status.NOT_ACCEPTABLE).build();   //the system blocks..
    }

    @POST
    @Path("overallaverage")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response overallAverage(Statistics s){

        return Response.ok(RESTServer.getArrange().getOverallAveragePollutions(s.getT1(), s.getT2())).build();
    }


}