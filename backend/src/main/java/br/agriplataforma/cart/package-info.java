@org.springframework.modulith.ApplicationModule(
		displayName = "cart",
		type = org.springframework.modulith.ApplicationModule.Type.CLOSED,
		allowedDependencies = {"catalog :: application", "tenant :: application"})
package br.agriplataforma.cart;
