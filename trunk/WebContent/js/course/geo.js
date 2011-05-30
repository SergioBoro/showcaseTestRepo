dojo.provide("course.geo");
dojo.require("course.geo.Map");

(function(){
var cgc = course.geo.charting;
var g = course.geo;

var mapRegistry = {maps:{}, data:{}};

var geo = {
    
    setMap: function(id) {
		this.id = id;
		this.map = mapRegistry[id].map;
    },

	// the format is {eventName, functionName}
    makeEvents: function(events) {
		
		dojo.forEach(events, function(event){
			var eventNames = event[0];
			var handlerFunc = dojo.isString(event[1]) ? dojo.getObject(event[1]) : event[1];
			this.map.connect(eventNames, handlerFunc);
		}, this);
    },
	
    makeControl: function(actions) {
		if (!actions) return;
		if (!dojo.isArray(actions)) actions = [actions];
		dojo.forEach(actions, function(action){
			var actionType = action.type;
			if (dojo.isString(actionType)) {
				dojo.require(actionType);
				actionType = dojo.getObject(actionType);
			}
			var options = action.options ;
			if (options) {
				var easing = options.easing;
				if (easing && dojo.isString(easing)) {
					dojo.require("dojo.fx.easing");
					options.easing = dojo.getObject(easing);
				}
			}
			else options = {};
			options.map = this.map;
			new actionType(this.map, options);
		}, this);
    }

}

g.makeMap = function(mapOptions) {	
    var o = mapOptions;
    // check if we have a convertor function
    if (arguments.length > 1) {
		// convertor function is the last argument
		var _convertorFunc = arguments[arguments.length-1];
		if (dojo.isFunction(_convertorFunc)) o = _convertorFunc.apply(null, arguments);
	}
    if (dojo.isString(o)) o = dojo.fromJson(o);
    
    var mapNode = dojo.byId(o.id);
    if (!mapNode) return null;
    
	o.currentProjection = "RUSSIA-ALBERS";
	o.attributesInFeature = false;
    g.destroyMap(o.id);
    var map = new g.Map(o.id, o);
    mapRegistry[o.id] = {map: map};
    geo.setMap(o.id);
	
	map.ready(function() {
		geo.makeControl(o.action);
		geo.makeEvents(o.events);
	});
    return map;
}

g.destroyMap = function(id) {
    if (mapRegistry[id]) {
	mapRegistry[id].map.destroy();
	if (mapRegistry[id].legend) mapRegistry[id].legend.destroy(true);
	delete mapRegistry[id];
    }
}

})();