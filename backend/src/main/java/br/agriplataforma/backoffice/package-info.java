@org.springframework.modulith.ApplicationModule(
		displayName = "backoffice",
		type = org.springframework.modulith.ApplicationModule.Type.CLOSED,
		allowedDependencies = {"order :: application", "identity :: application"})
package br.agriplataforma.backoffice;
