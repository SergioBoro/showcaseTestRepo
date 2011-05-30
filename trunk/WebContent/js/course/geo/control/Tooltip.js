dojo.provide("course.geo.control.Tooltip");

dojo.require("course.geo.control.Base");

(function(){
	var DEFAULT_TEXT = function(feature){
		return feature.tooltip || feature.name || feature.id;
	};
	
	dojo.declare("course.geo.control.Tooltip", course.geo.control.Base, {
		
		factoryType: "control.Tooltip",

		constructor: function(map, kwArgs) {
			this.text = kwArgs && kwArgs.text ? kwArgs.text : DEFAULT_TEXT;
			
			this.attachFactory(this.enabled);
		},
		
		process: function() {

		}
	});
})();
