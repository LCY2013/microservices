package com.lcydream.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class GateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GateWayApplication.class, args);
	}

	//WebFlux Function Endpoint
	@Bean
	public RouterFunction<ServerResponse> sayRouterFunction(){
		return route(GET("/fallback"), //返回失败
				request -> //结果集
				ServerResponse.ok() //返回状态码
						.body(Mono.just("fallback..."),String.class)
		);
	}

	//GateWay Functional Endpoint
	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes()
				.route("gateWay-service", //ID
						route ->
							route.path("/say") //Path Mapping
								.uri("http://localhost:8888") //URI Mapping

				)
				.route("gateWay-service", //ID
						route ->
							route.path("/say/{message}") //Path Mapping
								.uri("http://localhost:8888") //URI Mapping

				)
				.build(); //返回RouteLocator
	}

}
