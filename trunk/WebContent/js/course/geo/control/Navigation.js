dojo.provide("course.geo.control.Navigation");


(function(){

dojo.declare("course.geo.control.Navigation", null, {
	
	factoryType: "control.Navigation",
	
	constructor: function(map) {
		this.map = map;
		var factory = this.map.engine.getFactory(this.factoryType);
		if (factory) {
			dojo.mixin(this, factory);
			this.enable();
		}
	},

	enable: function() {
		
	},
	
	disable: function() {
		
	}
});

})();
