dojo.provide("course.geo.Map");

(function() {
	
var g = course.geo;
	
var defaultEngine = {type: "gfx", options:{}};

var restoreMethods = function(map) {
	if (!map.methods) map.methods = {};
	for (var type in g.methods) {
		if (!map.methods[type]) map.methods[type] = {};
		dojo.mixin(map.methods[type], g.methods[type]);
	}
	
	dojo.mixin(map.methods, {
		Map: {render: map._render, loadStyles: map._loadStyles}
	});
};


dojo.declare("course.geo.Map", null, {

	engine: null,
	
	// registry of externally available methods
	methods: null,
	
	extent: null,
	
    //	features: Object
	//		A registry of features that can be referenced by id.
	features: null,
    //	geometries: Object
	//		A registry of geometries that can be referenced by id.
	geometries: null,
    //	styles: Object
	//		A registry of styles that can be referenced by id.
	styles: null,
	//	featureContainer: course.geo.FeatureContainer
	//		Top level course.geo.FeatureContainer
	featureContainer: null,
	
	attributesInFeature: true,
	
	_featuresByClass: null,

	constructor: function(/* DOMNode */container, /* Object? */kwArgs){

		restoreMethods(this);
		
		// get map container coords
		dojo.style(container, "display", "block");
		this.container = container;

		if(!kwArgs) kwArgs = {};
		dojo.mixin(this, kwArgs);

		// features
		this.features = {};
		// geometries
		this.geometries = {};
		if (kwArgs.geometries) this.loadGeometries(kwArgs.geometries);
		// features
		this.featureContainer = new course.geo.FeatureContainer(null, {
			map: this,
			parent: this
		});
		// alias for this.featureContainer
		this.document = this.featureContainer;
		this.addStyle(course.geo.styling.style, true);
		
		this._featuresByClass = {};
		if (kwArgs.style) this.addStyle(kwArgs.style, true);
		
		// set engine
		this.setEngine(kwArgs.engine || (window.djConfig&&djConfig.mapEngine) || defaultEngine);
		
		if (kwArgs.features) this.featureContainer.addFeatures(kwArgs.features);
	},
	
	ready: function(/* Function */readyFunction) {
		if (!this.engine.isInitialized()) this.engine.initialize(dojo.hitch(this, function(){
			this.render();
			if (readyFunction) readyFunction();
		}));
	},
	
	render: function(stylingOnly, mode, features) {
		this.methods.Map.render.call(this, stylingOnly, mode, features);
	},

	_render: function(stylingOnly, mode, features) {
		if (features) {
			// render only the given features instead of the whole map tree
			for(var fid in features) {
				// TODO: avoid double rendering
				features[fid]._render(stylingOnly, mode);
			}
		}
		else {
			if (!this.extent) this.extent = this.getBbox();
			this._calculateViewport();
			this.engine.prerender();
			this.document._render(stylingOnly, mode);
		}
	},

	resize: function(width, height) {
		this._calculateViewport(width, height);
		this.render();
	},
	
	getContainer: function() {
		return this.engine.getTopContainer();
	},

	resize: function(width, height) {
		this.width = width;
		this.height = height;
		this.render();
	},

	destroy:function(){
		this.surface.destroy();
	},

	_calculateViewport: function() {
		var contentBox = dojo.contentBox(this.container);
		var coords = dojo.coords(this.container);
		this.width = this.width || contentBox.w || 100;
		this.height = this.height || contentBox.h || 100;
		this.x = coords.x;
		this.y = coords.y;
	},

	loadGeometries: function(/* String|Array|Object */geometries) {
		if (dojo.isString(geometries)) {
			dojo.xhrGet({
				url: geometries,
				handleAs: "json",
				sync: true,
				load: dojo.hitch(this, function(/* Array|Object */_geometries){
					this.loadGeometries(_geometries);
				})
			});
		}
		else if (dojo.isArray(geometries)) {
			dojo.forEach(geometries, function(geometry){
				if (geometry.id) this.geometries[geometry.id] = geometry;
			}, this);
		}
		else { // Object
			this.geometries = geometries;
		}
	},
	
	addStyle: function(/* Array|Object */style, preventRendering) {
		this.document.addStyle(style);
		if (!preventRendering) this.document._render(true);;
	},
	
	setStyle: function(/* Array|Object */style) {
		this.document.setStyle(style);
	},

	getGeometryById: function(id) {
		return this.geometries[id];
	},

	getStyleById: function(id) {
		return this.styles[id];
	},

	registerFeature: function(feature) {
		if (feature.id) this.features[feature.id] = feature;
	},

	getFeatureById: function(id) {
		return this.features[id];
	},

	connect: function(/* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		return this.featureContainer.connect(events, context, method);
	},

	connectWithHandle: function(/* Integer */handle, /* String|Array? */events, /*Object|null*/ context, /*String|Function*/ method) {
		return this.featureContainer.connectWithHandle(handle, events, context, method);
	},
	
	disconnect: function(handle) {
		this.featureContainer.disconnect(handle);
	},
	
	setEngine: function(engine) {
		if (dojo.isString(engine)) engine = {type: engine};
		if (!engine.declaredClass) {
			var options = {map: this};
			if (engine.options) dojo.mixin(options, engine.options);
			dojo.require("course.geo."+engine.type+".Engine");
			engine = new course.geo[engine.type].Engine(options);
		}
		this.engine = engine;
	},
	
	getBbox: function() {
		return this.document.getBbox();
	},
	
	destroy: function() {
		this.engine.surface.destroy();
	}
});

// default methods;
var p = g.Map.prototype;
g.methods = {
	Map: {render: p._render}
}

}());

dojo.require("course.geo.styling");
dojo.require("course.geo.FeatureContainer");
dojo.require("course.geo.Placemark");