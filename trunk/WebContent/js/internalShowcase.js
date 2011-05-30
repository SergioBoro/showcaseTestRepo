// Jenks breaks algorithm
dojo.require("course.geo.util.jenks");

// projection for the map of Russia
dojo.require("course.geo.util.proj4js");
dojo.require("course.geo.util.proj4js.aea");
dojo.require("course.geo.projection");
course.geo.util.proj4js.addDef("RUSSIA-ALBERS", "+proj=aea +lat_1=52 +lat_2=64 +lat_0=0 +lon_0=105 +x_0=18500000 +y_0=0 +ellps=krass +units=m +towgs84=28,-130,-95,0,0,0,0 +no_defs");

var convertorFunc = function(chartId, chartLegendId, optionSet1, optionSet2) {

   if (dojo.isString(optionSet1)) optionSet1 = dojo.fromJson(optionSet1);
   if (dojo.isString(optionSet2)) optionSet2 = dojo.fromJson(optionSet2);
   var chartOptions = optionSet2;

   // copy id, seriesstrokeWidth: 2,
   chartOptions.id = chartId;
   chartOptions.series = optionSet1.series;
   
   // copy width and height
   if (optionSet1.width) chartOptions.width = optionSet1.width;
   if (optionSet1.height) chartOptions.height = optionSet1.height;

   // copy legend settings
   if (chartLegendId) {
      chartOptions.legend = {id: chartLegendId, options: {horizontal: false}};
   }

   //copy eventHandler
   if (optionSet1.eventHandler) chartOptions.eventHandler = optionSet1.eventHandler;

   // copy labels to the chartOptions
   var axis = chartOptions.axis;
   
   if (chartOptions.axisX && optionSet1.labelsX) {
      chartOptions.axisX.labels = optionSet1.labelsX;
   }
   if (chartOptions.axisY && optionSet1.labelsY) {
      chartOptions.axisY.labels = optionSet1.labelsY;
   }

   return chartOptions;
};

var eventCallbackChartHandler = function(chartEvent) {
   if (chartEvent.type=="onclick") {
      //console.debug(chartEvent.chart.node.id, chartEvent.run.name, chartEvent.index);
      gwtChartFunc(chartEvent.chart.node.id, chartEvent.run.name, chartEvent.index);
   }
};

function mapConvertorFunc(mapId, optionSet1, optionSet2) {
	if (dojo.isString(optionSet1)) optionSet1 = dojo.fromJson(optionSet1);
	if (dojo.isString(optionSet2)) optionSet2 = dojo.fromJson(optionSet2);

	if (optionSet1.layers) dojo.forEach(optionSet1.layers, function(layer){
		if (layer.features) dojo.forEach(layer.features, function(feature){
			// process geometry
			var geometry = feature.geometry;
			if (geometry) {
				if (geometry.pointCoordinates) {
					geometry.coordinates = geometry.pointCoordinates;
					geometry.type = "Point";
				}
				else if (geometry.polygonCoordinates) {
					geometry.coordinates = geometry.polygonCoordinates;
					geometry.type = "Polygon";
				}
				else if (geometry.multiPolygonCoordinates) {
					geometry.coordinates = geometry.multiPolygonCoordinates;
					geometry.type = "MultiPolygon";
				}
				if ("pointCoordinates" in geometry) delete geometry.pointCoordinates;
				if ("polygonCoordinates" in geometry) delete geometry.polygonCoordinates;
				if ("multiPolygonCoordinates" in geometry) delete geometry.multiPolygonCoordinates;
			}
			// process style
			var style = feature.style;
			if (style && dojo.isString(style)) {
				if (style[0]=="#") feature.style = {fill: style};
			}
		})
	})
	
	// load geometries
	var geometries = optionSet2.geometries;
	if (geometries) {
		dojo.require(geometries[0]);
		optionSet2.geometries = dojo.getObject(geometries[1]);
	}
	
	var o = optionSet1;
	o.id = mapId;
	dojo.mixin(optionSet1, optionSet2);
	
	if (o.layers) {
		o.features = o.layers;
		delete o.layers;
		dojo.forEach(o.features, function(featureCollection){
			featureCollection.type = "FeatureCollection";
			dojo.forEach(featureCollection.features, function(feature){
				feature.type = "Feature";
				if (feature.geometry && feature.geometry.type=="Point") feature.geometry.projection = "EPSG:4326";
			})
		});
	}
	
	return o;
}

var eventCallbackMapHandler = function(event) {
   gwtMapFunc(event.feature.map.container, event.feature.id);
}


var eventHandler2 = function(chartEvent) {
   if (chartEvent.type=="onclick") console.debug("2", chartEvent);
};
