@org.springframework.modulith.ApplicationModule(
		displayName = "order",
		type = org.springframework.modulith.ApplicationModule.Type.CLOSED,
		allowedDependencies = {
			"cart :: application",
			"producer :: application",
			"identity :: application",
			"catalog :: application"
		})
package br.agriplataforma.order;
