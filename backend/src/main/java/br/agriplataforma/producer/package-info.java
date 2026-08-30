@org.springframework.modulith.ApplicationModule(
		displayName = "producer",
		type = org.springframework.modulith.ApplicationModule.Type.CLOSED,
		allowedDependencies = {"identity :: application", "tenant :: application"})
package br.agriplataforma.producer;
