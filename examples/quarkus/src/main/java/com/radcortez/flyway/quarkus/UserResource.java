package com.radcortez.flyway.quarkus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UserRepository userRepository;

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") final String id) {
        return userRepository.find(id)
                             .map(user -> Response.ok(user).build())
                             .orElse(Response.status(NOT_FOUND).build());
    }
}
