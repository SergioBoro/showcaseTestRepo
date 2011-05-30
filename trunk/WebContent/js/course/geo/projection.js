dojo.provide("course.geo.projection");

dojo.require("course.geo.Map");

(function(){
	
var g = course.geo,
	gp = g.projection,
	u = g.util,
	transforms = {},
	projInstances = {};
	
var getProjInstance = function(proj) {
	var instance;
	// try different ways to perform the transformation
	// dojo wrapper for Proj4js
	if (dojo._loadedModules["course.geo.util.proj4js"]) {
		if (!projInstances[proj]) projInstances[proj] = new u.proj4js.Proj(proj);
		if (projInstances[proj] && projInstances[proj].readyToUse) instance = projInstances[proj];
	}
	// original Proj4js
	else if (window.Proj4js) {
		if (!projInstances[proj]) projInstances[proj] = new Proj4js.Proj(proj);
		if (projInstances[proj] && projInstances[proj].readyToUse) instance = projInstances[proj];
	}
	// direct transform function
	else {
		instance = proj;
	}
	return instance;
}

var getTransformFunction = function() {
	var transformFunction;
	// try different ways to perform the transformation

	// dojo wrapper for Proj4js
	if (dojo._loadedModules["course.geo.util.proj4js"]) {
		transformFunction = function(fromProj, toProj, point) {
			return u.proj4js.transform(fromProj, toProj, point);
		};
	}
	// original Proj4js
	else if (window.Proj4js) {
		transformFunction = function(fromProj, toProj, point) {
			return Proj4js.transform(fromProj, toProj, point);
		}
	}
	// direct transform function
	else {
		transformFunction = function(fromProj, toProj, point) {
			return transforms[fromProj][toProj](point);
		};
	}
	return transformFunction;
}

// transform a single point (depth == 1)
// transform an array of points (depth == 2)
// transform an array of arrays of points (depth == 3)
// transfrom an array of arrays of arrays of points (depth == 4)
var transform = function(depth, /* Array */entities, /* Array */_entities, kwArgs) {
	if (depth == 1) {
		var p = kwArgs.transformFunction(kwArgs.fromProj, kwArgs.toProj, {x: entities[0], y: entities[1]});
		_entities.push(p.x, p.y);
		u.bbox.extend(kwArgs.bbox, [p.x, p.y]);
	}
	else {
		dojo.forEach(entities, function(entity){
			var _entity = [];
			transform(depth-1, entity, _entity, kwArgs);
			_entities.push(_entity);
		});
	}
}

gp.transform = function(fromProj, toProj, geometry) {
	// _ prefix means "projected"
	var _geometry = geometry;
	var transformFunction = getTransformFunction();
	fromProj = getProjInstance(fromProj);
	toProj = getProjInstance(toProj);
	if (transformFunction && fromProj && toProj) {
		if (dojo.isArray(geometry)) {
			var p1 = transformFunction(fromProj, toProj, {x: geometry[0], y: geometry[1]});
			var p2 = transformFunction(fromProj, toProj, {x: geometry[2], y: geometry[3]});
			_geometry = [p1.x, p1.y, p2.x, p2.y];
		}
		else {
			_geometry = {
				type: geometry.type,
				coordinates: [],
				bbox: [Infinity,Infinity,-Infinity,-Infinity]
			};
			var depth = 0;
			switch (geometry.type) {
				case "Point":
					depth = 1;
					break;
				case "LineString":
					depth = 2;
					break;
				case "Polygon":
					depth = 3;
					break;
				case "MultiLineString":
					depth = 3;
					break;
				case "MultiPolygon":
					depth = 4;
					break;
			}
			if (depth) transform(depth, geometry.coordinates, _geometry.coordinates, {
				fromProj: fromProj,
				toProj: toProj,
				transformFunction: transformFunction,
				bbox: _geometry.bbox
			});
		}
	}
	return _geometry;
}

gp.addTransform = function(fromProj, toProj, transformFunc) {
	if (!transforms[fromProj]) transforms[fromProj] = {};
	transforms[fromProj][toProj] = transformFunc;
}


var p = g.Placemark.prototype;
// patch getGeometry method of the Placemark class
var getGeometry = p.getGeometry; // original getGeometry
p.getGeometry = function() {
	var geometry  = this._geometry;
	if (!geometry) {
		// calling original getGeometry
		geometry = getGeometry.call(this);
		if (geometry) {
			var projection = this.getProjection();
			// compare geometry and current map projection
			var mapProjection = this.map.currentProjection;
			if (projection && mapProjection && projection !== mapProjection) {
				geometry = g.projection.transform(projection, mapProjection, geometry);
				this._geometry = geometry;
			}
		}
	}
	return geometry;
};

// patch the Placemark class
dojo.extend(g.Placemark, {
	getProjection: function() {
		var geometry = this._getGeometry();
		return geometry.projection || this.projection || getCrs(geometry) || getCrs(this) || this.parent.getProjection();
	}
});


// patch the FeatureContainer class
dojo.extend(g.FeatureContainer, {
	getProjection: function() {
		var projection = this.projection || getCrs(this) || this._projection;
		if (!projection) {
			projection = this.parent.getProjection();
			// store the projection for future calls
			this._projection = projection;
		}
		return projection;
	}
});

// patch the Map class
dojo.extend(g.Map, {
	getProjection: function() {
		return this.geometryProjection || this.currentProjection;
	}
});


var getCrs = function(obj) {
	// check if have GeoJson projection definition
	return obj.crs && obj.crs.properties && obj.crs.properties.name;
}


}());