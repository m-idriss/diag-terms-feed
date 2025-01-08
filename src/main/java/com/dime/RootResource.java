package com.dime;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/")
public class RootResource {

  @ConfigProperty(name = "quarkus.swagger-ui.path")
  String swaggerPath;

  @GET
  @Operation(hidden = true) // This hides the endpoint from the Swagger UI
  public Response redirectToSwagger() {
    return Response.status(Response.Status.FOUND)
        .location(java.net.URI.create(swaggerPath))
        .build();
  }
}

