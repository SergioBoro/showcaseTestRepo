dojo.provide("course.geo.control.Highlight");

dojo.require("course.geo.control.Base");
dojo.require("dojox.color");

/*=====
dojo.declare("dojox.charting.action2d.__HighlightCtorArgs", dojox.charting.action2d.__BaseCtorArgs, {
	//	summary:
	//		Additional arguments for highlighting actions.

	//	highlight: String|dojo.Color|Function?
	//		Either a color or a function that creates a color when highlighting happens.
	highlight: null
});
=====*/
(function(){
var DEFAULT_SATURATION  = 100,	// %
	DEFAULT_LUMINOSITY1 = 75,	// %
	DEFAULT_LUMINOSITY2 = 50,	// %

	c = dojox.color,

	cc = function(color){
		return function(){ return color; };
	},

	hl = function(color){
		var a = new c.Color(color),
			x = a.toHsl();
		if(x.s == 0){
			x.l = x.l < 50 ? 100 : 0;
		}else{
			x.s = DEFAULT_SATURATION;
			if(x.l < DEFAULT_LUMINOSITY2){
				x.l = DEFAULT_LUMINOSITY1;
			}else if(x.l > DEFAULT_LUMINOSITY1){
				x.l = DEFAULT_LUMINOSITY2;
			}else{
				x.l = x.l - DEFAULT_LUMINOSITY2 > DEFAULT_LUMINOSITY1 - x.l ?
					DEFAULT_LUMINOSITY2 : DEFAULT_LUMINOSITY1;
			}
		}
		return c.fromHsl(x);
	},
	
	DEFAULT_SCALE = 1.2;

dojo.declare("course.geo.control.Highlight", course.geo.control.Base, {
	//	summary:
	//		Creates a highlighting action on a plot, where an element on that plot
	//		has a highlight on it.
	
	factoryType: "control.Highlight",

	constructor: function(map, kwArgs){
		var highlight = kwArgs && kwArgs.highlight;
		this.colorFun = highlight ? (dojo.isFunction(highlight) ? highlight : cc(highlight)) : hl;
		
		// process optional named parameters
		this.scale = kwArgs && typeof kwArgs.scale == "number" ? kwArgs.scale : DEFAULT_SCALE;

		this.attachFactory(this.enabled);
	},

	process: function(event){
	}
});
})();
