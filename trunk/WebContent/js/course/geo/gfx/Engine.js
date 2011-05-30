dojo.provide("course.geo.gfx.Engine");

dojo.require("course.geo.Engine");

dojo.require("dojox.gfx");
dojo.require("dojox.gfx.move");

dojo.require("course.geo.gfx.Placemark");

(function(){

dojo.declare("course.geo.gfx.Engine", course.geo.Engine, {
	
	type: "gfx",
	
	initialize: function(/* Function */readyFunction) {
		this.surface = dojox.gfx.createSurface(this.map.container, this.map.width, this.map.height);
		this.group = this.surface.createGroup();
		
		// initialize some factories
		this.factories["Placemark"] = new course.geo.gfx.Placemark({
			group: this.group,
			map: this.map
		});

		this.initialized = true;
		
		readyFunction();
	},
	
	createContainer: function(parentContainer, featureType) {
		// parentContainer is actually parent group
		return parentContainer.createGroup();
	},

	prerender: function() {
		var mapExtent = this.map.extent;
		//transform map to fit container
		var mapWidth = mapExtent[2] - mapExtent[0];
		var mapHeight = mapExtent[3] - mapExtent[1];
		var scale = Math.min(this.map.width / mapWidth, this.map.height / mapHeight);
		this.group.setTransform([
			dojox.gfx.matrix.scale(scale)
		]);
		this.factories.Placemark.prerender();
	},
	
	getTopContainer: function() {
		return this.group;
	},
	
	connect: function(feature, event, method) {
		var connections = [];
		dojo.forEach(feature.baseShapes, function(shape){
			connections.push([shape, shape.connect(event, method)]);
		});
		return connections;
	},
	
	disconnect: function(connections) {
		dojo.forEach(connections, function(connection){
			connection[0].disconnect(connection[1]);
		});
	}
});

}());