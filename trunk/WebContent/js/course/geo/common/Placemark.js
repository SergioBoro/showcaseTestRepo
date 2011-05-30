dojo.provide("course.geo.common.Placemark");

(function() {

dojo.declare("course.geo.common.Placemark", null, {

	getStyleType: function(geometry) {
		var styleType = "point";
		if (geometry.type == "Polygon" || geometry.type == "MultiPolygon") styleType = "polygon";
		else if (geometry.type == "LineString" || geometry.type == "MultiLineString") styleType = "line";
		return styleType;
	},
	
	createShape: function(feature, geometry) {
		var shape,
			coordinates = geometry.coordinates;
		switch (geometry.type) {
			case "Point":
				shape = this.makePoint(feature, coordinates);
				break;
			case "LineString":
				shape = this.makeLineString(feature, coordinates);
				break;			
			case "Polygon":
				shape = this.makePolygon(feature, coordinates);
				break;
			case "MultiPolygon":
				shape = this.makeMultiPolygon(feature, coordinates);
				break;
			case "MultiLineString":
				shape = this.makeMultiLineString(feature, coordinates);
				break;
			case "MultiPoint":
				shape = this.makeMultiPoint(feature, coordinates);
				break;
		}
		return shape;
	}
});

course.geo.common.Placemark.get = function(attr, calculatedStyle, specificStyle) {
	return specificStyle&&specificStyle[attr] ? specificStyle[attr] : calculatedStyle[attr];
};

}());
