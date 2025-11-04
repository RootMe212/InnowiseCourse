package com.innowisekir.apigateway.security;

import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchange.Builder;
import reactor.core.publisher.Mono;

public class JwtAuthFilter implements GlobalFilter, Ordered {

  private final AntPathMatcher matcher = new AntPathMatcher();
  private final List<String> allowList;
  private final JwtService jwtService;

  public JwtAuthFilter(SecProperties secProperties, JwtService jwtService) {
    this.allowList = secProperties.getAllowlist();
    this.jwtService = jwtService;
  }


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    var request = exchange.getRequest();

    if ("OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
      return chain.filter(exchange);
    }

    String path = request.getPath().value();
    boolean allowed = allowList.stream().anyMatch(allow -> matcher.match(allow, path));
    if (allowed) {
      return chain.filter(exchange);
    }

    String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (auth == null || !auth.startsWith("Bearer ")) {
      return unauthorized(exchange, "Missing or invalid Authorization header");
    }

    String token = auth.substring(7);

    return jwtService.parseAndValidate(token)
        .flatMap(claims -> chain.filter(
            exchange.mutate()
                .request(builder -> propagateClaims((Builder) builder, claims))
                .build()
        ))
        .onErrorResume(ex -> unauthorized(exchange, "Invalid token"));
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 10;
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    var response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] body = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
    return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
  }

  private void propagateClaims(ServerWebExchange.Builder builder, Claims claims) {
    String subject = claims.getSubject();
    Object roles = claims.get("roles");
    builder.request(req -> req.headers(http -> {
      http.add("X-Auth-Subject", subject != null ? subject : "");
      http.add("X-Auth-Roles", roles != null ? String.valueOf(roles) : "");
    }));
  }
}
